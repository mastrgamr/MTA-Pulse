package net.mastrgamr.mtapulse.gtfs_realtime;

import android.content.Context;
import android.location.Location;
import android.os.Environment;
import android.util.Log;

import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;
import com.google.transit.realtime.GtfsRealtime.VehiclePosition;

import net.mastrgamr.mtapulse.BuildConfig;
import net.mastrgamr.mtapulse.gtfs_static.Stops;
import net.mastrgamr.mtapulse.tools.DataMaps;
import net.mastrgamr.mtapulse.tools.MtaFeeds;
import net.mastrgamr.mtapulse.tools.PointD;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Project: MTA Pulse
 * Author: Stuart Smith
 * Date: 3/20/2015
 */

/**
 * Used to parse real time MTA routes, returns a list of live train times to be used by TripListAdapter.
 * TODO: return a list of tripUpdates.
 */
public class RtGtfsParser {

    public final static String LOG_TAG = RtGtfsParser.class.getSimpleName();
    private final static boolean DEBUG = true;

    //public static HashMap<String, HashMap<String, ArrayList<StopTimeUpdate>>> surroundingMap = new HashMap<>(); //last to put
    //private SurroundingStops nearbyStops;
    private SurroundingStopsList nearbyStopsList;

    private Context c;

    private EntityFactory factory;
    private FeedMessage feedMessage;
    private URL url;

    private ArrayList<String> stopIds;

