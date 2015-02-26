package net.mastrgamr.mtapulse.feedobjects;

/**
 * Project: GTFSParsing
 * Author: Stuart Smith
 * Date: 1/23/2015
 */
public class TransportationType {

    private String name;
    private String status;
    private String text;
    private String date;
    private String time;

    public TransportationType(){ }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "SubwayLine{" +
                "name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", text='" + text + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
