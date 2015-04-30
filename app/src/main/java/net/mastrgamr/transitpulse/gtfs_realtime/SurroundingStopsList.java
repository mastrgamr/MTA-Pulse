package net.mastrgamr.transitpulse.gtfs_realtime;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.google.transit.realtime.GtfsRealtime;

import net.mastrgamr.transitpulse.gtfs_static.Stops;
import net.mastrgamr.transitpulse.tools.ArrayListSearcher;
import net.mastrgamr.transitpulse.tools.DataMaps;
import net.mastrgamr.transitpulse.tools.PointD;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Project: MTA Pulse
 * Created: Stuart Smith
 * Date: 4/3/2015
 * TODO: Find efficient way of returning this so it's easy to digest for LoganSquare.
 */

@JsonObject
public class SurroundingStopsList {

    @JsonIgnore
    private final String LOG_TAG = getClass().getSimpleName();

    @JsonField
    public ArrayList<ArrayList<NearbyStopsInfo>> nearbyStops;

    @JsonIgnore
    public ArrayList<NearbyStopsInfo> nearbyNorth;
    @JsonIgnore
    public ArrayList<NearbyStopsInfo> nearbySouth;
    @JsonIgnore
    private NearbyStopsInfo nearby;
    @JsonIgnore
    private RTRoutes route;
    @JsonIgnore
    private GtfsRealtime.FeedMessage feedMessage;
    @JsonIgnore
    private Context c;
    @JsonIgnore
    private File preCheck;

    public SurroundingStopsList() { }

    public SurroundingStopsList(GtfsRealtime.FeedMessage feedMessage, Context c){
        this.feedMessage = feedMessage;
        this.c = c;
    }

