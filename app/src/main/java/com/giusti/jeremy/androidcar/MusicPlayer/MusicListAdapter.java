package com.giusti.jeremy.androidcar.MusicPlayer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.giusti.jeremy.androidcar.R;
import com.giusti.jeremy.androidcar.Utils.Utils;

import java.util.ArrayList;

/**
 * Created by jérémy on 05/05/2016.
 */
public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder> {

    private ArrayList<MusicFile> musicList;
    private IItemEventListener itemListener;

    public MusicListAdapter(ArrayList<MusicFile> musicList, IItemEventListener itemListener) {
        this.musicList = musicList;
        this.itemListener = itemListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_music, parent, false);
        final ViewHolder vh = new ViewHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemListener.onItemClick(vh);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.data = musicList.get(position);
        holder.musicDurationTV.setText(Utils.getDisplayableTime(holder.data.getDuration()));
        holder.musicTitleTV.setText(holder.data.getTitle());
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public MusicFile data;
        public TextView musicTitleTV;
        public TextView musicDurationTV;

        public ViewHolder(View v) {
            super(v);
            musicTitleTV = (TextView) v.findViewById(R.id.im_title_tv);
            musicDurationTV = (TextView) v.findViewById(R.id.im_duration_tv);
        }
    }

    public interface IItemEventListener {
        void onItemClick(ViewHolder item);
    }

    public void updateList(ArrayList<MusicFile> newList) {
        this.musicList = newList;
        notifyDataSetChanged();

    }


}
