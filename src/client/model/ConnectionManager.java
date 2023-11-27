package client.model;


import data.Message;
import data.MessageTypes;


import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class ConnectionManager {

        private ModelManager modelManager;
        private Socket socket;
        private ObjectOutputStream oos;
        private ObjectInputStream ois;
        private Message messageFromServer;
        private Message lastMessageSent;
        private boolean keepListening = true;


        /*---------------------SETUP---------------------*/
        public ConnectionManager(ModelManager modelManager, String[] args) {
            this.modelManager = modelManager;
            if(!connectToServer(args)){
                System.exit(1);
            }
            getSocketStreams();
            listenToServer();
        }

        private boolean connectToServer(String[] args) {
            try{
                socket = new Socket(args[0], Integer.parseInt(args[1]));
                return true;
            }catch (IOException e){
                System.out.println("Error connecting to server");
                e.printStackTrace();
            }
            return false;
        }

       private void getSocketStreams(){
            try{
                oos = new ObjectOutputStream(socket.getOutputStream());
                ois = new ObjectInputStream(socket.getInputStream());
            }catch (IOException e){
                System.out.println("Error opening streams");
            }
        }

        /*---------------------THREAD---------------------*/
        private void listenToServer(){
            Thread listenForUpdates = new Thread(() -> {
                while(keepListening) {
                    try {
                        messageFromServer = (Message) ois.readObject();
                        checkMessageReceived();
                    } catch (ClassNotFoundException | IOException e) {
                        keepListening=false;
                    }
                }
            });
            listenForUpdates.start();
        }

        /*---------------------EXIT---------------------*/
        public void closeConnection(){
            try {
                ois.close();
                oos.close();
                socket.close();
            }catch (IOException e){
                System.out.println("Error closing connection");
            }
        }

        /*---------------------MESSAGES---------------------*/
        public void sendMessageToServer(Message message) {
            try {
                oos.writeObject(message);
                oos.flush();
                lastMessageSent = message;
            }catch (SocketException e){
                System.out.println("Socket Exception");
            }catch (IOException e){
                System.out.println("Error sending message to server");
                e.printStackTrace();
            }
        }
        public void resendLastMessage(){
            sendMessageToServer(lastMessageSent);
            System.out.println("ResendLastMessage: " + lastMessageSent.getType());
        }

        private void checkMessageReceived(){
            System.out.println("Response from server: " + messageFromServer.getType());
            switch (messageFromServer.getType()) {
                case LOGGED_IN -> modelManager.loginSuccess(messageFromServer.getClientData());
                case ACC_CREATED -> modelManager.signinSuccess(messageFromServer.getClientData());
                case LOGOUT -> modelManager.logout();
                case EDIT_LOG_INFO -> modelManager.editUserInformation(messageFromServer.getClientData());
                case SUBMIT_CODE, GENERATE_EVENT_CODE -> modelManager.fireCodeUpdate();
                case CHECK_USER_REGISTERED_PRESENCES, CHECK_REGISTERED_PRESENCES -> modelManager.fireEventUpdate();
                case CLIENT_UPDATE-> modelManager.fireUpdate();
                case ADD_PRESENCE -> modelManager.fireAddPresenceUpdate();
                case CHECK_PRESENCES,CHECK_CREATED_EVENTS -> modelManager.events();
                case EVENT_UPDATE -> modelManager.fireEventRefreshUpdate();
                case QUIT -> modelManager.fireQuitUpdate();
                //Received a message from server and notifies modelManager to update the view
                default -> modelManager.fireUpdate();
            }
        }
        public Message getLastMessageFromServer(){return messageFromServer;}
}
