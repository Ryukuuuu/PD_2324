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

    public void setRmiPort(int rmiPort) {
        this.rmiPort = rmiPort;
    }

    public String getRmiServiceName() {
        return rmiServiceName;
    }

    public void setRmiServiceName(String rmiServiceName) {
        this.rmiServiceName = rmiServiceName;
    }

    public long getDataBaseVersionNumber() {
        return dataBaseVersionNumber;
    }

    public void setDataBaseVersionNumber(long dataBaseVersionNumber) {
        this.dataBaseVersionNumber = dataBaseVersionNumber;
    }
}
