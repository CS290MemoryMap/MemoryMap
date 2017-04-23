package compsci290.duke.edu.memorymap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import compsci290.duke.edu.memorymap.firebase.database.MarkerTagModel;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MarkerTag implements Parcelable {
    private static final String TAG = "MarkerTag";
    private String title = "";
    private String dateStr = "";
    private Date date = null;
    private String details = "";
    private Bitmap img = null;
    private Double latitude = null;
    private Double longitude = null;

    /*new fields*/
    private Boolean isPublic = false;
    private int ID = 0;
    //TODO: get rid of this constructor that doesn't have the isPublic boolean and the ID int
    /*
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
    }*/
    public MarkerTag(){

    }
    public MarkerTag(String title, String date, String details, Bitmap img, Double latitude, Double longitude, boolean isPublic, int ID){
        if(title != null) this.title = title;
        if(!date.equals("Date")){
            this.dateStr = date;
            this.date = createDateFromString(date);
        }
        if(details != null) this.details = details;
        this.img = img;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isPublic = isPublic;
        this.ID = ID;
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

    public Date createDateFromString(String date){
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        try{
            return format.parse(date);
        }catch(ParseException pe){
            Log.d(TAG, "Failed to parse the date");
            return null;
        }
    }

    public String createStringFromDate(Date date){
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        return format.format(date);
    }

    /* getters */
    public String getTitle(){return this.title;}
    public String getDate(){return this.dateStr;}
    public String getDateStr(){ return this.dateStr;}
    public Date getDateDate(){ return this.date;}
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
    public Boolean getIsPublic(){return this.isPublic;}
    public int getID(){return this.ID;}

    /* setters */
    public void setTitle(String title){this.title = title;}
    public void setDate(String date){
        this.dateStr = date;
        this.date = createDateFromString(date);
    }
    public void setDate(Date date){
        this.date = date;
        this.dateStr = createStringFromDate(date);
    }
    public void setDetails(String details){this.details = details;}
    public void setImg(Bitmap img){this.img = img;}
    public void setLatitude(Double latitude){this.latitude = latitude;}
    public void setLongitude(Double longitude){this.longitude = longitude;}
    public void setIsPublic(Boolean isPublic){this.isPublic = isPublic;}
    public void setID(int ID){this.ID = ID;}

    protected MarkerTag(Parcel in) {
        title = in.readString();
        dateStr = in.readString();
        long tmpDate = in.readLong();
        date = tmpDate != -1 ? new Date(tmpDate) : null;
        details = in.readString();
        img = (Bitmap) in.readValue(Bitmap.class.getClassLoader());
        latitude = in.readByte() == 0x00 ? null : in.readDouble();
        longitude = in.readByte() == 0x00 ? null : in.readDouble();
        isPublic = in.readByte() != 0x00;
        ID = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(dateStr);
        dest.writeLong(date != null ? date.getTime() : -1L);
        dest.writeString(details);
        dest.writeValue(img);
        if (latitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(latitude);
        }
        if (longitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(longitude);
        }
        dest.writeByte((byte) (isPublic ? 0x01 : 0x00));
        dest.writeInt(ID);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MarkerTag> CREATOR = new Parcelable.Creator<MarkerTag>() {
        @Override
        public MarkerTag createFromParcel(Parcel in) {
            return new MarkerTag(in);
        }

        @Override
        public MarkerTag[] newArray(int size) {
            return new MarkerTag[size];
        }
    };
}
