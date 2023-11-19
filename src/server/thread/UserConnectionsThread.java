package server.thread;

import testdatabase.TestDatabase;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class UserConnectionsThread extends Thread{
    // falta meter dados a receber no construtor
    private final int listening_port;
    private List<Socket> usersConnected;
    private int nCreatedThreads;

    public UserConnectionsThread(int listening_port){
        this.listening_port = listening_port;

        usersConnected = new ArrayList<>();
        nCreatedThreads = 0;
    }

    @Override
    public void run(){
        Socket toClientSocket;
        try(ServerSocket ss = new ServerSocket(listening_port)){
            try{
                //ciclo há espera de conexões
                while (true) {
                    //novo cliente conecta-se
                    toClientSocket = ss.accept();
                    //não sei se isto é preciso, mas por enquanto deixo estar.
                    usersConnected.add(toClientSocket);
                    nCreatedThreads++;
                    //crio Thread para efetuar comunicação com o cliente e arranco logo com ela
                    Thread t = new Thread(new NewUserConnection(toClientSocket, new TestDatabase()), "Thread " + nCreatedThreads);
                    t.start();
                }
            }catch (IOException e){
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
