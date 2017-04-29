package compsci290.duke.edu.memorymap.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import compsci290.duke.edu.memorymap.memory.MarkerTag;
import compsci290.duke.edu.memorymap.R;
import compsci290.duke.edu.memorymap.firebase.database.FirebaseDatabaseHandler;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        //GoogleMap.OnMapLongClickListener,
        GoogleMap.InfoWindowAdapter
        //GoogleMap.OnInfoWindowLongClickListener
        {

    protected GoogleMap mMap;
    protected static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    protected static final int CREATE_MEMORY = 1;
    protected static final int EDIT_MEMORY = 2;
    protected static final String MARKERTAG = "markertag";
    protected static final float ZOOM = 13;
    protected Location userCurrLocation = null;
    protected LatLng userCurrLatLng = null;

    protected GoogleApiClient mGoogleApiClient;
    private static final String TAG = "MapsActivity";

    //MarkerTagDbHandler mDbHandler = new MarkerTagDbHandler();
    protected FirebaseDatabaseHandler mDbHandler;
    protected static List<MarkerTag> mTagList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate ...............................");
        setContentView(R.layout.activity_maps);
        mDbHandler = new FirebaseDatabaseHandler();

        //TODO: saveInstanceState (need to save current camera position)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initializing mGoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }


        // Set up UI
        UiSettings settings = mMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        settings.setMyLocationButtonEnabled(false);//TODO: make so this is true AND it works :(

        // Set up listeners and adapters
        //mMap.setOnMapLongClickListener(this);
        mMap.setInfoWindowAdapter(this);
        //mMap.setOnInfoWindowLongClickListener(this);

        //restore all markers
        mTagList = mDbHandler.queryAllMarkerTags();
        for(MarkerTag tag : mTagList){
            addMarkerFromTag(tag);
        }
    }

    /**
     * Creates the options menu
     *
     * @param  menu the latlng to include in the intent
     * @return      true on successful handling
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    /**
     * Describes on how to handle each menu item being clicked.
     *
     * @param  item the clicked menu item
     * @return      true on successful handling
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.menu_get_current_location:
                getCurrentLocation();
                if(userCurrLatLng != null){
                    Log.d(TAG,"moving camera to user current location (by menu seleciton)");
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userCurrLatLng,ZOOM));
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Placeholder function in order to use the "getInfoContents" method,
     * as it only gets called when this function returns null.
     *
     * @param  m    the marker about which the InfoWindow is concerned
     * @return      returns the InfoWindow view
     */
    @Override
    public View getInfoWindow(Marker m) {
        return null;
    }

    /**
     * Defines the content of the InfoWindow
     *
     * @param  m    the marker about which the InfoWindow contents are concerned
     * @return      returns the InfoWindow view
     */
    @Override
    public View getInfoContents(Marker m) {

        // Getting view from the layout file info_window_layout
        View v = getLayoutInflater().inflate(R.layout.infowindowlayout, null);
        MarkerTag markerTag = (MarkerTag) m.getTag();

        if(markerTag == null){ //use default window if markerTag is null
            return null;
        }
        TextView titleView = (TextView) v.findViewById(R.id.infowin_title);
        titleView.setText(markerTag.getTitle());
        TextView dateView = (TextView) v.findViewById(R.id.infowin_date);
        dateView.setText(markerTag.getDate());
        TextView detailsView = (TextView) v.findViewById(R.id.infowin_details);
        detailsView.setText(markerTag.getDetails());

        return v;
    }



    /**
     * Attempts to update the variables userCurrLocation and userCurrLatLng
     * by using LocationServices (which requires the Google API client)
     */
    protected void getCurrentLocation() {
        Log.d(TAG,"Attempting to get current location");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
        mMap.setMyLocationEnabled(true);
        userCurrLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (userCurrLocation != null) {
            Log.d(TAG,"Got current location successfully");
            userCurrLatLng = new LatLng(userCurrLocation.getLatitude(),userCurrLocation.getLongitude());
        }else{
            Log.d(TAG,"userCurrLocation == null :(");
        }
    }



    /**
     * Occurs when the the GoogleApiClient becomes connected
     *
     * @param  bundle   bundle passed, not used
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
        Log.d(TAG,"moving camera to user current location");
        if(userCurrLatLng != null){
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userCurrLatLng,ZOOM));
        }
    }

    /**
     * Placeholder function for when connection is suspended
     */
    @Override
    public void onConnectionSuspended(int i) {
    }

    /**
     * Logs if the connection fails
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG,"Google API Client connection failed.");
    }

    /**
     * Connects the GoogleApiClient when the activity is started
     */
    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    /**
     * Disconnects the GoogleApiClient when the activity is stopped
     */
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Adds a marker to the mMap from MarkerTag's information
     *
     * @param  markerTag    MarkerTag object containing all info needed
     *                      to create a marker
     */
    protected void addMarkerFromTag(MarkerTag markerTag){
        Bitmap img = markerTag.getImg();
        Double latitude = markerTag.getLatitude();
        Double longitude = markerTag.getLongitude();
        LatLng latLng = new LatLng(latitude,longitude);
        Marker marker;
        if(img != null){
            marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(img)));
            marker.setTag(markerTag);
        }
        else{
            marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng));
            marker.setTag(markerTag);
        }
    }
}