package compsci290.duke.edu.memorymap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import compsci290.duke.edu.memorymap.firebase.database.MarkerTagModel;

public class MarkerTag {
    private String title = "";
    private String date = "";
    private String details = "";
    private Bitmap img = null;
    private Double latitude = null;
    private Double longitude = null;

    public MarkerTag(String title, String date, String details, Bitmap img, Double latitude, Double longitude){
        if(title != null) this.title = title;
        if(!date.equals("date")) this.date = date;
        if(details != null) this.details = details;
        this.img = img;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * create a MarkerTag from a MarkerTagModel (firebase model)
     * @param markerTagModel firebase database MarkerTag model
     */
    public MarkerTag(MarkerTagModel markerTagModel) {
        this.title = markerTagModel.getTitle();
        this.date = markerTagModel.getDate();
        this.details = markerTagModel.getDetails();
        this.img = base64ToBitmap(markerTagModel.getImgBase64());
        this.latitude = markerTagModel.getLatitude();
        this.longitude = markerTagModel.getLongitude();
    }

    public String getTitle(){return this.title;}
    public String getDate(){return this.date;}
    public String getDetails(){return this.details;}
    public Bitmap getImg(){return this.img;}
    public Double getLatitude(){return this.latitude;}
    public Double getLongitude(){return this.longitude;}

    /**
     * convert Base64 image String to Bitmap
     * @param imgBase64
     * @return
     */
    private Bitmap base64ToBitmap(String imgBase64) {
        byte[] imgArray = Base64.decode(imgBase64, Base64.DEFAULT);
        //Bitmap img = BitmapFactory.decodeByteArray(imgArray, 0, imgArray.length);
        return Bitmap.createScaledBitmap(BitmapFactory.
                decodeByteArray(imgArray, 0, imgArray.length), 100, 100, true);
    }
}
