package net.mastrgamr.transitpulse.gtfs_realtime;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;

/**
 * Project: MTA Pulse
 * Created: Stuart Smith
 * Date: 4/3/2015
 */

@JsonObject
public class NearbyStopsInfo {

    @JsonField
    public String stopId;
    @JsonField
    public String stopName; //actual stop name from DataMaps<Stops>
    //Map of StopIDs and Array of routes approaching that stop
    @JsonField
    public ArrayList<RTRoutes> trains;

    public NearbyStopsInfo() {
    }
}
