package net.mastrgamr.transitpulse;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.mastrgamr.transitpulse.interfaces.LineMaps;
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

    StatusRowItemHolder srih;

    private SpannableStringBuilder ssb;
    private SpanString spanString;

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

        //Inflate the row items
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.status_list_item, parent, false);

            //Log.d(LOG_TAG, "Inflating ViewHolder");
            srih = new StatusRowItemHolder();
            srih.lineText = (TextView) convertView.findViewById(R.id.lineText);
            srih.statusText = (TextView) convertView.findViewById(R.id.statusText);
            srih.dateTimeText = (TextView) convertView.findViewById(R.id.dateTimeText);

            convertView.setTag(srih);
        } else {
            //Log.d(LOG_TAG, "Getting Tag");
            srih = (StatusRowItemHolder) convertView.getTag();
        }

        //Set up data to be displayed in the views
        transitType = serviceStatus.getSubways().get(position);

        srih.lineText.setText(transitType.getName());
        //TODO: Make efficient, AsyncTask most likely not going to work
//        spanString = new SpanString(srih.lineText.getLineHeight());
//        Log.d(LOG_TAG, "Transit line name: " + transitType.getName());
//        spanString.execute(transitType.getName());

        if (transitType.getDate() == null || transitType.getTime() == null) {
            srih.dateTimeText.setText("");
        } else {
            srih.dateTimeText.setText("Updated: " + transitType.getDate() + ", at " + transitType.getTime());
            //srih.dateTimeText.setTextColor(c.getResources().getColor(R.color.light_white));
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

    private class SpanString extends AsyncTask<String, Void, SpannableStringBuilder> {
        int lineHeight;

        public SpanString(int lineHeight) {
            this.lineHeight = lineHeight;
        }

        @Override
        protected SpannableStringBuilder doInBackground(String... s) {
            ssb = new SpannableStringBuilder(s[0]);

            Drawable subIcon;
            Bitmap b;

            for(int i = 0; i < ssb.length(); i++) {
                switch (ssb.charAt(i)){
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case 'A':
                    case 'C':
                    case 'E':
                    case 'B':
                    case 'D':
                    case 'F':
                    case 'M':
                    case 'N':
                    case 'Q':
                    case 'R':
                    case 'S':
                    case 'L':
                        if(LineMaps.subwayLines.containsKey(String.valueOf(ssb.charAt(i)))){
                            subIcon = c.getResources().getDrawable(LineMaps.subwayLines.get(String.valueOf(ssb.charAt(i))));
                            b = Bitmap.createScaledBitmap(((BitmapDrawable) subIcon).getBitmap(), lineHeight, lineHeight, false);
                            ssb.setSpan(new ImageSpan(c, b), i, i + 1, Spanned.SPAN_INTERMEDIATE);
                        }
                        break;
                    default:
                        ssb = null;
                        break;
                }
            }
            return ssb;
        }

        @Override
        protected void onPostExecute(SpannableStringBuilder s) {
            super.onPostExecute(s);

            srih.lineText.setText(s);
        }
    }
}
