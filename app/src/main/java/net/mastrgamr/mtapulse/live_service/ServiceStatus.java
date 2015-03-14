package net.mastrgamr.mtapulse.live_service;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Project: MTA Pulse
 * Author: Stuart Smith
 * Date: 3/11/2015
 */
@Root(name = "Service")
public class ServiceStatus {

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

    public List<Line> getSubways(){
        return subways;
    }

    public List<Line> getBuses(){
        return busses;
    }

    public List<Line> getLirr(){
        return lirr;
    }

    public List<Line> getMetronorth(){
        return metronorth;
    }

    public String getTimeStamp() {
        return timestamp;
    }

    public int getResponsecode() {
        return responsecode;
    }
}
