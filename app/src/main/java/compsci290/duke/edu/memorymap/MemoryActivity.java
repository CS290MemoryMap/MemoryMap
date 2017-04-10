package compsci290.duke.edu.memorymap;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

public class MemoryActivity extends AppCompatActivity {
    private static final int SELECT_PICTURE = 1;
    private static final String DATE = "date";
    private static final String DETAILS = "details";
    private static final String TITLE = "title";
    private static final String BITMAP = "bitmap";
    private static final String LATLNG = "latlng";
    private Intent mToMapsIntent;
    private Bundle mToMapsBundle;
    private static TextView mDateTextView;
    private String mLatLngStr;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);

        /* Create mToMapsIntent for MapsActivity */
        mToMapsIntent = new Intent(this,MapsActivity.class);
        mToMapsBundle = new Bundle();

        /* include previous location in next location */
        Bundle prevBundle = getIntent().getExtras();
        if(prevBundle != null){
            mLatLngStr = prevBundle.getString(LATLNG);
            //System.out.println("mLatLngStr in Memory activity: "+ mLatLngStr);
            if(mLatLngStr != null) {
                mToMapsBundle.putString(LATLNG, mLatLngStr);
                //System.out.println("put latlng into mToMapsBundle from MemoryActivity");
            }
        }

        /*find mDateTextView */
        mDateTextView = (TextView) findViewById(R.id.text_date);

        /* set imageview to uploadmedia.png */
        ImageView imageView = (ImageView) findViewById(R.id.image_upload);
        Drawable res = getResources().getDrawable(R.drawable.uploadmedia,getTheme());
        imageView.setImageDrawable(res);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    public void onClickImageUpload(View view){
        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String pickTitle = "Select or take a new Picture"; // Or get from strings.xml
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,new Intent[] { takePhotoIntent });

        startActivityForResult(chooserIntent, SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == SELECT_PICTURE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a picture to put on the marker (I think?)
                final Bundle extras = data.getExtras();
                if (extras != null) {
                    //Get image
                    Bitmap mPic = extras.getParcelable("data");
                    //Put image in imageview
                    ImageView imageView = (ImageView) findViewById(R.id.image_upload);
                    imageView.setImageBitmap(mPic);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setAdjustViewBounds(true);
                    //also put image in mToMapsBundle
                    mToMapsBundle.putParcelable(BITMAP,mPic);

                }
            }
        }
    }

    public void onClickSetDate(View view){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show((this.getSupportFragmentManager()), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String str = String.format("%d/%d/%d",month,day,year);
            mDateTextView.setText(str);
        }
    }

    public void onClickConfirmMemory(View view){
        EditText details = (EditText) findViewById(R.id.editor_details);
        EditText title = (EditText) findViewById(R.id.editor_title);

        mToMapsBundle.putString(DATE, mDateTextView.getText().toString());
        mToMapsBundle.putString(DETAILS, details.getText().toString());
        mToMapsBundle.putString(TITLE, title.getText().toString());
        mToMapsIntent.putExtras(mToMapsBundle);
        setResult(Activity.RESULT_OK, mToMapsIntent);
        finish();
    }

}
