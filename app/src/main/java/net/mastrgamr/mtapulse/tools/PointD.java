package net.mastrgamr.mtapulse.tools;

/**
 * Project: MTA Pulse
 * Created: Stuart Smith
 * Date: 1/27/2015.
 */

/**
 * "Scapegoat" class to avoid using LatLng in android project for Double point precision.
 */
public class PointD {

    private double x;
    private double y;

    public PointD() { }

    public PointD(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
