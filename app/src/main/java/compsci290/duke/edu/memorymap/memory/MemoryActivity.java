package compsci290.duke.edu.memorymap.memory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import compsci290.duke.edu.memorymap.R;
import compsci290.duke.edu.memorymap.firebase.database.FirebaseDatabaseHandler;

/**
 * Activity for View-Only memories
 **/

public class MemoryActivity extends AppCompatActivity {

    protected static final String MARKERTAG = "markertag";
    private static final String TAG = "MemoryActivity";
    private static final int EDIT_MEMORY = 1;

    protected static TextView mDateView;
    protected EditText mTitleView;
    protected EditText mDetailsView;
    protected ImageView mImageView;
    protected Button mButton;
    protected ToggleButton mToggleButton;

    protected Bitmap mPic;
    protected LatLng mLatLng;
    protected MarkerTag mTag;
    protected boolean mIsPublic;
    protected boolean publicList;

    private FirebaseDatabaseHandler mFirebaseDbHandler;


    /**
     * onCreate sets the content view, gets all of the views,
     * sets views with information, and restores state if necessary.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Starting MemoryActivity...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);
        mFirebaseDbHandler = new FirebaseDatabaseHandler();

        /* get all views */
        mDateView = (TextView) findViewById(R.id.text_date);
        mTitleView = (EditText) findViewById(R.id.editor_title);
        mDetailsView = (EditText) findViewById(R.id.editor_details);
        mImageView = (ImageView) findViewById(R.id.image_upload);
        mButton = (Button) findViewById(R.id.button_memory);
        mToggleButton = (ToggleButton) findViewById(R.id.button_toggle);

        /* don't allow users to upload a new photo */
        mImageView.setClickable(false);

        Bundle data = getIntent().getExtras();
        mTag = data.getParcelable(MARKERTAG);
        publicList = data.getBoolean("PublicList");

        if (mTag != null) {
             /* set data of elements */
            mDateView.setText(mTag.getDate(), TextView.BufferType.EDITABLE);
            mDateView.setEnabled(false);
            mDateView.setTextColor(Color.BLACK);

            mTitleView.setText(mTag.getTitle(), TextView.BufferType.EDITABLE);
            mTitleView.setEnabled(false);
            mTitleView.setTextColor(Color.BLACK);

            mDetailsView.setText(mTag.getDetails(), TextView.BufferType.EDITABLE);
            mDetailsView.setEnabled(false);
            mDetailsView.setTextColor(Color.BLACK);

            mImageView.setImageBitmap(mTag.getImg());
            mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            mImageView.setAdjustViewBounds(true);

            if (publicList) {
                mButton.setVisibility(View.INVISIBLE);
            } else {
                mButton.setText(getResources().getString(R.string.memory_edit));
            }

            /* hide ToggleButton */
            mToggleButton.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "MarkerTag is null");
        }
    }

    /**
     * Defines the "Edit Memory" button's functionality
     *  passes the current MarkerTag to be edited
     *
     *  @param  view  unused button view parameter
     **/
    public void onClickMemory(View view) {
        Intent intent = new Intent(this, EditableMemoryActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(MARKERTAG, mTag);
        intent.putExtras(bundle);
        startActivity(intent);
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
        if (requestCode == EDIT_MEMORY) {
            if (resultCode == RESULT_OK) {
                final Bundle extras = data.getExtras();
                //Get MarkerTag
                if (extras != null) {
                    MarkerTag mMarkerTag = extras.getParcelable(MARKERTAG);
                    if (mMarkerTag != null) {
                        //mMarkerTag = mFirebaseDbHandler.updateMarkerTag(mMarkerTag);

                    }
                }

            }
        }
    }
}