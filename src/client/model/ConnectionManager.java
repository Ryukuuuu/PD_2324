package client.model;


import data.Message;


import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ConnectionManager {

        private ModelManager modelManager;
        private Socket socket;
        private ObjectOutputStream oos;
        private ObjectInputStream ois;
        private Message messageFromServer;
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
                        //modelManager.fireUpdate();
                    }catch (SocketTimeoutException e) {
                        System.out.println("Socket timeout Exception");
                    }catch (ClassNotFoundException e){
                        System.out.println("Class not found Exception");
                        keepListening=false;
                    }catch (IOException e) {
                        System.out.println("Error reading from server");
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
        public void sendMessageToServer(Message message){
        try{
            oos.writeObject(message);
            oos.flush();
        }catch (IOException e){
            System.out.println("Error sending message to server");
            e.printStackTrace();
        }
    }
        private void checkMessageReceived(){
            switch (messageFromServer.getType()) {
                case LOGGED_IN -> modelManager.loginSuccess(messageFromServer.getClientData());
                case ACC_CREATED -> modelManager.signinSuccess(messageFromServer.getClientData());
                case LOGOUT -> modelManager.logout();
                case EDIT_LOG_INFO -> modelManager.editUserInformation(messageFromServer.getClientData());
                case SUBMIT_CODE -> modelManager.fireCodeUpdate();
                //Received a message from server and notifies modelManager to update the view
                default -> System.out.println("Not implemented(default of checkMessageReceived)");
            }
        }
        public Message getLastMessageFromServer(){return messageFromServer;}
}
