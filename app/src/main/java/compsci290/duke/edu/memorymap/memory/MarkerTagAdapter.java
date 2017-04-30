package compsci290.duke.edu.memorymap.memory;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import compsci290.duke.edu.memorymap.R;

public class MarkerTagAdapter extends RecyclerView.Adapter<MarkerTagAdapter.MarkerTagHolder> {

    private static RecyclerViewClickListener mListener;

    private List<MarkerTag> mMemoryList;
    private Context mContext;
    private MarkerTagAdapter.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public MarkerTagAdapter(List<MarkerTag> list, Context c, RecyclerViewClickListener itemClickListener) {
        this.mMemoryList = list;
        this.mContext = c;
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
        holder.mMemDate.setText(mMemoryList.get(i).getDate());
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
    public void swap(List<MarkerTag> datas) {
        if(mMemoryList.size() != 0) {
            mMemoryList.clear();
            mMemoryList.addAll(datas);
        } else {
            mMemoryList = datas;
        }
        notifyDataSetChanged();
    }

    public static class MarkerTagHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        static CardView cv;
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

        @Override
        public void onClick(View v) {
            mListener.recyclerViewListClicked(v, getLayoutPosition());
        }
    }

}