package net.mastrgamr.mtapulse;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.mastrgamr.mtapulse.feedobjects.TransportationType;
import net.mastrgamr.mtapulse.tools.XMLParser;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Project: MTA Pulse
 * Author: Stuart Smith
 * Date: 1/25/15
 */
public class StatusList extends BaseAdapter
{
    private Context c;
    private XMLParser xmlParser;

    private TextView lineText;
    private TextView statusText;
    private TextView dateTimeText;

    public StatusList(Context c, XMLParser xmlParser){
        this.c = c;
        this.xmlParser = xmlParser;
    }

    @Override
    public int getCount()
    {
        return xmlParser.getTransportationTypes().size();
    }

    @Override
    public Object getItem(int position)
    {
        System.out.println(xmlParser.getTransportationTypes().get(position));
        return xmlParser.getTransportationTypes().get(position);
    }

    //May need in order to reference the transit types in the ArrayList.
    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    //TODO: Obviously optimize later. RecyclerView/ViewHolder yada yada.
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.status_list_item, parent, false);

        lineText = (TextView)row.findViewById(R.id.lineText);
        statusText = (TextView)row.findViewById(R.id.statusText);
        dateTimeText = (TextView)row.findViewById(R.id.dateTimeText);

        TransportationType transitType = xmlParser.getTransportationTypes().get(position);

        lineText.setText(transitType.getName());
        dateTimeText.setText("Updated: " + transitType.getDate() + ", at " + transitType.getTime());

        if(transitType.getStatus().equalsIgnoreCase("Good Service")){
            statusText.setText(transitType.getStatus());
            statusText.setTextColor(c.getResources().getColor(R.color.green));
        } else if(transitType.getStatus().equalsIgnoreCase("Planned Work") ||
                transitType.getStatus().equalsIgnoreCase("Service Change") ||
                transitType.getStatus().equalsIgnoreCase("Planned Detour")){
            statusText.setText(transitType.getStatus());
            statusText.setTextColor(c.getResources().getColor(R.color.yellow));
        } else if(transitType.getStatus().equalsIgnoreCase("Delays"))
        {
            statusText.setText(transitType.getStatus());
            statusText.setTextColor(c.getResources().getColor(R.color.red));
        }else if(transitType.getStatus().equalsIgnoreCase("Suspended"))
        {
            statusText.setText(transitType.getStatus());
            statusText.setTextColor(c.getResources().getColor(R.color.red));
        }

        return row;
    }
}
