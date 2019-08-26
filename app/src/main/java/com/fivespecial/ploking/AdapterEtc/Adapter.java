package com.fivespecial.ploking.AdapterEtc;


import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fivespecial.ploking.Fragment.AlbumFragment;
import com.fivespecial.ploking.MemberClass.Album;
import com.fivespecial.ploking.R;

import java.io.File;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private List<Album> albumlist;
    private AlbumFragment activity;

    public interface  OnItemClickListener{
        void onItemClick(View v, int pos);


    }
    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }


    public Adapter(AlbumFragment activity, List<Album> albumlist){
        this.activity= activity;
        this.albumlist= albumlist;
    }
    @Override
    public int getItemCount() {
        return albumlist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img=(ImageView)itemView.findViewById(R.id.imgPhoto);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity)itemView.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int devicewidth = displaymetrics.widthPixels / 3;

            img.getLayoutParams().width = devicewidth;
            img.getLayoutParams().height = devicewidth;


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos= getAdapterPosition();
                    if(pos != RecyclerView.NO_POSITION){
                        if(mListener != null){
                            mListener.onItemClick(v, pos);
                        }
                    }
                }
            });
        }


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items,parent,false);
        ViewHolder viewHolder= new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Album data= albumlist.get(position);

        File f= new File(data.getPath(),data.getFile_name());

       /* Bitmap b= null;
        try {
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        Glide.with(holder.img.getContext()).load(f).into(holder.img);

        //holder.img.setImageBitmap(b);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}