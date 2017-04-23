package compsci290.duke.edu.memorymap.firebase.database;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

import compsci290.duke.edu.memorymap.MarkerTag;

/**
 * Define a MarkerTag model for Firebase Real-time Database objects/nodes
 * This class contains the model's parameters and getters
 */

public class MarkerTagModel {

    private String id = "";
    private String title = "";
    private String date = "";
    private String details = "";
    private String imgBase64 = "";
    private Double latitude = null;
    private Double longitude = null;

    public MarkerTagModel() {
        // Default constructor required for calls to DataSnapshot.getValue(MarkerTagModel.class)
        // for firebase real-time database
    }

    public MarkerTagModel(MarkerTag markerTag) {
        this.id = markerTag.getID();
        this.title = markerTag.getTitle();
        this.date = markerTag.getDate();
        this.details = markerTag.getDetails();
        this.imgBase64 = bitmaptoBase64(markerTag.getImg());
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
     * @param bmp
     * @return
     */
    private String bitmaptoBase64(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        img.recycle();
        byte[] byteArray = stream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
