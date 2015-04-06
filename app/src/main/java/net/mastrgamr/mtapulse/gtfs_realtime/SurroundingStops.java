package net.mastrgamr.mtapulse.gtfs_realtime;

import android.location.Location;
import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonIgnore;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.google.transit.realtime.GtfsRealtime;

import net.mastrgamr.mtapulse.gtfs_static.Stops;
import net.mastrgamr.mtapulse.tools.DataMaps;
import net.mastrgamr.mtapulse.tools.PointD;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Project: MTA Pulse
 * Created: Stuart Smith
 * Date: 4/3/2015.
 */

@JsonObject
public class SurroundingStops extends HashMap<String, HashMap<String, ArrayList<GtfsRealtime.TripUpdate.StopTimeUpdate>>> {

    //public static HashMap<String, HashMap<String, ArrayList<GtfsRealtime.TripUpdate.StopTimeUpdate>>> surroundingMap = new HashMap<>(); //last to put

    @JsonIgnore
    private GtfsRealtime.FeedMessage feedMessage;

    @JsonField
    HashMap<String, ArrayList<GtfsRealtime.TripUpdate.StopTimeUpdate>> trainsMapN;
    @JsonField
    HashMap<String, ArrayList<GtfsRealtime.TripUpdate.StopTimeUpdate>> trainsMapS;

    public SurroundingStops() { }

    public SurroundingStops(@NonNull GtfsRealtime.FeedMessage message){
        this.feedMessage = message;
    }

    public HashMap<String, HashMap<String, ArrayList<GtfsRealtime.TripUpdate.StopTimeUpdate>>> getStopsByLocationMap(Location loc, DataMaps<Stops> stopsDataMap){

        //StopIDs, Map<RouteIDs, List<StopTimes>>
        //HashMap<String, HashMap<String, ArrayList<StopTimeUpdate>>> surroundingMap = new HashMap<>(); //last to put

        trainsMapN = new HashMap<>();
        trainsMapS = new HashMap<>();

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
                            if(stu.getStopId().endsWith("N"))
                            {
                                if(!this.containsKey(stu.getStopId())){
                                    this.put(stu.getStopId(), trainsMapN);
                                }
                                if(!trainsMapN.containsKey(entity.getTripUpdate().getTrip().getRouteId())){
                                    ArrayList<GtfsRealtime.TripUpdate.StopTimeUpdate> upList = new ArrayList<>();
                                    upList.add(stu);
                                    trainsMapN.put(entity.getTripUpdate().getTrip().getRouteId(), upList);
                                } else {
                                    trainsMapN.get(entity.getTripUpdate().getTrip().getRouteId()).add(stu);
                                }

                                //uptown.add(stu);
                            } else {
                                if(!this.containsKey(stu.getStopId())){
                                    this.put(stu.getStopId(), trainsMapS);
                                }
                                if(!trainsMapS.containsKey(entity.getTripUpdate().getTrip().getRouteId())){
                                    ArrayList<GtfsRealtime.TripUpdate.StopTimeUpdate> downList = new ArrayList<>();
                                    downList.add(stu);
                                    trainsMapS.put(entity.getTripUpdate().getTrip().getRouteId(), downList);
                                } else {
                                    trainsMapS.get(entity.getTripUpdate().getTrip().getRouteId()).add(stu);
                                }
                                //downtown.add(stu);
                            }
                            //Log.d(LOG_TAG, stu.getStopId() + ": " + stu.getDeparture().getTime());
                            break;
                        }
                    }
                }
            }
        }
        for(String s : this.keySet())
        {
            System.out.println(s);
        }
        for(String s : trainsMapN.keySet())
        {
            System.out.println(s + " trainsMapN");
        }
        for(String s : trainsMapS.keySet())
        {
            System.out.println(s + " trainsMapS");
        }
        return this;
    }
}
