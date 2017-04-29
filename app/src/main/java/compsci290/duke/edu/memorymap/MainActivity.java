package compsci290.duke.edu.memorymap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickMapBtn(View v)
    {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    /*public void onClickCreateMemory(View v){
        Intent intent = new Intent(this, EditableMemoryActivity.class);
        startActivity(intent);
    }*/

    public void onClickListBtn(View v) {
        Intent intent = new Intent(this, MemoryListActivity.class);
        startActivity(intent);
    }

}