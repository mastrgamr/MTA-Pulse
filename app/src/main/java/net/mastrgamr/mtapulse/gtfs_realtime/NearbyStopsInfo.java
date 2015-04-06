package net.mastrgamr.mtapulse.gtfs_realtime;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * Project: MTA Pulse
 * Created: Stuart Smith
 * Date: 4/3/2015
 */

@JsonObject
public class NearbyStopsInfo {

    //Map of StopIDs and Array of routes approaching that stop
    @JsonField
    public Map<String, ArrayList<RTRoutes>> stops;

    public NearbyStopsInfo() { }
}
