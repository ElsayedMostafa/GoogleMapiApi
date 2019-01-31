package com.example.kdeuser.testproject;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.kdeuser.testproject.models.ClusterMarker;
import com.example.kdeuser.testproject.models.PolyLineData;
import com.example.kdeuser.testproject.util.MyClusterManagerRender;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;


import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnPolylineClickListener {
    private static final String TAG = "MapActivity";
    private MapView mapView;
    private GoogleMap mMap;
    private LatLngBounds mMapBoundary;
    private Location mUsercurrentLocation;
    private ClusterManager mClusterManager;
    private MyClusterManagerRender mClusterManagerRender;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<ClusterMarker>();
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private static final int LOCATION_UPDATE_INTERVAL = 3000;
    private GeoApiContext mGeoApiContext = null;
    private ArrayList<PolyLineData> mPolyLineData = new ArrayList<PolyLineData>();
    private Marker mSelectedMarker = null;
    private ArrayList<Marker> mTripMarkers = new ArrayList<Marker>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mapView = findViewById(R.id.map_view);
        initGoogleMap(savedInstanceState);

    }

    private void removeTripMarkers() {
        for (Marker marker : mTripMarkers) {
            marker.remove();
        }
    }

    private void resetSelectedMarker() {
        if (mSelectedMarker != null) {
            mSelectedMarker.setVisible(true);
            mSelectedMarker = null;
            removeTripMarkers();
        }
    }

    private void initGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_key))
                    .build();
        }

    }

    private void addMapMarkers() {
        if (mMap != null) {
            if (mClusterManager == null) {
                mClusterManager = new ClusterManager<ClusterMarker>(this.getApplicationContext(), mMap);
            }
            if (mClusterManagerRender == null) {
                mClusterManagerRender = new MyClusterManagerRender(
                        this,
                        mMap,
                        mClusterManager);
                mClusterManager.setRenderer(mClusterManagerRender);
            }
        }
        try {
            int avater = R.drawable.technician_img;
            try {
                //avater = Integer.parseInt()
            } catch (NumberFormatException e) {

            }
            ClusterMarker newClusterMarker = new ClusterMarker(
                    new LatLng(40.7143528, -74.0059731), "title", "snippet", avater);
            ClusterMarker newClusterMarker2 = new ClusterMarker(
                    new LatLng(40.7243528, -74.0159731), "title", "snippet", avater);
            ClusterMarker newClusterMarker3 = new ClusterMarker(
                    new LatLng(40.7253528, -74.0158731), "title", "snippet", avater);

            mClusterManager.addItem(newClusterMarker);
            mClusterMarkers.add(newClusterMarker);
            mClusterManager.addItem(newClusterMarker2);
            mClusterMarkers.add(newClusterMarker2);
            mClusterManager.addItem(newClusterMarker3);
            mClusterMarkers.add(newClusterMarker3);
        } catch (NullPointerException e) {
            Log.e(TAG, "addMapMarkers: Null pointerException");
        }

        mClusterManager.cluster();


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng ny = new LatLng(40.7143528, -74.0059731);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(ny);
//        mMap.addMarker(new MarkerOptions().position(ny).title("marker"));
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
        addMapMarkers();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setOnPolylineClickListener(this);
        mMap.setMyLocationEnabled(true);
        double bottomBoundary = ny.latitude - .1;
        double leftBoundary = ny.longitude - .1;
        double topBoundary = ny.latitude + .1;
        double rightBoundary = ny.longitude + .1;
        mMapBoundary = new LatLngBounds(new LatLng(bottomBoundary, leftBoundary), new LatLng(topBoundary, rightBoundary));

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(ny));
        mMap.setOnInfoWindowClickListener(this);
    }

    private void setCameraView() {

    }

    private void setUserPostion() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        startUserLocationsRunnable();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
        stopLocationUpdates();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //update location
    private void startUserLocationsRunnable() {
        Log.d(TAG, "startUserLocationsRunnable: starting runnable for retrieving updated locations.");
        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                retrieveUserLocations();
                mHandler.postDelayed(mRunnable, LOCATION_UPDATE_INTERVAL);
            }
        }, LOCATION_UPDATE_INTERVAL);
    }

    private void stopLocationUpdates() {
        mHandler.removeCallbacks(mRunnable);
    }

    private void retrieveUserLocations() {
        Log.d(TAG, "retrieveUserLocations: retrieving location of all users in the chatroom.");

//        try{
//            for(final ClusterMarker clusterMarker: mClusterMarkers){
//
//                DocumentReference userLocationRef = FirebaseFirestore.getInstance()
//                        .collection(getString(R.string.collection_user_locations))
//                        .document(clusterMarker.getUser().getUser_id());
//
//                userLocationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if(task.isSuccessful()){
//
//                            final UserLocation updatedUserLocation = task.getResult().toObject(UserLocation.class);
//
//                            // update the location
//                            for (int i = 0; i < mClusterMarkers.size(); i++) {
//                                try {
//                                    if (mClusterMarkers.get(i).getUser().getUser_id().equals(updatedUserLocation.getUser().getUser_id())) {
//
//                                        LatLng updatedLatLng = new LatLng(
//                                                updatedUserLocation.getGeo_point().getLatitude(),
//                                                updatedUserLocation.getGeo_point().getLongitude()
//                                        );
//
//                                        mClusterMarkers.get(i).setPosition(updatedLatLng);
        //mClusterManagerRender.setUpdateMarker(mClusterMarkers.get(i));
//
//                                    }
//
//
//                                } catch (NullPointerException e) {
//                                    Log.e(TAG, "retrieveUserLocations: NullPointerException: " + e.getMessage());
//                                }
//                            }
//                        }
//                    }
//                });
//            }
//        }catch (IllegalStateException e){
//            Log.e(TAG, "retrieveUserLocations: Fragment was destroyed during Firestore query. Ending query." + e.getMessage() );
//        }

    }

    @Override
    public void onInfoWindowClick(final Marker marker) {
        if (marker.getTitle().contains("trip #")) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Open Google Maps?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            String latitude = String.valueOf(marker.getPosition().latitude);
                            String longitude = String.valueOf(marker.getPosition().longitude);
                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");

                            try {
                                if (mapIntent.resolveActivity(MapActivity.this.getPackageManager()) != null) {
                                    startActivity(mapIntent);
                                }
                            } catch (NullPointerException e) {
                                Log.e(TAG, "onClick: NullPointerException: Couldn't open map." + e.getMessage());
                                Toast.makeText(MapActivity.this, "Couldn't open map", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        } else {
            if (marker.getSnippet().equals("This is you")) {
                marker.hideInfoWindow();
            } else {

                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(marker.getSnippet())
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                resetSelectedMarker();
                                calculateDirections(marker);
                                mSelectedMarker = marker;
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                                dialog.cancel();
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }
        }

    }

    private void calculateDirections(Marker marker) {
        Log.d(TAG, "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(false);
        directions.origin(
                new com.google.maps.model.LatLng(
                        40.7143528, -74.0059731
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage());

            }
        });
    }

    private void addPolylinesToMap(final DirectionsResult result) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);

                if (mPolyLineData.size() > 0) {
                    for (PolyLineData polyLineData : mPolyLineData) {
                        polyLineData.getPolyline().remove();
                    }
                    mPolyLineData.clear();
                    mPolyLineData = new ArrayList<PolyLineData>();
                }

                for (DirectionsRoute route : result.routes) {
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for (com.google.maps.model.LatLng latLng : decodedPath) {

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(MapActivity.this, R.color.darkGrey));
                    polyline.setClickable(true);
                    mPolyLineData.add(new PolyLineData(polyline, route.legs[0]));
                    onPolylineClick(polyline);
                    zoomRoute(polyline.getPoints());
                    mSelectedMarker.setVisible(false);

                }
            }
        });
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        int index = 0;
        for (PolyLineData polylineData : mPolyLineData) {
            index++;
            Log.d(TAG, "onPolylineClick: toString: " + polylineData.toString());
            if (polyline.getId().equals(polylineData.getPolyline().getId())) {
                polylineData.getPolyline().setColor(ContextCompat.getColor(this, R.color.blue1));
                polylineData.getPolyline().setZIndex(1);
                LatLng endLocation = new LatLng(polylineData.getLeg().endLocation.lat,
                        polylineData.getLeg().endLocation.lng
                );
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(endLocation)
                        .title("trip # +index")
                        .snippet("Duration: " + polylineData.getLeg().duration)
                );
                marker.showInfoWindow();
                mTripMarkers.add(marker);
            } else {
                polylineData.getPolyline().setColor(ContextCompat.getColor(this, R.color.darkGrey));
                polylineData.getPolyline().setZIndex(0);
            }
        }
    }

    public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 120;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }

    private void resetMap() {
        if (mMap != null) {
            mMap.clear();

            if (mClusterManager != null) {
                mClusterManager.clearItems();
            }

            if (mClusterMarkers.size() > 0) {
                mClusterMarkers.clear();
                mClusterMarkers = new ArrayList<>();
            }

            if (mPolyLineData.size() > 0) {
                mPolyLineData.clear();
                mPolyLineData = new ArrayList<>();
            }
        }
    }
}
