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
import net.mastrgamr.transitpulse.tools.NearbyStopsProto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    private NearbyStopsProto.UpDownStops nearbyStops2;
    NearbyStopsProto.NearbyStopsFeed nearbyFeed;
    private ArrayList<RTRoutes> nearbyStopRoutes;
    private List<NearbyStopsProto.Routes> nearbyStopRoutes1;

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

    public TripListAdapter(Context context, NearbyStopsProto.NearbyStopsFeed nearbyStops) {
        c = context;
        this.nearbyFeed = nearbyStops;
        this.nearbyStops2 = nearbyStops.getUpdown(upDownFlip);
        System.out.println(nearbyStops.getUpdownCount() + " size passed in Adapter");

        for (int i = 0; i < nearbyStops.getUpdownList().size(); i++) {
            seperators.add(nearbyStops.getUpdown(i).getNearbyList().size() - 1);
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

    public void setStationSelected(boolean state, int selectedStation) {
        stationSelected = state;
        this.selectedStation = selectedStation;
        this.notifyDataSetChanged();
    }

    public void setUpDownFlip() {
        upDownFlip = (upDownFlip == 0) ? 1 : 0;
        this.nearbyStops2 = nearbyFeed.getUpdown(upDownFlip);
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
        if (stationSelected)
            return nearbyStops2.getNearby(selectedStation).getRoutesList().size();
        return nearbyStops2.getNearbyList().size();
    }

    //Ignore for now
    @Override
    public Object getItem(int position) {
        return nearbyStops2.getNearby(selectedStation);
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

        if (stationSelected) {
            //Inflate the row items
            if (convertView == null || !(convertView.getTag() instanceof TripGridItemHolder)) {
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
                tgih = (TripGridItemHolder) convertView.getTag();
            }
        } else {
            if (convertView == null || !(convertView.getTag() instanceof TripGridStationHolder)) {
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
                tgsh = (TripGridStationHolder) convertView.getTag();
            }
        }

        if (stationSelected) {
            long time = 0;
            long time2 = 0;
            long diff = 0;
            long diff2 = 0;

            nearbyStopRoutes1 = nearbyStops2.getNearby(selectedStation).getRoutesList();
            NearbyStopsProto.Routes rtRoute = nearbyStopRoutes1.get(position);
            int stopList = rtRoute.getStopTimesList().size();
            for(int i = 0; i < stopList; i++) {
                System.out.println("Looking up stoptimes");
                time = rtRoute.getStopTimes(i);
                if((i+1) != stopList)
                    time2 = rtRoute.getStopTimes(i+1);
                diff = (time * 1000) - System.currentTimeMillis();
                diff2 = (time2 * 1000) - System.currentTimeMillis();
                if (diff >= 0) //if positive break out loop and set up the text
                    break;
            }

            /*for (Long stopTime : rtRoute.getStopTimesList()) {
                System.out.println("Looking up stoptimes");
                time = stopTime;
                diff = (time * 1000) - System.currentTimeMillis();
                if (diff >= 0) //if positive break out loop and set up the text
                    break;
            }*/

            tgih.routeText.setText(nearbyStops2.getNearby(selectedStation).getRoutes(position).getRouteId());

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
                tgih.nextText.setText(diff2 / 60000 + " next");
            }
        } else {
            tgsh.stationName.setText(nearbyStops2.getNearby(position).getStopName());

            StringBuilder sb = new StringBuilder();
            for (NearbyStopsProto.Routes routes : nearbyStops2.getNearby(position).getRoutesList()) {
                sb.append(routes.getRouteId());
                sb.append(" ");
            }
            tgsh.routes.setText(sb);
            tgsh.nextText.setText("");
            if (upDownFlip == 0) {
                tgsh.upDownText.setText("Uptown Trains");
            } else {
                tgsh.upDownText.setText("Downtown Trains");
            }
        }

        return convertView;
    }

    public boolean isStationSelected() {
        return stationSelected;
    }
}
