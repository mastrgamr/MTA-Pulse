package net.mastrgamr.mtapulse.gtfs_realtime;

import android.location.Location;
import android.util.Log;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.google.transit.realtime.GtfsRealtime;

import net.mastrgamr.mtapulse.gtfs_static.Stops;
import net.mastrgamr.mtapulse.tools.ArrayListSearcher;
import net.mastrgamr.mtapulse.tools.DataMaps;
import net.mastrgamr.mtapulse.tools.PointD;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Project: MTA Pulse
 * Created: Stuart Smith
 * Date: 4/3/2015.
 */

@JsonObject
public class SurroundingStopsList extends ArrayList<ArrayList<NearbyStopsInfo>> {

    @JsonIgnore
    public ArrayList<NearbyStopsInfo> nearbyNorth;
    @JsonIgnore
    public ArrayList<NearbyStopsInfo> nearbySouth;
    @JsonField
    public ArrayList<ArrayList<NearbyStopsInfo>> nearbyStops;

    @JsonIgnore
    private NearbyStopsInfo nearby;
    @JsonIgnore
    private RTRoutes route;

    @JsonIgnore
    private GtfsRealtime.FeedMessage feedMessage;

    public SurroundingStopsList() { }

    public SurroundingStopsList(GtfsRealtime.FeedMessage feedMessage){
        this.feedMessage = feedMessage;
    }

