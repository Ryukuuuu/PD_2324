package server;

import database.DatabaseConnection;
import server.thread.UserConnectionsThread;
import server.thread.multicast.SendHeartBeats;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainServer extends UnicastRemoteObject implements GetRemoteDatabaseService {
    public static final int MAX_CHUNK_SIZE = 1000;
    public static final String DEFAULTDATABASE = "databaseDB.db";

    private final String databasePath;
    private DatabaseConnection dbConnection;

    // ------ CALLBACK
    List<GetRemoteDatabaseObserver> observers;

    protected MainServer(String databasePath, DatabaseConnection dbConnection) throws RemoteException {
        this.databasePath = databasePath;
        this.dbConnection = dbConnection;
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
                System.out.println("<SERVER> Removido observador");
        }
    }
    public void notifyObservers() {
        List<GetRemoteDatabaseObserver> observersToRemove = new ArrayList<>();

        System.out.println("<SERVER> A notificar observers: " + dbConnection.getDBVersion()); // apagar

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
        synchronized (dbConnection) {
            String requestedCanonicalFilePath = null;
            byte [] fileChunk = new byte[MAX_CHUNK_SIZE];
            int nbytes;

            try {
                requestedCanonicalFilePath = new File(databasePath).getCanonicalPath();

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
    }

    public static void main(String[] args) {
        int client_port, registry_port;

        String service_name, local_DB_Path, url;
        File local_DB_File_or_Directory;

        DatabaseConnection dbConnection;
        MainServer mainDBService;

        if(args.length != 4){
            System.out.println("<Sintaxe> java MainServer <porto clientes> <diretoria da BD> <nome do serviço> <porto registry>");
            return;
        }

        local_DB_Path = args[1].trim();
        local_DB_File_or_Directory = new File(local_DB_Path);

        if (local_DB_File_or_Directory.isDirectory()){
            File[] list = local_DB_File_or_Directory.listFiles();
            if (list.length == 0){
                System.out.println("<Servidor|Arranque> Nao ha ficheiros nesta diretoria. Vamos criar um '" + DEFAULTDATABASE + "' por si!");
                local_DB_Path += File.separator + DEFAULTDATABASE;
            }
            else{
                for (File file : list){
                    if (file.getName().toLowerCase().endsWith(".db")) {
                        if (!(local_DB_File_or_Directory.canRead() && local_DB_File_or_Directory.canWrite())) {
                            System.out.println("<Servidor|Arranque> Sem permissoes de leitura e/ou escrita do ficheiro '" + file.getName() + "'!");
                            continue;
                        }
                        try {
                            local_DB_Path = file.getCanonicalPath();
                            break;
                        } catch (IOException e) {
                            System.out.println("<Servidor|ERRO> Nao foi possivel aceder ao sistema de ficheiros!");
                        }
                    }
                }
                if (!local_DB_Path.toLowerCase().endsWith(".db")){
                    System.out.println("<Servidor|Arranque> Nao existe ficheiros de base de dados nesta diretoria. Vamos criar um '" + DEFAULTDATABASE + "' por si!");
                    local_DB_Path += File.separator + DEFAULTDATABASE;
                }
            }
        }
        else {
            if (local_DB_File_or_Directory.isFile()) {
                if(!local_DB_File_or_Directory.getName().toLowerCase().endsWith(".db")) {
                    System.out.println("<Servidor|Arranque> O caminho " + local_DB_File_or_Directory + " nao corresponde a um ficheiro de base de dados!");
                    return;
                }
                if (!(local_DB_File_or_Directory.canRead() && local_DB_File_or_Directory.canWrite())) {
                    System.out.println("<Servidor|Arranque> Sem permissoes de leitura e/ou escrita do ficheiro '" + local_DB_File_or_Directory + "'!");
                    return;
                }
            }
            else{
                System.out.println("<Servidor|Arranque> O caminho " + local_DB_File_or_Directory + " nao corresponde a um ficheiro de base de dados nem a uma diretoria existente!");
                return;
            }
        }

        url = "jdbc:sqlite:" + local_DB_Path;
        dbConnection = new DatabaseConnection(url);

        service_name = args[2].trim();

        System.out.println("<Servidor|Arranque> A iniciar servidor principal ...");

        try{
            client_port = Integer.parseInt(args[0]);
            registry_port = Integer.parseInt(args[3]);
        }catch (NumberFormatException e){
            System.out.println("<Servidor|ERRO> O porto de escuta para os clientes ou para o lancamento do registry introduzido, nao e um numero valido!\n");
            return;
        }

        try {
            try {
                LocateRegistry.createRegistry(registry_port);
                System.out.println("<Servidor|Registry> Lancado o RMI registry no porto: " + registry_port);
            } catch (RemoteException e) {
                System.out.println("<Servidor|ERRO> Registry provavelmente ja em execucao na maquina local!");
            }
            // serviço criado
            mainDBService = new MainServer(local_DB_Path, dbConnection);
            System.out.println("<Servidor|Servico> Servico GetRemoteDatabase criado e em execucao.");

            // Regista o servico no rmiregistry
            Naming.bind("rmi://localhost/" + service_name, mainDBService);
            System.out.println("<Servidor|Registry> Servico " + service_name + " registado no registry...");


        }catch(RemoteException e){
            System.out.println("<Servidor|ERRO> Erro remoto - " + e.getMessage());
            return;
        }catch(Exception e){
            System.out.println("<Servidor|ERRO> - " + e.getMessage());
            return;
        }

        SendHeartBeats sendHeartBeats = new SendHeartBeats(registry_port,service_name,dbConnection.getDBVersion());
        UserConnectionsThread userConnectionsThread = new UserConnectionsThread(client_port, dbConnection, sendHeartBeats,mainDBService);
        
        System.out.println("<Servidor|Threads> Thread para criacao de Conexoes com Clients criada!");
        System.out.println("<Servidor|Threads> Thread para envio de heartbeats aos BackupServers criada!");
        userConnectionsThread.start();
        sendHeartBeats.start();
    }
}