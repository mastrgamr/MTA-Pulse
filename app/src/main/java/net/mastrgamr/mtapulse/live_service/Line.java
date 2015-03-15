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
    private String name;

    @Element(name = "status")
    private String status;

    @Element(name = "text", required = false)
    private String text;

    @Element(name = "Date", required = false)
    private String date;

    @Element(name = "Time", required = false)
    private String time;

    @Element(name = "url", required = false)
    private String url;

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
