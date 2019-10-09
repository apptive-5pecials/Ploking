package com.fivespecial.ploking.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.fivespecial.ploking.adapterEtc.Adapter;
import com.fivespecial.ploking.adapterEtc.ViewpagerAdapter;
import com.fivespecial.ploking.fragment.AlbumFragment;
import com.fivespecial.ploking.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class TabbedActivity extends AppCompatActivity {

    ViewPager viewPager;
    ViewpagerAdapter vpadater;
    private Context context;
    AlbumFragment albumFragment;
    Adapter adapter;

    private ArrayList<String> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);
        context = getApplicationContext();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView(R.drawable.home)));
        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView(R.drawable.picture)));
        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView(R.drawable.camera)));
        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView(R.drawable.map_location)));
        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView(R.drawable.settings)));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        viewPager = findViewById(R.id.viewpager);
        vpadater = new ViewpagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(vpadater);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

    }

    public void refresh(){
        AlbumFragment album = (AlbumFragment)vpadater.getFragment(1);
        album.refresh();
    }

    private View createTabView(int tabIcon) {
        View tabView = LayoutInflater.from(context).inflate(R.layout.custom_tab,null);
        ImageView tab_icon = (ImageView)tabView.findViewById(R.id.tab_icon);
        //if(tabIcon == R.drawable.home) tab_icon.setPadding(45, 45, 45, 45);
        tab_icon.setImageResource(tabIcon);
        return tabView;
    }


}