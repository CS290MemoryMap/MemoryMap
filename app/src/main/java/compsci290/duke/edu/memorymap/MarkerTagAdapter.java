package compsci290.duke.edu.memorymap;

/**
 * Created by taranagar on 4/14/17.
 */

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MarkerTagAdapter extends RecyclerView.Adapter<MarkerTagAdapter.MarkerTagHolder> {

    private ArrayList<MarkerTag> mMemoryList;
    private Context mContext;

    public MarkerTagAdapter(ArrayList<MarkerTag> list, Context c) {
        this.mMemoryList = list;
        this.mContext = c;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public MarkerTagHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.memorylist, parent, false);
        MarkerTagHolder mth = new MarkerTagHolder(v);
        return mth;
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

    public void swap(ArrayList<MarkerTag> datas) {
        if(mMemoryList != null) {
            mMemoryList.clear();
            mMemoryList.addAll(datas);
        } else {
            mMemoryList = datas;
        }
        notifyDataSetChanged();
    }

    public static class MarkerTagHolder extends RecyclerView.ViewHolder {

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
        }
    }

}