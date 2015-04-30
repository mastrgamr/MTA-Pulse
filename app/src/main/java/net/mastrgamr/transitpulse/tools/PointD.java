package net.mastrgamr.transitpulse.tools;

/**
 * Project: MTA Pulse
 * Created: Stuart Smith
 * Date: 1/27/2015.
 */

/**
 * "Scapegoat" class to avoid using LatLng and PointF in android project for Double point precision.
 */
public class PointD {

    private double x;
    private double y;

    public PointD() { }

    public PointD(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double distance(PointD pt) {
        double px = pt.getX() - this.getX();
        double py = pt.getY() - this.getY();
        return Math.sqrt(px * px + py * py);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "PointD{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
