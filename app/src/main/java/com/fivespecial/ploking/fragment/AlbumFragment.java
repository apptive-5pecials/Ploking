package com.fivespecial.ploking.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fivespecial.ploking.adapterEtc.AlbumAdapter;
import com.fivespecial.ploking.adapterEtc.AlbumDbHelper;
import com.fivespecial.ploking.base.BaseFragment;
import com.fivespecial.ploking.model.Album;
import com.fivespecial.ploking.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class AlbumFragment extends BaseFragment {

    private RecyclerView mAlbumRecyclerView;
    private AlbumAdapter mAlbumAdapter;
    private List<Album> mAlbumList;
    private AlbumDbHelper mDbHelper;

    @Override
    public int getResourceId() {
        return R.layout.fragment_album;
    }

    @Override
    public void initComponent(@NotNull View view) {

        mDbHelper = new AlbumDbHelper(getActivity());

        mAlbumList = mDbHelper.getAlbumData();
        mAlbumAdapter = new AlbumAdapter(mAlbumList);

        if(getActivity() != null) {
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 3);
            mAlbumRecyclerView = view.findViewById(R.id.recyclerView);
            mAlbumRecyclerView.setLayoutManager(layoutManager);
            mAlbumRecyclerView.setAdapter(mAlbumAdapter);
        }

    }

    @Override
    public void setupListeners(@NotNull View view) {

        mAlbumAdapter.setOnItemClickListener((v, position) -> {
            Album photo = mAlbumList.get(position);
            Bundle args = new Bundle();
            args.putString("key","value");
            args.putString("name",photo.getFile_name());
            args.putString("path",photo.getPath());
            args.putDouble("longitude", photo.getLongitude());
            args.putDouble("latitude", photo.getLatitude());

            if(getActivity() != null) {
                FragmentDialog dialog = new FragmentDialog();
                dialog.setOnDismissListener( dialogInterface -> refresh() );
                dialog.setArguments(args);
                dialog.show(getActivity().getSupportFragmentManager(),"tag");
            }

        });

    }

    @Override
    public void setupImplementation() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 105) {
                refresh();
            }
        }
    }

    public void refresh() {
        if (getFragmentManager() != null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.detach(this).attach(this).commit();
        }
    }

}
