package compsci290.duke.edu.memorymap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Created by taranagar on 4/13/17.
 */

public class MemoryList extends AppCompatActivity {

    private ArrayList<MarkerTag> mMemoryList;
    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        setContentView(R.layout.activity_memorylist);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);

        // get MarkerTag set from database, convert to ArrayList
        // markerTagSet = MarkerTagDbHelper.queryAllMarkerTags();
        // mMemoryList = new ArrayList<MarkerTag>(markerTagSet);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setHasFixedSize(true);

        initializeAdapter();
    }

    private void initializeAdapter() {
        MarkerTagAdapter adapter = new MarkerTagAdapter(mMemoryList, this);
        mRecyclerView.setAdapter(adapter);
    }

}
