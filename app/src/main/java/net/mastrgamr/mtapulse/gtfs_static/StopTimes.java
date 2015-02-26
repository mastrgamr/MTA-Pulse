package net.mastrgamr.mtapulse.gtfs_static;

import java.io.Serializable;

/**
 * Project: MTA Pulse
 * Created: Stuart Smith
 * Date: 1/25/2015.
 */
public class StopTimes implements Serializable {

    private String tripId;
    private String arrivalTime;
    private String departureTime;
    private String stopId;
    private String stopSeq;

    public StopTimes() { }

    public StopTimes(String tripId, String arrivalTime, String departureTime, String stopId, String stopSeq) {
        this.tripId = tripId;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.stopId = stopId;
        this.stopSeq = stopSeq;
    }

    public StopTimes(String arrivalTime, String departureTime, String stopId, String stopSeq) {
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.stopId = stopId;
        this.stopSeq = stopSeq;
    }

    @Override
    public String toString() {
        return "StopTimes{" +
                "tripId='" + tripId + '\'' +
                ", arrivalTime='" + arrivalTime + '\'' +
                ", departureTime='" + departureTime + '\'' +
                ", stopId='" + stopId + '\'' +
                ", stopSeq='" + stopSeq + '\'' +
                '}';
    }

    public String getTripId() {
        return tripId;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getStopId() {
        return stopId;
    }

    public String getStopSeq() {
        return stopSeq;
    }
}
