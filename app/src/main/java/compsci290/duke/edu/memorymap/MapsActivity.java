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

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.InfoWindowAdapter,
        GoogleMap.OnInfoWindowLongClickListener{

    private GoogleMap mMap;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int CREATE_MEMORY = 1;
    private static final String DATE = "date";
    private static final String DETAILS = "details";
    private static final String TITLE = "title";
    private static final String BITMAP = "bitmap";
    private static final String LATLNG = "latlng";
    private static final float ZOOM = 13;
    private Location userCurrLocation = null;
    private LatLng userCurrLatLng = null;
    private String userInputAddress = "";
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "MapsActivity";

    private LatLng mNewMarkerLatLng;
    private boolean mSeeNewMarker = false;

    MarkerTagDbHandler mDbHandler = new MarkerTagDbHandler();

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
        mMap.setOnMapClickListener(this);
        mMap.setInfoWindowAdapter(this);
        mMap.setOnInfoWindowLongClickListener(this);

        //restore all markers
        ArrayList<MarkerTag> tagList = mDbHandler.queryAllMarkerTags();
        for(MarkerTag tag : tagList){
            addMarkerFromTag(tag);
        }
    }

    @Override
    public void onMapClick(final LatLng latLng) {
        new AlertDialog.Builder(new ContextThemeWrapper(MapsActivity.this, R.style.myDialog))
                .setMessage("Create a memorylist at this location?")
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

    // Use default InfoWindow frame
    @Override
    public View getInfoWindow(Marker m) {
        return null;
    }

    // Defines the contents of the InfoWindow
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

    @Override
    public void onInfoWindowLongClick(final Marker marker) {
        new AlertDialog.Builder(new ContextThemeWrapper(MapsActivity.this, R.style.myDialog))
                .setMessage("Delete this memorylist")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MarkerTag markerTag = (MarkerTag) marker.getTag();
                        int count = mDbHandler.deleteMarkerTag(markerTag);
                        marker.remove();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == CREATE_MEMORY) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user successfully created a memorylist
                final Bundle extras = data.getExtras();
                if (extras != null) {
                    mNewMarkerLatLng = extras.getParcelable(LATLNG);
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
    }


    private void addMarkerFromBundle(Bundle extras) {
        MarkerTag markerTag =
                new MarkerTag(extras.getString(TITLE),
                        extras.getString(DATE),
                        extras.getString(DETAILS),
                        (Bitmap) extras.getParcelable(BITMAP),
                        mNewMarkerLatLng.latitude,
                        mNewMarkerLatLng.longitude);
        if(mNewMarkerLatLng != null){
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
            mDbHandler.insertMarkerTag(markerTag);
        }else{
            Log.d(TAG,"LatLng from previous MemoryActivity is null. Not adding marker.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
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
     * Returns an intent to a specified class that has a bundle with a LatLng
     * parcelable object already inside it
     *
     * @param  latlng  the latlng to include in the intent
     * @param  toClass the class to which the intent will be passed
     * @return      the intent
     */
    private Intent createIntentWithLatLng(LatLng latlng, Class<?> toClass){
        Intent intent = new Intent(MapsActivity.this,toClass);
        Bundle bundle = new Bundle();
        bundle.putParcelable(LATLNG, latlng);
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
                    Log.d(TAG,"failed to create memorylist with user input address");
                    Toast.makeText(MapsActivity.this,"Failed to find location of address.",Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

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

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG,"Google API Client connection failed.");
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

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