package compsci290.duke.edu.memorymap.memory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

import compsci290.duke.edu.memorymap.R;

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

    /**
     * onCreate sets the content view, gets all of the views,
     * and restores state if necessary.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Starting MemoryActivity...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);

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

        if (mTag != null) {
             /* set data of elements */
            //mTag = savedInstanceState.getParcelable(MARKERTAG);
            //Bundle data = getIntent().getExtras();
            //mTag = data.getParcelable(MARKERTAG);
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

            mButton.setText(getResources().getString(R.string.memory_edit));

            /* hide ToggleButton */
            mToggleButton.setVisibility(View.GONE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_MEMORY) {
            if (resultCode == RESULT_OK) {
                final Bundle extras = data.getExtras();
                //Get MarkerTag
                mTag = extras.getParcelable(MARKERTAG);

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

                mButton.setText(getResources().getString(R.string.memory_edit));

                /* hide ToggleButton */
                mToggleButton.setVisibility(View.GONE);
            }
        }
    }
}
