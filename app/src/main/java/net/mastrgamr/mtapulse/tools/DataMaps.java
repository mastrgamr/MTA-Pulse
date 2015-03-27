package net.mastrgamr.mtapulse.tools;

import android.content.Context;
import android.util.Log;

import net.mastrgamr.mtapulse.gtfs_static.Routes;
import net.mastrgamr.mtapulse.gtfs_static.Stops;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * Project: GTFSParsing
 * Author: Stuart Smith
 * Date: 1/18/2015
 */

/**
 * (BETA)
 * Creates a serialized version of data read in from the static GTFS feed.
 *
 * To use, initialize a new {@link net.mastrgamr.mtapulse.tools.DataMaps}, and include the Class in
 * the '<>'. Remember to call the {@link net.mastrgamr.mtapulse.tools.DataMaps#serialize(Class)} class
 * before operating utilizing the instance.
 *
 * @param <T> Static GTFS data: agency, calendar, calendar_dates, routes, shapes, stop_times, stops,
 *           transfers, trips.
 */
public class DataMaps<T> extends HashMap<String, T>{

    private T t;
    private transient Context context;

    private String serializedData;

    public DataMaps(Context c){
        this.context = c;
    }

    public void serialize(Class<?> cls) {
        //this.t = t;
        try {
            if(cls.equals(Routes.class)) {
                serializedData = "routeMap.ser";
            } else if(cls.equals(Stops.class)) {
                serializedData = "stopMap.ser";
            }

            FileOutputStream fos = new FileOutputStream(new File(context.getCacheDir(), serializedData));
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap getMap(Class<?> cls) {
        //De-serialize map stored in memory
        HashMap map = null;
        try
        {
            if(cls.equals(Routes.class)) {
                serializedData = "routeMap.ser";
            } else if(cls.equals(Stops.class)) {
                serializedData = "stopMap.ser";
            }

            FileInputStream fis = new FileInputStream(new File(context.getCacheDir(), serializedData));
            ObjectInputStream ois = new ObjectInputStream(fis);
            map = (HashMap)ois.readObject();
            ois.close();
            fis.close();
        } catch(IOException e) {
            e.printStackTrace();
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }

        return map;
    }
}
