package net.mastrgamr.mtapulse.gtfs_static;

import java.io.Serializable;

/**
 * Project: GTFSParsing
 * Author: Stuart Smith
 * Date: 1/19/2015
 */

/**
 * Contains information parsed in from the GTFS Static feed's 'routes.txt'.
 */
public class Routes implements Serializable {

    private static final long serialVersionUID = 1L;

    private String routeId;
    private String routeShortName;
    private String routeLongName;
    private String routeDesc;
    private String routeUrl;
    private String routeColor;
    private String routeTextColor;

    public Routes() { }

    public Routes(String routeId, String routeShortName, String routeLongName, String routeDesc, String routeUrl, String routeColor, String routeTextColor){
        this.routeId = routeId;
        this.routeShortName = routeShortName;
        this.routeLongName = routeLongName;
        this.routeDesc = routeDesc;
        this.routeUrl = routeUrl;
        this.routeColor = routeColor;
        this.routeTextColor = routeTextColor;
    }

    public Routes(String routeId, String routeShortName, String routeLongName, String routeDesc, String routeUrl, String routeColor){
        this.routeId = routeId;
        this.routeShortName = routeShortName;
        this.routeLongName = routeLongName;
        this.routeDesc = routeDesc;
        this.routeUrl = routeUrl;
        this.routeColor = routeColor;
    }

    @Override
    public String toString() {
        if(routeTextColor == null){
            return "Route ID: " + routeId +
                    ", Short Name: " + routeShortName +
                    ", Long Name: " + routeLongName +
                    ", Description: " + routeDesc +
                    ", Url: " + routeUrl +
                    ", Color: " + routeColor;
        } else {
            return "Route ID: " + routeId +
                    ", Short Name: " + routeShortName +
                    ", Long Name: " + routeLongName +
                    ", Description: " + routeDesc +
                    ", Url: " + routeUrl +
                    ", Color: " + routeColor +
                    ", Text Color: " + routeTextColor;
        }
    }

    public String getRouteId() {
        return routeId;
    }

    public String getRouteLongName() {
        return routeLongName;
    }

    public String getRouteDesc() {
        return routeDesc;
    }

    public String getRouteUrl() {
        return routeUrl;
    }

    public String getRouteColor() {
        return routeColor;
    }

    public String getRouteTextColor() {
        return routeTextColor;
    }
}