    /**
     * Sets up URLs needed to be parsed into app.
     */
    public RtGtfsParser(Context c) {
        this.c = c;
        try {
            URI vehicleUrl = new URI(MtaFeeds.realtimeDivA);
            url = vehicleUrl.toURL();
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, e.getMessage());
        } catch (URISyntaxException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    /**
     * Generates a new feed from MTA servers and assigns elements from feed to the respective
     * accessor fields.
     */
    public void refreshFeed() {
        try {
            factory = new EntityFactory(FeedMessage.parseFrom(url.openStream()), c);
            feedMessage = factory.publishEntity();
            //System.out.println(feedMessage);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        if(DEBUG) {
            Log.d(LOG_TAG, "Refreshing Feed...");
        }

        stopIds = new ArrayList<>();

        /**
         * Get ALLLLL teh entitiez!!
         */
        for (FeedEntity entity : feedMessage.getEntityList()) {

            TripUpdate tripUpdate = entity.getTripUpdate();
            VehiclePosition vehicle = entity.getVehicle();
            //Alert alert = entity.getAlert(); //TODO: Handle alerts later app version.

            if(DEBUG) {
                Log.d(LOG_TAG, "LISTING ENTITIES");
                Log.d(LOG_TAG, "----------------");
            }
            /*
            System.out.println(entity.getId() + " -- ENTITIY ID");
            //System.out.println(tripUpdate.getTrip());
            System.out.println(tripUpdate.getTrip().getRouteId() + " -- ROUTE ID");

            //if(tripUpdate.getStopTimeUpdateCount() == 0)
            //    continue;

            if(entity.hasTripUpdate()) {
                StopTimeUpdate stopTimeUpdate = tripUpdate.getStopTimeUpdate(0);
                if (stopTimeUpdate.hasArrival()) {
                    if(DEBUG) {
                        Log.d(LOG_TAG, "STOPTIMEUPDATE");
                        Log.d(LOG_TAG, "----------------");
                    }
                    System.out.println(stopTimeUpdate.getStopId() + " arriving at " + new Date(stopTimeUpdate.getArrival().getTime() * 1000) + " -- Arrival Time Converted");
                }
            }*/

            for(StopTimeUpdate stu : tripUpdate.getStopTimeUpdateList()){
                if(!entity.hasTripUpdate())
                    continue;
                if(stu.getArrival().getTime() == 0) //TODO: Check this out for trains approaching last stop
                    continue;
                //System.out.println(stu.getStopId() + " -- ENTITY STOPid");
                stopIds.add(stu.getStopId());
            }
            /*
            //if (!entity.hasVehicle()) {
            //    continue;
            //}
            if(DEBUG) {
                Log.d(LOG_TAG, "VEHICLE POSITION");
                Log.d(LOG_TAG, "----------------");
            }

            System.out.println(vehicle.getTrip().getTripId() + " -- TRIP_ID");

            //System.out.println(vehicle.getTimestamp() + " -- TIMESTAMP");
            System.out.println(new Date(vehicle.getTimestamp() * 1000) + " -- TIME TAKEN");
            System.out.println(vehicle.getStopId() + " -- NEXT STOP");
            System.out.println(vehicle.getCurrentStatus().toString() + " -- STOP STATUS");

            *//*if(DEBUG) {
                Log.d(LOG_TAG, "ALERT");
                Log.d(LOG_TAG, "----------------");
            }
            for(EntitySelector aler : entity.getAlert().getInformedEntityList()){
                if(!entity.hasAlert())
                    continue;
                //System.out.println(aler.toString() + " -- ALERT");
                System.out.println(entity.getAlert().getHeaderText());
            }*/ //TODO:Handle alerts in later app version
        }
    }

	public class TrainStop {
        public String routeId;
        public TripUpdate.StopTimeUpdate stu;
    }

    /**
     * Returns trains for specific stop.
     * TODO: Use for gMap Marker(which will be a train stop) click.
     */
    public ArrayList<TrainStop> getTrainsForStop(String stopId){
		ArrayList<TrainStop> trainStopList = new ArrayList<>();
        TrainStop trainStop;

        for(FeedEntity entity : feedMessage.getEntityList()) {
            for (StopTimeUpdate stu : entity.getTripUpdate().getStopTimeUpdateList()) {
                if (stu.getStopId().equals(stopId)) {
                    if (DEBUG) {
                        Log.d(LOG_TAG, "STOPTIMEUPDATE");
                        Log.d(LOG_TAG, "------" + stopId + "------");
                    }
                    //System.out.print(entity.getTripUpdate().getTrip().getRouteId() + " Train -- ");
                    //System.out.println(stu.getStopId() + " arriving at " + new Date(stopTimeUpdate.getArrival().getTime() * 1000) + " -- Arrival Time Converted");
                    trainStop = new TrainStop();
                    trainStop.routeId = entity.getTripUpdate().getTrip().getRouteId();
                    trainStop.stu = stu;
                    trainStopList.add(trainStop);

                    break; //exit S.T.U. cycle to check the next entity in feed.
                }
            }
        }
	    return trainStopList;
    }

    /*public HashMap<String, HashMap<String, ArrayList<StopTimeUpdate>>> getStopsByLocationMap(Location loc, DataMaps<Stops> stopsDataMap){

        nearbyStops = new SurroundingStops(feedMessage);
        HashMap<String, HashMap<String, ArrayList<StopTimeUpdate>>> closeStops = nearbyStops.getStopsByLocationMap(loc, stopsDataMap);

        nearbyStopsList = new SurroundingStopsList(feedMessage);

        for(Map.Entry<String, HashMap<String, ArrayList<StopTimeUpdate>>> m : closeStops.entrySet()){
            nearbyStops.put(m.getKey(), m.getValue());
        }

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        FileOutputStream file = null;
        try {
            file = new FileOutputStream(new File(path, "nearbyStops.json"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            LoganSquare.serialize(nearbyStops, file);
            System.out.println("SERIALIZED JSON!!");
            if(file != null)
                file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return closeStops;
    }*/

    public ArrayList<ArrayList<NearbyStopsInfo>> getStopsByLocationList(Location loc, DataMaps<Stops> stopsDataMap){

        nearbyStopsList = new SurroundingStopsList(feedMessage);
        //nearbyStopsList.getStopsByLocationList(loc, stopsDataMap);
        ArrayList<ArrayList<NearbyStopsInfo>> nsiList = nearbyStopsList.getStopsByLocationList(loc, stopsDataMap);

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        FileOutputStream file;
        try {
            file = new FileOutputStream(new File(path, "nearbyStopsList.json"));
            LoganSquare.serialize(nearbyStopsList.getStopsByLocationList(loc, stopsDataMap), file);
            System.out.println("SERIALIZED JSON!!");
            String json = LoganSquare.serialize(nearbyStopsList.getStopsByLocationList(loc, stopsDataMap));
            System.out.println(json);
            file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return nsiList;
    }

    /**
     * Gets list of stops around a specific location. Default 2,500 feet.
     *
     * @param loc Location passed in (usually current location based on gMap position.
     * @param stopsDataMap DataMap to parse through to match stops.
     * @return ArrayList of trains approaching a stop surrounding a location.
     */
    public ArrayList<TrainStop> getStopsByLocation(Location loc, DataMaps<Stops> stopsDataMap){
        ArrayList<TrainStop> trainStopList = new ArrayList<>();
        TrainStop trainStop;

        ArrayList<StopTimeUpdate> stuList = new ArrayList<>();

        for(FeedEntity entity : feedMessage.getEntityList())
        {
            //home - 40.882305, -73.833145
            PointD start = new PointD(loc.getLatitude(), loc.getLongitude());
            Log.d(LOG_TAG, start.toString());
            PointD points;

            for(String stopId : stopsDataMap.keySet())
            {
                points = new PointD(
                        Double.parseDouble(stopsDataMap.get(stopId).getStopLat()),
                        Double.parseDouble(stopsDataMap.get(stopId).getStopLon()));
                if(start.distance(points) <= 0.0067)
                { //If stop is within 2,500 feet, get list of stops
                    for (StopTimeUpdate stu : entity.getTripUpdate().getStopTimeUpdateList()) {
                        if (stu.getStopId().equals(stopId)) {
                            Log.d(LOG_TAG, "Found stop");
                            if (DEBUG) {
                                Log.d(LOG_TAG, "STOPTIMEUPDATE");
                                Log.d(LOG_TAG, "------" + stopId + "------");
                            }
                            trainStop = new TrainStop();
                            trainStop.routeId = entity.getTripUpdate().getTrip().getRouteId();
                            trainStop.stu = stu;
                            trainStopList.add(trainStop);
                            break;
                        }
                    }
                }
            }
        }
        Log.d(LOG_TAG, trainStopList.size() + " trains approaching stop nearby.");
        return trainStopList;
    }
}
