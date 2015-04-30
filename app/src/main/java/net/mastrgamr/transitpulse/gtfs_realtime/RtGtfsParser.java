package net.mastrgamr.transitpulse.gtfs_realtime;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;
import com.google.transit.realtime.GtfsRealtime.VehiclePosition;

import net.mastrgamr.transitpulse.gtfs_static.Stops;
import net.mastrgamr.transitpulse.tools.DataMaps;
import net.mastrgamr.transitpulse.tools.HttpRequest;
import net.mastrgamr.transitpulse.tools.MtaFeeds;
import net.mastrgamr.transitpulse.tools.NetworkStatics;
import net.mastrgamr.transitpulse.tools.PointD;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

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
    private final static boolean DEBUG = false;

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
            url = new URL(MtaFeeds.realtimeDivA);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    /**
     * Generates a new feed from MTA servers and assigns elements from feed to the respective
     * accessor fields.
     */
    public void refreshFeed() {
        //Attempt to get cached file, if none exist Assign the new feed
        if(NetworkStatics.isDeviceOnline(c))
            saveMessage();
        feedMessage = getSavedMessage();

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

    public ArrayList<ArrayList<NearbyStopsInfo>> getStopsByLocationList(Location loc, DataMaps<Stops> stopsDataMap){

        //nearbyStopsList = new SurroundingStopsList(feedMessage, c);
        //return nearbyStopsList.getStopsByLocationList(loc, stopsDataMap);
        nearbyStopsList = new SurroundingStopsList(feedMessage, c);
        return nearbyStopsList.getCachedFile(loc, stopsDataMap);
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

    /**
     * Persist feedMessage.
     */
    public void saveMessage(){
        HttpRequest request =  HttpRequest.get(url);

        /**
         * Check if file exists to delete and prevent cluttering the cache dir.
         */
        Log.d(LOG_TAG, "checking if FeedMessage exists. Prevent cluttering cache dir.");
        File[] files = c.getCacheDir().listFiles();
        for(File file : files){
            if(file.getName().startsWith("feedMessage")) {
                file.delete();
                break;
            }
        }

        File file = null;
        try {
            if (request.ok()) {
                file = File.createTempFile("feedMessage", "", c.getCacheDir());
                file.setLastModified(System.currentTimeMillis());
                request.receive(file);
            }
        } catch (IOException e){
            Log.e(LOG_TAG, "File couldn't be created");
        }
    }

    /**
     * De-serialize FeedMessage. Perfect for retrieving saved feeds for the app to look
     * back to when device is offline.
     * @return De-serialized FeedMessage.
     */
    public FeedMessage getSavedMessage() {
        /**
         * Check if file exists
         */
        Log.d(LOG_TAG, "checking if FeedMessage exists");
        File[] files = c.getCacheDir().listFiles();
        File preCheck = null;
        for(File file : files){
            if(file.getName().startsWith("feedMessage")) {
                preCheck = file;
                try {
                    Log.d(LOG_TAG, "cached FeedMessage exists");
                    factory = new EntityFactory(FeedMessage.parseFrom(new FileInputStream(preCheck)), c);
                    return factory.publishEntity();
                } catch (IOException e) {
                    //TODO: URGENT possibly download/generate brand new FeedMessage here.
                    Log.e(LOG_TAG, "File Not Found?");
                }
                break;
            }
        }
        Log.d(LOG_TAG, " FeedMessage doesn't exit! Returning null!");
        return null;
    }
}
