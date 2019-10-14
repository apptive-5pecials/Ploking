package com.fivespecial.ploking.adapterEtc;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.fivespecial.ploking.fragment.*;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ViewpagerAdapter extends FragmentPagerAdapter {

    private FragmentManager mFragmentManager;
    private Map<Integer,String> mFragmentTags;
    private int PageCount;

    public ViewpagerAdapter(FragmentManager fm, int pageCount) {
        super(fm);

        mFragmentManager = fm;
        mFragmentTags = new HashMap<>();
        this.PageCount = pageCount;

    }

    @NotNull
    @Override
    public Fragment getItem(int i) throws IllegalStateException{
        switch (i){
            case 0:
                return new HomeFragment();
            case 1:
                return new AlbumFragment();
            case 2:
                return new GPSFragment();
            case 3:
                return new SettingFragment();
            default:
                throw new IllegalStateException("올바르지 않은 접근입니다");
        }
    }

    @Override
    public int getCount() {
        return PageCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Page " + position;
    }

    @NotNull
    @Override
    public Object instantiateItem(@NotNull ViewGroup container, int position) {
        Object object = super.instantiateItem(container, position);
        if (object instanceof Fragment) {
            Fragment fragment = (Fragment) object;
            String tag = fragment.getTag();
            mFragmentTags.put(position, tag);
        }
        return object;
    }

    public Fragment getFragment(int position) {
        Fragment fragment = null;
        String tag = mFragmentTags.get(position);
        if (tag != null) {
            fragment = mFragmentManager.findFragmentByTag(tag);
        }
        return fragment;
    }
    public int getItemPosition(@NotNull Object object) {
        return POSITION_NONE;
    }

}
