package compsci290.duke.edu.memorymap.map;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import compsci290.duke.edu.memorymap.firebase.database.MarkerTagModel;
import compsci290.duke.edu.memorymap.memory.MarkerTag;
import compsci290.duke.edu.memorymap.memory.MemoryActivity;
import compsci290.duke.edu.memorymap.R;

public class EditableMapsActivity extends MapsActivity
        implements GoogleMap.OnMapLongClickListener,
        GoogleMap.OnInfoWindowLongClickListener{

    private static final String TAG = "EditableMapsActivity";
    protected String userInputAddress = "";
    protected LatLng mNewMarkerLatLng;
    protected boolean mSeeNewMarker = false;
    private Marker markerToRemove = null;


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
        super.onMapReady(mMap);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnInfoWindowLongClickListener(this);


        queryMyMarkerTagList();
//        //restore all markers
//        mTagList = mDbHandler.queryAllMarkerTags();
//        for(MarkerTag tag : mTagList){
//            addMarkerFromTag(tag);
//        }

    }


    /* Activated when the user long clicks on the map.
     * Asks the user if they would like to create a memory at the clicked location.
     * Upon answering yes, MemoryActivity is started
     *
     * @param  latLng  LatLng of clicked location
     */
    @Override
    public void onMapLongClick(final LatLng latLng) {
        new AlertDialog.Builder(new ContextThemeWrapper(EditableMapsActivity.this, R.style.myDialog))
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
     * Describes on how to handle the infowindow being long clicked.
     * If the InfoWindow is long clicked, a dialog appears offering
     * the user the options to delete or edit the marker.
     *
     * @param  marker   the marker whose InfoWindow was clicked
     */
    @Override
    public void onInfoWindowLongClick(final Marker marker) {
        new AlertDialog.Builder(new ContextThemeWrapper(EditableMapsActivity.this, R.style.myDialog))
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
                        Intent intent = new Intent(EditableMapsActivity.this, MemoryActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(MARKERTAG, markerTag);
                        intent.putExtras(bundle);
                        markerToRemove = marker; //remove marker to be edited, will replace when returns
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
                    if(markerTag != null) {
                        markerTag = mDbHandler.updateMarkerTag(markerTag);
                        for (MarkerTag tag : mTagList) {
                            if (tag.getID().equals(markerTag.getID())) {
                                mTagList.remove(tag);
                                mTagList.add(markerTag);
                            }
                        }
                        Marker newMarker;
                        if (markerTag.getImg() == null) {
                            newMarker = mMap.addMarker(new MarkerOptions()
                                    .position(mNewMarkerLatLng));
                            newMarker.setTag(markerTag);

                        } else {
                            newMarker = mMap.addMarker(new MarkerOptions()
                                    .position(mNewMarkerLatLng)
                                    .icon(BitmapDescriptorFactory.fromBitmap(markerTag.getImg())));
                            newMarker.setTag(markerTag);
                        }
                        mSeeNewMarker = true;
                        if(markerToRemove != null){
                            markerToRemove.remove();
                            markerToRemove = null;
                        }
                    }else{
                        Log.d(TAG, "MarkerTag in extras was null");
                    }
                }else{
                    Log.d(TAG, "Result from EDIT_MEMORY extras == null");
                }
            }else{
                markerToRemove = null;
                Toast.makeText(this,"Memory not successfully edited.", Toast.LENGTH_SHORT).show();
            }
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
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu_editable, menu);
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
        }catch (IOException e){
            Log.d(TAG, "Could not convert address to latlng. Error: "+e);
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
    protected Intent createIntentWithLatLng(LatLng latlng, Class<?> toClass){
        Intent intent = new Intent(EditableMapsActivity.this,toClass);
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
                    Toast.makeText(EditableMapsActivity.this,"Failed to find location of address.",Toast.LENGTH_LONG).show();
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
        super.onConnected(bundle);
        if(mSeeNewMarker && (mNewMarkerLatLng != null)) {
            Log.d(TAG, "moving camera to user's new marker");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mNewMarkerLatLng, ZOOM));
            mSeeNewMarker = false;
        }
    }

    private void queryMyMarkerTagList() {
        final List<MarkerTag> markerTagList = new ArrayList<>(); // empty MarkerTag list

        mDbHandler.getDatabase().child(MarkerTagModel.TABLE_NAME_MARKERTAG)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot noteSnapshot: dataSnapshot.getChildren()){
                            MarkerTagModel markerTagModel = noteSnapshot.getValue(MarkerTagModel.class);
                            // filter user MarkerTag only
                            if (markerTagModel.getUserId().equals(mDbHandler.getUserId())) {
                                markerTagList.add(new MarkerTag((markerTagModel)));
                                Log.d(TAG, "QUERIED MarkerTag " + markerTagModel.getTitle());
                            }
                        }

                        //restore all markers
                        mTagList = markerTagList;
                        for (MarkerTag tag : mTagList) {
                            addMarkerFromTag(tag);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "Error Querying Data: " + databaseError.getMessage());
                    }
                });
    }

}
