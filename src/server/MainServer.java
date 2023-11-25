package server;

import database.DatabaseConnection;
import server.thread.UserConnectionsThread;
import server.thread.multicast.SendHeartBeats;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainServer extends UnicastRemoteObject implements GetRemoteDatabaseService {
    private static final int MULTICAST_PORT = 4444;
    private static final String MULTICAST_ADDRESS = "230.44.44.44";
    public static final int MAX_CHUNK_SIZE = 10000;

    private final String databasePath;

    // ------ CALLBACK
    List<GetRemoteDatabaseObserver> observers;

    protected MainServer(String databasePath) throws RemoteException {
        this.databasePath = databasePath;
        observers = new ArrayList<>();
    }

    @Override
    public void addObserver(GetRemoteDatabaseObserver observer) throws RemoteException {
        synchronized (observers) {
            if (!observers.contains(observer)) {
                observers.add(observer);
                System.out.println("<SERVER> Adicionado observador");
            }
        }
    }
    @Override
    public void removeObserver(GetRemoteDatabaseObserver observer) throws RemoteException {
        synchronized (observers) {
            if (observers.remove(observer))
                System.out.println("<SERVER> Removido observador");        }
    }
    protected void notifyObservers() {
        List<GetRemoteDatabaseObserver> observersToRemove = new ArrayList<>();

        synchronized (observers) {
            for (GetRemoteDatabaseObserver observer : observers) {
                try {
                    observer.notifyDatabaseUpdate();
                } catch (RemoteException e) {
                    observersToRemove.add(observer);
                }
            }
            observers.removeAll(observersToRemove);
        }
    }


    @Override
    public byte[] getDatabaseCopy(long offset) throws RemoteException, IOException {

        //synchronized (dbConnection)

        String requestedCanonicalFilePath = null;

        byte [] fileChunk = new byte[MAX_CHUNK_SIZE];
        int nbytes;


        try {
            requestedCanonicalFilePath = new File(databasePath).getCanonicalPath();
            // acho que não é preciso validar mais
            try(FileInputStream requestedFileInputStream = new FileInputStream(requestedCanonicalFilePath))
            {
                requestedFileInputStream.skip(offset);
                nbytes = requestedFileInputStream.read(fileChunk);
            }

            if(nbytes == -1)
                return null;

            if(nbytes < fileChunk.length) {
                return Arrays.copyOf(fileChunk, nbytes);
            }
            return fileChunk;
        }catch(IOException e){
            System.out.println("Ocorreu a excepcao de E/S: \n\t" + e);
            throw new IOException(e.getCause());
        }
    }

    public static void main(String[] args) {
        int client_port, registry_port;
        String service_name, local_DB_Path, url;
        File local_DB_Directory;
        DatabaseConnection dbConnection;

        if(args.length != 4){
            System.out.println("<Sintaxe> java MainServer <porto clientes> <diretoria da BD> <nome do serviço> <porto registry>");
            return;
        }

        local_DB_Path = args[1].trim();
        local_DB_Directory = new File(local_DB_Path);

        if (!local_DB_Directory.exists()) {
            System.out.println("<Servidor> A diretoria " + local_DB_Directory + " nao existe!");
            return;
        }
        if (!local_DB_Directory.isDirectory()) {
            if(!(local_DB_Directory.isFile() && local_DB_Directory.getName().toLowerCase().endsWith(".db"))) {
                System.out.println("<Servidor> O caminho " + local_DB_Directory + " nao corresponde a uma diretoria nem a um ficheiro de base de dados!");
                return;
            }
        }
        if (!local_DB_Directory.canRead()) {
            System.out.println(" <Servidor>Sem permissoes de leitura na directoria " + local_DB_Directory);
            return;
        }

        url = "jdbc:sqlite:" + local_DB_Path;
        service_name = args[2].trim();

        System.out.println("<Servidor> A iniciar servidor principal ...");

        try{
            client_port = Integer.parseInt(args[0]);
            registry_port = Integer.parseInt(args[3]);
        }catch (NumberFormatException e){
            System.out.println("<ERRO> O porto de escuta para os clientes ou para o lancamento do registry introduzido, nao e um numero valido!\n");
            return;
        }

        try {
            try {
                LocateRegistry.createRegistry(registry_port);
                System.out.println("<Servidor> Lancado o RMI registry no porto: " + registry_port);
            } catch (RemoteException e) {
                System.out.println("<Servidor> Registry provavelmente ja em execucao na maquina local!");
            }
            // serviço criado
            MainServer databaseService = new MainServer(local_DB_Path);
            System.out.println("<Servidor> Servico GetRemoteDatabase criado e em execucao (" + databaseService.getRef().remoteToString() + "...");

            // Regista o servico no rmiregistry
            Naming.bind("rmi://localhost/" + service_name, databaseService);
            System.out.println("<Servidor> Servico " + service_name + " registado no registry...");


        }catch(RemoteException e){
            System.out.println("Erro remoto - " + e);
            return;
            //System.exit(1);
        }catch(Exception e){
            System.out.println("Erro - " + e);
            return;
            //System.exit(1);
        }


        dbConnection = new DatabaseConnection(url);
        SendHeartBeats sendHeartBeats = new SendHeartBeats(registry_port,service_name,dbConnection.getDBVersion());
        UserConnectionsThread userConnectionsThread = new UserConnectionsThread(client_port, dbConnection,sendHeartBeats);
        System.out.println("<Servidor> Thread para criacao de Conexoes com Users criada!");
        userConnectionsThread.start();
        sendHeartBeats.start();
    }

}
