package net.mastrgamr.mtapulse;

/**
 * Project: MTA Pulse
 * Created: Stuart Smith
 * Date: 1/23/2015.
 */
import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import net.mastrgamr.mtapulse.tools.XMLParser;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.ParserConfigurationException;

public class ServiceStatusFragment extends Fragment {

    private final String LOG_TAG = getTag();
    private static final String ARG_SECTION_NUMBER = "section_number";

    private View rootView;
    private ListView listView;

    private XMLParser xmlParser;

    Thread thread;
    private PopulateList populateList;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ServiceStatusFragment newInstance(int sectionNumber) {
        ServiceStatusFragment fragment = new ServiceStatusFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ServiceStatusFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: Use AsyncTask/Service instead.
        populateList = new PopulateList();
        populateList.execute("http://web.mta.info/status/serviceStatus.txt");
        /*thread = new Thread(this);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.d(LOG_TAG, e.getMessage());
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        listView = (ListView)rootView.findViewById(R.id.status_list);
        //listView.setAdapter(new StatusList(rootView.getContext(), xmlParser));

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    /*@Override
    public void run() {
        try {
            xmlParser = new XMLParser("http://web.mta.info/status/serviceStatus.txt");
            xmlParser.parse();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            Log.d(LOG_TAG, e.getMessage());
        }
    }*/

    private class PopulateList extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                xmlParser = new XMLParser(params[0]);
                xmlParser.parse();
            } catch (ParserConfigurationException | SAXException | IOException e) {
                Log.d(LOG_TAG, e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            listView.setAdapter(new StatusList(rootView.getContext(), xmlParser));
        }
    }
}