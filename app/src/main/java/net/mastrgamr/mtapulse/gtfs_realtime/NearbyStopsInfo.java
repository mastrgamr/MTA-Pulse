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

    @JsonField
    public String stopId;
    //Map of StopIDs and Array of routes approaching that stop
    @JsonField
    public ArrayList<RTRoutes> trains;

    public NearbyStopsInfo() { }

    public boolean containsRoute(ArrayList<NearbyStopsInfo> nsi, String routeId){
        for(NearbyStopsInfo nsi1 : nsi) {
            for (RTRoutes routes : nsi1.trains) {
                if (routes.routeId.equals(routeId))
                    return true;
            }
        }
        return false;
    }

    public boolean containsStop(ArrayList<NearbyStopsInfo> nsi, String stopId){
        for(NearbyStopsInfo nsi1 : nsi) {
            if (nsi1.stopId.equals(stopId))
                return true;
        }
        return false;
    }
}
