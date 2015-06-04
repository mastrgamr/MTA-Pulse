package net.mastrgamr.transitpulse;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.mastrgamr.transitpulse.interfaces.LineMaps;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ServiceStatusInfoFragment.OnStatusClickedListener} interface
 * to handle interaction events.
 * Use the {@link ServiceStatusInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceStatusInfoFragment extends Fragment {
    private static final String STATUS_TEXT = "statusText";

    private String serviceStatusText;
    private SpannableStringBuilder ssb;
    private TextView statusInfo;
    private SpanString spanStatus;

    private View adaFrag;

    private OnStatusClickedListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param text Text received from ServiceStatus.
     * @return A new instance of fragment ServiceStatusInfoFragment.
     */
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
        statusInfo = (TextView) v.findViewById(R.id.statusText);
        spanStatus = new SpanString(statusInfo.getLineHeight());
        spanStatus.execute(serviceStatusText);
        adaFrag = v.findViewById(R.id.include);
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
        void onStatusClicked(String string);
    }

    private class SpanString extends AsyncTask<String, Void, SpannableStringBuilder> {
        int lineHeight;
        boolean hideada = true;

        public SpanString(int lineHeight) {
            this.lineHeight = lineHeight;
        }

        @Override
        protected SpannableStringBuilder doInBackground(String... s) {
            ssb = new SpannableStringBuilder(s[0]);
            int subIconLen = 3;

            Drawable subIcon;
            Bitmap b;

            for(int i = 0; i < ssb.length(); i++) {
                if(ssb.charAt(i) == '[') {
                    if(LineMaps.subwayLines.containsKey(String.valueOf(ssb.charAt(i+1)))){
                        subIcon = getActivity().getResources().getDrawable(LineMaps.subwayLines.get(String.valueOf(ssb.charAt(i+1))));
                        b = Bitmap.createScaledBitmap(((BitmapDrawable) subIcon).getBitmap(), lineHeight, lineHeight, false);
                        ssb.setSpan(new ImageSpan(getActivity(), b), i, i + subIconLen, Spanned.SPAN_INTERMEDIATE);
                        i += subIconLen - 1;
                    }
                }
                if(ssb.charAt(i) == '[' && ssb.charAt(i+1) == 'a') {
                    ssb.replace(i, ssb.length() - 1, "");
                    //ssb.setSpan("", i, i + 50, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    hideada = false;
                }
            }
            return ssb;
        }

        @Override
        protected void onPostExecute(SpannableStringBuilder s) {
            super.onPostExecute(s);

            statusInfo.setText(s);
            if(!hideada)
                adaFrag.setVisibility(View.GONE);
        }
    }
}
