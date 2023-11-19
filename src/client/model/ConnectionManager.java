package client.model;

import client.thread.ListenToServerThread;
import data.Message;
import data.MessageTypes;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
            }
            return false;
        }

        private void getSocketStreams(){
            try{
                ois = new ObjectInputStream(socket.getInputStream());
                oos = new ObjectOutputStream(socket.getOutputStream());
            }catch (IOException e){
                System.out.println("Error opening streams");
            }
        }

        /*---------------------THREAD---------------------*/
        private void listenToServer(){
            Thread listenForUpdates = new Thread(() -> {
                //System.out.println("Thread Running");
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
            if (messageFromServer.isMessageReaded()) return;
            switch (messageFromServer.getType()) {
                case LOGGED_IN -> modelManager.loginSuccess(messageFromServer.getClientData());
                case ACC_CREATED -> modelManager.signinSuccess(messageFromServer.getClientData());
                case LOGOUT -> modelManager.logout();
                //Received a message from server and notifies modelManager to update the view
                default -> modelManager.fireUpdate();
            }
        }
        public Message getLastMessageFromServer(){return messageFromServer;}
}
