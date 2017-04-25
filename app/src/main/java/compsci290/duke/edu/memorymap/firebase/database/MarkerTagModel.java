package compsci290.duke.edu.memorymap.firebase.database;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import compsci290.duke.edu.memorymap.MarkerTag;

/**
 * Define a MarkerTag model for Firebase Real-time Database objects/nodes
 * This class contains the model's parameters, getters, and constants
 */

public class MarkerTagModel {
    static final String TABLE_NAME_MARKERTAG = "markertags";
    static final String CHILD_NAME_TITLE = "title";
    static final String CHILD_NAME_DATE = "date";
    // TODO change this String value to "location" when ready
    static final String CHILD_NAME_LOCATION = "latitude";

    private String id = "";
    private String title = "";
    private String date = "";
    private String details = "";
    private String imgBase64 = "";
    private Double latitude = null;
    private Double longitude = null;
    // TODO add location parameter so we can use it for sorting by location

    /**
     * Default ocnstructor required for calls to DataSnapshot.getValue(MarkerTagModel.class)
     * for firebase real-time database
     */
    public MarkerTagModel() {
        // This is intentionally left empty
    }

    MarkerTagModel(MarkerTag markerTag) {
        this.id = markerTag.getID();
        this.title = markerTag.getTitle();
        this.date = convertDateToString(markerTag.getDateDate());
        this.details = markerTag.getDetails();
        this.imgBase64 = bitmapToBase64(markerTag.getImg());
        this.latitude = markerTag.getLatitude();
        this.longitude = markerTag.getLongitude();
    }

    /* Getters */
    public String getId() {return this.id;}
    public String getTitle(){return this.title;}
    public String getDate(){return this.date;}
    public String getDetails(){return this.details;}
    public String getImgBase64(){
        return imgBase64;
    }
    public Double getLatitude(){return this.latitude;}
    public Double getLongitude(){return this.longitude;}

    /**
     * Converts the bitmap to String Base64
     * @param bmp a Bitmap of the image
     * @return a Base64 stream of the given image
     */
    private String bitmapToBase64(Bitmap bmp) {
        if (bmp == null){
            return "";
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        img.recycle();
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
