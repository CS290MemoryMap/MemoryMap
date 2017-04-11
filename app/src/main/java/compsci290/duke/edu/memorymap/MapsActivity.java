package compsci290.duke.edu.memorymap;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
    private static final int CREATE_MEMORY = 1;
    private static final String DATE = "date";
    private static final String DETAILS = "details";
    private static final String TITLE = "title";
    private static final String BITMAP = "bitmap";
    private static final String LATLNG = "latlng";
    private Location userCurrLocation = null;
    private LatLng userCurrLatLng = null;
    private String userInputAddress = "";
    private LocationManager locationManager;
    private String provider;
    private String toMemoryActivityLatLngStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        // Showing status
        if(status!= ConnectionResult.SUCCESS){ // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        }else { // Google Play Services are available
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

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
        /* check permissions again... :/ */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

        }
        // Enabling MyLocation Layer of Google Map
        googleMap.setMyLocationEnabled(true);
        // Getting LocationManager object from System Service LOCATION_SERVICE
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        provider = locationManager.getBestProvider(criteria, true);


        // Getting Current Location TODO: check if current location actually is working
        userCurrLocation = locationManager.getLastKnownLocation(provider);
        if (userCurrLocation != null) {
            userCurrLatLng = new LatLng(userCurrLocation.getLatitude(), userCurrLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(userCurrLatLng));
        }

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
                updateLocation();
                Intent intent = createIntentWithLatLng(userCurrLatLng.toString(),MemoryActivity.class);
                startActivityForResult(intent, CREATE_MEMORY);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private LatLng addressToLatLng(String address){
        //TODO: create address to LatLng method!
        return new LatLng(1.0,1.0);
    }

    private void updateLocation(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            userCurrLocation = locationManager.getLastKnownLocation(provider);
            userCurrLatLng = new LatLng(userCurrLocation.getLatitude(), userCurrLocation.getLongitude());
        }
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

    private LatLng stringToLatLng(String latlngstr){
        String[] latLongStrArr = latlngstr.split(",");
        Double latitude = Double.parseDouble(latLongStrArr[0].substring(10));
        Double longitude = Double.parseDouble(latLongStrArr[1].substring(0,latLongStrArr[1].length()-1));
        return new LatLng(latitude,longitude);
    }

}