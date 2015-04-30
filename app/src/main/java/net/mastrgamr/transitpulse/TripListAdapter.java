package net.mastrgamr.transitpulse;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;

import net.mastrgamr.transitpulse.gtfs_realtime.NearbyStopsInfo;
import net.mastrgamr.transitpulse.gtfs_realtime.RTRoutes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeSet;

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

    private LayoutInflater inflater;
    private Context c;
    //private ArrayList<ArrayList<NearbyStopsInfo>> nearbyStops;
    private ArrayList<NearbyStopsInfo> nearbyStops1;
    private ArrayList<RTRoutes> nearbyStopRoutes;
    LinkedList<NearbyStopsInfo> nsi;

    private TreeSet seperators = new TreeSet();
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private static final int SEPERATOR_COUNT = 2;

    private boolean stationSelected = false;
    private int upDownFlip = 1;
    private int selectedStation = 0;

    public TripListAdapter(Context context, ArrayList<ArrayList<NearbyStopsInfo>> nearbyStops) {
        c = context;
        this.nearbyStops1 = nearbyStops.get(upDownFlip);
        System.out.println(this.nearbyStops1.size() + " size passed in Adapter");

        for (int i = 0; i < nearbyStops.size(); i++) {
            seperators.add(nearbyStops.get(i).size() - 1);
        }
    }

    private static class TripGridItemHolder {
        TextView routeText;
        TextView destText;
        TextView prevText;
        ShimmerFrameLayout shimmerLive;
        TextView liveTimeText;
        TextView staticTimeText;
        TextView nextText;
    }

    private static class TripGridStationHolder {
        TextView stationName;
        TextView nextText;
        TextView routes;
        TextView upDownText;
    }

    public void setStationSelected(boolean state, int selectedStation){
        stationSelected = state;
        //nearbyStops1.get(1);
        this.notifyDataSetChanged();
    }

    public void setUpDownFlip(){
        upDownFlip = (upDownFlip == 0) ? 1 : 0;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return seperators.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return SEPERATOR_COUNT;
    }

    @Override
    public int getCount() {
        if(stationSelected)
            return nearbyStops1.get(upDownFlip).trains.size();
        return nearbyStops1.size();
    }

    //Ignore for now
    @Override
    public Object getItem(int position) {
        return nearbyStops1.get(upDownFlip);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TripGridStationHolder tgsh = new TripGridStationHolder();
        TripGridItemHolder tgih = new TripGridItemHolder();

        int type = getItemViewType(position);

        if(stationSelected) {
            //Inflate the row items
            if(convertView == null  || !(convertView.getTag() instanceof TripGridItemHolder))
            {
                inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //get inflater

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
                tgih.nextText = (TextView) convertView.findViewById(R.id.next_time_text);

                convertView.setTag(tgih);
            } else {
                Log.d(LOG_TAG, "Getting Tag");
                tgih = (TripGridItemHolder)convertView.getTag();
            }
        } else {
            if(convertView == null || !(convertView.getTag() instanceof TripGridStationHolder))
            {
                inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //get inflater

                /*switch (type) {
                    case TYPE_ITEM:*/
                        convertView = inflater.inflate(R.layout.trip_grid_station_list, parent, false); //convertview to tripgrid item
                        tgsh = new TripGridStationHolder();
                        tgsh.stationName = (TextView) convertView.findViewById(R.id.station_name);
                        tgsh.nextText = (TextView) convertView.findViewById(R.id.next_train_time);
                        tgsh.routes = (TextView) convertView.findViewById(R.id.station_routes);
                        tgsh.upDownText = (TextView) convertView.findViewById(R.id.up_down_text);
                        /*break;
                    case TYPE_SEPARATOR:
                        convertView = inflater.inflate(R.layout.list_seperator, parent, false);
                        TextView text = (TextView)convertView.findViewById(R.id.seperator_text);
                        text.setText("Uptown");
                        break;
                    default:
                        Log.w(LOG_TAG, "Could not get type");
                        break;
                }*/
                convertView.setTag(tgsh);
            } else {
                Log.d(LOG_TAG, "Getting Tag");
                tgsh = (TripGridStationHolder)convertView.getTag();
            }
        }

        if(stationSelected) {
            long time = 0;
            long diff = 0;
            long diffNext = 0;
            nearbyStopRoutes = nearbyStops1.get(position).trains;
            for (int i = 0; i < nearbyStopRoutes.size(); i++) {
                RTRoutes rtRoute = nearbyStopRoutes.get(i);
                ArrayList<Long> stopTimes = rtRoute.stopTimes;
                for (Long stopTime : stopTimes) {
                    time = stopTime;
                    diff = (time * 1000) - System.currentTimeMillis();
                    if (diff >= 0) //if positive break out loop and set up the text
                        break;
                }
            }

            tgih.routeText.setText(nearbyStops1.get(upDownFlip).trains.get(position).routeId);

            Log.d(LOG_TAG, (time * 1000) + " - " + System.currentTimeMillis());
            if (diff < 0) {
                tgih.prevText.setText(Math.abs(diff / 60000) + " mins ago");
            } else {
                tgih.prevText.setText("");
            }
            if (time == 0) {
                tgih.liveTimeText.setText("Last Stop");
                tgih.prevText.setText("");
                tgih.nextText.setText("");
            } else {
                tgih.liveTimeText.setText(diff / 60000 + " mins");
            }
        } else {
            tgsh.stationName.setText(nearbyStops1.get(position).stopName);

            StringBuilder sb = new StringBuilder();
            for(RTRoutes routes : nearbyStops1.get(position).trains){
                    sb.append(routes.routeId);
                    sb.append(" ");
            }
            tgsh.routes.setText(sb);
            tgsh.nextText.setText("");
            if(upDownFlip == 0) {
                tgsh.upDownText.setText("Uptown Trains");
            } else {
                tgsh.upDownText.setText("Downtown Trains");
            }
        }

        return convertView;
    }

    public boolean isStationSelected(){
        return stationSelected;
    }
}
