package client.thread;

import data.Message;

public class ListenToServerThreadData {

    //Last message received from server
    private Message message;
    //Condition to keep the Tread running
    private boolean keepRunning = true;
    //True if there's new info to read

    private boolean infoToRead = false;

    public ListenToServerThreadData(){}

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public boolean isKeepRunning() {
        return keepRunning;
    }

    public void setKeepRunning(boolean keepRunning) {
        this.keepRunning = keepRunning;
    }

    public boolean isInfoToRead() {
        return infoToRead;
    }

    public void setInfoToRead(boolean infoToRead) {
        this.infoToRead = infoToRead;
    }
}
