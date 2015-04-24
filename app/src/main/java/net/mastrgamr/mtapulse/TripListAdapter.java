package net.mastrgamr.mtapulse;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bluelinelabs.logansquare.LoganSquare;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.transit.realtime.GtfsRealtime;

import net.mastrgamr.mtapulse.gtfs_realtime.NearbyStopsInfo;
import net.mastrgamr.mtapulse.gtfs_realtime.RTRoutes;
import net.mastrgamr.mtapulse.gtfs_realtime.RtGtfsParser;
import net.mastrgamr.mtapulse.gtfs_realtime.StopInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Project: MTA Pulse
 * Author: Stuart Smith
 * Date: 3/20/2015
 */

/**
 * Used to populate the grid list of live trip times on TripFragemnt.
 */
public class TripListAdapter extends BaseAdapter {
    private final String LOG_TAG = TripListAdapter.class.getSimpleName();

    private Context c;
    private ArrayList<ArrayList<NearbyStopsInfo>> nearbyStops;
    private ArrayList<NearbyStopsInfo> nearbyStops1;
    private ArrayList<RTRoutes> nearbyStopRoutes;
    private HashMap<String, HashMap<String, ArrayList<GtfsRealtime.TripUpdate.StopTimeUpdate>>> tripMap;

    String[] keys;

    public TripListAdapter(Context context, ArrayList<ArrayList<NearbyStopsInfo>> nearbyStops) {
        c = context;
        this.nearbyStops1 = nearbyStops.get(1);
        System.out.println(this.nearbyStops1.size() + " size pased in Adapter");
    }

    public TripListAdapter(Context context, HashMap<String, HashMap<String, ArrayList<GtfsRealtime.TripUpdate.StopTimeUpdate>>> tripMap) {
        c = context;
        this.tripMap = tripMap;

        int i = 0;
        for(HashMap.Entry entry : tripMap.entrySet()){
            if(entry.getKey().toString().endsWith("N"))
                i++;
        }
        keys = new String[i];

        i = 0;
        for(HashMap.Entry entry : tripMap.entrySet()){
            if(entry.getKey().toString().endsWith("N")){
                keys[i] = entry.getKey().toString();
                i++;
            }
        }

        i = 0;
        for(String s : keys){
            System.out.println(s + " TRIPADAPTER");
            for(HashMap.Entry entry : tripMap.get(keys[i]).entrySet()){
                System.out.println(entry.getKey() + " key in entry");
                i++;
            }
            i = 0;
        }
    }

    private static class TripGridItemHolder {
        TextView routeText;
        TextView destText;
        TextView prevText;
        ShimmerFrameLayout shimmerLive;
        TextView liveTimeText;
        TextView staticTimeText;
    }

    @Override
    public int getCount() {
        return nearbyStops1.size();
    }

    //Ignore for now
    @Override
    public Object getItem(int position) {
        return nearbyStops1.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TripGridItemHolder tgih;

        //Inflate the row items
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //get inflater
            convertView = inflater.inflate(R.layout.trip_grid_list_item, parent, false); //convertview to tripgrid item

            Log.d(LOG_TAG, "Inflating TripViewHolder");
            tgih = new TripGridItemHolder();
            tgih.routeText = (TextView) convertView.findViewById(R.id.route_text);
            tgih.destText = (TextView) convertView.findViewById(R.id.dest_text);
            tgih.prevText = (TextView) convertView.findViewById(R.id.past_time_text);
            tgih.shimmerLive = (ShimmerFrameLayout) convertView.findViewById(R.id.shimmer_live);
            tgih.shimmerLive.startShimmerAnimation();
            tgih.liveTimeText = (TextView) convertView.findViewById(R.id.live_time_text);
            tgih.staticTimeText = (TextView) convertView.findViewById(R.id.static_time_text);

            convertView.setTag(tgih);
        } else {
            Log.d(LOG_TAG, "Getting Tag");
            tgih = (TripGridItemHolder)convertView.getTag();
        }

        long time = 0;
        long diff = 0;
        nearbyStopRoutes = nearbyStops1.get(position).trains;
        for(int i = 0; i < nearbyStopRoutes.size(); i++){
            RTRoutes rtRoute = nearbyStopRoutes.get(i);
            ArrayList<Long> stopTimes = rtRoute.stopTimes;
            for(Long stopTime: stopTimes){
                time = stopTime;
                diff = (time * 1000) - System.currentTimeMillis();
                if(diff >= 0) //if positive break out loop and set up the text
                    break;
            }
        }

        tgih.routeText.setText(nearbyStops1.get(position).stopId);
        //time = tripMap.get(s).get(entry.getKey()).get(position).getArrival().getTime();

        //long diff = stu.getDeparture().getTime() * 1000) - new Date().getTime();
        //long time = tripMap.get("501N").get("5").get(0).getDeparture().getTime();
        //long diff = (time * 1000) - new Date().getTime();
        Log.d(LOG_TAG, (time * 1000) +" - "+ System.currentTimeMillis());
        if(diff < 0){
            tgih.prevText.setText(Math.abs(diff/60000) + " mins ago");
        } else {
            tgih.prevText.setText("");
        }
        tgih.liveTimeText.setText(diff/60000 + " mins");

        return convertView;
    }
}
