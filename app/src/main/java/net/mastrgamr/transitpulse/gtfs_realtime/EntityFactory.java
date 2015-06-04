package net.mastrgamr.transitpulse.gtfs_realtime;

import android.content.Context;
import android.util.Log;

import com.google.transit.realtime.GtfsRealtime.FeedEntity;
import com.google.transit.realtime.GtfsRealtime.FeedHeader;
import com.google.transit.realtime.GtfsRealtime.FeedMessage;
import com.google.transit.realtime.GtfsRealtimeConstants;

/**
 * Project: RealTimeMTA
 * Author: Stuart Smith
 * Date: 2/18/2015
 */

/**
 * Takes a FeedMessage and 'prettifies' it for readability and (theoretically) shorter
 * iteration times.
 * Can cache GTFS message for fast, offline retrieval.
 */
public class EntityFactory {
    private final String LOG_TAG = getClass().getSimpleName();

    FeedMessage feedMessage;

    FeedHeader.Builder header;
    FeedMessage.Builder fm;

    private Context c;

    public EntityFactory(FeedMessage feedMessage, Context c) {

        this.feedMessage = feedMessage;
        this.c = c;
        header = FeedHeader.newBuilder();
        header.setTimestamp(System.currentTimeMillis());
        header.setIncrementality(FeedHeader.Incrementality.FULL_DATASET);
        header.setGtfsRealtimeVersion(GtfsRealtimeConstants.VERSION);
    }

    public FeedMessage publishEntity() {
        long before = System.currentTimeMillis();
        int i = 0;
        fm = FeedMessage.newBuilder();
        fm.setHeader(header);

        FeedEntity.Builder builder = FeedEntity.newBuilder();

        for (FeedEntity entity : feedMessage.getEntityList()) {
            builder.setId(String.valueOf(i));

            //all entities have TripUpdates
            if (entity.hasTripUpdate()) {
                //if entity is a TripUpdate and builder has a TripUpdate,
                //that means the last entity had no Vehicle (last stop train), so build Entity
                if (builder.hasTripUpdate()) {
                    fm.addEntity(builder);
                    i++;
                    builder.clearTripUpdate(); //TODO:Potential bug later, keep eye on EntityID
                }
                builder.setTripUpdate(entity.getTripUpdate()); //add TripUpdate
            }

            //some entities don't have Vehicles, if it6 does add builder and reset builder
            if (builder.hasTripUpdate() && entity.hasVehicle()) {
                builder.setVehicle(entity.getVehicle()); //add Vehicle
                fm.addEntity(builder);
                i++;
                builder.clearTripUpdate();
                builder.clearVehicle();
            }
        }

        feedMessage = fm.build();
        Log.d(LOG_TAG, System.currentTimeMillis() - before + " TIME TO PUBLISH FEED!!");
        return feedMessage;
    }
}
