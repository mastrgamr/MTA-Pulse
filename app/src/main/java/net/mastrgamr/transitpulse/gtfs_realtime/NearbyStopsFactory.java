package net.mastrgamr.transitpulse.gtfs_realtime;

import android.location.Location;
import android.util.Log;

import com.google.transit.realtime.GtfsRealtime;

import net.mastrgamr.transitpulse.gtfs_static.Stops;
import net.mastrgamr.transitpulse.tools.ArrayListSearcher;
import net.mastrgamr.transitpulse.tools.DataMaps;
import net.mastrgamr.transitpulse.tools.NearbyStopsProto;
import net.mastrgamr.transitpulse.tools.PointD;

public class NearbyStopsFactory {

    private final String LOG_TAG = getClass().getSimpleName();

    GtfsRealtime.FeedMessage feedMessage;

    NearbyStopsProto.NearbyStopsFeed.Builder nearbyFeed;
    NearbyStopsProto.FeedHeader.Builder header;

    NearbyStopsProto.UpDownStops.Builder nearbyNorth;
    NearbyStopsProto.UpDownStops.Builder nearbySouth;
    NearbyStopsProto.NearbyStops.Builder nearby;
    NearbyStopsProto.Routes.Builder route;

    public NearbyStopsFactory(GtfsRealtime.FeedMessage feedMessage){
        this.feedMessage = feedMessage;
        header = NearbyStopsProto.FeedHeader.newBuilder();
        header.setTimestamp(System.currentTimeMillis());
        nearbyNorth = NearbyStopsProto.UpDownStops.newBuilder();
        nearbySouth = NearbyStopsProto.UpDownStops.newBuilder();
        nearbyNorth.setUpOrDownTown(NearbyStopsProto.UpDownStops.UpOrDowntown.UPTOWN);
        nearbySouth.setUpOrDownTown(NearbyStopsProto.UpDownStops.UpOrDowntown.DOWNTOWN);
        //nearby = NearbyStopsProto.NearbyStops.newBuilder();
    }

