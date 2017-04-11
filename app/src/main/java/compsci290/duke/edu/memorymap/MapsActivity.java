package compsci290.duke.edu.memorymap;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

import static android.location.LocationManager.GPS_PROVIDER;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback,
//        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

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
    private String toMemoryActivityLatLngStr;
    private GoogleApiClient mGoogleApiClient;
    private static final String TAG = "MapsActivity";



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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
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

    private void getCurrentLocation() {
        // TODO: make blue dot appear! :(
        Log.d(TAG,"Attempting to get current location");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
        userCurrLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (userCurrLocation != null) {
            Log.d(TAG,"Got current location successfully");
            //moving the map to location
            userCurrLatLng = new LatLng(userCurrLocation.getLatitude(),userCurrLocation.getLongitude());
            Log.d(TAG,"moving camera to user current location");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userCurrLatLng,ZOOM));
        }else{
            Log.d(TAG,"userCurrLocation == null :(");
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //getCurrentLocation();

        // Set up on click listener. If clicked, make a marker
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                toMemoryActivityLatLngStr = latLng.toString();
                new AlertDialog.Builder(new ContextThemeWrapper(MapsActivity.this, R.style.myDialog))
                        .setMessage("Create a memory at this location?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = createIntentWithLatLng(toMemoryActivityLatLngStr,MemoryActivity.class);
                                startActivityForResult(intent, CREATE_MEMORY);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        System.out.println("returning to onActivityResult");
        if (requestCode == CREATE_MEMORY) {
            // Make sure the request was successful
            System.out.println("request code is CREATE_MEMORY");
            if (resultCode == RESULT_OK) {
                // The user successfully created a memory
                final Bundle extras = data.getExtras();
                if (extras != null) {
                    //TODO: add everything to marker including date and details
                    LatLng latLng = stringToLatLng(extras.getString(LATLNG));
                    String date = extras.getString(DATE);
                    String details = extras.getString(DETAILS);
                    String title = extras.getString(TITLE);
                    Bitmap bitmap = extras.getParcelable(BITMAP);
                    if(bitmap == null){
                        mMap.addMarker(new MarkerOptions()
                                .title(title)
                                .position(latLng));
                    }else{
                        mMap.addMarker(new MarkerOptions()
                                .title(title)
                                .position(latLng)
                                .icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                    }
                    //TODO: save marker somehow. currently is not saved
                }else{
                    Toast.makeText(this,"Memory not successfully created.", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this,"Memory not successfully created.", Toast.LENGTH_SHORT).show();
            }
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
        switch (item.getItemId())
        {
            case R.id.menu_search_address:
                createMemoryWithUserInputAddress();
                return true;
            case R.id.menu_create_memory_at_location:
                getCurrentLocation();
                if(userCurrLatLng == null){
                    Toast.makeText(getApplicationContext(),"Current location not found. Try pressing the location button first.",Toast.LENGTH_LONG).show();
                }
                else{
                    Intent intent = createIntentWithLatLng(userCurrLatLng.toString(),MemoryActivity.class);
                    startActivityForResult(intent, CREATE_MEMORY);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private LatLng addressToLatLng(String address){
        //TODO: create address to LatLng method!
        return new LatLng(1.0,1.0);
    }

    private Intent createIntentWithLatLng(String latlngstr, Class<?> toClass){
        Intent intent = new Intent(MapsActivity.this,toClass);
        Bundle bundle = new Bundle();
        if(latlngstr != null || latlngstr != ""){
            bundle.putString(LATLNG,latlngstr);
        }
        intent.putExtras(bundle);
        return intent;
    }

    private void createMemoryWithUserInputAddress(){
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
                Intent intent = createIntentWithLatLng(latLng.toString(),MemoryActivity.class);
                startActivityForResult(intent, CREATE_MEMORY);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    /* the string is of the format "lat/lng: (x.xxxxx...,x.xxxx....)" */
    private LatLng stringToLatLng(String latlngstr){
        String[] latLongStrArr = latlngstr.split(",");
        Double latitude = Double.parseDouble(latLongStrArr[0].substring(10));
        Double longitude = Double.parseDouble(latLongStrArr[1].substring(0,latLongStrArr[1].length()-1));
        return new LatLng(latitude,longitude);
    }

}