package Client.model;

import Data.ClientData;
import Data.Message;
import Data.MessageTypes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

public class ClientManager {

    private ClientData clientData;
    private Socket clientSocket;
    private final String serverIp;
    private final int serverPort;

    private static final int TIMEOUT_LOGIN = 10000;

    public ClientManager(String ipServer,String portServer){
        this.clientData = new ClientData();
        this.serverPort = Integer.parseInt(portServer);
        this.serverIp = ipServer;
    }

    //Connects to server and creates ObjectStreams
    //Returns true if the connection is created
    //False if there's a problem
    private boolean connectSocket(){
        try{
            Socket socket = new Socket(InetAddress.getByName(serverIp),serverPort);
            this.setClientSocket(socket);
        }catch (IOException e){
            System.out.println("Error creating socket");
        }
        return clientSocket == null;
    }


    //Returns true if logIn was successfull
    //False otherwise
    public boolean logInClient(ClientData logInInfo){
        try{
            if(connectSocket()){
                return false;
            }

            Message loginMessage = new Message(MessageTypes.LOGIN, logInInfo);

            try(ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream())){

                oos.writeObject(loginMessage);
                oos.flush();

                clientSocket.setSoTimeout(TIMEOUT_LOGIN);

                loginMessage = (Message) ois.readObject();

                System.out.println("recebido: " + loginMessage.getType());

                this.clientData = loginMessage.getClientData();

            }catch (ClassNotFoundException e){
                System.out.println("ClassNotFoundException[ClientManager.logInClient]");
            }catch (SocketTimeoutException e){
                System.out.println("SocketTimeoutException[ClientManager.logInClient]");
            }


        }catch (IOException e) {
            System.out.println("IOException[ClientManager.logInClient]");
        }
        return true;
    }


    public ClientData getClientData() {
        return clientData;
    }

    public void setClientData(ClientData clientData) {
        this.clientData = clientData;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    
}