    public NearbyStopsProto.NearbyStopsFeed publishNearbyFeed(Location loc, DataMaps<Stops> stopsDataMap){

        ArrayListSearcher search = new ArrayListSearcher();
        nearbyFeed = NearbyStopsProto.NearbyStopsFeed.newBuilder();
        nearbyFeed.setHeader(header);

        long before = System.currentTimeMillis();

        /*nearbyStops = new ArrayList<>();
        nearbyNorth = new ArrayList<>();
        nearbySouth = new ArrayList<>();
        nearby = new NearbyStopsInfo();*/

        for(GtfsRealtime.FeedEntity entity : feedMessage.getEntityList())
        {
            //PointD start = new PointD(40.882305, -73.833145); //HOME
            PointD start = new PointD(40.754191, -73.982881); //RANDOM, between TS and GS
            //PointD start = new PointD(loc.getLatitude(), loc.getLongitude());

            //Log.d(LOG_TAG, start.toString());
            PointD points;

            for(String stopId : stopsDataMap.keySet())
            {
                points = new PointD(
                        Double.parseDouble(stopsDataMap.get(stopId).getStopLat()),
                        Double.parseDouble(stopsDataMap.get(stopId).getStopLon()));
                if(start.distance(points) <= 0.0067)
                { //If stop is within 2,500 feet, get list of stops
                    for (GtfsRealtime.TripUpdate.StopTimeUpdate stu : entity.getTripUpdate().getStopTimeUpdateList()) {
                        if (stu.getStopId().equals(stopId)) {

                            //Check for down/uptown trainstop
                            if (stu.getStopId().endsWith("N"))
                            {
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

                                    NearbyStopsProto.Routes.Builder routes = NearbyStopsProto.Routes.newBuilder();
                                    routes.setRouteId(entity.getTripUpdate().getTrip().getRouteId());
                                    //Create inner class with actual stop names and stop times field
                                    if(stu.getArrival().getTime() == 0) {
                                        routes.addStopTimes(stu.getDeparture().getTime());
                                    } else {
                                        routes.addStopTimes(stu.getArrival().getTime());
                                    }
                                    nearby.addRoutes(routes);
                                }
                                else //contain the stop
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
                                        if(stu.getArrival().getTime() == 0) {
                                            route.addStopTimes(stu.getDeparture().getTime());
                                        } else {
                                            route.addStopTimes(stu.getArrival().getTime());
                                        }
                                    } else {
                                        NearbyStopsProto.NearbyStops.Builder nearBuild =
                                                NearbyStopsProto.NearbyStops.newBuilder(nearbyNorth.getNearby(nearbyInd));
                                        NearbyStopsProto.Routes.Builder routeBuild =
                                                NearbyStopsProto.Routes.newBuilder(nearBuild.getRoutes(routeInd));
                                        routeBuild.addStopTimes(stu.getArrival().getTime());
                                        nearBuild.removeRoutes(routeInd);
                                        nearBuild.addRoutes(routeBuild);
                                        nearbyNorth.removeNearby(nearbyInd);
                                        nearbyNorth.addNearby(nearBuild);
                                    }
                                }
                                if(addNewNorth)
                                    nearbyNorth.addNearby(nearby);
                                if(addNewRoute) {
                                    nearby = NearbyStopsProto.NearbyStops.newBuilder(nearbyNorth.getNearby(nearbyInd)).addRoutes(route);
                                    nearbyNorth.removeNearby(nearbyInd);
                                    nearbyNorth.addNearby(nearby);
                                }
                            }
                            else if (stu.getStopId().endsWith("S"))
                            {
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

                                    NearbyStopsProto.Routes.Builder routes = NearbyStopsProto.Routes.newBuilder();
                                    //routes.stopTimes = new ArrayList<>();
                                    routes.setRouteId(entity.getTripUpdate().getTrip().getRouteId());
                                    //Create inner class with actual stop names and stop times field
                                    if(stu.getArrival().getTime() == 0) {
                                        routes.addStopTimes(stu.getDeparture().getTime());
                                    } else {
                                        routes.addStopTimes(stu.getArrival().getTime());
                                    }
                                    //nearby.trains = new ArrayList<>();
                                    nearby.addRoutes(routes);
                                }
                                else {
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
                                        if(stu.getArrival().getTime() == 0) {
                                            route.addStopTimes(stu.getDeparture().getTime());
                                        } else {
                                            route.addStopTimes(stu.getArrival().getTime());
                                        }
                                    } else {
//                                        NearbyStopsProto.Routes.Builder rtRoute =
//                                                NearbyStopsProto.Routes.newBuilder(nearbySouth.getNearby(nearbyInd).getRoutes(routeInd)).addStopTimes(stu.getArrival().getTime());
                                        NearbyStopsProto.NearbyStops.Builder nearBuild =
                                                NearbyStopsProto.NearbyStops.newBuilder(nearbySouth.getNearby(nearbyInd));
                                        NearbyStopsProto.Routes.Builder routeBuild =
                                                NearbyStopsProto.Routes.newBuilder(nearBuild.getRoutes(routeInd));
                                        routeBuild.addStopTimes(stu.getArrival().getTime());
                                        nearBuild.removeRoutes(routeInd);
                                        nearBuild.addRoutes(routeBuild);
                                        nearbySouth.removeNearby(nearbyInd);
                                        nearbySouth.addNearby(nearBuild);
                                    }
                                }
                                if(addNewSouth)
                                    nearbySouth.addNearby(nearby);
                                if(addNewRoute) {
                                    NearbyStopsProto.NearbyStops.Builder near = NearbyStopsProto.NearbyStops.newBuilder(nearbySouth.getNearby(nearbyInd)).addRoutes(route);
                                    nearbySouth.removeNearby(nearbyInd);
                                    nearbySouth.addNearby(near);
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        nearbyFeed.addUpdown(nearbyNorth);
        nearbyFeed.addUpdown(nearbySouth);
        Log.d(LOG_TAG, System.currentTimeMillis() - before + " FINISHED GENERATING NEARBY STOPS!!");
        return nearbyFeed.build();
    }
}
