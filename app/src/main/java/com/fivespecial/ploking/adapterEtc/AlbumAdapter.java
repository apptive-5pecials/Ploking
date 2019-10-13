package com.fivespecial.ploking.adapterEtc;


import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fivespecial.ploking.model.Album;
import com.fivespecial.ploking.R;

import java.io.File;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    public interface OnAlbumItemClickListener {
        void onItemClick(View v, int pos);
    }

    private List<Album> albumList;
    private OnAlbumItemClickListener mListener = null;

    public AlbumAdapter(List<Album> albumList){
        this.albumList = albumList;
    }


    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items,parent,false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AlbumViewHolder holder, int position) {
        Album data= albumList.get(position);
        File imageFile = new File(data.getPath(),data.getFile_name());

        holder.bindTo(imageFile);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public void setOnItemClickListener(OnAlbumItemClickListener listener) {
        this.mListener = listener;
    }

    class AlbumViewHolder extends RecyclerView.ViewHolder{

        private ImageView photoImageView;

        private AlbumViewHolder(@NonNull View itemView) {
            super(itemView);

            photoImageView = itemView.findViewById(R.id.imgPhoto);

        }

        private void bindTo(File imageFile) {

            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity) itemView.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int deviceSizeParams = displaymetrics.widthPixels / 3;

            photoImageView.getLayoutParams().width = deviceSizeParams;
            photoImageView.getLayoutParams().height = deviceSizeParams;


            Glide.with(photoImageView.getContext()).load(imageFile).into(photoImageView);

            itemView.setOnClickListener(view -> {

                if(getAdapterPosition() != RecyclerView.NO_POSITION){
                    if(mListener != null){
                        mListener.onItemClick(view, getAdapterPosition());
                    }
                }
            });
        }


    }

}