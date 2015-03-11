package net.mastrgamr.mtapulse.live_service;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Project: MTA Pulse
 * Author: Stuart Smith
 * Date: 3/11/2015
 */
@Root(name="line")
public class Line{

    @Element(name = "name")
    public String name;

    @Element(name = "status")
    public String status;

    @Element(name = "text")
    public String text;

    @Element(name = "Date")
    public String date;

    @Element(name = "Time")
    public String time;

    public Line(){ }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
