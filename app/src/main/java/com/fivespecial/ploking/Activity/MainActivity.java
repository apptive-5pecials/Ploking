package com.fivespecial.ploking.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.provider.ContactsContract;

import com.fivespecial.ploking.AdapterEtc.Adapter;
import com.fivespecial.ploking.AdapterEtc.DbHelper;
import com.fivespecial.ploking.Fragment.PhotoAlbum;
import com.fivespecial.ploking.AdapterEtc.ViewpagerAdapter;
import com.fivespecial.ploking.R;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    ViewpagerAdapter vpadater;
    PhotoAlbum photoAlbum;
    Adapter adapter;

    public  static DbHelper sqLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sqLiteHelper = new DbHelper(this);

        viewPager = findViewById(R.id.viewpager);
        vpadater = new ViewpagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(vpadater);

    }

    public void refresh(){
        PhotoAlbum album = (PhotoAlbum)vpadater.getFragment(1);
        album.refresh();
    }
}
