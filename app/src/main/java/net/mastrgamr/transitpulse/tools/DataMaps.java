package net.mastrgamr.transitpulse.tools;

import android.content.Context;
import android.util.Log;

import net.mastrgamr.transitpulse.gtfs_static.Routes;
import net.mastrgamr.transitpulse.gtfs_static.Stops;

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
 * <p/>
 * To use, initialize a new {@link net.mastrgamr.transitpulse.tools.DataMaps}, and include the Class in
 * the '<>'. Remember to call the {@link net.mastrgamr.transitpulse.tools.DataMaps#serialize(Class)} class
 * before operating utilizing the instance.
 *
 * @param <T> Static GTFS data: agency, calendar, calendar_dates, routes, shapes, stop_times, stops,
 *            transfers, trips.
 */
public class DataMaps<T> extends HashMap<String, T> {

    private T t;
    private transient Context context;

    private String serializedData;

    public DataMaps(Context c) {
        this.context = c;
    }

    public boolean doesMapExist(Class<?> cls) {
        if (cls.equals(Routes.class)) {
            serializedData = "routeMap.ser";
        } else if (cls.equals(Stops.class)) {
            serializedData = "stopMap.ser";
        }

        File test = context.getFileStreamPath(serializedData);
        Log.d("DataMaps", "Serialized data already exists");
        return test.exists();
    }

    public void serialize(Class<?> cls) {
        //this.t = t;
        try {
            if (cls.equals(Routes.class)) {
                serializedData = "routeMap.ser";
            } else if (cls.equals(Stops.class)) {
                serializedData = "stopMap.ser";
            }

            File test = context.getFileStreamPath(serializedData);
            if (!test.exists()) {
                Log.d("DataMaps", "FILE GETTING CREATED");

                FileOutputStream fos = context.openFileOutput(serializedData, Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(this);
                oos.close();
                fos.close();
            } else {
                Log.d("Datamaps", "FiLE EXISTS");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DataMaps<T> getMap(Class<?> cls) {
        //De-serialize map stored in memory
        DataMaps<T> map = null;
        try {
            if (cls.equals(Routes.class)) {
                serializedData = "routeMap.ser";
            } else if (cls.equals(Stops.class)) {
                serializedData = "stopMap.ser";
            }

            FileInputStream fis = context.openFileInput(serializedData);
            ObjectInputStream ois = new ObjectInputStream(fis);
            map = (DataMaps<T>) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return map;
    }
}