    public ArrayList<ArrayList<NearbyStopsInfo>> getStopsByLocationList(Location loc, DataMaps<Stops> stopsDataMap){

        ArrayListSearcher search = new ArrayListSearcher();

        long before = System.currentTimeMillis();

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
            //PointD start = new PointD(40.754191, -73.982881); //RANDOM, between TS and GS
            PointD start = new PointD(loc.getLatitude(), loc.getLongitude());
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

                                nearbyInd = search.containsStop(nearbyNorth, stu.getStopId());
                                //Log.d("surrstopslist", nearbyInd + " index of stopN - " + stu.getStopId());
                                //TODO:Change to if nsi.stopID == stu.stopID, w/o adding multiple new routes
                                if (nearbyInd < 0) //-1, does not contain stop
                                {
                                    addNewNorth = true;
                                    //System.out.println("nearbyStop not in route");
                                    nearby = new NearbyStopsInfo();
                                    nearby.stopId = stu.getStopId();
                                    nearby.stopName = stopsDataMap.get(stopId).getStopName();

                                    RTRoutes routes = new RTRoutes();
                                    routes.routeId = entity.getTripUpdate().getTrip().getRouteId();
                                    routes.stopTimes = new ArrayList<>();
                                    //Create inner class with actual stop names and stop times field
                                    if(stu.getArrival().getTime() == 0) {
                                        routes.stopTimes.add(stu.getDeparture().getTime());
                                    } else {
                                        routes.stopTimes.add(stu.getArrival().getTime());
                                    }
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
                                    int routeInd = search.containsRoute(nearbyNorth.get(nearbyInd).trains, entity.getTripUpdate().getTrip().getRouteId());
                                    //Log.d("surrstopslist", nearbyInd + " index of stopN");
                                    //TODO:Change to if nsi.stopID == stu.stopID, w/o adding multiple new routes
                                    if (routeInd < 0) //-1, does not contain route
                                    {
                                        addNewRoute = true;
                                        route = new RTRoutes();
                                        route.routeId = entity.getTripUpdate().getTrip().getRouteId();
                                        route.stopTimes = new ArrayList<>();
                                        if(stu.getArrival().getTime() == 0) {
                                            route.stopTimes.add(stu.getDeparture().getTime());
                                        } else {
                                            route.stopTimes.add(stu.getArrival().getTime());
                                        }
                                    } else {
                                        nearbyNorth.get(nearbyInd).trains.get(routeInd).stopTimes.add(stu.getArrival().getTime());
                                        Collections.sort(nearbyNorth.get(nearbyInd).trains.get(routeInd).stopTimes);
                                    }
                                }
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
                                int nearbyInd;
                                nearbyInd = search.containsStop(nearbySouth, stu.getStopId());
                                //Log.d("surrstopslist", nearbyInd + " index of stopS - " + stu.getStopId());
                                if (nearbyInd < 0) //-1, stop not in list
                                {
                                    addNewSouth = true;
                                    //System.out.println("nearbyStop not in route");
                                    nearby = new NearbyStopsInfo();
                                    nearby.stopId = stu.getStopId();
                                    nearby.stopName = stopsDataMap.get(stopId).getStopName();

                                    RTRoutes routes = new RTRoutes();
                                    routes.stopTimes = new ArrayList<>();
                                    routes.routeId = entity.getTripUpdate().getTrip().getRouteId();
                                    //Create inner class with actual stop names and stop times field
                                    if(stu.getArrival().getTime() == 0) {
                                        routes.stopTimes.add(stu.getDeparture().getTime());
                                    } else {
                                        routes.stopTimes.add(stu.getArrival().getTime());
                                    }
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
                                    int routeInd = search.containsRoute(nearbySouth.get(nearbyInd).trains, entity.getTripUpdate().getTrip().getRouteId());
                                    //Log.d("surrstopslist", nearbyInd + " index of stopN");
                                    //TODO:Change to if nsi.stopID == stu.stopID, w/o adding multiple new routes
                                    if (routeInd < 0) //-1, does not contain route
                                    {
                                        addNewRoute = true;
                                        route = new RTRoutes();
                                        route.routeId = entity.getTripUpdate().getTrip().getRouteId();
                                        route.stopTimes = new ArrayList<>();
                                        if(stu.getArrival().getTime() == 0) {
                                            route.stopTimes.add(stu.getDeparture().getTime());
                                        } else {
                                            route.stopTimes.add(stu.getArrival().getTime());
                                        }
                                    } else {
                                        nearbySouth.get(nearbyInd).trains.get(routeInd).stopTimes.add(stu.getArrival().getTime());
                                        Collections.sort(nearbySouth.get(nearbyInd).trains.get(routeInd).stopTimes);
                                    }
                                }
                                if(addNewSouth)
                                    nearbySouth.add(nearby);
                                if(addNewRoute) {
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
        cacheFile(this);
        Log.d(LOG_TAG, System.currentTimeMillis() - before + " FINISHED GENERATING STOPS!!");
        return nearbyStops;
    }

    public ArrayList<ArrayList<NearbyStopsInfo>> getNearbyStops(){
        return nearbyStops;
    }

    private void cacheFile(SurroundingStopsList stopsList){
        //File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File path = c.getCacheDir();
        FileOutputStream file;
        try {
            file = new FileOutputStream(new File(path, "nearbyStopsList.json"));
            LoganSquare.serialize(stopsList, file);
            System.out.println("SERIALIZED JSON!!");
            //String json = LoganSquare.serialize(stopsList);
            //System.out.println(json);
            file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ArrayList<NearbyStopsInfo>> getCachedFile(Location loc, DataMaps<Stops> stopsDataMap){
        FileInputStream fis;

        Log.d(LOG_TAG, "checking if NearbyStopsJSON exists");
        File[] files = c.getCacheDir().listFiles();
        preCheck = null;

        for(File file : files){
            if(file.getName().startsWith("nearbyStops")) {
                preCheck = file;
                try {
                    Log.d(LOG_TAG, "cached NearbyStopsJSON exists");
                    fis = new FileInputStream(preCheck);
                    SurroundingStopsList ssl = LoganSquare.parse(fis, SurroundingStopsList.class);
                    System.out.println("OKOKOKOKOKOKOKOK");
                    break;
                    //return ssl.getNearbyStops();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "File Not Found?");
                }
                break;
            }
        }
        Log.d(LOG_TAG, "Generating new stops by location.");
        return getStopsByLocationList(loc, stopsDataMap);
    }
}
