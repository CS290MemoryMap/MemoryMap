package compsci290.duke.edu.memorymap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MemoryActivity extends AppCompatActivity {
    private static final int SELECT_PICTURE = 1;
    private Intent intent;
    private Bundle bundle;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);

        /* Create intent for MapsActivity */
        intent = new Intent(this,MapsActivity.class);
        bundle = new Bundle();

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
                    //also put image in bundle
                    bundle.putParcelable("bitmap",mPic);

                }
            }
        }
    }

    public void onClickConfirmMemory(View view){
        EditText date = (EditText) findViewById(R.id.editor_date);
        EditText details = (EditText) findViewById(R.id.editor_details);
        bundle.putString("date", date.getText().toString());
        bundle.putString("details", date.getText().toString());
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
