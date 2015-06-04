package net.mastrgamr.transitpulse.live_service;

import android.content.Context;

import net.mastrgamr.transitpulse.R;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Project: MTA Pulse
 * Author: Stuart Smith
 * Date: 3/11/2015
 */

/**
 * The XML parser for the live MTA Service Status.
 */
@Root(name = "Service")
public class ServiceStatus {

    private static ServiceStatus instance = null;
    private URL url;
    private static Context c;

    public ServiceStatus() {
    }

    public ServiceStatus(Context c) {
        this.c = c;
    }

    public ServiceStatus(String url) throws MalformedURLException {
        this.url = new URL(url); //TODO: download the URL and store internally on device cache
    }

    public static ServiceStatus getParsedStatus() {
        Serializer serializer = new Persister();
        try {
            instance = serializer.read(ServiceStatus.class, c.getResources().openRawResource(R.raw.servicestatus));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }

    @Element(name = "responsecode")
    private int responsecode;

    @Element(name = "timestamp")
    private String timestamp;

    @ElementList(name = "subway")
    private List<Line> subways;

    @ElementList(name = "bus")
    private List<Line> busses;

    @ElementList(name = "BT")
    private List<Line> bt;

    @ElementList(name = "LIRR")
    private List<Line> lirr;

    @ElementList(name = "MetroNorth")
    private List<Line> metronorth;

    public List<Line> getSubways() {
        return subways;
    }

    public List<Line> getBuses() {
        return busses;
    }

    public List<Line> getLirr() {
        return lirr;
    }

    public List<Line> getMetronorth() {
        return metronorth;
    }

    public String getTimeStamp() {
        return timestamp;
    }

    public int getResponsecode() {
        return responsecode;
    }
}
