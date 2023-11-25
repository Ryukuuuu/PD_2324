package server;

import server.thread.multicast.HeartBeat;

import java.io.*;
import java.net.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class BackupServer extends UnicastRemoteObject implements GetRemoteDatabaseObserver /* implements Runnable */ {
    private static final int MULTICAST_PORT = 4444;
    private static final String MULTICAST_ADDRESS = "230.44.44.44";

    private static final int HEARTBEAT_TIMEOUT = 30000;
    private static final String BACKUP_DB_NAME = "BackupDB";
    public static final int MAX_SIZE = 1000;

    private long DB_version;
    private MulticastSocket socket;
    protected boolean running;

    // ?
    String localFilePath;

    public BackupServer(MulticastSocket socket, String localFilePath ) throws RemoteException {
        DB_version = -1;
        this.socket = socket;

        this.localFilePath = localFilePath;
    }

    public long getDBVersion() {
        return DB_version;
    }

    public void setDBVersion(long DB_version) {
        this.DB_version = DB_version;
    }

    public void terminate() {
        running = false;
    }

    @Override
    public void writeFileChunk(byte[] fileChunk, int nbytes) throws RemoteException, IOException {

    }

    @Override
    public void notifyDatabaseUpdate() throws RemoteException {

    }


   // @Override
    public void run() {
        DatagramPacket packet;
        HeartBeat heartBeat;

        // Função para ir buscar a base de dados
        /*
        try {
            localFilePath = new File(localDirectory.getPath() + File.separator + BACKUP_DB_NAME).getCanonicalPath();
        } catch (IOException e) {
            System.out.println(e);
            return;
        }

        try (FileOutputStream localFileOutputStream = new FileOutputStream(localFilePath)) {
            System.out.println("Ficheiro " + localFilePath + " criado.");

            //Obtem a referencia remota para o servico com nome "servidor-ficheiros-pd"
            objectURL = "rmi://" + packet.getAddress().getHostAddress() + "/" + heartBeat.getRmiServiceName();

            GetRemoteDatabaseService databaseService = (GetRemoteDatabaseService) Naming.lookup(objectURL);

            offset = 0;
            while ((buff = databaseService.getDatabaseCopy(offset)) != null) {
                localFileOutputStream.write(buff);
                offset += buff.length;
            }

            System.out.println("Transferencia do ficheiro " + databaseName + " concluida.");
        }
            } catch(
    RemoteException e)

    {
        System.out.println("Erro remoto - " + e);
    } catch(
    NotBoundException e)

    {
        System.out.println("Servico remoto desconhecido - " + e);
    } catch(
    IOException e)

    {
        System.out.println("Erro E/S - " + e);
    } catch(
    Exception e)

    {
        System.out.println("Erro - " + e);
    }
*/

        while (running) {
            packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);

            try {
                socket.receive(packet);

                try (ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(packet.getData()))) {
                    heartBeat = (HeartBeat) oin.readObject();

                    if (DB_version < heartBeat.getDataBaseVersionNumber()) {
                        terminate();
                        System.out.println("<Backup> Servidor de Backup desatualizado. A terminar...");
                    }

                } catch (ClassNotFoundException e) {
                    System.out.println("\nMensagem recebida de tipo inesperado!");
                }

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

        }
        // remove observer
        socket.close();
        System.exit(1);
}

    public static void main(String[] args) {
        File localDirectory;

        String localFilePath;

        String objectURL;

        byte[] buff;
        long offset;

        BackupServer backupServer = null;

        // ----------- MULTICAST -----------
        InetAddress group;
        MulticastSocket multicastSocket = null;
        DatagramPacket packet;
        NetworkInterface nif; // ?

        HeartBeat heartBeat;

        // BufferedReader bin = new BufferedReader(new InputStreamReader(System.in));
        // ----------------------

        if (args.length != 1) {
            // databases/backup1
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
            group = InetAddress.getByName(MULTICAST_ADDRESS);
            /*
            try {
                nif = NetworkInterface.getByInetAddress(InetAddress.getByName(args[3])); //e.g., 127.0.0.1, 192.168.10.1, ...
            } catch (SocketException | NullPointerException | UnknownHostException | SecurityException ex){
                nif = NetworkInterface.getByName(args[3]); //e.g., lo0, eth0, wlan0, en0, ...
            }
            */
            multicastSocket = new MulticastSocket(MULTICAST_PORT);
            multicastSocket.joinGroup(group); // deprecated... idk
            multicastSocket.setSoTimeout(HEARTBEAT_TIMEOUT);

            //boolean running = true;

            // ---- teste
            backupServer = new BackupServer(multicastSocket, localFilePath);

            packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
// --------------------  ouve o 1º heartbeat
            multicastSocket.receive(packet);

            try (ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(packet.getData())))
            {
                heartBeat = (HeartBeat) oin.readObject();

// ----------------- vai buscar uma cópia da BD
        // --------- (e atualiza o versão)
                //backupServer.setDBVersion(heartBeat.getDataBaseVersionNumber());

                try {
                    localFilePath = new File(localDirectory.getPath() + File.separator + BACKUP_DB_NAME).getCanonicalPath();
                } catch (IOException e) {
                    System.out.println(e);
                    return;
                }

                try (FileOutputStream localFileOutputStream = new FileOutputStream(localFilePath))
                {
                    System.out.println("Ficheiro " + localFilePath + " criado.");

                    //Obtem a referencia remota para o servico com nome "servidor-ficheiros-pd"
                    objectURL = "rmi://" + packet.getAddress().getHostAddress() + "/" + heartBeat.getRmiServiceName();

                    GetRemoteDatabaseService databaseService = (GetRemoteDatabaseService) Naming.lookup(objectURL);

                    offset = 0;
                    while ((buff = databaseService.getDatabaseCopy(offset)) != null) {
                        localFileOutputStream.write(buff);
                        offset += buff.length;
                    }

                    System.out.println("Transferencia do ficheiro " + localFilePath + " concluida.");


    // ----------- Regista-se como listener

                    databaseService.addObserver(backupServer);

                } catch (NotBoundException e) {
                    throw new RuntimeException(e);
                }


                // connection db

                while(true) {

                }



                // -----
                // Thread para aguardar e processar datagramas no socket
                /*

                backupServer = new BackupServer(multicastSocket);
                Thread thread = new Thread(backupServer);
                thread.start();

                */

            } catch (ClassNotFoundException e) {
                System.out.println("\nMensagem recebida de tipo inesperado!");
            } catch (SocketTimeoutException e) {
                System.out.println("<BACKUP> A terminar... Passar 30 segundos sem receber heart beats.");
                System.exit(1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (backupServer != null) {
                    backupServer.terminate();
                }
                if (multicastSocket != null) {
                    multicastSocket.close();
                }
                // eliminar .db
            }
        } catch (SocketException | UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}