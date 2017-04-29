package compsci290.duke.edu.memorymap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by taranagar on 4/29/17.
 */

public class MemoryActivity extends AppCompatActivity {

    protected static final String MARKERTAG = "markertag";

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);

        /* get all views */
        mDateView = (TextView) findViewById(R.id.text_date);
        mTitleView = (EditText) findViewById(R.id.editor_title);
        mDetailsView = (EditText) findViewById(R.id.editor_details);
        mImageView = (ImageView) findViewById(R.id.image_upload);

        /* don't allow users to upload a new photo */
        mImageView.setClickable(false);

        /* set data of elements */
        mTag = savedInstanceState.getParcelable(MARKERTAG);
        mDateView.setText(mTag.getDate(), TextView.BufferType.EDITABLE);
        mTitleView.setText(mTag.getTitle(), TextView.BufferType.EDITABLE);
        mDetailsView.setText(mTag.getDetails(), TextView.BufferType.EDITABLE);
        mImageView.setImageBitmap(mTag.getImg());
        mButton.setText("Edit Memory");

        /* hide ToggleButton */
        mToggleButton.setVisibility(View.GONE);
    }

    /**
     * Defines the "Edit Memory" button's functionality
     *  passes the current MarkerTag to be edited
     *
     *  @param  view  unused button view parameter
     **/
    public void onClickMemory(View view) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(MARKERTAG, mTag);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
