package net.mastrgamr.transitpulse;

import android.app.Activity;
import android.app.Fragment;
<<<<<<< HEAD
import android.content.Context;
=======
>>>>>>> 867398e9a6dd3ef7fa2ac567baf99476867b420d
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import net.mastrgamr.transitpulse.live_service.ServiceStatus;
import net.mastrgamr.transitpulse.tools.HttpRequest;
import net.mastrgamr.transitpulse.tools.MtaFeeds;
import net.mastrgamr.transitpulse.tools.NetworkStatics;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
<<<<<<< HEAD
import java.io.FileOutputStream;
=======
>>>>>>> 867398e9a6dd3ef7fa2ac567baf99476867b420d
import java.io.IOException;
import java.net.URL;

/**
 * Project: MTA Pulse
 * Created: Stuart Smith
 * Date: 1/23/2015.
 */
public class ServiceStatusFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private final String LOG_TAG = getClass().getSimpleName();
    private static final String ARG_SECTION_NUMBER = "section_number";

    private static final String[] menuItems = {"Settings", "About"};
    public static boolean settingsPressed = false;

    private View rootView;
    private ListView listView;
    private ListView menuList;
    private SwipeRefreshLayout refreshLayout;

    private ServiceStatus serviceStatus;
    private StatusListAdapter statusListAdapter;
    private boolean refreshStatus = false;

    private PopulateList populateList;

    private SharedPreferences appSettings;
    private boolean isDarkTheme;

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

        appSettings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        isDarkTheme = appSettings.getBoolean("theme_checkbox", false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.service_status_fragment, container, false);

        //statusListAdapter = new StatusListAdapter(rootView.getContext());
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_container);
        if(isDarkTheme) {
            refreshLayout.setBackgroundColor(rootView.getResources().getColor(R.color.menu_back_dark));
            Log.d(LOG_TAG, "We got a dark theme.");
        }
        refreshLayout.setOnRefreshListener(this);
        if (Build.VERSION.SDK_INT < 21) {
            refreshLayout.setColorSchemeColors(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
        }

        listView = (ListView) rootView.findViewById(R.id.status_list);

        //needed for back button from another fragment/activity
        if (statusListAdapter != null)
            listView.setAdapter(statusListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (statusListAdapter.getStatusText(position) != null) {
                    ((MainActivity) getActivity()).onStatusClicked(statusListAdapter.getStatusText(position));
                } else {
                    Toast.makeText(rootView.getContext(), "Service is good!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        menuList = (ListView)rootView.findViewById(R.id.menu_list);
        menuList.setAdapter(new ArrayAdapter<>(rootView.getContext(), android.R.layout.simple_list_item_1, menuItems));
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){ //Settings
                    settingsPressed = true;
                    TripActivity ta = (TripActivity)getActivity();
                    ta.showSetingsFragment();
                } else if(position == 1){
                    Intent i = new Intent(rootView.getContext(), AboutActivity.class);
                    startActivity(i);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //((MainActivity) activity).onSectionAttached(
        //        getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onRefresh() {
        //Toast.makeText(rootView.getContext(), "Refresh not supported yet!", Toast.LENGTH_SHORT).show();
        Log.d(LOG_TAG, "REFRESHING STATUS INFO!");
        refreshLayout.setRefreshing(true);
        if (populateList.getStatus() != AsyncTask.Status.RUNNING) {
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
<<<<<<< HEAD
            File[] files = getActivity().getFilesDir().listFiles();
            File preCheck = null;
            for (File file : files) {
                if (file.getName().equals("serviceStat.xml")) {
=======
            File[] files = getActivity().getCacheDir().listFiles();
            File preCheck = null;
            for (File file : files) {
                if (file.getName().startsWith("serviceStat")) {
>>>>>>> 867398e9a6dd3ef7fa2ac567baf99476867b420d
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
                if (preCheck != null) {
                    Log.d(LOG_TAG, "Assigning servicestatus to found file");
                    serviceStatus = serializer.read(ServiceStatus.class, preCheck);
                    Log.d(LOG_TAG, (serviceStatus == null) + " found to be null");
                }

                System.out.println(refreshStatus + "RefreshStatus");
                //TODO:Potential crash! Expectation is the first bool == null
                if (refreshStatus || serviceStatus == null || (((System.currentTimeMillis() - preCheck.lastModified()) >= 300000) && NetworkStatics.isDeviceOnline(getActivity()))) {
                    Log.d(LOG_TAG, "5 mins older, or file dont exist, or refresh prompted. Generating new file");
                    try {
                        if (preCheck != null)
                            preCheck.delete();

                        url = new URL(urls[0]);
                        serviceStatus = serializer.read(ServiceStatus.class, url.openStream());
                        statusListAdapter = new StatusListAdapter(rootView.getContext(), serviceStatus);

                        HttpRequest request = HttpRequest.get(url);
<<<<<<< HEAD
                        File file;
                        FileOutputStream fos;
                        if (request.ok()) {
                            //file = File.createTempFile("serviceStat", ".xml", getActivity().getCacheDir());
                            fos = getActivity().openFileOutput("serviceStat.xml", Context.MODE_PRIVATE);
                            //file.setLastModified(System.currentTimeMillis());
                            fos.write(request.bytes());
                            fos.close();
                            //request.receive(file);
                            request.receive(fos);
                        }

                        refreshStatus = false;
                        file = getActivity().getFileStreamPath("serviceStat.xml");
=======
                        File file = null;
                        if (request.ok()) {
                            file = File.createTempFile("serviceStat", ".xml", getActivity().getCacheDir());
                            file.setLastModified(System.currentTimeMillis());
                            request.receive(file);
                        }

                        refreshStatus = false;
>>>>>>> 867398e9a6dd3ef7fa2ac567baf99476867b420d
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