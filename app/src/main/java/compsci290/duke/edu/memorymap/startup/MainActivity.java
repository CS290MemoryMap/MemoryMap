package compsci290.duke.edu.memorymap.startup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import compsci290.duke.edu.memorymap.map.MapsActivity;
import compsci290.duke.edu.memorymap.memory.MemoryListActivity;
import compsci290.duke.edu.memorymap.R;
import compsci290.duke.edu.memorymap.map.EditableMapsActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private TextView mStatusTextView;



    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Views
        mStatusTextView = (TextView) findViewById(R.id.status);



        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        new AsyncActivity().execute();

        updateUI(currentUser);
    }
    // [END on_start_check_user]


    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }


    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt, user.getEmail()/*, user.isEmailVerified()*/));


            findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
            findViewById(R.id.email_password_fields).setVisibility(View.GONE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.VISIBLE);

        } else {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_out_button) {
            signOut();
        }
    }

    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    public void onClickPersonalMapBtn(View v)
    {
        Intent intent = new Intent(this, EditableMapsActivity.class);
        startActivity(intent);
    }

    public void onClickPersonalListBtn(View v) {
        Intent intent = new Intent(this, MemoryListActivity.class);
        startActivity(intent);
    }

    public void onClickPublicMapBtn(View v)
    {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void onClickPublicListBtn(View v) {
        // TODO update Intent with public MemoryListActivity class
//        Intent intent = new Intent(this, MemoryListActivity.class);
//        startActivity(intent);
    }

    class AsyncActivity extends AsyncTask  {

        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.show();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            long sum = 0;
            for (long i = 0; i < 100; i++) {
                sum += 1;
            }
            return sum;
        }


        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            //this method will be running on UI thread

            pdLoading.dismiss();
        }



    }
}