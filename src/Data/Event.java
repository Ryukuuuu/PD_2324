package Data;

import java.io.Serial;
import java.io.Serializable;

public class Event implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private String name;
    private String local;
    private String date;
    private String startingTime;
    private String endingTime;

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
