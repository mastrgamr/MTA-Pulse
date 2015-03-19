package net.mastrgamr.mtapulse;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import net.mastrgamr.mtapulse.live_service.ServiceStatus;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;

/**
 * Project: MTA Pulse
 * Created: Stuart Smith
 * Date: 1/23/2015.
 */
public class ServiceStatusFragment extends Fragment {

    private final String LOG_TAG = getTag();
    private static final String ARG_SECTION_NUMBER = "section_number";

    private View rootView;
    private ListView listView;

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

    public ServiceStatusFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //populateList = new PopulateList();
        //populateList.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        statusListAdapter = new StatusListAdapter(rootView.getContext());

        listView = (ListView)rootView.findViewById(R.id.status_list);

        listView.setAdapter(statusListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(statusListAdapter.getStatusText(position) != null)
                    ((MainActivity)getActivity()).onStatusClicked(statusListAdapter.getStatusText(position)); //TODO: Add string to onStatusClicked for the status info found
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

    private class PopulateList extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Serializer serializer = new Persister();
            try {
                ServiceStatusFragment.this.serviceStatus = serializer.read(ServiceStatus.class, getActivity().getResources().openRawResource(R.raw.servicestatus));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            listView.setAdapter(new StatusListAdapter(rootView.getContext()));
        }
    }
}