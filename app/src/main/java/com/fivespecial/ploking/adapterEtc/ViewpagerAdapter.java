package com.fivespecial.ploking.adapterEtc;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.fivespecial.ploking.fragment.*;

import java.util.HashMap;
import java.util.Map;

public class ViewpagerAdapter extends FragmentPagerAdapter {

    private FragmentManager mFragmentManager;
    private Map<Integer,String> mFragmentTags;
    private int PageCount;


    public ViewpagerAdapter(FragmentManager fm, int pageCount) {
        super(fm);
        mFragmentManager = fm;
        mFragmentTags= new HashMap<Integer, String>();
        this.PageCount = pageCount;

    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                return new HomeFragment().newInstance();
            case 1:
                return new AlbumFragment().newInstance();
            case 2:
                return new CameraFragment().newInstance();
            case 3:
                return new GPSFragment().newInstance();
            case 4:
                return new SettingFragment().newInstance();
            default:
                return null;
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

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
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
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
