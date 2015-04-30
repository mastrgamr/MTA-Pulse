package net.mastrgamr.transitpulse;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ServiceStatusInfoFragment.OnStatusClickedListener} interface
 * to handle interaction events.
 * Use the {@link ServiceStatusInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceStatusInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String STATUS_TEXT = "statusText";

    // TODO: Rename and change types of parameters
    private String serviceStatusText;
    private TextView statusInfo;

    private OnStatusClickedListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param text Text received from ServiceStatus.
     * @return A new instance of fragment ServiceStatusInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ServiceStatusInfoFragment newInstance(String text) {
        ServiceStatusInfoFragment fragment = new ServiceStatusInfoFragment();
        Bundle args = new Bundle();
        args.putString(STATUS_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    public ServiceStatusInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            serviceStatusText = getArguments().getString(STATUS_TEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_service_status_info, container, false);
        //serviceStatusText = getArguments().getString(STATUS_TEXT);
        statusInfo = (TextView)v.findViewById(R.id.statusText);
        statusInfo.setText(serviceStatusText);

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnStatusClickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnStatusClickedListener {
        // TODO: Pass string of service change into this.
        public void onStatusClicked(String string);
    }

}
