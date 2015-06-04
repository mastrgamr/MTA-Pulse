package net.mastrgamr.transitpulse.gtfs_realtime;

import android.location.Location;
import android.util.Log;

import com.google.transit.realtime.GtfsRealtime;

import net.mastrgamr.transitpulse.gtfs_static.Stops;
import net.mastrgamr.transitpulse.tools.ArrayListSearcher;
import net.mastrgamr.transitpulse.tools.DataMaps;
import net.mastrgamr.transitpulse.tools.NearbyStopsProto;
import net.mastrgamr.transitpulse.tools.PointD;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class NearbyStopsFactory {

    private final String LOG_TAG = getClass().getSimpleName();

    GtfsRealtime.FeedMessage feedMessage;

    NearbyStopsProto.NearbyStopsFeed.Builder nearbyFeed;
    NearbyStopsProto.FeedHeader.Builder header;

    NearbyStopsProto.UpDownStops.Builder nearbyNorth;
    NearbyStopsProto.UpDownStops.Builder nearbySouth;
    NearbyStopsProto.NearbyStops.Builder nearby;
    NearbyStopsProto.Routes.Builder route;

    public NearbyStopsFactory(GtfsRealtime.FeedMessage feedMessage) {
        this.feedMessage = feedMessage;
        header = NearbyStopsProto.FeedHeader.newBuilder();
        header.setTimestamp(System.currentTimeMillis());
        nearbyNorth = NearbyStopsProto.UpDownStops.newBuilder();
        nearbySouth = NearbyStopsProto.UpDownStops.newBuilder();
        nearbyNorth.setUpOrDownTown(NearbyStopsProto.UpDownStops.UpOrDowntown.UPTOWN);
        nearbySouth.setUpOrDownTown(NearbyStopsProto.UpDownStops.UpOrDowntown.DOWNTOWN);
    }

    public NearbyStopsProto.NearbyStopsFeed publishNearbyFeed(Location loc, DataMaps<Stops> stopsDataMap) {

        Set<String> keyset = stopsDataMap.keySet();
        List<GtfsRealtime.FeedEntity> entityList = feedMessage.getEntityList();

        ArrayListSearcher search = new ArrayListSearcher();
        nearbyFeed = NearbyStopsProto.NearbyStopsFeed.newBuilder();
        nearbyFeed.setHeader(header);

        long before = System.currentTimeMillis();

        for (GtfsRealtime.FeedEntity entity : entityList) {
            //PointD start = new PointD(40.882305, -73.833145); //HOME
            //PointD start = new PointD(40.754191, -73.982881); //RANDOM, between TS and GS
            PointD start = new PointD(loc.getLatitude(), loc.getLongitude());

            //Log.d(LOG_TAG, start.toString());
            PointD points;

            boolean stopMatched = false;
            for (String stopId : keyset) {
                points = new PointD(
                        Double.parseDouble(stopsDataMap.get(stopId).getStopLat()),
                        Double.parseDouble(stopsDataMap.get(stopId).getStopLon()));
                if (start.distance(points) <= 0.0067) { //If stop is within 2,500 feet, get list of stops
                    for (GtfsRealtime.TripUpdate.StopTimeUpdate stu : entity.getTripUpdate().getStopTimeUpdateList()) {
                        if (stu.getStopId().equals(stopId)) {
                            stopMatched = true;

                            //Check for down/uptown trainstop
                            if (stu.getStopId().endsWith("N")) {
                                boolean addNewNorth = false;
                                boolean addNewRoute = false;
                                int nearbyInd;

                                nearbyInd = search.containsStop(nearbyNorth.getNearbyList(), stu.getStopId());
                                //Log.d("surrstopslist", nearbyInd + " index of stopN - " + stu.getStopId());
                                //TODO:Change to if nsi.stopID == stu.stopID, w/o adding multiple new routes
                                if (nearbyInd < 0) //-1, does not contain stop
                                {
                                    addNewNorth = true;
                                    //System.out.println("nearbyStop not in route");
                                    nearby = NearbyStopsProto.NearbyStops.newBuilder();
                                    nearby.setStopId(stu.getStopId());
                                    nearby.setStopName(stopsDataMap.get(stopId).getStopName());

                                    route = NearbyStopsProto.Routes.newBuilder();
                                    route.setRouteId(entity.getTripUpdate().getTrip().getRouteId());
                                    //Create inner class with actual stop names and stop times field
                                    if (stu.getArrival().getTime() == 0) {
                                        route.addStopTimes(stu.getDeparture().getTime());
                                    } else {
                                        route.addStopTimes(stu.getArrival().getTime());
                                    }
                                    nearby.addRoutes(route);
                                } else //contain the stop
                                {
                                    /**
                                     * If a stop is contained within this NearbyStop, it means there's a route inside.
                                     * A Route has StopTimes in it, check if the route is listed within the stop:
                                     * -if it is, add a stop time to the route
                                     * -if not, create a new Route, StopTime within it, and add it to the NearbyStop index.
                                     */
                                    int routeInd = search.containsRoute(nearbyNorth.getNearby(nearbyInd).getRoutesList(), entity.getTripUpdate().getTrip().getRouteId());
                                    //Log.d("surrstopslist", nearbyInd + " index of stopN");
                                    //TODO:Change to if nsi.stopID == stu.stopID, w/o adding multiple new routes
                                    if (routeInd < 0) //-1, does not contain route
                                    {
                                        addNewRoute = true;
                                        route = NearbyStopsProto.Routes.newBuilder();
                                        route.setRouteId(entity.getTripUpdate().getTrip().getRouteId());
                                        //route.stopTimes = new ArrayList<>();
                                        if (stu.getArrival().getTime() == 0) {
                                            route.addStopTimes(stu.getDeparture().getTime());
                                        } else {
                                            route.addStopTimes(stu.getArrival().getTime());
                                        }
                                    } else {
                                        nearby =
                                                NearbyStopsProto.NearbyStops.newBuilder(nearbyNorth.getNearby(nearbyInd));
                                        route =
                                                NearbyStopsProto.Routes.newBuilder(nearby.getRoutes(routeInd));
                                        route.addStopTimes(stu.getArrival().getTime());
                                        nearby.removeRoutes(routeInd);
                                        //Collections.sort(route.getStopTimesList());
                                        nearby.addRoutes(route);
                                        nearbyNorth.removeNearby(nearbyInd);
                                        nearbyNorth.addNearby(nearby);
                                    }
                                }
                                if (addNewNorth)
                                    nearbyNorth.addNearby(nearby);
                                if (addNewRoute) {
                                    nearby = NearbyStopsProto.NearbyStops.newBuilder(nearbyNorth.getNearby(nearbyInd)).addRoutes(route);
                                    nearbyNorth.removeNearby(nearbyInd);
                                    nearbyNorth.addNearby(nearby);
                                }
                            } else if (stu.getStopId().endsWith("S")) {
                                boolean addNewSouth = false;
                                boolean addNewRoute = false;
                                int nearbyInd;
                                nearbyInd = search.containsStop(nearbySouth.getNearbyList(), stu.getStopId());
                                //Log.d("surrstopslist", nearbyInd + " index of stopS - " + stu.getStopId());
                                if (nearbyInd < 0) //-1, stop not in list
                                {
                                    addNewSouth = true;
                                    //System.out.println("nearbyStop not in route");
                                    nearby = NearbyStopsProto.NearbyStops.newBuilder();
                                    nearby.setStopId(stu.getStopId());
                                    nearby.setStopName(stopsDataMap.get(stopId).getStopName());

                                    route = NearbyStopsProto.Routes.newBuilder();
                                    //routes.stopTimes = new ArrayList<>();
                                    route.setRouteId(entity.getTripUpdate().getTrip().getRouteId());
                                    //Create inner class with actual stop names and stop times field
                                    if (stu.getArrival().getTime() == 0) {
                                        route.addStopTimes(stu.getDeparture().getTime());
                                    } else {
                                        route.addStopTimes(stu.getArrival().getTime());
                                    }
                                    //nearby.trains = new ArrayList<>();
                                    nearby.addRoutes(route);
                                } else {
                                    /**
                                     * If a stop is contained within this NearbyStop, it means there's a route inside.
                                     * A Route has StopTimes in it, check if the route is listed within the stop:
                                     * -if it is, add a stop time to the route
                                     * -if not, create a new Route, StopTime within it, and add it to the NearbyStop index.
                                     */
                                    int routeInd = search.containsRoute(nearbySouth.getNearby(nearbyInd).getRoutesList(), entity.getTripUpdate().getTrip().getRouteId());
                                    //Log.d("surrstopslist", nearbyInd + " index of stopN");
                                    //TODO:Change to if nsi.stopID == stu.stopID, w/o adding multiple new routes
                                    if (routeInd < 0) //-1, does not contain route
                                    {
                                        addNewRoute = true;
                                        route = NearbyStopsProto.Routes.newBuilder();
                                        route.setRouteId(entity.getTripUpdate().getTrip().getRouteId());
                                        if (stu.getArrival().getTime() == 0) {
                                            route.addStopTimes(stu.getDeparture().getTime());
                                        } else {
                                            route.addStopTimes(stu.getArrival().getTime());
                                        }
                                    } else {
                                        nearby =
                                                NearbyStopsProto.NearbyStops.newBuilder(nearbySouth.getNearby(nearbyInd));
                                        route =
                                                NearbyStopsProto.Routes.newBuilder(nearby.getRoutes(routeInd));
                                        route.addStopTimes(stu.getArrival().getTime());
                                        nearby.removeRoutes(routeInd);
                                        //Collections.sort(route.getStopTimesList());
                                        nearby.addRoutes(route);
                                        nearbySouth.removeNearby(nearbyInd);
                                        nearbySouth.addNearby(nearby);
                                    }
                                }
                                if (addNewSouth)
                                    nearbySouth.addNearby(nearby);
                                if (addNewRoute) {
                                    nearby = NearbyStopsProto.NearbyStops.newBuilder(nearbySouth.getNearby(nearbyInd)).addRoutes(route);
                                    nearbySouth.removeNearby(nearbyInd);
                                    nearbySouth.addNearby(nearby);
                                }
                            }
                            break;
                        }
                    }
                }
                if (stopMatched)
                    break;
            }
        }
        nearbyFeed.addUpdown(nearbyNorth);
        nearbyFeed.addUpdown(nearbySouth);
        Log.d(LOG_TAG, System.currentTimeMillis() - before + " FINISHED GENERATING NEARBY STOPS!!");
        return nearbyFeed.build();
    }
}
