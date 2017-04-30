package compsci290.duke.edu.memorymap.memory;

import android.view.View;

/**
 * interface to handle the RecyclerView click
 **/
interface RecyclerViewClickListener {
    void recyclerViewListClicked(View v, int position);
}
