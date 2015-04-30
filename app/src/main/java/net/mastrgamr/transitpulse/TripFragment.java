package net.mastrgamr.transitpulse;

/**
 * Project: MTA Pulse
 * Author: Stuart Smith
 * Date: 1/24/15
 */

import android.app.Fragment;
import android.app.ProgressDialog;
import android.location.Location;
import android.os.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import net.mastrgamr.transitpulse.gtfs_realtime.RtGtfsParser;
import net.mastrgamr.transitpulse.gtfs_static.Routes;
import net.mastrgamr.transitpulse.gtfs_static.Shapes;
import net.mastrgamr.transitpulse.gtfs_static.Stops;
import net.mastrgamr.transitpulse.tools.DataMaps;
import net.mastrgamr.transitpulse.tools.HeaderGridView;
import net.mastrgamr.transitpulse.tools.StikkyHeaderBuilderEx;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

//Will contain the schedule information of the MTA system.
public class TripFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationChangeListener
{
    private final String LOG_TAG = getClass().getSimpleName();
    private final int CSV_HEADER = 1;

    private GoogleMap gMap;
    private boolean track = true;
    private boolean initialLoad = true;
    private Location loadedLoc;
    private MapView mapView;
    private MapFragment mapFragment;
    private HeaderGridView subwayList;

    private Routes routes;
    private Shapes shapes;
    private Stops stops;

    private ArrayList<Routes> routesList;
    private ArrayList<Shapes> shapesList;
    private ArrayList<Stops> stopsList;

    private RtGtfsParser gtfsParser;
    private TripListAdapter tripListAdapter;

    private DataGenerator dataGen;
    private DataMaps<Stops> stopsDataMap;
    private DataMaps<Routes> routesDataMap;

    PolylineOptions shapesOptions = new PolylineOptions();

    public TripFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapsInitializer.initialize(getActivity());

        //routesList = new ArrayList<>();
        shapesList = new ArrayList<>();
        //stopsList = new ArrayList<>();

        gtfsParser = new RtGtfsParser(getActivity());

        stopsDataMap = new DataMaps<>(getActivity());
        routesDataMap = new DataMaps<>(getActivity());

        //dataGen = new DataGenerator();
        //dataGen.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_trip, container, false);

        /*try {
            dataGen.get(); //wait for data to generate
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/

        mapView = (MapView)rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);//TODO: Potential bug? calling OnCreate inside OnCreateView
        mapView.getMapAsync(this);

        subwayList = (HeaderGridView)rootView.findViewById(R.id.subwayList);

        subwayList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(tripListAdapter != null) {
                    if(!tripListAdapter.isStationSelected()) {
                        tripListAdapter.setStationSelected(true, position);
                        subwayList.setNumColumns(2);
                    }
                }
            }
        });
        subwayList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(tripListAdapter != null) {
                    tripListAdapter.setUpDownFlip();
                }
                return true;
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*if(gMap == null) {
            mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMap();//.getMapAsync(this);
        }*/

