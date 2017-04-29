package compsci290.duke.edu.memorymap.firebase.database;

import android.graphics.Bitmap;
import android.util.Base64;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import compsci290.duke.edu.memorymap.memory.MarkerTag;

/**
 * Define a MarkerTag model for Firebase Real-time Database objects/nodes
 * This class contains the model's parameters, getters, and constants
 */

public class MarkerTagModel {
    public static final String TABLE_NAME_MARKERTAG = "markertags";
    public static final String CHILD_NAME_TITLE = "title";
    public static final String CHILD_NAME_DATE = "date";
    public static final String CHILD_NAME_LOCATION = "latitude";
    public static final String CHILD_NAME_PUBLICMARKERTAG = "publicMarkerTag";

    private String markerTagId; // MarkerTag unique ID
    private String title;
    private String date;
    private String details;
    private String imgBase64;
    private Double latitude;
    private Double longitude;
    private String userId; // authenticated user ID
    private boolean publicMarkerTag;

    /**
     * Default constructor required for calls to DataSnapshot.getValue(MarkerTagModel.class)
     * for firebase real-time database
     */
    public MarkerTagModel() {
        // This is intentionally left empty
    }

    /**
     * Constructor
     * @param markerTag a MarkerTag object used to construct the MArkerTagModel object
     */
    MarkerTagModel(MarkerTag markerTag) {
        this.markerTagId = markerTag.getID();
        this.title = markerTag.getTitle();
        // convert date to year, month day for sorting purposes
        this.date = convertDateToString(markerTag.getDateDate());
        this.details = markerTag.getDetails();
        // convert Bitmap to Base64 to store in firebase
        this.imgBase64 = bitmapToBase64(markerTag.getImg());
        this.latitude = markerTag.getLatitude();
        this.longitude = markerTag.getLongitude();
        // user must always be logged in (but just in case)
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            this.userId = user.getUid();
        else
            this.userId = "anonymous";
        this.publicMarkerTag = markerTag.getIsPublic();
    }

    /* Getters */
    public String getMarkerTagId() {return this.markerTagId;}
    public String getTitle(){return this.title;}
    public String getDate(){return this.date;}
    public String getDetails(){return this.details;}
    public String getImgBase64(){
        return imgBase64;
    }
    public Double getLatitude(){return this.latitude;}
    public Double getLongitude(){return this.longitude;}
    public String getUserId() {
        // used for database object creation
        return userId;
    }
    public boolean isPublicMarkerTag() {
        return publicMarkerTag;
    }

    /**
     * Converts the bitmap to String Base64
     * @param bmp a Bitmap of the image
     * @return a Base64 stream of the given image
     */
    private String bitmapToBase64(Bitmap bmp) {
        if (bmp == null) {
           return "";
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    /**
     * converts a Date object into a String
     * @param date a Date object
     * @return a string of the date in "yyyy, MM dd" format
     */
    private String convertDateToString(Date date) {
        if (date == null) {
            date = new Date();
        }
        DateFormat format = new SimpleDateFormat("yyyy, MM dd", Locale.ENGLISH);
        return format.format(date);
    }
}
