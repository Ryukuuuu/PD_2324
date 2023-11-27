package server.thread.multicast;

import java.io.Serial;
import java.io.Serializable;

public class HeartBeat implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int rmiPort;
    private String rmiServiceName;
    private long dataBaseVersionNumber;

    public HeartBeat(int rmiPort, String rmiServiceName, long dataBaseVersionNumber) {
        this.rmiPort = rmiPort;
        this.rmiServiceName = rmiServiceName;
        this.dataBaseVersionNumber = dataBaseVersionNumber;
    }

    public int getRmiPort() {
        return rmiPort;
    }


    public String getRmiServiceName() {
        return rmiServiceName;
    }


    public long getDataBaseVersionNumber() {
        return dataBaseVersionNumber;
    }

    @Override
    public String toString() {
        return "HeartBeat{" +
                "rmiPort=" + rmiPort +
                ", rmiServiceName='" + rmiServiceName + '\'' +
                ", dataBaseVersionNumber=" + dataBaseVersionNumber +
                '}';
    }
}
