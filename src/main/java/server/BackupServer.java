package server;

import server.thread.multicast.HeartBeat;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;

public class BackupServer extends UnicastRemoteObject implements GetRemoteDatabaseObserver /* implements Runnable */ {
    private static final int MULTICAST_PORT = 4444;
    private static final String MULTICAST_ADDRESS = "230.44.44.44";
    private static final int HEARTBEAT_TIMEOUT = 30000;
    public static final int MAX_SIZE = 10000;
    private static final String BACKUP_DB_NAME = "BackupDB.db";
    GetRemoteDatabaseService databaseService;
    String localFilePath;
    String databaseCanonicalPath;
    String databasePath;

    public BackupServer(GetRemoteDatabaseService databaseService, String localFilePath) throws RemoteException {
        this.databaseService = databaseService;
        this.localFilePath = localFilePath;
    }

    public long getDBVersion() {
        String url = "jdbc:sqlite:" + localFilePath + File.separator + BACKUP_DB_NAME;

        try(Connection conn = DriverManager.getConnection(url))
        {
            Statement stm = conn.createStatement();
            ResultSet result = stm.executeQuery("SELECT version FROM Databaseversion");

            if(result.next()) {
                long version = result.getLong("version");
                return version;
            }
        } catch (SQLException e) {
            System.out.println("<Backup> Erro ao aceder a base de dados");
        }
        return -1L;
    }

    @Override
    public void notifyDatabaseUpdate() throws RemoteException {
        File dbpath = new File(localFilePath + File.separator + BACKUP_DB_NAME);

        try (FileOutputStream localFileOutputStream = new FileOutputStream(dbpath.getCanonicalPath()))
        {
            System.out.println("<BACKUP> Ficheiro " + dbpath + " aberto.");

            int offset = 0;
            byte[] buff;

            while ((buff = databaseService.getDatabaseCopy(offset)) != null) {
                localFileOutputStream.write(buff);
                offset += buff.length;
            }

            System.out.println("<BACKUP> Transferencia do ficheiro " + dbpath + " concluida.");

        } catch (FileNotFoundException e) {
            System.out.println("<BACKUP> Ocorreu a excecao {" + e + "} ao tentar abrir o ficheiro!");
        } catch (IOException e) {
            System.out.println("<BACKUP> Ocorreu a excecao de E/S: \n\t" + e);
        }
    }

    public static void main(String[] args) {
        File localDirectory;
        String databaseCanonicalPath = null, objectURL;

        byte[] buff;
        long offset;

        BackupServer backupServer = null;
        GetRemoteDatabaseService databaseService = null;

        MulticastSocket multicastSocket = null;
        DatagramPacket packet;

        if (args.length != 1) {
            System.out.println("<Sintaxe> java BackupServer <diretoria da réplica da BD>");
            return;
        }
        localDirectory = new File(args[0].trim());
        if (!localDirectory.exists()) {
            System.out.println("<BACKUP> A diretoria " + localDirectory + " nao existe!");
            return;
        }
        if (!localDirectory.isDirectory()) {
            System.out.println("<BACKUP> O caminho " + localDirectory + " nao se refere a uma diretoria!");
            return;
        }
        if (!localDirectory.canWrite()) {
            System.out.println("<BACKUP> Sem permissoes de escrita na directoria " + localDirectory);
            return;
        }

        String[] files = localDirectory.list();
        if (files == null || files.length > 0) {
            System.out.println("<BACKUP> A diretoria " + localDirectory + " nao esta vazia!");
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
                HeartBeat heartBeat = (HeartBeat) oin.readObject();
        // ----------------- vai buscar uma cópia da BD
                try {
                    databaseCanonicalPath = new File(localDirectory.getPath() + File.separator + BACKUP_DB_NAME).getCanonicalPath();
                } catch (IOException e) {
                    System.out.println(e);
                    return;
                }
                System.out.println(heartBeat);
                try (FileOutputStream localFileOutputStream = new FileOutputStream(databaseCanonicalPath))
                {
                    System.out.println("<BACKUP> Ficheiro " + databaseCanonicalPath + " criado.");

                    //Obtem a referencia remota para o servico
                    System.out.println("vamos ver " + packet.getAddress().getHostAddress());

                    objectURL = "rmi://" + packet.getAddress().getHostAddress() + ":" + heartBeat.getRmiPort() + "/" + heartBeat.getRmiServiceName();
                    System.out.println(objectURL);

                    databaseService = (GetRemoteDatabaseService) Naming.lookup(objectURL);

                    offset = 0;
                    while ((buff = databaseService.getDatabaseCopy(offset)) != null) {
                        localFileOutputStream.write(buff);
                        offset += buff.length;
                    }

                    System.out.println("<BACKUP> Transferencia do ficheiro " + databaseCanonicalPath + " concluida.");


                    // ----------- Regista-se como listener
                    backupServer = new BackupServer(databaseService, args[0].trim());

                    databaseService.addObserver(backupServer);

                } catch (NotBoundException e) {
                    throw new RuntimeException(e);
                }
            }

            while (true) {

                try {
                    multicastSocket.receive(packet);

                    try (ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(packet.getData())))
                    {
                        HeartBeat heartBeatCiclo = (HeartBeat) oin.readObject();

                        System.out.println(heartBeatCiclo);
                        System.out.println("Recebi heart beat com versão: » " + heartBeatCiclo.getDataBaseVersionNumber());
                        System.out.println("My DB version: » " + backupServer.getDBVersion());


                        if (backupServer.getDBVersion() != heartBeatCiclo.getDataBaseVersionNumber()) {
                            System.out.println("<BACKUP> Servidor de Backup desatualizado. A terminar...");
                            break;
                        }
                    } catch (ClassNotFoundException e) {
                        System.out.println("<BACKUP> Mensagem recebida de tipo inesperado!");
                    }
                } catch (IOException ex) {
                    System.out.println("<BACKUP> Erro I/O!");
                    break;
                }
            }

            System.out.println("<BACKUP> vou terminar... ");

        } catch (ClassNotFoundException e) {
            System.out.println("<BACKUP> Mensagem recebida de tipo inesperado!");
        } catch (SocketTimeoutException e) {
            System.out.println("<BACKUP> A terminar... Passar 30 segundos sem receber heart beats.");
        } catch (IOException e) {
            System.out.println("<BACKUP> Erro I/O...");
        } finally {
            if (multicastSocket != null) {
                multicastSocket.close();
            }
            try {
                System.out.println("<BACKUP> A libertar recursos ");
                assert databaseService != null;
                databaseService.removeObserver(backupServer);
                UnicastRemoteObject.unexportObject(backupServer, true);
                Files.delete(Paths.get(databaseCanonicalPath));
            } catch (Exception ignored) { }
        }
    }
}