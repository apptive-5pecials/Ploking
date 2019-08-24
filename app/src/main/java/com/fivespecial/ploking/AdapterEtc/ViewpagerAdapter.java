package com.fivespecial.ploking.AdapterEtc;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.fivespecial.ploking.Fragment.Camera1;
import com.fivespecial.ploking.Fragment.PhotoAlbum;

import java.util.HashMap;
import java.util.Map;

public class ViewpagerAdapter extends FragmentPagerAdapter {

    private FragmentManager mFragmentManager;
    private Map<Integer,String> mFragmentTags;
    private static int NUM_ITEMS=2;


    public ViewpagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
        mFragmentTags= new HashMap<Integer, String>();

    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                return new Camera1().newInstance();
            case 1:
                return new PhotoAlbum().newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
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
