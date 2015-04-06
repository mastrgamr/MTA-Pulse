package net.mastrgamr.mtapulse.gtfs_realtime;

import android.content.Context;

import com.google.transit.realtime.GtfsRealtime.*;
import com.google.transit.realtime.GtfsRealtimeConstants;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Project: RealTimeMTA
 * Author: Stuart Smith
 * Date: 2/18/2015
 */

/**
 * Takes a FeedMessage and 'prettifies' it for readability and (theoretically) shorter
 * iteration times.
 */
public class EntityFactory {

    FeedMessage feedMessage;
    FeedMessage outMessage;

    FeedHeader.Builder header;
    FeedMessage.Builder fm;

    private Context c;

    public EntityFactory(FeedMessage feedMessage, Context c){
        this.feedMessage = feedMessage;
        this.c = c;
        header = FeedHeader.newBuilder();
        header.setTimestamp(System.currentTimeMillis());
        header.setIncrementality(FeedHeader.Incrementality.FULL_DATASET);
        header.setGtfsRealtimeVersion(GtfsRealtimeConstants.VERSION);
    }

    public FeedMessage publishEntity() {
        int i = 0;
        fm = FeedMessage.newBuilder();
        fm.setHeader(header);

        FeedEntity.Builder builder = FeedEntity.newBuilder();

        for (FeedEntity entity : feedMessage.getEntityList()) {
            builder.setId(String.valueOf(i));

            //all entities have TripUpdates
            if(entity.hasTripUpdate()){
                //if entity is a TripUpdate and builder has a TripUpdate,
                //that means the last entity had no Vehicle (last stop train), so build Entity
                if(builder.hasTripUpdate()) {
                    fm.addEntity(builder);
                    i++;
                    builder.clearTripUpdate(); //TODO:Potential bug later, keep eye on EntityID
                }
                builder.setTripUpdate(entity.getTripUpdate()); //add TripUpdate
            }

            //some entities don't have Vehicles, if it6 does add builder and reset builder
            if(builder.hasTripUpdate() && entity.hasVehicle()) {
                builder.setVehicle(entity.getVehicle()); //add Vehicle
                fm.addEntity(builder);
                i++;
                builder.clearTripUpdate();
                builder.clearVehicle();
            }
        }

        feedMessage = fm.build();
        return feedMessage;
    }

    /**
     * Persist feedMessage.
     */
    public void saveMessage(){
        try {
            FileOutputStream fos = c.openFileOutput("feedmesage", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(outMessage);
            oos.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * De-serialize FeedMessage. Perfect for retrieving saved feeds for the app to look
     * back to when device is offline.
     * @return De-serialized FeedMessage.
     */
    public FeedMessage getSavedMessage(){
        FeedMessage savedMessage = null;
        try
        {
            FileInputStream fis = c.openFileInput("feedmessage");
            ObjectInputStream ois = new ObjectInputStream(fis);
            savedMessage = (FeedMessage)ois.readObject();
            ois.close();
            fis.close();
        } catch(IOException e) {
            e.printStackTrace();
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        return savedMessage;
    }
}