    public ArrayList<ArrayList<NearbyStopsInfo>> getStopsByLocationList(Location loc, DataMaps<Stops> stopsDataMap){

        ArrayListSearcher search = new ArrayListSearcher();

        nearbyStops = new ArrayList<>();
        nearbyNorth = new ArrayList<>();
        nearbySouth = new ArrayList<>();
        nearby = new NearbyStopsInfo();

        //placeholder nearbys
        nearby = new NearbyStopsInfo();
        nearby.stopId = "temp";
        nearby.trains = new ArrayList<>();
        nearbyNorth.add(nearby);
        nearbySouth.add(nearby);

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
                                int nearbyInd = 0;

                                //System.out.println("NORTHBOUND");
                                //for(NearbyStopsInfo nsi : nearbyNorth)
                                //{
                                    nearbyInd = search.containsStop(nearbyNorth, stu.getStopId());
                                    Log.d("surrstopslist", nearbyInd + " index of stopN - " + stu.getStopId());
                                    //TODO:Change to if nsi.stopID == stu.stopID, w/o adding multiple new routes
                                    if (nearbyInd < 0) //-1, does not contain stop
                                    {
                                        addNewNorth = true;
                                        //System.out.println("nearbyStop not in route");
                                        nearby = new NearbyStopsInfo();
                                        nearby.stopId = stu.getStopId();

                                        RTRoutes routes = new RTRoutes();
                                        routes.routeId = entity.getTripUpdate().getTrip().getRouteId();
                                        routes.stopTimes = new ArrayList<>();
                                        //Create inner class with actual stop names and stop times field
                                        routes.stopTimes.add(stu.getArrival().getTime());
                                        nearby.trains = new ArrayList<>();
                                        nearby.trains.add(routes);
                                    }
                                    else //contain the stop
                                    {
                                        /**
                                         * If a stop is contained within this NearbyStop, it means there's a route inside.
                                         * A Route has StopTimes in it, check if the route is listed within the stop:
                                         * -if it is, add a stop time to the route
                                         * -if not, create a new Route, StopTime within it, and add it to the NearbyStop index.
                                         */
                                        //for(RTRoutes routes : nearbyNorth.get(nearbyInd).trains){
                                            int routeInd = search.containsRoute(nearbyNorth.get(nearbyInd).trains, entity.getTripUpdate().getTrip().getRouteId());
                                            //Log.d("surrstopslist", nearbyInd + " index of stopN");
                                            //TODO:Change to if nsi.stopID == stu.stopID, w/o adding multiple new routes
                                            if (routeInd < 0) //-1, does not contain route
                                            {
                                                addNewRoute = true;
                                                route = new RTRoutes();
                                                route.routeId = entity.getTripUpdate().getTrip().getRouteId();
                                                route.stopTimes = new ArrayList<>();
                                                route.stopTimes.add(stu.getArrival().getTime());
                                            } else {
                                                nearbyNorth.get(nearbyInd).trains.get(routeInd).stopTimes.add(stu.getArrival().getTime());
                                                Collections.sort(nearbyNorth.get(nearbyInd).trains.get(routeInd).stopTimes);
                                            }
                                        //}
                                    }
                                //}
                                if(addNewNorth)
                                    nearbyNorth.add(nearby);
                                if(addNewRoute) {
                                    nearbyNorth.get(nearbyInd).trains.add(route);
                                }
                            }
                            else if (stu.getStopId().endsWith("S"))
                            {
                                boolean addNewSouth = false;
                                boolean addNewRoute = false;
                                int nearbyInd = 0;
                                //System.out.println("SOUTHBOUND");
                                //for(NearbyStopsInfo nsi : nearbySouth)
                                //{
                                    nearbyInd = search.containsStop(nearbySouth, stu.getStopId());
                                    Log.d("surrstopslist", nearbyInd + " index of stopS - " + stu.getStopId());
                                    if (nearbyInd < 0) //-1, stop not in list
                                    {
                                        addNewSouth = true;
                                        //System.out.println("nearbyStop not in route");
                                        nearby = new NearbyStopsInfo();
                                        nearby.stopId = stu.getStopId();
                                        //downList = new ArrayList<>();
                                        RTRoutes routes = new RTRoutes();
                                        routes.stopTimes = new ArrayList<>();
                                        routes.routeId = entity.getTripUpdate().getTrip().getRouteId();
                                        //Create inner class with actual stop names and stop times field
                                        routes.stopTimes.add(stu.getArrival().getTime());
                                        nearby.trains = new ArrayList<>();
                                        nearby.trains.add(routes);
                                    }
                                    else {
                                        /**
                                         * If a stop is contained within this NearbyStop, it means there's a route inside.
                                         * A Route has StopTimes in it, check if the route is listed within the stop:
                                         * -if it is, add a stop time to the route
                                         * -if not, create a new Route, StopTime within it, and add it to the NearbyStop index.
                                         */
                                        //for(RTRoutes routes : nsi.trains){
                                            int routeInd = search.containsRoute(nearbySouth.get(nearbyInd).trains, entity.getTripUpdate().getTrip().getRouteId());
                                            //Log.d("surrstopslist", nearbyInd + " index of stopN");
                                            //TODO:Change to if nsi.stopID == stu.stopID, w/o adding multiple new routes
                                            if (routeInd < 0) //-1, does not contain route
                                            {
                                                addNewRoute = true;
                                                route = new RTRoutes();
                                                route.routeId = entity.getTripUpdate().getTrip().getRouteId();
                                                route.stopTimes = new ArrayList<>();

                                                route.stopTimes.add(stu.getArrival().getTime());
                                            } else {
                                                nearbySouth.get(nearbyInd).trains.get(routeInd).stopTimes.add(stu.getArrival().getTime());
                                                Collections.sort(nearbySouth.get(nearbyInd).trains.get(routeInd).stopTimes);
                                            }
                                        //}
                                    }
                                //}
                                if(addNewSouth)
                                    nearbySouth.add(nearby);
                                if(addNewRoute) {
                                    //nearbySouth.get(nearbyInd).trains = new ArrayList<>();
                                    nearbySouth.get(nearbyInd).trains.add(route);
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        nearbyNorth.remove(0);
        nearbySouth.remove(0);
        nearbyStops.add(nearbyNorth);
        nearbyStops.add(nearbySouth);
        this.add(nearbyNorth);
        this.add(nearbySouth);
        return this;
    }
}
