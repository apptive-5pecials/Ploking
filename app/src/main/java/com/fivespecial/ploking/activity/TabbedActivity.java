package com.fivespecial.ploking.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fivespecial.ploking.adapterEtc.ViewpagerAdapter;
import com.fivespecial.ploking.fragment.AlbumFragment;
import com.fivespecial.ploking.R;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class TabbedActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private ViewpagerAdapter mPagerAdapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);

        tabLayout = findViewById(R.id.activity_tabbed_tab_layout);
        mViewPager = findViewById(R.id.activity_tabbed_view_pager);

        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView(R.drawable.v2_selector_tab_home, "Home")));
        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView(R.drawable.v2_selector_tab_gallery, "Gallery")));
        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView(R.drawable.v2_selector_tab_maps, "Maps")));
        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView(R.drawable.v2_selector_tab_settings, "Settings")));

        tabLayout.addOnTabSelectedListener(setTabSelectedListener());

        mPagerAdapter = new ViewpagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        if(tabLayout.getTabAt(0) != null) {
            // 첫번째 탭의 텍스트 색상을 Selected 로 변경
            setHomeTabTextColor(Objects.requireNonNull(tabLayout.getTabAt(0)));
        }
    }

    public void refresh(){
        AlbumFragment album = (AlbumFragment) mPagerAdapter.getFragment(1);
        album.refresh();
    }

    private View createTabView(int tabIcon, String tabDescription) {

        View tabView = View.inflate(this, R.layout.custom_tab, null);

        ImageView tab_icon = tabView.findViewById(R.id.tab_icon);
        TextView tab_description = tabView.findViewById(R.id.tab_description);

        tab_icon.setImageResource(tabIcon);
        tab_description.setText(tabDescription);

        return tabView;
    }

    private TabLayout.OnTabSelectedListener setTabSelectedListener() {

        return new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());

                if(tab.getCustomView() != null) {
                    // 선택된 탭의 텍스트 색상을 변경
                    TextView tabDescription = tab.getCustomView().findViewById(R.id.tab_description);
                    tabDescription.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.v2_color_tab_selected));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if(tab.getCustomView() != null) {
                    // 선택 해제된 탭의 텍스트 색상을 변경
                    TextView tabDescription = tab.getCustomView().findViewById(R.id.tab_description);
                    tabDescription.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.v2_color_tab_unselected));
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        };
    }

    private void setHomeTabTextColor(TabLayout.Tab tab) {

        if(tab.getCustomView() != null) {
            TextView tabDescription = tab.getCustomView().findViewById(R.id.tab_description);
            tabDescription.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.v2_color_tab_selected));
        }
    }

}
