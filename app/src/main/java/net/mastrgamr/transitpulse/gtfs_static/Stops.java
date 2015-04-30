package net.mastrgamr.transitpulse.gtfs_static;
/**
 * Project: GTFSParsing
 * Author: Stuart Smith
 * Date: 1/18/2015
 */

import java.io.Serializable;

/**
 * Contains information parsed in from the GTFS Static feed's 'stops.txt'.
 * TODO: PointD may not be needed
 */
public class Stops implements Serializable{

    private static final long serialVersionUID = 2L;

    private String stopId;
    private String stopName;
    private String stopLat;
    private String stopLon;

    //private PointD point;

    public Stops() { }

    public Stops(String id, String name, String lat, String lon){
        stopId = id;
        stopName = name;
        stopLat = lat;
        stopLon = lon;
        //point = new PointD(Double.parseDouble(lat), Double.parseDouble(lon));
    }

    public Stops(String name, String lat, String lon){
        stopName = name;
        stopLat = lat;
        stopLon = lon;
        //point = new PointD(Double.parseDouble(lat), Double.parseDouble(lon));
    }

    @Override
    public String toString() {
        return "Name: " + stopName + ", Location: (" + stopLat + ", " + stopLon + ")";
    }

    public String getStopId() {
        return stopId;
    }

    public String getStopName() {
        return stopName;
    }

    public String getStopLat() {
        return stopLat;
    }

    public String getStopLon() {
        return stopLon;
    }

    //public PointD getPoint() { return point; }
}
