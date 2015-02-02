package net.mastrgamr.mtapulse;

/**
 * Project: MTA Pulse
 * Author: Stuart Smith
 * Date: 1/24/15
 */

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import net.mastrgamr.mtapulse.gtfs_static.Routes;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

//Will contain the schedule information of the MTA system.
public class TripFragment extends Fragment implements OnMapReadyCallback
{
    private final String LOG_TAG = getTag();
    private final int CSV_HEADER = 1;

    private GoogleMap gMap;
    private MapView mapView;
    private GridView subwayList;

    private Routes routes;
    private ArrayList<Routes> routesList;
    private ArrayList<String> routeIds;

    public TripFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(getActivity());

        routesList = new ArrayList<>();
        routeIds = new ArrayList<>();

        InputStream input = getActivity().getResources().openRawResource(R.raw.routes);
        BufferedReader rbr = new BufferedReader(new InputStreamReader(input));

        try {
            CSVParser csvParser = CSVFormat.DEFAULT.parse(rbr);
            for(CSVRecord record : csvParser){
                if(csvParser.getCurrentLineNumber() == CSV_HEADER)
                    continue; //Skip header in gtfs csv files
                if(record.size() < 9) {
                    routes = new Routes(record.get(0), record.get(3), record.get(4), record.get(6), record.get(7));
                } else {
                    routes = new Routes(record.get(0), record.get(3), record.get(4), record.get(6), record.get(7), record.get(8));
                }

                routesList.add(routes);
                routeIds.add(routes.getRouteId());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_trip, container, false);

        mapView = (MapView)rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);//TODO: Potential bug, calling OnCreate in OnCreateView?
        mapView.getMapAsync(this);

        subwayList = (GridView)rootView.findViewById(R.id.subwayList);
        ArrayAdapter routesAdapter =
                new ArrayAdapter<>(rootView.getContext(),
                        android.R.layout.simple_list_item_1,
                        routeIds);
        subwayList.setAdapter(routesAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        this.gMap = gMap;
        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(true);
        gMap.getUiSettings().setZoomControlsEnabled(true);
        //Set initial city-wide view
        LatLngBounds NYC = new LatLngBounds(new LatLng(40.50, -74.30), new LatLng(40.92, -73.57));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NYC.getCenter(), 10f));
    }
}
