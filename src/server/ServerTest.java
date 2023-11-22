package server;

import data.ClientData;
import data.Message;

import testdatabase.TestDatabase;

import java.util.ArrayList;
import java.util.Scanner;

public class ServerTest {
    public static void main(String[] args) {

        int serverPort;
        boolean stop = false;
        Message mess, newMess;
        Scanner sc = new Scanner(System.in);
        int op;
        ClientData connectedClient;

        ArrayList<Thread> clients = new ArrayList<>();

        /*DATABASE TESTE*/
        TestDatabase testDatabase = new TestDatabase();
        /*--------------*/


         /*try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]))) {



            System.out.println("Waiting for client");
           try (Socket clientSocket = serverSocket.accept();
                 ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                 ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream())) {

                while (!stop) {
                    Message messageReceived = (Message) ois.readObject();

                    switch (messageReceived.getType()) {
                        case LOGIN -> {
                            connectedClient = testLogin(messageReceived.getClientData(), testDatabase);
                            if (connectedClient == null) {
                                System.out.println("Client not found in map");
                                newMess = new Message(MessageTypes.FAILED);
                            } else {
                                System.out.println("Log in successfull");
                                newMess = new Message(MessageTypes.LOGGED_IN, connectedClient);
                            }
                        }
                        case SIGNING -> {
                            if (testDatabase.addNewEntryToClients(messageReceived.getClientData())) {
                                System.out.println("New ACC-> "+messageReceived.getClientData());
                                newMess = new Message(MessageTypes.ACC_CREATED,messageReceived.getClientData());
                            } else
                                newMess = new Message(MessageTypes.FAILED);
                        }
                        case EDIT_LOG_INFO -> {
                            System.out.println("ClientData received-> " + messageReceived.getClientData().toString());
                            if (testDatabase.editUserInfo(messageReceived.getClientData())) {
                                newMess = new Message(MessageTypes.EDIT_LOG_INFO, messageReceived.getClientData());
                            } else
                                newMess = new Message(MessageTypes.FAILED);
                        }

                        default -> {
                            newMess = new Message(MessageTypes.FAILED);
                        }
                        case LOGOUT -> {
                            newMess = new Message(MessageTypes.LOGOUT);
                        }
                    }

                    //newMess.setClientData(fillClientWithData(messageReceived.getClientData().getEmail(), messageReceived.getClientData().getPassword()));


                    oos.writeObject(newMess);
                    oos.flush();
                    System.out.println("Message sent: " + newMess.getType());

                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }



        } catch (IOException e) {
            throw new RuntimeException(e);
        }
*/
        while(true){}
    }

    private static ClientData fillClientWithData(String email, String password) {
        ClientData newClient = new ClientData(email, password);

        newClient.setId(1);
        newClient.setName("John");
        newClient.setAdmin(false);

        return newClient;
    }

    private static ClientData testLogin(ClientData clientData, TestDatabase testDatabase) {
        return testDatabase.getClient(clientData.getEmail(), clientData.getPassword());
    }
}
