package net.mastrgamr.transitpulse.tools;

import net.mastrgamr.transitpulse.gtfs_realtime.NearbyStopsInfo;
import net.mastrgamr.transitpulse.gtfs_realtime.RTRoutes;

import java.util.ArrayList;

/**
 * Project: MTA Pulse
 * Created: Stuart Smith
 * Date: 4/23/2015
 */
public class ArrayListSearcher2 {

    public ArrayListSearcher2() {
    }

    public int containsStop(ArrayList<NearbyStopsInfo> nsi, String stopId) {
        for (NearbyStopsInfo nsi1 : nsi) {
            if (nsi1.stopId.equals(stopId))
                return nsi.indexOf(nsi1);
        }
        return -1;
    }

    public int containsRoute(ArrayList<RTRoutes> rtr, String routeId) {
        for (RTRoutes rtr1 : rtr) {
            if (rtr1.routeId.equals(routeId))
                return rtr.indexOf(rtr1);
        }
        return -1;
    }

}
