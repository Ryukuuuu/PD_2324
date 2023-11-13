package client.thread;

import data.Message;

import java.io.IOException;
import java.io.ObjectInputStream;


public class ListenToServerThread implements Runnable{

    //ObjectInputStream from the socket connected to the server
    protected ObjectInputStream ois;
    //Contains all the info needed to manage the thread
    protected ListenToServerThreadData threadData;

    public ListenToServerThread(ObjectInputStream ois){
        this.ois = ois;
        this.threadData = new ListenToServerThreadData();
    }


    @Override
    public void run() {
        while (threadData.isKeepRunning()){
            try{
                //System.out.println("Thread started");
                threadData.setMessage((Message)ois.readObject());
                //notify();
            }catch (IOException e){
                System.out.println("IOException[ListenToServerThread]");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
