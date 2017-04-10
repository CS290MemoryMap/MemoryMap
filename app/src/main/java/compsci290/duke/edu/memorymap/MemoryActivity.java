package compsci290.duke.edu.memorymap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MemoryActivity extends AppCompatActivity {
    private static final int SELECT_PICTURE = 1;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory);

        /* Pass the current date */
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());

        /*EditText editText = (EditText) findViewById(R.id.text_editor);
        editText.setEnabled(false);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getApplicationWindowToken(), 0);*/
    }

    public void choosePicture(){
        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String pickTitle = "Select or take a new Picture"; // Or get from strings.xml
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,new Intent[] { takePhotoIntent });

        startActivityForResult(chooserIntent, SELECT_PICTURE);
    }
    /*
    public void onClickCreateTextMarker(View v){
        EditText editText = (EditText) findViewById(R.id.text_editor);
        editText.setEnabled(true);
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("text",v.getText().toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                    handled = true;
                }
                return handled;
            }
        });
    }*/

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
                    //pass to the map activity
                    Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("bitmap",mPic);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        }
    }
}
