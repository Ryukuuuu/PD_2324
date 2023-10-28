package Data;

import java.io.Serial;
import java.io.Serializable;

public class Event implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private String name;
    private String local;
    private String date;
    private long activeCode;
    private String codeValidityEnding = null;
    private String startingTime;
    private String endingTime;

    public Event(String name, String local, String date, long activeCode, String codeValidityEnding, String startingTime, String endingTime) {
        this.name = name;
        this.local = local;
        this.date = date;
        this.activeCode = activeCode;
        this.codeValidityEnding = codeValidityEnding;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
    }

    public Event(String name, String local, String date, String startingTime, String endingTime) {
        this.name = name;
        this.local = local;
        this.date = date;
        this.startingTime = startingTime;
        this.endingTime = endingTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(String startingTime) {
        this.startingTime = startingTime;
    }

    public String getEndingTime() {
        return endingTime;
    }

    public void setEndingTime(String endingTime) {
        this.endingTime = endingTime;
    }

    public long getActiveCode() {
        return activeCode;
    }

    public void setActiveCode(long activeCode) {
        this.activeCode = activeCode;
    }

    public String getCodeValidityEnding() {
        return codeValidityEnding;
    }

    public void setCodeValidityEnding(String codeValidityEnding) {
        this.codeValidityEnding = codeValidityEnding;
    }

    @Override
    public String toString() {
        return "Event{" +
                "name='" + name + '\'' +
                ", local='" + local + '\'' +
                ", date='" + date + '\'' +
                ", startingTime='" + startingTime + '\'' +
                ", endingTime='" + endingTime + '\'' +
                '}';
    }
}
