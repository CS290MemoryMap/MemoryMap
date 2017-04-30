package compsci290.duke.edu.memorymap.memory;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

import compsci290.duke.edu.memorymap.R;
import compsci290.duke.edu.memorymap.memory.MemoryActivity;

public class EditableMemoryActivity extends MemoryActivity {
    private static final int SELECT_PICTURE = 1;
    private static final String DATE = "date";
    private static final String DETAILS = "details";
    private static final String TITLE = "title";
    private static final String BITMAP = "bitmap";
    private static final String LATLNG = "latlng";
    private static final String BUNDLE = "bundle";
    private static final String ISPUBLIC = "ispublic";
    //private static final String MARKERTAG = "markertag";

    private static final String TAG = "EditableMemoryActivity";

    /**
     * onCreate sets the content view, gets all of the views,
     * and restores state if necessary.
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);

        /* get all views */
        mDateView = (TextView) findViewById(R.id.text_date);
        mTitleView = (EditText) findViewById(R.id.editor_title);
        mDetailsView = (EditText) findViewById(R.id.editor_details);
        mImageView = (ImageView) findViewById(R.id.image_upload);
        mButton = (Button) findViewById(R.id.button_memory);
        mButton.setText(getResources().getString(R.string.memory_confirm));
        mToggleButton = (ToggleButton) findViewById(R.id.button_toggle);

        if (savedInstanceState != null) {
            mTag = savedInstanceState.getParcelable(MARKERTAG);//getBundle(BUNDLE);
            if(mTag != null) {
                setFieldsFromTag();
            } else{
                Log.d(TAG,"mTag was null.");
            }
        }else{
            Bundle prevBundle = getIntent().getExtras();
            if(prevBundle != null){
                mTag = prevBundle.getParcelable(MARKERTAG);
            }
            if(mTag == null){
                Log.d(TAG,"savedInstanceState and prevBundle both did not contain a markertag");
            /* set the default camera upload picture */
                displayDefaultPicture();
            }else{
                setFieldsFromTag();
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
                    mTag.setImg(mPic);

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
            return new DatePickerDialog(getActivity(), R.style.MyDatePickerStyle,this, year, month, day);
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
            mDateView.setText(str);
        }
    }

    /**
     * Defines the "Confirm Memory" button's functionality.
     * Puts the MarkerTag in a bundle
     * and passes them back to MapsActivity
     *
     * @param  view  unused button view parameter
     */
    public void onClickMemory(View view){
        Intent toMapsIntent = new Intent();
        Bundle bundle = new Bundle();
        putFieldsInTag();
        bundle.putParcelable(MARKERTAG,mTag);
        toMapsIntent.putExtras(bundle);
        Log.d(TAG, "Sending intent back to MemoryActivity");
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
        putFieldsInTag();
        outState.putParcelable(MARKERTAG, mTag);
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

    /**
     * Put updated fields into MarkerTag
     **/
    private void putFieldsInTag(){/*image not included because don't want to include default picture*/
        mTag.setDate(mDateView.getText().toString());
        mTag.setTitle(mTitleView.getText().toString());
        mTag.setDetails(mDetailsView.getText().toString());
        mTag.setIsPublic(mToggleButton.isChecked());
    }

    /**
     * Sets fields of MemoryActivity UI components from MarkerTag
     **/
    private void setFieldsFromTag(){
        mPic = mTag.getImg();
        if(mPic == null){
            displayDefaultPicture();
        }else{
            mImageView.setImageBitmap(mPic);
            mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            mImageView.setAdjustViewBounds(true);
        }
        String title = mTag.getTitle();
        if(title != "" && title != null) mTitleView.setText(title);
        String date = mTag.getDate();
        if(date != null) mDateView.setText(date);
        String details = mTag.getDetails();
        if(details != "" && details != null) mDetailsView.setText(details);
        Boolean checked = mTag.getIsPublic();
        if(checked != null) mToggleButton.setChecked(checked);
    }
}
