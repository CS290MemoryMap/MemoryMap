package compsci290.duke.edu.memorymap;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import compsci290.duke.edu.memorymap.database.MarkerTagDbHandler;
import compsci290.duke.edu.memorymap.firebase.database.FirebaseDatabaseHandler;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.InfoWindowAdapter,
        GoogleMap.OnInfoWindowLongClickListener{

    private GoogleMap mMap;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int CREATE_MEMORY = 1;
    private static final int EDIT_MEMORY = 2;
    private static final String DATE = "date";
    private static final String DETAILS = "details";
    private static final String TITLE = "title";
    private static final String BITMAP = "bitmap";
    private static final String LATLNG = "latlng";
    private static final String ISPUBLIC = "ispublic";
    private static final String MARKERTAG = "markertag";
    private static final float ZOOM = 13;
    private Location userCurrLocation = null;
    private LatLng userCurrLatLng = null;
    private String userInputAddress = "";
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "MapsActivity";

    private LatLng mNewMarkerLatLng;
    private boolean mSeeNewMarker = false;


    //MarkerTagDbHandler mDbHandler = new MarkerTagDbHandler();
    FirebaseDatabaseHandler mDbHandler = new FirebaseDatabaseHandler();
    private static List<MarkerTag> mTagList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate ...............................");
        setContentView(R.layout.activity_maps);

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

        // Set up UI
        UiSettings settings = mMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        settings.setMyLocationButtonEnabled(false);//TODO: make so this is true AND it works :(

        // Set up listeners and adapters
        mMap.setOnMapLongClickListener(this);
        mMap.setInfoWindowAdapter(this);
        mMap.setOnInfoWindowLongClickListener(this);

        //restore all markers
        mTagList = mDbHandler.queryAllMarkerTags();
        for(MarkerTag tag : mTagList){
            addMarkerFromTag(tag);
        }

    }

    /* Activated when the user long clicks on the map.
     * Asks the user if they would like to create a memory at the clicked location.
     * Upon answering yes, MemoryActivity is started
     *
     * @param  latLng  LatLng of clicked location
     */
    @Override
    public void onMapLongClick(final LatLng latLng) {
        new AlertDialog.Builder(new ContextThemeWrapper(MapsActivity.this, R.style.myDialog))
                .setMessage("Create a memory at this location?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = createIntentWithLatLng(latLng,MemoryActivity.class);
                        startActivityForResult(intent, CREATE_MEMORY);
                    }
                })
                .setNegativeButton("No", null)
                .show();
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
            case R.id.menu_search_address:
                askAddressCreateMemory();
                return true;
            case R.id.menu_create_memory_at_location:
                getCurrentLocation();
                if(userCurrLatLng == null){
                    Toast.makeText(getApplicationContext(),
                            "Current location not found.",
                            Toast.LENGTH_LONG).show();
                }
                else{
                    Intent intent = createIntentWithLatLng(userCurrLatLng,MemoryActivity.class);
                    startActivityForResult(intent, CREATE_MEMORY);
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
     * Describes on how to handle the infowindow being long clicked.
     * If the InfoWindow is long clicked, a dialog appears offering
     * the user the options to delete or edit the marker.
     *
     * @param  marker   the marker whose InfoWindow was clicked
     */
    @Override
    public void onInfoWindowLongClick(final Marker marker) {
        new AlertDialog.Builder(new ContextThemeWrapper(MapsActivity.this, R.style.myDialog))
                .setMessage("Delete/edit this memory?")
                .setCancelable(true)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MarkerTag markerTag = (MarkerTag) marker.getTag();
                        mDbHandler.deleteMarkerTag(markerTag);
                        marker.remove();
                    }
                })
                .setNeutralButton("Edit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MarkerTag markerTag = (MarkerTag) marker.getTag();
                        Intent intent = new Intent(MapsActivity.this, MemoryActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(MARKERTAG, markerTag);
                        intent.putExtras(bundle);
                        marker.remove(); //remove marker to be edited, will replace when returns
                        startActivityForResult(intent, EDIT_MEMORY);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Handles the result from an activity.
     * Currently defined only for MemoryActivity passing a result
     *
     * @param  requestCode  code identifying the previous activity
     * @param  resultCode   describes if the previous activity was a success
     * @param  data         intent from the previous activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == CREATE_MEMORY) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user successfully created a memory
                final Bundle extras = data.getExtras();
                if (extras != null) {
                    //mNewMarkerLatLng = extras.getParcelable(LATLNG);
                    addMarkerFromBundle(extras);
                    Log.d(TAG,"moving camera to new marker location");
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mNewMarkerLatLng,ZOOM));
                    mSeeNewMarker = true;
                }else{
                    Toast.makeText(this,"Memory not successfully created.", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this,"Memory not successfully created.", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == EDIT_MEMORY){
            if(resultCode == RESULT_OK){
                final Bundle extras = data.getExtras();
                if(extras != null){
                    MarkerTag markerTag = extras.getParcelable(MARKERTAG);
                    mDbHandler.updateMarkerTag(markerTag);
                    for(MarkerTag tag : mTagList){
                        if(tag.getID() == markerTag.getID()){
                            mTagList.remove(tag);
                            mTagList.add(markerTag);
                        }
                    }
                    Marker newMarker;
                    if(markerTag.getImg() == null){
                        newMarker = mMap.addMarker(new MarkerOptions()
                                .position(mNewMarkerLatLng));
                        newMarker.setTag(markerTag);

                    }else{
                        newMarker = mMap.addMarker(new MarkerOptions()
                                .position(mNewMarkerLatLng)
                                .icon(BitmapDescriptorFactory.fromBitmap(markerTag.getImg())));
                        newMarker.setTag(markerTag);
                    }
                }else{
                    Log.d(TAG, "Result from EDIT_MEMORY extras == null");
                }
            }else{
                Toast.makeText(this,"Memory not successfully edited.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Adds a marker to mMap based on the bundle passed to it
     *
     * @param  extras The bundle containing marker information
     */
    private void addMarkerFromBundle(Bundle extras) {
        MarkerTag markerTag = extras.getParcelable(MARKERTAG);
        if(markerTag != null){
            mNewMarkerLatLng = new LatLng(markerTag.getLatitude(),markerTag.getLongitude());
            Marker newMarker;
            if(markerTag.getImg() == null){
                newMarker = mMap.addMarker(new MarkerOptions()
                        .position(mNewMarkerLatLng));
                newMarker.setTag(markerTag);

            }else{
                newMarker = mMap.addMarker(new MarkerOptions()
                        .position(mNewMarkerLatLng)
                        .icon(BitmapDescriptorFactory.fromBitmap(markerTag.getImg())));
                newMarker.setTag(markerTag);
            }
            markerTag = mDbHandler.insertMarkerTag(markerTag);
            mTagList.add(markerTag);
        }else{
            Log.d(TAG,"MarkerTag from previous MemoryActivity is null. Not adding marker.");
        }
    }

    /**
     * Attempts to update the variables userCurrLocation and userCurrLatLng
     * by using LocationServices (which requires the Google API client)
     */
    private void getCurrentLocation() {
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
     * Returns a LatLng describing the input String address, or null if the address
     * can't be translated. Uses Google's Geocoder.
     *
     * @param  address  String address to be convered to a LatLng
     * @return      the LatLng of the input address
     */
    private LatLng addressToLatLng(String address){
        Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
        List<Address> addresses;
        Address address1;
        LatLng latLng;
        try {
            addresses = geo.getFromLocationName(address,1);
            address1 = addresses.get(0);
            latLng = new LatLng(address1.getLatitude(),address1.getLongitude());
        }catch (Exception e){//TODO: make this more specific
            e.printStackTrace();
            latLng = null;
        }
        return latLng;
    }

    /**
     * Returns an intent to a specified class that has a bundle with a MarkerTag
     * parcelable object already inside it
     *
     * @param  latlng  the latlng to include in the intent
     * @param  toClass the class to which the intent will be passed
     * @return      the intent
     */
    private Intent createIntentWithLatLng(LatLng latlng, Class<?> toClass){
        Intent intent = new Intent(MapsActivity.this,toClass);
        Bundle bundle = new Bundle();
        MarkerTag mTag = new MarkerTag();
        mTag.setLatitude(latlng.latitude);
        mTag.setLongitude(latlng.longitude);
        bundle.putParcelable(MARKERTAG, mTag);
        intent.putExtras(bundle);
        return intent;
    }

    /**
     * Creates a dialog that asks the user to input an address. Attempts to convert
     * the address to a LatLng and then starts MemoryActivity with that information.
     * Upon failure to convert the address, this method makes a toast to alert the user
     * that their address could not be converted.
     */
    private void askAddressCreateMemory(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Address");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT );
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userInputAddress = input.getText().toString();
                LatLng latLng = addressToLatLng(userInputAddress);
                if(latLng != null){
                    Intent intent = createIntentWithLatLng(latLng,MemoryActivity.class);
                    startActivityForResult(intent, CREATE_MEMORY);
                }else{
                    Log.d(TAG,"failed to create memory with user input address");
                    Toast.makeText(MapsActivity.this,"Failed to find location of address.",Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    /**
     * Occurs when the the GoogleApiClient becomes connected
     *
     * @param  bundle   bundle passed, not used
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
        if(mSeeNewMarker && (mNewMarkerLatLng != null)){
            Log.d(TAG,"moving camera to user's new marker");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mNewMarkerLatLng,ZOOM));
            mSeeNewMarker = false;
        }else{
            Log.d(TAG,"moving camera to user current location");
            if(userCurrLatLng != null){
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userCurrLatLng,ZOOM));
            }
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
    void addMarkerFromTag(MarkerTag markerTag){
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