package compsci290.duke.edu.memorymap.memory;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import compsci290.duke.edu.memorymap.R;

class MarkerTagAdapter extends RecyclerView.Adapter<MarkerTagAdapter.MarkerTagHolder> {

    private static RecyclerViewClickListener mListener;
    private List<MarkerTag> mMemoryList;

    MarkerTagAdapter(List<MarkerTag> list, RecyclerViewClickListener itemClickListener) {
        this.mMemoryList = list;
        mListener = itemClickListener;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public MarkerTagHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.memorylist, parent, false);
        return new MarkerTagHolder(v);
    }

    @Override
    public void onBindViewHolder(MarkerTagHolder holder, int i) {
        holder.mMemTitle.setText(mMemoryList.get(i).getTitle());
        holder.mMemDescription.setText(mMemoryList.get(i).getDetails());
        Date date = mMemoryList.get(i).getDateDate();
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        if(date != null) holder.mMemDate.setText(format.format(date));
        holder.mMemImage.setImageBitmap(mMemoryList.get(i).getImg());
    }

    @Override
    public int getItemCount() {
        return mMemoryList.size();
    }

    /*
        Updates the data in the MarkerTagAdapter and notifies
        the view that is has changed
     */
    void swap(List<MarkerTag> datas) {
        if(mMemoryList.size() != 0) {
            mMemoryList.clear();
            mMemoryList.addAll(datas);
        } else {
            mMemoryList = datas;
        }
        notifyDataSetChanged();
    }

    class MarkerTagHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CardView cv;
        ImageView mMemImage;
        TextView mMemTitle;
        TextView mMemDate;
        TextView mMemDescription;

        MarkerTagHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            mMemImage = (ImageView) itemView.findViewById(R.id.mem_photo);
            mMemTitle = (TextView) itemView.findViewById(R.id.mem_title);
            mMemDate = (TextView) itemView.findViewById(R.id.mem_date);
            mMemDescription = (TextView) itemView.findViewById(R.id.mem_description);

            cv.setOnClickListener(this);
        }

        /**
         * defines onClick for RecyclerView used in MemoryList
         **/
        @Override
        public void onClick(View v) {
            mListener.recyclerViewListClicked(v, getLayoutPosition());
        }
    }

}