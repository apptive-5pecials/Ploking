package com.fivespecial.ploking.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fivespecial.ploking.Activity.FullimageActivity;
import com.fivespecial.ploking.AdapterEtc.Adapter;
import com.fivespecial.ploking.AdapterEtc.DbHelper;
import com.fivespecial.ploking.AdapterEtc.ViewpagerAdapter;
import com.fivespecial.ploking.MemberClass.Album;
import com.fivespecial.ploking.R;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class PhotoAlbum extends Fragment {

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    Adapter adapter ;
    List<Album> list;
    DbHelper database;
    ViewpagerAdapter vpadpater;
    Context mContext;

    public PhotoAlbum(){

    }

    public static PhotoAlbum newInstance(){
        return new PhotoAlbum();
    }

    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photoalbum, null, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getContext());
        list = new ArrayList<Album>();

        database = new DbHelper(getActivity());
        list = database.getdata();

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new Adapter(this, list);

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Album photo = list.get(pos);
                Intent intent = new Intent(getContext(), FullimageActivity.class);
                intent.putExtra("name",photo.getFile_name());
                intent.putExtra("path",photo.getPath());
                intent.putExtra("id",photo.getId());

                startActivityForResult(intent, 105);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 105:
                    refresh();
                    break;
            }
        }
    }

    public void refresh() {
       FragmentTransaction transaction = getFragmentManager().beginTransaction();
       transaction.detach(this).attach(this).commit();
   }
}
