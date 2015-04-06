package net.mastrgamr.mtapulse.gtfs_realtime;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;

/**
 * Project: MTA Pulse
 * Created: Stuart Smith
 * Date: 4/3/2015
 */

@JsonObject
public class RTRoutes {

    //RouteID for train approaching stop specified in NearbyStopMap
    @JsonField
    public String routeId;
    //List of stop times for approaching trains to the stop
    @JsonField
    public ArrayList<Long> stopTimes;

    public RTRoutes() { }
}
