package net.mastrgamr.mtapulse.gtfs_realtime;

import android.util.Log;

import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.TripUpdate;
import com.google.transit.realtime.GtfsRealtime.TripUpdate.StopTimeUpdate;
import com.google.transit.realtime.GtfsRealtime.VehiclePosition;

import net.mastrgamr.mtapulse.tools.MtaFeeds;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

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

    private FeedMessage feedMessage;
    private URL url;

    private long arrivalTime;

    /**
     * Sets up URLs needed to be parsed into app.
     */
    public RtGtfsParser() {
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
            feedMessage = FeedMessage.parseFrom(url.openStream());
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

        if(DEBUG) {
            Log.d(LOG_TAG, "Refreshing Feed...");
        }

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
            }

            for(StopTimeUpdate stu : tripUpdate.getStopTimeUpdateList()){
                if(!entity.hasTripUpdate())
                    continue;
                if(stu.getArrival().getTime() == 0)
                    continue;
                System.out.println(stu.getStopId() + " -- ENTITY STOPid");

                //System.out.println(stu.getArrival().getTime() + " -- Arrival Time");
                System.out.println(new Date(stu.getArrival().getTime() * 1000) + " -- Arrival Time Converted");
            }

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

            /*if(DEBUG) {
                Log.d(LOG_TAG, "ALERT");
                Log.d(LOG_TAG, "----------------");
            }
            for(EntitySelector aler : entity.getAlert().getInformedEntityList()){
                if(!entity.hasAlert())
                    continue;
                //System.out.println(aler.toString() + " -- ALERT");
                System.out.println(entity.getAlert().getHeaderText());
            }*/ //TODO:Handle alerts later app version
        }
    }

    /**
     * Returns trains for specific stop.
     */
    public void getTrainsForStop(String stopId){
        for(FeedEntity entity : feedMessage.getEntityList())
        {
            if(!entity.hasTripUpdate())
                continue;
            if(entity.hasTripUpdate()) 
            {
                StopTimeUpdate stopTimeUpdate = entity.getTripUpdate().getStopTimeUpdate(0);
                for(StopTimeUpdate stu : entity.getTripUpdate().getStopTimeUpdateList()) 
                {
                    if (stu.getStopId().equals(stopId)) 
                    {
                        if(DEBUG) {
                            Log.d(LOG_TAG, "STOPTIMEUPDATE");
                            Log.d(LOG_TAG, "------" + stopId + "------");
                        }
                        System.out.print(entity.getTripUpdate().getTrip().getRouteId() + " Train -- ");
                        System.out.println(stu.getStopId() + " arriving at " + new Date(stopTimeUpdate.getArrival().getTime() * 1000) + " -- Arrival Time Converted");
                        break;
                    }
                }
            }
        }
    }
}
