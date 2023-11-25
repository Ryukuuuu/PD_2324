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
    private static final int SLEEPTIME = 10000;
    private MulticastSocket multicastSocket;
    private ByteArrayOutputStream baos;
    private ObjectOutputStream oos;
    private HeartBeat heartBeat;
    //private long dataBaseVersion = 0;
    private boolean keepRunning = true;

    public SendHeartBeats(int rmiPort,String rmiServiceName,long dataBaseVersion){
        this.heartBeat = new HeartBeat(rmiPort,rmiServiceName,dataBaseVersion);
    }


    @Override
    public void run(){
        try{
            multicastSocket = new MulticastSocket(multicastPort);
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);

            InetAddress group = InetAddress.getByName(multicastGroup);
            SocketAddress sa = new InetSocketAddress(group,multicastPort);
            NetworkInterface ni = NetworkInterface.getByName("localhost");
            multicastSocket.joinGroup(sa,ni);

            while(keepRunning){
                Thread.sleep(SLEEPTIME);
                synchronized (this) {
                    oos.writeObject(heartBeat);
                    DatagramPacket dp = new DatagramPacket(baos.toByteArray(), baos.size(),InetAddress.getByName(multicastGroup),multicastPort);
                    multicastSocket.send(dp);
                    //System.out.println("HeartBeat sent[Thread]");
                }
            }
        } catch (IOException | InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    public synchronized void setDataBaseVersion(long dataBaseVersion){
        if(heartBeat.getDataBaseVersionNumber() == dataBaseVersion)
            return;
        heartBeat.setDataBaseVersionNumber(dataBaseVersion);
        try {
            oos.writeObject(heartBeat);
            DatagramPacket dp = new DatagramPacket(baos.toByteArray(), baos.size(),InetAddress.getByName(multicastGroup),multicastPort);
            multicastSocket.send(dp);
            //System.out.println("HeartBeat sent[setDataBaseVersion]");
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    /*
    Enumeration<NetworkInterface> allNis = NetworkInterface.getNetworkInterfaces();
        while(allNis.hasMoreElements()){
            NetworkInterface ni = allNis.nextElement();
            System.out.println(ni.getName());
            Enumeration<InetAddress> allIPs = ni.getInetAddresses();
            while(allIPs.hasMoreElements()){
                System.out.println(allIPs.nextElement().getHostAddress());
            }
        }
    */
}
