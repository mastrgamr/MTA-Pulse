package net.mastrgamr.mtapulse;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.mastrgamr.mtapulse.feedobjects.TransportationType;
import net.mastrgamr.mtapulse.live_service.Line;
import net.mastrgamr.mtapulse.live_service.ServiceStatus;
import net.mastrgamr.mtapulse.tools.XMLParser;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.w3c.dom.Text;

import java.util.List;

/**
 * Project: MTA Pulse
 * Author: Stuart Smith
 * Date: 1/25/15
 */
public class StatusListAdapter extends BaseAdapter
{
    private final String LOG_TAG = StatusListAdapter.class.getSimpleName();

    private Context c;
    private ServiceStatus serviceStatus = new ServiceStatus();

    public StatusListAdapter(Context c){
        this.c = c;
        Serializer serializer = new Persister();
        try {
            serviceStatus = serializer.read(ServiceStatus.class, c.getResources().openRawResource(R.raw.servicestatus));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getCount()
    {
        return serviceStatus.getSubways().size();
    }

    @Override
    public Object getItem(int position)
    {
        System.out.println(serviceStatus.getSubways().get(position));
        return serviceStatus.getSubways().get(position);
    }

    //May need in order to reference the transit types in the ArrayList.
    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    static class StatusRowItemHolder {
        TextView lineText;
        TextView statusText;
        TextView dateTimeText;
    }

    //TODO: Check efficiency, doesn't seem right in teh logcatz. Getting Tags, then inflating views?
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        StatusRowItemHolder srih;

        if(convertView == null)
        {
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
            srih = (StatusRowItemHolder)convertView.getTag();
        }

        Line transitType = serviceStatus.getSubways().get(position);

        /*if(srih.lineText.getText().equals("123"))
            convertView.setBackgroundColor(c.getResources().getColor(R.color.s123));
        if(srih.lineText.getText().equals("456"))
            convertView.setBackgroundColor(c.getResources().getColor(R.color.s456));
        if(srih.lineText.getText().equals("7"))
            convertView.setBackgroundColor(c.getResources().getColor(R.color.s7));*/

        srih.lineText.setText(transitType.getName());
        if(transitType.getDate() == null || transitType.getTime() == null){
            srih.dateTimeText.setText("");
        } else {
            srih.dateTimeText.setText("Updated: " + transitType.getDate() + ", at " + transitType.getTime());
        }

        if(transitType.getStatus().equalsIgnoreCase("Good Service")){
            srih.statusText.setText(transitType.getStatus());
            srih.statusText.setTextColor(c.getResources().getColor(R.color.green));
        } else if(transitType.getStatus().equalsIgnoreCase("Planned Work") ||
                transitType.getStatus().equalsIgnoreCase("Service Change") ||
                transitType.getStatus().equalsIgnoreCase("Planned Detour")){
            srih.statusText.setText(transitType.getStatus());
            srih.statusText.setTextColor(c.getResources().getColor(R.color.yellow));
        } else if(transitType.getStatus().equalsIgnoreCase("Delays") ||
                transitType.getStatus().equalsIgnoreCase("Suspended"))
        {
            srih.statusText.setText(transitType.getStatus());
            srih.statusText.setTextColor(c.getResources().getColor(R.color.red));
        }

        return convertView;
    }
}
