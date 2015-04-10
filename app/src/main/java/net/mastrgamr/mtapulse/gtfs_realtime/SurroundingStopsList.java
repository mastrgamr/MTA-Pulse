package net.mastrgamr.mtapulse.gtfs_realtime;

import android.location.Location;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.google.transit.realtime.GtfsRealtime;

import net.mastrgamr.mtapulse.gtfs_static.Stops;
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
    private GtfsRealtime.FeedMessage feedMessage;

    public SurroundingStopsList() { }

    public SurroundingStopsList(GtfsRealtime.FeedMessage feedMessage){
        this.feedMessage = feedMessage;
    }

    public ArrayList<ArrayList<NearbyStopsInfo>> getStopsByLocationList(Location loc, DataMaps<Stops> stopsDataMap){

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
            //home - 40.882305, -73.833145
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
                                //System.out.println("NORTHBOUND");
                                for(NearbyStopsInfo nsi : nearbyNorth)
                                {
                                    if (!nsi.containsStop(nearbyNorth, stu.getStopId()))
                                    {
                                        addNewNorth = true;
                                        //System.out.println("nearbyStop not in route");
                                        nearby = new NearbyStopsInfo();
                                        nearby.stopId = stu.getStopId();
                                        //upList = new ArrayList<>();
                                        RTRoutes routes = new RTRoutes();
                                        routes.stopTimes = new ArrayList<>();
                                        routes.routeId = entity.getTripUpdate().getTrip().getRouteId();
                                        routes.stopTimes.add(stu.getArrival().getTime());
                                        nearby.trains = new ArrayList<>();
                                        nearby.trains.add(routes);
                                        //nearbyNorth.add(nearby);
                                    } else if (nsi.containsStop(nearbyNorth, stu.getStopId()))
                                    {
                                        //System.out.println("nearbyStop in route");
                                        RTRoutes routes;
                                        for (int i = 0; i < nsi.trains.size(); i++) {
                                            routes = nsi.trains.get(i);
                                            if (routes.routeId.equals(entity.getTripUpdate().getTrip().getRouteId())) {
                                                routes.stopTimes.add(stu.getArrival().getTime());
                                                Collections.sort(routes.stopTimes);
                                                break;
                                            }
                                        }
                                    }
                                }
                                if(addNewNorth)
                                    nearbyNorth.add(nearby);
                            }
                            else if (stu.getStopId().endsWith("S"))
                            {
                                boolean addNewNorth = false;
                                //System.out.println("SOUTHBOUND");
                                for(NearbyStopsInfo nsi : nearbySouth)
                                {
                                    if (!nsi.containsStop(nearbySouth, stu.getStopId()))
                                    {
                                        addNewNorth = true;
                                        //System.out.println("nearbyStop not in route");
                                        nearby = new NearbyStopsInfo();
                                        nearby.stopId = stu.getStopId();
                                        //downList = new ArrayList<>();
                                        RTRoutes routes = new RTRoutes();
                                        routes.stopTimes = new ArrayList<>();
                                        routes.routeId = entity.getTripUpdate().getTrip().getRouteId();
                                        routes.stopTimes.add(stu.getArrival().getTime());
                                        nearby.trains = new ArrayList<>();
                                        nearby.trains.add(routes);
                                        //nearbySouth.add(nearby);
                                    } else if (nsi.containsStop(nearbySouth, stu.getStopId())) {
                                        //System.out.println("nearbyStop  in route");
                                        RTRoutes routes;
                                        for (int i = 0; i < nsi.trains.size(); i++) {
                                            routes = nsi.trains.get(i);
                                            if (routes.routeId.equals(entity.getTripUpdate().getTrip().getRouteId())) {
                                                routes.stopTimes.add(stu.getArrival().getTime());
                                                Collections.sort(routes.stopTimes);
                                                break;
                                            }
                                        }
                                    }
                                }
                                if(addNewNorth)
                                    nearbySouth.add(nearby);
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
