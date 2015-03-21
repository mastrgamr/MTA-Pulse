package net.mastrgamr.mtapulse;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

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

    public TripListAdapter(Context context, ArrayList<String> tripList) {
        c = context;
    }

    private static class TripGridItemHolder {
        TextView routeText;
        TextView destText;
        TextView liveTimeText;
        TextView staticTimeText;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
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
            tgih.liveTimeText = (TextView) convertView.findViewById(R.id.live_time_text);
            tgih.staticTimeText = (TextView) convertView.findViewById(R.id.static_time_text);

            convertView.setTag(tgih);
        } else {
            Log.d(LOG_TAG, "Getting Tag");
            tgih = (TripGridItemHolder)convertView.getTag();
        }


        return convertView;
    }
}
