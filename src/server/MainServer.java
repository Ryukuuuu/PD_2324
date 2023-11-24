package server;

import database.DatabaseConnection;
import server.thread.UserConnectionsThread;
import server.thread.multicast.SendHeartBeats;

public class MainServer {
    private static final int MULTICAST_PORT = 4444;
    private static final String MULTICAST_ADDRESS = "230.44.44.44";

    public static void main(String[] args) {
        int client_port, registry_port;
        String local_DB_Directory, service_name, url;
        DatabaseConnection dbConnection;

        if(args.length != 4){
            System.out.println("<Sintaxe> java MainServer <porto clientes> <diretoria da BD> <nome do serviço> <porto registry>");
            return;
        }

        local_DB_Directory = args[1];
        //falta verificar se o caminho corresponde a uma .db

        url = "jdbc:sqlite:" + local_DB_Directory;
        service_name = args[2];
        System.out.println("<Servidor> A iniciar sistema...");
        try{
            client_port = Integer.parseInt(args[0]);
            registry_port = Integer.parseInt(args[3]);
        }catch (NumberFormatException e){
            System.out.println("<ERRO> O porto de escuta para os clientes ou para o lancamento do registry introduzido, nao e um numero valido!\n");
            return;
        }

        dbConnection = new DatabaseConnection(url);
        SendHeartBeats sendHeartBeats = new SendHeartBeats(registry_port,service_name,dbConnection.getDBVersion());
        UserConnectionsThread userConnectionsThread = new UserConnectionsThread(client_port, dbConnection,sendHeartBeats);
        System.out.println("<Servidor> Thread para criacao de Conexoes com Users criada!");
        userConnectionsThread.start();
        sendHeartBeats.start();
    }
}
