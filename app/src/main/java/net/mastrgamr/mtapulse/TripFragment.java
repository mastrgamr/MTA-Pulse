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

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import net.mastrgamr.mtapulse.gtfs_static.Routes;
import net.mastrgamr.mtapulse.gtfs_static.Shapes;
import net.mastrgamr.mtapulse.gtfs_static.Stops;
import net.mastrgamr.mtapulse.tools.HeaderGridView;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import it.carlom.stikkyheader.core.StikkyHeaderBuilder;

//Will contain the schedule information of the MTA system.
public class TripFragment extends Fragment implements OnMapReadyCallback
{
    private final String LOG_TAG = getTag();
    private final int CSV_HEADER = 1;

    private GoogleMap gMap;
    private MapView mapView;
    private HeaderGridView subwayList;
    FloatingActionButton fab;

    private Routes routes;
    private Shapes shapes;
    private Stops stops;

    private ArrayList<Routes> routesList;
    private ArrayList<String> routeIds;
    private ArrayList<Shapes> shapesList;
    private ArrayList<Stops> stopsList;

    PolylineOptions shapesOptions = new PolylineOptions();

    public TripFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(getActivity());

        routesList = new ArrayList<>();
        routeIds = new ArrayList<>();
        shapesList = new ArrayList<>();

        InputStream input = getActivity().getResources().openRawResource(R.raw.routes);
        BufferedReader rbr = new BufferedReader(new InputStreamReader(input));

        InputStream sInput = getActivity().getResources().openRawResource(R.raw.shapesex);
        BufferedReader sbr = new BufferedReader(new InputStreamReader(sInput));

        InputStream stInput = getActivity().getResources().openRawResource(R.raw.stops);
        BufferedReader stbr = new BufferedReader(new InputStreamReader(stInput));

        try {
            CSVParser csvParser = new CSVParser(rbr,
                    CSVFormat.DEFAULT.withHeader("route_id" , "agency_id", "route_short_name", "route_long_name",
                            "route_desc", "route_type", "route_url", "route_color", "route_text_color"));

            for(CSVRecord record : csvParser){
//                if(csvParser.getCurrentLineNumber() == CSV_HEADER)
//                    continue; //Skip header in gtfs csv files
                if(record.size() < 9) {
                    routes = new Routes(record.get(0), record.get(3), record.get(4), record.get(6), record.get(7));
                } else {
                    routes = new Routes(record.get(0), record.get(3), record.get(4), record.get(6), record.get(7), record.get(8));
                }

                routesList.add(routes);
                routeIds.add(routes.getRouteId());
            }

            csvParser = new CSVParser(sbr,
                    CSVFormat.DEFAULT.withHeader("shape_id", "shape_pt_lat", "shape_pt_lon", "shape_pt_sequence", "shape_dist_traveled"));
            for(CSVRecord record : csvParser){
//                if(csvParser.getCurrentLineNumber() == CSV_HEADER)
//                    continue; //Skip header in gtfs csv files
                shapes = new Shapes(record.get(0), record.get(1), record.get(2), record.get(3));

                shapesOptions.add(new LatLng(Double.parseDouble(shapes.getShapePtLat()), Double.parseDouble(shapes.getShapePtLon())));
                shapesList.add(shapes);
            }

            csvParser = new CSVParser(stbr,
                    CSVFormat.DEFAULT.withHeader("stop_id", "stop_code", "stop_name", "stop_desc", "stop_lat", "stop_lon", "zone_id", "stop_url", "location_type", "parent_station"));
            for(CSVRecord record : csvParser){
                stops = new Stops(record.get("stop_id"), record.get("stop_name"), record.get("stop_lat"), record.get("stop_lon"));

                stopsList.add(stops);
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

        ArrayAdapter routesAdapter =
                new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_list_item_1,
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
        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(true);
        gMap.getUiSettings().setZoomControlsEnabled(true);
        //Set initial city-wide view
        LatLngBounds NYC = new LatLngBounds(new LatLng(40.50, -74.30), new LatLng(40.92, -73.57));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NYC.getCenter(), 10f));

        gMap.addPolyline(shapesOptions);
    }
}
