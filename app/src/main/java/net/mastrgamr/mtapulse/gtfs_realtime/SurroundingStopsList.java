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
import java.util.HashMap;

/**
 * Project: MTA Pulse
 * Created: Stuart Smith
 * Date: 4/3/2015.
 */

@JsonObject
public class SurroundingStopsList extends ArrayList<NearbyStopsInfo> {

    @JsonField
    public NearbyStopsInfo nearby;

    @JsonIgnore
    private GtfsRealtime.FeedMessage feedMessage;

    public SurroundingStopsList() { }

    public SurroundingStopsList(GtfsRealtime.FeedMessage feedMessage){
        this.feedMessage = feedMessage;
    }

    public ArrayList<NearbyStopsInfo> getStopsByLocationList(Location loc, DataMaps<Stops> stopsDataMap){

        nearby = new NearbyStopsInfo();
        ArrayList<RTRoutes> upList;
        ArrayList<RTRoutes> downList;

        nearby.stops = new HashMap<>();

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
                            if(stu.getStopId().endsWith("N")){
                                if(!nearby.stops.containsKey(stu.getStopId())){
                                    upList = new ArrayList<>();
                                    RTRoutes routes = new RTRoutes();
                                    routes.stopTimes = new ArrayList<>();
                                    routes.routeId = entity.getTripUpdate().getTrip().getRouteId();
                                    routes.stopTimes.add(stu.getArrival().getTime());
                                    upList.add(routes);
                                    nearby.stops.put(stu.getStopId(), upList);
                                } else if(nearby.stops.containsKey(stu.getStopId())){
                                    RTRoutes routes;
                                    for(int i = 0; i < nearby.stops.get(stu.getStopId()).size(); i++) {
                                        routes = nearby.stops.get(stu.getStopId()).get(i);
                                            if (routes.routeId.equals(entity.getTripUpdate().getTrip().getRouteId())) {
                                                routes.stopTimes.add(stu.getArrival().getTime());
                                                break;
                                            }
                                    }
                                }
                            } else if(stu.getStopId().endsWith("S")) {
                                if(!nearby.stops.containsKey(stu.getStopId())){
                                    downList = new ArrayList<>();
                                    RTRoutes routes = new RTRoutes();
                                    routes.stopTimes = new ArrayList<>();
                                    routes.routeId = entity.getTripUpdate().getTrip().getRouteId();
                                    routes.stopTimes.add(stu.getArrival().getTime());
                                    downList.add(routes);
                                    nearby.stops.put(stu.getStopId(), downList);
                                } else if(nearby.stops.containsKey(stu.getStopId())){
                                    RTRoutes routes;
                                    for(int i = 0; i < nearby.stops.get(stu.getStopId()).size(); i++) {
                                        routes = nearby.stops.get(stu.getStopId()).get(i);
                                        if (routes.routeId.equals(entity.getTripUpdate().getTrip().getRouteId())) {
                                            routes.stopTimes.add(stu.getArrival().getTime());
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        return this;
    }
}
