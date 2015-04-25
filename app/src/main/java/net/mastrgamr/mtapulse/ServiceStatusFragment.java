package net.mastrgamr.mtapulse;

import android.app.Activity;
import android.app.Fragment;
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
import net.mastrgamr.mtapulse.tools.HttpRequest;
import net.mastrgamr.mtapulse.tools.MtaFeeds;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

/**
 * Project: MTA Pulse
 * Created: Stuart Smith
 * Date: 1/23/2015.
 */
public class ServiceStatusFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private final String LOG_TAG = getTag();
    private static final String ARG_SECTION_NUMBER = "section_number";

    private View rootView;
    private ListView listView;
    private SwipeRefreshLayout refreshLayout;

    private ServiceStatus serviceStatus;
    private StatusListAdapter statusListAdapter;
    private boolean refreshStatus = false;

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

        populateList = new PopulateList();
        populateList.execute(MtaFeeds.serviceStatus);
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
        //Toast.makeText(rootView.getContext(), "Refresh not supported yet!", Toast.LENGTH_SHORT).show();
        refreshLayout.setRefreshing(true);
        if(populateList.getStatus() != AsyncTask.Status.RUNNING) {
            refreshStatus = true;
            populateList = new PopulateList();
            populateList.execute(MtaFeeds.serviceStatus);
        }
        refreshLayout.setRefreshing(false);
    }

    private class PopulateList extends AsyncTask<String, Void, File> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected File doInBackground(String... urls) {
            URL url;
            Serializer serializer = new Persister();

            /**
             * Check if file exists
             */
            Log.d(LOG_TAG, "checking if file exists");
            File[] files = getActivity().getCacheDir().listFiles();
            File preCheck = null;
            for(File file : files){
                if(file.getName().startsWith("serviceStat")) {
                    preCheck = file;
                    Log.d(LOG_TAG, "file exists");
                    break;
                }
            }

            /**
             * If the file exists and is not more than 5 minutes old (or file exists), keep it.
             * Else generate a new one.
             */
            try {
                if(preCheck != null)
                    serviceStatus = serializer.read(ServiceStatus.class, preCheck);

                if(serviceStatus == null || (System.currentTimeMillis() - preCheck.lastModified()) >= 300000 || refreshStatus){
                    Log.d(LOG_TAG, "5 mins older, or file dont exist, or refresh prompted. Generating new file");
                    try {
                        if(preCheck != null)
                            preCheck.delete();

                        url = new URL(urls[0]);
                        serviceStatus = serializer.read(ServiceStatus.class, url.openStream());
                        statusListAdapter = new StatusListAdapter(rootView.getContext(), serviceStatus);

                        HttpRequest request =  HttpRequest.get(url);
                        File file = null;
                        if (request.ok()) {
                            file = File.createTempFile("serviceStat", ".xml", getActivity().getCacheDir());
                            file.setLastModified(System.currentTimeMillis());
                            request.receive(file);
                        }
                        return file;
                    } catch (HttpRequest.HttpRequestException exception) {
                        return null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        Log.e(LOG_TAG, "Unable to open URL for ServiceStatus.");
                    }
                } else {
                    statusListAdapter = new StatusListAdapter(rootView.getContext(), serviceStatus);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(File aFile) {
            super.onPostExecute(aFile);
            listView.setAdapter(statusListAdapter);
        }
    }
}