package compsci290.duke.edu.memorymap;

import android.graphics.Bitmap;

public class MarkerTag {
    private String title = "";
    private String date = "";
    private String details = "";
    private Bitmap img = null;
    private Double latitude = null;
    private Double longitude = null;

    public MarkerTag() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        // for firebase real-time database
    }

    public MarkerTag(String title, String date, String details, Bitmap img, Double latitude, Double longitude){
        if(title != null) this.title = title;
        if(!date.equals("date")) this.date = date;
        if(details != null) this.details = details;
        this.img = img;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getTitle(){return this.title;}
    public String getDate(){return this.date;}
    public String getDetails(){return this.details;}
    public Bitmap getImg(){return this.img;}
    public Double getLatitude(){return this.latitude;}
    public Double getLongitude(){return this.longitude;}
}
