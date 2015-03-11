package net.mastrgamr.mtapulse.live_service;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by mastrgamr on 3/10/15.
 */

@Root(name = "Service")
public class Service {

    @Element(name = "responsecode")
    private int responsecode;

    @Element(name = "timestamp")
    private String timestamp;

    @ElementList(name = "subway")
    private List<Line> subway;

    public List<Line> getProperties(){
        return subway;
    }

    public String getTimeStamp() {
        return timestamp;
    }

    public int getResponsecode() {
        return responsecode;
    }
}
