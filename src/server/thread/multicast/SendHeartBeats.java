package server.thread.multicast;

import data.Event;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;

public class SendHeartBeats extends Thread{

    private static final int multicastPort = 4444;
    private static final String multicastGroup = "230.44.44.44";
    private HeartBeat heartBeat;
    //private long dataBaseVersion = 0;
    private boolean keepRunning = true;

    public SendHeartBeats(int rmiPort,String rmiServiceName,long dataBaseVersion){
        this.heartBeat = new HeartBeat(rmiPort,rmiServiceName,dataBaseVersion);
        //this.dataBaseVersion = dataBaseVersion;
    }


    @Override
    public void run(){
        try(DatagramSocket datagramSocket = new DatagramSocket(multicastPort,InetAddress.getByName(multicastGroup));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos)
        ){
            while(keepRunning){
                Thread.sleep(10000);
                oos.writeObject(heartBeat);
                DatagramPacket dp = new DatagramPacket(baos.toByteArray(),baos.size());
                datagramSocket.send(dp);
            }
        } catch (IOException | InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    public void setDataBaseVersion(long dataBaseVersion){
        heartBeat.setDataBaseVersionNumber(dataBaseVersion);
    }
}
