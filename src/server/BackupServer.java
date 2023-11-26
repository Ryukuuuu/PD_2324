package server;

import server.thread.multicast.HeartBeat;

import java.io.*;
import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;

public class BackupServer extends UnicastRemoteObject implements GetRemoteDatabaseObserver /* implements Runnable */ {
    private static final int MULTICAST_PORT = 4444;
    private static final String MULTICAST_ADDRESS = "230.44.44.44";
    private static final int HEARTBEAT_TIMEOUT = 30000;
    public static final int MAX_SIZE = 1000;
    private static final String BACKUP_DB_NAME = "BackupDB";
    GetRemoteDatabaseService databaseService;
    String localFilePath;

    public BackupServer(GetRemoteDatabaseService databaseService, String localFilePath) throws RemoteException {
        this.databaseService = databaseService;
        this.localFilePath = localFilePath;
    }

    public long getDBVersion() {
        String url = "jdbc:sqlite:" + localFilePath + File.separator + BACKUP_DB_NAME;
        try {
            Connection conn = DriverManager.getConnection(url);

            Statement stm = conn.createStatement();
            ResultSet result = stm.executeQuery("SELECT version FROM Databaseversion");

            if(result.next())
                return result.getLong("version");
        } catch (SQLException e) {
            System.out.println("<Backup> Erro ao aceder a base de dados");
        }
        return -1L;
    }

    @Override
    public void notifyDatabaseUpdate() throws RemoteException {
        File dbpath = new File(localFilePath + File.separator + BACKUP_DB_NAME);

        try (FileOutputStream localFileOutputStream = new FileOutputStream(dbpath.getCanonicalPath())) {
            System.out.println("Ficheiro " + dbpath + " criado.");

            int offset = 0;
            byte[] buff;

            while ((buff = databaseService.getDatabaseCopy(offset)) != null) {
                localFileOutputStream.write(buff);
                offset += buff.length;
            }

            System.out.println("Transferencia do ficheiro " + dbpath + " concluida.");

        } catch (FileNotFoundException e) {
            System.out.println("Ocorreu a excecao {" + e + "} ao tentar abrir o ficheiro!");
        } catch (IOException e) {
            System.out.println("Ocorreu a excecao de E/S: \n\t" + e);
        }
    }

    public static void main(String[] args) {
        File localDirectory;
        String localFilePath, objectURL;

        byte[] buff;
        long offset;

        BackupServer backupServer = null;
        GetRemoteDatabaseService databaseService = null;

        MulticastSocket multicastSocket = null;
        DatagramPacket packet;
        HeartBeat heartBeat;

        if (args.length != 1) {
            System.out.println("<Sintaxe> java BackupServer <diretoria da réplica da BD>");
            return;
        }

        localDirectory = new File(args[0].trim());
        if (!localDirectory.exists()) {
            System.out.println("A diretoria " + localDirectory + " nao existe!");
            return;
        }
        if (!localDirectory.isDirectory()) {
            System.out.println("O caminho " + localDirectory + " nao se refere a uma diretoria!");
            return;
        }
        if (!localDirectory.canWrite()) {
            System.out.println("Sem permissoes de escrita na directoria " + localDirectory);
            return;
        }
        String[] files = localDirectory.list();
        if (files != null && files.length == 0) {
            System.out.println("A diretoria " + localDirectory + "nao esta vazia!");
            return;
        }

        // --------------------- MULTICAST -----------
        try {
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            multicastSocket = new MulticastSocket(MULTICAST_PORT);
            NetworkInterface nif = NetworkInterface.getByName("localhost");

            multicastSocket.joinGroup(new InetSocketAddress(group, MULTICAST_PORT), nif);
            multicastSocket.setSoTimeout(HEARTBEAT_TIMEOUT);

        // --------------------  recebe o 1º heartbeat
            packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
            multicastSocket.receive(packet);

            try (ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(packet.getData())))
            {
                heartBeat = (HeartBeat) oin.readObject();
        // ----------------- vai buscar uma cópia da BD
                try {
                    localFilePath = new File(localDirectory.getPath() + File.separator + BACKUP_DB_NAME).getCanonicalPath();
                } catch (IOException e) {
                    System.out.println(e);
                    return;
                }

                try (FileOutputStream localFileOutputStream = new FileOutputStream(localFilePath)) {
                    System.out.println("Ficheiro " + localFilePath + " criado.");

                    //Obtem a referencia remota para o servico
                    objectURL = "rmi://" + packet.getAddress().getHostAddress() + "/" + heartBeat.getRmiServiceName();

                    databaseService = (GetRemoteDatabaseService) Naming.lookup(objectURL);

                    offset = 0;
                    while ((buff = databaseService.getDatabaseCopy(offset)) != null) {
                        localFileOutputStream.write(buff);
                        offset += buff.length;
                    }

                    System.out.println("Transferencia do ficheiro " + localFilePath + " concluida.");


                    // ----------- Regista-se como listener
                    backupServer = new BackupServer(databaseService, localFilePath);

                    databaseService.addObserver(backupServer);

                } catch (NotBoundException e) {
                    throw new RuntimeException(e);
                }
            }

            while (true) {
                packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);

                try {
                    multicastSocket.receive(packet);

                    try (ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(packet.getData())))
                    {
                        heartBeat = (HeartBeat) oin.readObject();

                        if (backupServer.getDBVersion() != heartBeat.getDataBaseVersionNumber()) {
                            System.out.println("<Backup> Servidor de Backup desatualizado. A terminar...");
                            break;
                        }

                    } catch (ClassNotFoundException e) {
                        System.out.println("\nMensagem recebida de tipo inesperado!");
                    }

                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            databaseService.removeObserver(backupServer);
            UnicastRemoteObject.unexportObject(backupServer, true);
            // remover .db
            new File(localFilePath + File.separator + BACKUP_DB_NAME).delete();

        } catch (ClassNotFoundException e) {
            System.out.println("\nMensagem recebida de tipo inesperado!");
        } catch (SocketTimeoutException e) {
            System.out.println("<BACKUP> A terminar... Passar 30 segundos sem receber heart beats.");
            System.exit(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (multicastSocket != null) {
                multicastSocket.close();
            }
        }
    }
}