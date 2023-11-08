import Data.Message;
import Data.MessageTypes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
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

        try(ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]))){

            while(!stop){
                System.out.println("Waiting for client");
                try(Socket clientSocket = serverSocket.accept();
                ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream())){

                System.out.println("Before read");
                System.out.println(ois.readObject());
                System.out.println("Received mess");

                System.out.println("Object received choose success or error");
                op = sc.nextInt();
                switch (op){
                    case 1 -> newMess = new Message(MessageTypes.SUCCESS);
                    default -> newMess = new Message(MessageTypes.FAILED);
                }

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
}
