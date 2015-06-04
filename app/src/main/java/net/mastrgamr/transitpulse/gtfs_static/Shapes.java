package net.mastrgamr.transitpulse.gtfs_static;

import java.io.Serializable;

/**
 * Project: MTA Pulse
 * Created: Stuart Smith
 * Date: 1/25/2015.
 */
public class Shapes implements Serializable {

    private String shapeId;
    private String shapePtLat;
    private String shapePtLon;
    private String shapePtSeq;

    public Shapes() {
    }

    public Shapes(String shapeId, String shapePtLat, String shapePtLon, String shapePtSeq) {
        this.shapeId = shapeId;
        this.shapePtLat = shapePtLat;
        this.shapePtLon = shapePtLon;
        this.shapePtSeq = shapePtSeq;
    }

    @Override
    public String toString() {
        return "Shapes{" +
                "shapeId='" + shapeId + '\'' +
                ": shapePtLat='" + shapePtLat + '\'' +
                ", shapePtLon='" + shapePtLon + '\'' +
                ", shapePtSeq='" + shapePtSeq + '\'' +
                '}';
    }

    public String getShapeId() {
        return shapeId;
    }

    public String getShapePtLat() {
        return shapePtLat;
    }

    public String getShapePtLon() {
        return shapePtLon;
    }

    public String getShapePtSeq() {
        return shapePtSeq;
    }
}
