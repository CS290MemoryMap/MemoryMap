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
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;
import java.util.Date;

public class MemoryActivity extends AppCompatActivity {
    private static final int SELECT_PICTURE = 1;
    private static final String DATE = "date";
    private static final String DETAILS = "details";
    private static final String TITLE = "title";
    private static final String BITMAP = "bitmap";
    private static final String LATLNG = "latlng";
    private static final String BUNDLE = "bundle";

    private static TextView mDateTextView;
    private static EditText mTitleView;
    private static EditText mDetailsView;
    private static ImageView mImageView;

    private static final String TAG = "MemoryActivity";

    private Bitmap mPic;
    private LatLng mLatLng;
    private Bundle mToMapsBundle;

    /**
     * onCreate sets the content view, gets all of the views,
     * and restores state if necessary.
     */
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);

        /* get all views */
        mDateTextView = (TextView) findViewById(R.id.text_date);
        mTitleView = (EditText) findViewById(R.id.editor_title);
        mDetailsView = (EditText) findViewById(R.id.editor_details);
        mImageView = (ImageView) findViewById(R.id.image_upload);

        if (savedInstanceState != null) {
            //TODO: restore state
            mToMapsBundle = savedInstanceState.getBundle(BUNDLE);
            if(mToMapsBundle != null){
                mPic = mToMapsBundle.getParcelable(BITMAP);
                if(mPic == null){
                    displayDefaultPicture();
                }else{
                    mImageView.setImageBitmap(mPic);
                    mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    mImageView.setAdjustViewBounds(true);
                }
                mTitleView.setText(mToMapsBundle.getString(TITLE));
                mDateTextView.setText(mToMapsBundle.getString(DATE));
                mDetailsView.setText(mToMapsBundle.getString(DETAILS));
            }
        }
        if(savedInstanceState == null || mToMapsBundle == null){
            /* Create mToMapsBundle for MapsActivity */
            mToMapsBundle = new Bundle();

            /* set the default camera upload picture */
            displayDefaultPicture();

            /* include previous location in next location */
            Bundle prevBundle = getIntent().getExtras();
            if(prevBundle != null){
                mLatLng = prevBundle.getParcelable(LATLNG);
            }else if(prevBundle == null || mLatLng == null){
                Log.d(TAG,"Got to MemoryActivity without a latlng");
            }
        }
    }

    /**
     * Creates intent and starts activity for choosing a photo to upload
     *
     * @param  view  unused parameter. is the button view
     */
    public void onClickImageUpload(View view){
        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String pickTitle = "Select or take a new picture"; // Or get from strings.xml
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,new Intent[] { takePhotoIntent });

        startActivityForResult(chooserIntent, SELECT_PICTURE);
    }

    /**
     * describes what to do on an activity returning with a result
     *
     * @param  requestCode  denotes which activity we're returning from
     * @param  resultCode   denotes success of the previous activity
     * @param  data         Intent from the previous activity
     */
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
                    mImageView.setImageBitmap(mPic);
                    mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    mImageView.setAdjustViewBounds(true);
                    //also put image in mToMapsBundle
                    mToMapsBundle.putParcelable(BITMAP,mPic);

                }
            }
        }
    }

    /**
     * Defines the Date TextView onClick method
     *
     * @param  view     unused button view parameter
     */
    public void onClickSetDate(View view){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show((this.getSupportFragmentManager()), "datePicker");
    }

    /**
     * Class that enables the user to pick a date
     */
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        /**
         * Creates the dialog that enables the user to pick a date.
         *
         * @param  savedInstanceState  bundle containing saved state
         * @return      the created dialog
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialogDatePicker = new DatePickerDialog(getActivity(), R.style.MyDatePickerStyle,this, year, month, day);
            // Create a new instance of DatePickerDialog and return it
            return dialogDatePicker;
        }

        /**
         * Defines what to do after the date has been set.
         *
         * @param  view     the DatePicker view
         * @param  year     year chosen
         * @param  month    month chosen
         * @param  day      day chosen
         */
        public void onDateSet(DatePicker view, int year, int month, int day) {
            String str = String.format("%d/%d/%d",month,day,year);
            mDateTextView.setText(str);
        }
    }

    /**
     * Defines the "Confirm Memory" button's functionality.
     * Puts the title, date, and details into an already-created bundle
     * and passes them back to MapsActivity
     *
     * @param  view  unused button view parameter
     */
    public void onClickConfirmMemory(View view){
        Intent toMapsIntent = new Intent();
        mToMapsBundle.putString(DATE, mDateTextView.getText().toString());
        mToMapsBundle.putString(DETAILS, mDetailsView.getText().toString());
        mToMapsBundle.putString(TITLE, mTitleView.getText().toString());
        toMapsIntent.putExtras(mToMapsBundle);
        setResult(Activity.RESULT_OK, toMapsIntent);
        finish();
    }

    /**
     * Saves the state of this activity, including the date,
     * details, title, and image (if the image has been set).
     *
     * @param  outState  bundle containing state
     */
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putCharSequence(KEY_TEXT_VALUE, mTextView.getText());
        /* mToMapsBundle already has picture if it has been chosen */
        mToMapsBundle.putString(DATE,mDateTextView.getText().toString());
        mToMapsBundle.putString(DETAILS,mDetailsView.getText().toString());
        mToMapsBundle.putString(TITLE,mTitleView.getText().toString());
        if(mLatLng != null) {
            mToMapsBundle.putParcelable(LATLNG, mLatLng);
        }
        outState.putBundle(BUNDLE,mToMapsBundle);
    }

    /**
     * Sets the image view to display the default picture
     */
    private void displayDefaultPicture(){
        /* set imageview to uploadmedia.png */
        Drawable res = getResources().getDrawable(R.drawable.uploadmedia,getTheme());
        mImageView.setImageDrawable(res);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
    }

}
