package com.fivespecial.ploking.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fivespecial.ploking.adapterEtc.Adapter;
import com.fivespecial.ploking.adapterEtc.DbHelper;
import com.fivespecial.ploking.adapterEtc.ViewpagerAdapter;
import com.fivespecial.ploking.model.Album;
import com.fivespecial.ploking.R;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class AlbumFragment extends Fragment {

    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    Adapter adapter ;
    List<Album> list;
    DbHelper database;
    ViewpagerAdapter vpadpater;
    Context mContext;

    public AlbumFragment(){

    }

    public static AlbumFragment newInstance(){
        return new AlbumFragment();
    }

    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, null, false);

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
                Bundle args= new Bundle();
                args.putString("key","value");
                args.putString("name",photo.getFile_name());
                args.putString("path",photo.getPath());
                args.putDouble("longitude", photo.getLongitude());
                args.putDouble("latitude", photo.getLatitude());
                FragmentDialog dialog=new FragmentDialog();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        refresh();
                    }
                });
                dialog.setArguments(args);
                dialog.show(getActivity().getSupportFragmentManager(),"tag");
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
