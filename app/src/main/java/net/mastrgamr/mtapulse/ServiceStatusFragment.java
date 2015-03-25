package net.mastrgamr.mtapulse;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import net.mastrgamr.mtapulse.live_service.ServiceStatus;
import net.mastrgamr.mtapulse.tools.MtaFeeds;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Project: MTA Pulse
 * Created: Stuart Smith
 * Date: 1/23/2015.
 */
public class ServiceStatusFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private final String LOG_TAG = getTag();
    private static final String ARG_SECTION_NUMBER = "section_number";
    private URL url;

    private View rootView;
    private ListView listView;
    private SwipeRefreshLayout refreshLayout;

    private ServiceStatus serviceStatus;
    private StatusListAdapter statusListAdapter;

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

    public ServiceStatusFragment() {
        try {
            url = new URL(MtaFeeds.serviceStatus);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        populateList = new PopulateList();
        populateList.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //statusListAdapter = new StatusListAdapter(rootView.getContext());
        refreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.refresh_container);
        refreshLayout.setOnRefreshListener(this);
        if(Build.VERSION.SDK_INT < 21){
            refreshLayout.setColorSchemeColors(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        }

        listView = (ListView)rootView.findViewById(R.id.status_list);

        //needed for back button from another fragment/activity
        if(statusListAdapter != null)
            listView.setAdapter(statusListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(statusListAdapter.getStatusText(position) != null) {
                    ((MainActivity) getActivity()).onStatusClicked(statusListAdapter.getStatusText(position));
                } else {
                    Toast.makeText(rootView.getContext(), "Service is good.\nTake the train!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onRefresh() {
        Toast.makeText(rootView.getContext(), "Refresh not supported yet!", Toast.LENGTH_SHORT).show();
        refreshLayout.setRefreshing(false);
    }

    private class PopulateList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Serializer serializer = new Persister();
            try {
                serviceStatus = serializer.read(ServiceStatus.class, url.openStream());
                statusListAdapter = new StatusListAdapter(rootView.getContext(), serviceStatus);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            listView.setAdapter(statusListAdapter);
        }
    }
}