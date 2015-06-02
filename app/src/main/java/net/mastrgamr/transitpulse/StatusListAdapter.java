package net.mastrgamr.transitpulse;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.mastrgamr.transitpulse.live_service.Line;
import net.mastrgamr.transitpulse.live_service.ServiceStatus;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * Project: MTA Pulse
 * Author: Stuart Smith
 * Date: 1/25/15
 */
public class StatusListAdapter extends BaseAdapter {
    private final String LOG_TAG = StatusListAdapter.class.getSimpleName();

    private Context c;
    private ServiceStatus serviceStatus;
    private Line transitType;

    public StatusListAdapter(Context c, ServiceStatus s) {
        this.c = c;
        Serializer serializer = new Persister();
        this.serviceStatus = s;
        /*try {
            serviceStatus = serializer.read(ServiceStatus.class, c.getResources().openRawResource(R.raw.servicestatus));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public int getCount() {
        return serviceStatus.getSubways().size();
    }

    @Override
    public Object getItem(int position) {
        //System.out.println(serviceStatus.getSubways().get(position));
        return serviceStatus.getSubways().get(position);
    }

    //May need in order to reference the transit types in the ArrayList.
    @Override
    public long getItemId(int position) {
        return 0;
    }

    private static class StatusRowItemHolder {
        TextView lineText;
        TextView statusText;
        TextView dateTimeText;
    }

    //TODO: Check efficiency, doesn't seem right in teh logcatz. Getting Tags, then inflating views?
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StatusRowItemHolder srih;

        //Inflate the row items
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.status_list_item, parent, false);

            Log.d(LOG_TAG, "Inflating ViewHolder");
            srih = new StatusRowItemHolder();
            srih.lineText = (TextView) convertView.findViewById(R.id.lineText);
            srih.statusText = (TextView) convertView.findViewById(R.id.statusText);
            srih.dateTimeText = (TextView) convertView.findViewById(R.id.dateTimeText);

            convertView.setTag(srih);
        } else {
            Log.d(LOG_TAG, "Getting Tag");
            srih = (StatusRowItemHolder) convertView.getTag();
        }

        //Set up data to be displayed in the views
        transitType = serviceStatus.getSubways().get(position);

        srih.lineText.setText(transitType.getName());
        srih.lineText.setTextColor(c.getResources().getColor(R.color.light_white));

        /*if(transitType.getName().equals("123")) {
            convertView.setBackgroundColor(c.getResources().getColor(R.color.s123));
        } else if(transitType.getName().equals("456")) {
            convertView.setBackgroundColor(c.getResources().getColor(R.color.s456));
        } else if(transitType.getName().equals("7")) {
            convertView.setBackgroundColor(c.getResources().getColor(R.color.s7));
        }*/

        if (transitType.getDate() == null || transitType.getTime() == null) {
            srih.dateTimeText.setText("");
        } else {
            srih.dateTimeText.setText("Updated: " + transitType.getDate() + ", at " + transitType.getTime());
            srih.dateTimeText.setTextColor(c.getResources().getColor(R.color.light_white));
        }

        if (transitType.getStatus().equalsIgnoreCase("Good Service")) {
            srih.statusText.setText(transitType.getStatus());
            srih.statusText.setTextColor(c.getResources().getColor(R.color.green));
        } else if (transitType.getStatus().equalsIgnoreCase("Planned Work") ||
                transitType.getStatus().equalsIgnoreCase("Service Change") ||
                transitType.getStatus().equalsIgnoreCase("Planned Detour")) {
            srih.statusText.setText(transitType.getStatus());
            srih.statusText.setTextColor(c.getResources().getColor(R.color.yellow));
        } else if (transitType.getStatus().equalsIgnoreCase("Delays") ||
                transitType.getStatus().equalsIgnoreCase("Suspended")) {
            srih.statusText.setText(transitType.getStatus());
            srih.statusText.setTextColor(c.getResources().getColor(R.color.red));
        }

        return convertView;
    }

    public String getStatusText(int position) {
        if (serviceStatus.getSubways().get(position).getText() != null)
            return Html.fromHtml(serviceStatus.getSubways().get(position).getText()).toString();
        return null;
    }
}
