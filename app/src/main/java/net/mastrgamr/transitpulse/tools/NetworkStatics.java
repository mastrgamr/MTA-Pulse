package net.mastrgamr.transitpulse.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Project: Transit Pulse
 * Created: Stuart Smith
 * Date: 4/28/2015.
 */
public class NetworkStatics {

    public static boolean isDeviceOnline(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return (ni != null);
    }
}
