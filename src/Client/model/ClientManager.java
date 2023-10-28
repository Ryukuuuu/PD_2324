package Client.model;

import Data.ClientData;
import Data.Message;
import Data.MessageTypes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientManager {

    private ClientData clientData;
    private Socket clientSocket;

    public ClientManager(String[] args){
        setupClientManager(args);
        clientData = new ClientData();
    }

    private boolean setupClientManager(String[] args){

        if(args.length != 2){
            System.out.println("Expected 2 arguments <serverAddress> <serverPort>");
            return false;
        }

        try(Socket socket = new Socket(InetAddress.getByName(args[0]),Integer.parseInt(args[1]))){
            setClientSocket(socket);
        }catch (UnknownHostException uhe){
            System.out.println("Unknown host exception");
        }catch (IOException ioe){
            System.out.println("IOException");
        }

        return true;
    }

    public boolean authentication(String email,String password){
        Message message = new Message(MessageTypes.LOGIN,new ClientData(email,password));

        try(ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());){

            oos.writeObject(message);
            oos.flush();

            message = (Message)ois.readObject();

            if(message.getType() == MessageTypes.SUCCESS){
                clientData.fillClientDataAfterLogin(message.getClientData());
                oos.close();
                ois.close();
                return true;
            }
            else{
                System.out.println("Error on login");
            }

        }catch (IOException ioe){
            System.out.println("IOException[ClientManager.authentication]");
        }catch (ClassNotFoundException cnfe){
            System.out.println("ClassNotFoundException[ClientManager.authentication]");
        }
        return false;
    }

    public boolean sendMessageToServer(MessageTypes type){
        Message message = new Message(type,clientData);

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