        StikkyHeaderBuilderEx.stickTo(subwayList)
                .setHeader(R.id.mapLayout, (ViewGroup)getView())
                .build();
        //subwayList.setAdapter(tripListAdapter);
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
        //TODO:If location is significantly further away from loaded location, Generate new data.
        //40.800148, -73.945238 randomLoc for test
        if(track && location != null) {
            //Toast.makeText(this.getActivity(), "Map Loaded", Toast.LENGTH_SHORT).show();
            LatLng loc = new LatLng(location.getLatitude(),
                    location.getLongitude());
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15f));

            if(initialLoad) {
                initialLoad = false;
                loadedLoc = location;
                dataGen = new DataGenerator();
                dataGen.execute();
            }

            //TODO: Set up efficient way to track location
            //tripListAdapter = new TripListAdapter(getActivity(), gtfsParser.getStopsByLocation(location, stopsDataMap));
            //tripListAdapter = new TripListAdapter(getActivity(), gtfsParser.getStopsByLocationList(location, stopsDataMap));
            //subwayList.setAdapter(tripListAdapter);

            /*Location mylocation = new Location("View Center"); //DEBUG STUFF!!
            mylocation.setLatitude(40.800148);
            mylocation.setLongitude(-73.945238);

            tripListAdapter = new TripListAdapter(getActivity(), gtfsParser.getStopsByLocation(mylocation, stopsDataMap));
            subwayList.setAdapter(tripListAdapter);*/
            track = false;
        }
    }

    private class DataGenerator extends AsyncTask<Void, Void, Void>{
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("\tGenerating Data.");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            long before = System.currentTimeMillis();
            gtfsParser.refreshFeed();

            if (!stopsDataMap.doesMapExist(Stops.class) || !routesDataMap.doesMapExist(Routes.class)) {
                InputStream input = getActivity().getResources().openRawResource(R.raw.routes);
                BufferedReader br = new BufferedReader(new InputStreamReader(input));

                try {
                    Log.d(LOG_TAG, "Generating HashMaps");
                    CSVParser csvParser = new CSVParser(br,
                            CSVFormat.DEFAULT.withHeader("route_id", "agency_id", "route_short_name", "route_long_name",
                                    "route_desc", "route_type", "route_url", "route_color", "route_text_color"));

                    for (CSVRecord record : csvParser) {
                        if (csvParser.getCurrentLineNumber() == CSV_HEADER)
                            continue; //Skip header in gtfs csv files
                        if (record.size() < 9) {
                            routes = new Routes(record.get(0), record.get(2), record.get(3), record.get(4), record.get(6), record.get(7));
                        } else {
                            routes = new Routes(record.get(0), record.get(2), record.get(3), record.get(4), record.get(6), record.get(7), record.get(8));
                        }

                        //routesList.add(routes);

                        routesDataMap.put(routes.getRouteId(), routes);
                    }

                    input = getActivity().getResources().openRawResource(R.raw.shapesex);
                    br = new BufferedReader(new InputStreamReader(input));

                    csvParser = new CSVParser(br,
                            CSVFormat.DEFAULT.withHeader("shape_id", "shape_pt_lat", "shape_pt_lon", "shape_pt_sequence", "shape_dist_traveled"));
                    for (CSVRecord record : csvParser) {
                        if (csvParser.getCurrentLineNumber() == CSV_HEADER)
                            continue; //Skip header in gtfs csv files
                        shapes = new Shapes(record.get("shape_id"), record.get("shape_pt_lat"), record.get("shape_pt_lon"), record.get("shape_pt_sequence"));

                        shapesOptions.add(new LatLng(Double.parseDouble(shapes.getShapePtLat()), Double.parseDouble(shapes.getShapePtLon())));
                        shapesList.add(shapes);
                    }

                    input = getActivity().getResources().openRawResource(R.raw.stops);
                    br = new BufferedReader(new InputStreamReader(input));

                    csvParser = new CSVParser(br,
                            CSVFormat.DEFAULT.withHeader("stop_id", "stop_code", "stop_name", "stop_desc", "stop_lat", "stop_lon", "zone_id", "stop_url", "location_type", "parent_station"));
                    for (CSVRecord record : csvParser) {
                        if (csvParser.getCurrentLineNumber() == CSV_HEADER)
                            continue; //Skip header in gtfs csv files
                        stops = new Stops(record.get("stop_id"), record.get("stop_name"), record.get("stop_lat"), record.get("stop_lon"));

                        //stopsList.add(stops);

                        stopsDataMap.put(stops.getStopId(), stops);
                    }


                    Log.d(LOG_TAG, "Finished Generating HashMaps");
                } catch (IOException e) {
                    Log.e(LOG_TAG, e.getMessage());
                }

                Log.d(LOG_TAG, "Serializing HashMaps");
                stopsDataMap.serialize(Stops.class);
                routesDataMap.serialize(Routes.class);
                Log.d(LOG_TAG, "Finished Serializing HashMaps");

                //tripListAdapter = new TripListAdapter(getActivity(), gtfsParser.getStopsByLocationList(loadedLoc, stopsDataMap));
                tripListAdapter = new TripListAdapter(getActivity(), gtfsParser.getStopsByLocationFeed(loadedLoc, stopsDataMap));
            } else {
                Log.d(LOG_TAG, "Using cached Datamap for TripAdapter");
                //tripListAdapter = new TripListAdapter(getActivity(), gtfsParser.getStopsByLocationList(loadedLoc, stopsDataMap.getMap(Stops.class)));
                tripListAdapter = new TripListAdapter(getActivity(), gtfsParser.getStopsByLocationFeed(loadedLoc, stopsDataMap.getMap(Stops.class)));
            }

            Log.d(LOG_TAG, System.currentTimeMillis() - before + " TOTAL TIME TO OUTPUT TRIPADAPTER!!");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            initialLoad = true;

            subwayList.setNumColumns(1);
            subwayList.setAdapter(tripListAdapter);

            pd.dismiss();
        }
    }
}
