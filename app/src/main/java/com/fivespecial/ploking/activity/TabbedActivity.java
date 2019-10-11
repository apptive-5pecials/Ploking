package com.fivespecial.ploking.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.fivespecial.ploking.adapterEtc.ViewpagerAdapter;
import com.fivespecial.ploking.fragment.AlbumFragment;
import com.fivespecial.ploking.R;
import com.google.android.material.tabs.TabLayout;

public class TabbedActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private ViewpagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);

        TabLayout tabLayout = findViewById(R.id.tabs);

        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView(R.drawable.home)));
        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView(R.drawable.picture)));
        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView(R.drawable.camera)));
        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView(R.drawable.map_location)));
        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView(R.drawable.settings)));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        mViewPager = findViewById(R.id.viewpager);
        mPagerAdapter = new ViewpagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        mViewPager.setAdapter(mPagerAdapter);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

    }

    public void refresh(){
        AlbumFragment album = (AlbumFragment) mPagerAdapter.getFragment(1);
        album.refresh();
    }

    private View createTabView(int tabIcon) {

        View tabView = View.inflate(this, R.layout.custom_tab, null);

        ImageView tab_icon = tabView.findViewById(R.id.tab_icon);
        tab_icon.setImageResource(tabIcon);
        //if(tabIcon == R.drawable.home) tab_icon.setPadding(45, 45, 45, 45);

        return tabView;
    }


}
