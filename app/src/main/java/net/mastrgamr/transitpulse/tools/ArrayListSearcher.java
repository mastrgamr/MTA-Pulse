package net.mastrgamr.transitpulse.tools;

import java.util.List;

/**
 * Project: MTA Pulse
 * Created: Stuart Smith
 * Date: 4/23/2015
 */
public class ArrayListSearcher {

    public ArrayListSearcher() { }

    public int containsStop(List<NearbyStopsProto.NearbyStops> nsi, String stopId){
        for(NearbyStopsProto.NearbyStops nsi1 : nsi) {
            if (nsi1.getStopId().equals(stopId))
                return nsi.indexOf(nsi1);
        }
        return -1;
    }

    public int containsRoute(List<NearbyStopsProto.Routes> rtr, String routeId){
        for(NearbyStopsProto.Routes rtr1 : rtr) {
            if (rtr1.getRouteId().equals(routeId))
                return rtr.indexOf(rtr1);
        }
        return -1;
    }

}
