package compsci290.duke.edu.memorymap;

import android.graphics.Bitmap;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MarkerTag {
    private static final String TAG = "MarkerTag";
    private String title = "";
    private String dateStr = "";
    private Date date = null;
    private String details = "";
    private Bitmap img = null;
    private Double latitude = null;
    private Double longitude = null;

    public MarkerTag(String title, String date, String details, Bitmap img, Double latitude, Double longitude){
        if(title != null) this.title = title;
        //if(!date.equals("Date")) this.date = date;
        if(!date.equals("Date")){
            this.dateStr = date;
            DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
            try{
                this.date = format.parse(date);
            }catch(ParseException pe){
                Log.d(TAG, "Failed to parse the date");
            }
        }
        if(details != null) this.details = details;
        this.img = img;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    /* Untested... not sure if necessary either
    public MarkerTag(String title, Date date, String details, Bitmap img, Double latitude, Double longitude){
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        if(title != null) this.title = title;
        if(date != null){
            this.date = date;
            this.dateStr = format.format(date);
        }
        if(details != null) this.details = details;
        this.img = img;
        this.latitude = latitude;
        this.longitude = longitude;
    }*/

    public String getTitle(){return this.title;}
    public String getDate(){return this.dateStr;}
    public String getDateStr(){ return this.dateStr;}
    public Date getDateDate(){ return this.date;}
    public String getDetails(){return this.details;}
    public Bitmap getImg(){return this.img;}
    public Double getLatitude(){return this.latitude;}
    public Double getLongitude(){return this.longitude;}
}
