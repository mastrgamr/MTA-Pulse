package net.mastrgamr.mtapulse;

/**
 * Project: MTA Pulse
 * Author: Stuart Smith
 * Date: 1/24/15
 */

import android.app.Fragment;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import net.mastrgamr.mtapulse.gtfs_static.Routes;
import net.mastrgamr.mtapulse.gtfs_static.Shapes;
import net.mastrgamr.mtapulse.gtfs_static.Stops;
import net.mastrgamr.mtapulse.tools.DataMaps;
import net.mastrgamr.mtapulse.tools.HeaderGridView;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;

import it.carlom.stikkyheader.core.StikkyHeaderBuilder;

//Will contain the schedule information of the MTA system.
public class TripFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationChangeListener
{
    private final String LOG_TAG = getTag();
    private final int CSV_HEADER = 1;

    private GoogleMap gMap;
    private boolean track = true;
    private MapView mapView;
    private HeaderGridView subwayList;

    private Routes routes;
    private Shapes shapes;
    private Stops stops;

    private ArrayList<Routes> routesList;
    private ArrayList<String> routeIds;
    private ArrayList<Shapes> shapesList;
    private ArrayList<Stops> stopsList;

    private DataGenerator dataGen;
    private DataMaps<Stops> stopsDataMap;

    PolylineOptions shapesOptions = new PolylineOptions();

    public TripFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(getActivity());

        routesList = new ArrayList<>();
        routeIds = new ArrayList<>();
        shapesList = new ArrayList<>();
        stopsList = new ArrayList<>();

        stopsDataMap = new DataMaps<>(getActivity());

        dataGen = new DataGenerator();
        dataGen.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_trip, container, false);

        mapView = (MapView)rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);//TODO: Potential bug? calling OnCreate inside OnCreateView
        mapView.getMapAsync(this);

        subwayList = (HeaderGridView)rootView.findViewById(R.id.subwayList);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        StikkyHeaderBuilder.stickTo(subwayList)
                .setHeader(R.id.mapLayout, (ViewGroup)getView())
                .build();

        //TODO: Create LiveTripListAdapter and set up this gridView
        ArrayAdapter routesAdapter =
                new ArrayAdapter<>(getActivity(),
                        R.layout.trip_grid_list_item,
                        R.id.route_text,
                        routeIds);
        subwayList.setAdapter(routesAdapter);
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
        this.gMap.setOnMyLocationChangeListener(this);
        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(true);
        gMap.getUiSettings().setZoomControlsEnabled(true);
        //Set initial city-wide view
        LatLngBounds NYC = new LatLngBounds(new LatLng(40.50, -74.30), new LatLng(40.92, -73.57));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NYC.getCenter(), 10f));

        Circle circle;
        gMap.addPolyline(shapesOptions);
    }

    @Override
    public void onMyLocationChange(Location location) {

        if(track && location != null) {
            //Toast.makeText(this.getActivity(), "Map Loaded", Toast.LENGTH_SHORT).show();
            LatLng loc = new LatLng(location.getLatitude(),
                    location.getLongitude());
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15f));
            track = false;

            Log.d(LOG_TAG, "DeSerializing HashMaps");
            Log.d(LOG_TAG, stopsDataMap.getMap(Stops.class).get("201").toString());
            Log.d(LOG_TAG, "Finished DeSerializing HashMaps");
        }
    }

    private class DataGenerator extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            InputStream input = getActivity().getResources().openRawResource(R.raw.routes);
            BufferedReader br = new BufferedReader(new InputStreamReader(input));

            try {
                Log.d(LOG_TAG, "Generating HashMaps");
                CSVParser csvParser = new CSVParser(br,
                        CSVFormat.DEFAULT.withHeader("route_id" , "agency_id", "route_short_name", "route_long_name",
                                "route_desc", "route_type", "route_url", "route_color", "route_text_color"));

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

                input = getActivity().getResources().openRawResource(R.raw.shapesex);
                br = new BufferedReader(new InputStreamReader(input));

                csvParser = new CSVParser(br,
                        CSVFormat.DEFAULT.withHeader("shape_id", "shape_pt_lat", "shape_pt_lon", "shape_pt_sequence", "shape_dist_traveled"));
                for(CSVRecord record : csvParser){
                    if(csvParser.getCurrentLineNumber() == CSV_HEADER)
                        continue; //Skip header in gtfs csv files
                    shapes = new Shapes(record.get("shape_id"), record.get("shape_pt_lat"), record.get("shape_pt_lon"), record.get("shape_pt_sequence"));

                    shapesOptions.add(new LatLng(Double.parseDouble(shapes.getShapePtLat()), Double.parseDouble(shapes.getShapePtLon())));
                    shapesList.add(shapes);
                }

                input = getActivity().getResources().openRawResource(R.raw.stops);
                br = new BufferedReader(new InputStreamReader(input));

                csvParser = new CSVParser(br,
                        CSVFormat.DEFAULT.withHeader("stop_id", "stop_code", "stop_name", "stop_desc", "stop_lat", "stop_lon", "zone_id", "stop_url", "location_type", "parent_station"));
                for(CSVRecord record : csvParser){
                    if(csvParser.getCurrentLineNumber() == CSV_HEADER)
                        continue; //Skip header in gtfs csv files
                    stops = new Stops(record.get("stop_id"), record.get("stop_name"), record.get("stop_lat"), record.get("stop_lon"));

                    stopsList.add(stops);

                    stopsDataMap.put(stops.getStopId(), new Stops(record.get("stop_name"), record.get("stop_lat"), record.get("stop_lon")));
                }

                Log.d(LOG_TAG, "Finished Generating HashMaps");

                Log.d(LOG_TAG, "Serializing HashMaps");
                stopsDataMap.serialize(Stops.class);
                Log.d(LOG_TAG, "Finished Serializing HashMaps");

            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
            return null;
        }
    }
}
