package server;

import client.Client;
import client.model.ClientManager;
import data.ClientData;
import data.Message;
import data.MessageTypes;
import testdatabase.TestDatabase;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerTest {
    public static void main(String[] args) {

        int serverPort;
        boolean stop = false;
        Message mess,newMess;
        Scanner sc = new Scanner(System.in);
        int op;
        ClientData connectedClient;

        /*DATABASE TESTE*/
        TestDatabase testDatabase = new TestDatabase();
        /*--------------*/

        try(ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]))){

            while(!stop){
                System.out.println("Waiting for client");
                try(Socket clientSocket = serverSocket.accept();
                ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream())){

                Message messageReceived = (Message) ois.readObject();

                System.out.println("Object received choose success or error");
                /*op = sc.nextInt();
                switch (op){
                    case 1 -> newMess = new Message(MessageTypes.SUCCESS);
                    default -> newMess = new Message(MessageTypes.FAILED);
                }*/
                switch (messageReceived.getType()){
                    case LOGIN -> {
                        connectedClient = testLogin(messageReceived.getClientData(),testDatabase);
                        if(connectedClient == null){
                            System.out.println("Client not found in map");
                            newMess = new Message(MessageTypes.FAILED);
                        }
                        else{
                            System.out.println("Log in successfull");
                            newMess = new Message(MessageTypes.SUCCESS,connectedClient);
                        }
                    }
                    case SIGNIN -> {
                        if(testDatabase.addNewEntryToClients(messageReceived.getClientData())){
                            newMess = new Message(MessageTypes.SUCCESS);
                        }
                        else
                            newMess = new Message(MessageTypes.FAILED);
                    }
                    default -> {
                        newMess = new Message(MessageTypes.FAILED);
                    }
                }

                //newMess.setClientData(fillClientWithData(messageReceived.getClientData().getEmail(), messageReceived.getClientData().getPassword()));


                oos.writeObject(newMess);
                oos.flush();

                }catch (IOException ioe){
                    System.out.println("Error client socket");
                }catch (ClassNotFoundException cnfe){
                    System.out.println("Class not found exception");
                }
            }
        }catch (IOException ioe){
            System.out.println("IOE");
        }
    }

    private static ClientData fillClientWithData(String email,String password){
        ClientData newClient = new ClientData(email,password);

        newClient.setId(1);
        newClient.setName("John");
        newClient.setAdmin(false);
        newClient.setLogged(true);

        return newClient;
    }

    private static ClientData testLogin(ClientData clientData,TestDatabase testDatabase){
        return testDatabase.getClient(clientData.getEmail(), clientData.getPassword());
    }
}
