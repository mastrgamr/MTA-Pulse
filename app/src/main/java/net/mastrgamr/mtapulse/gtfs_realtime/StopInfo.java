package net.mastrgamr.mtapulse.gtfs_realtime;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Project: MTA Pulse
 * Created: Stuart Smith
 * Date: 4/21/2015
 */

@JsonObject
public class StopInfo {

    @JsonField
    public String stopName;
    @JsonField
    public long stopTime;

    public StopInfo(){}
}
