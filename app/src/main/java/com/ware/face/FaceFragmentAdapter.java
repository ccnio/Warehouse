package com.ware.face;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by jianfeng.li on 19-6-5.
 */
public class FaceFragmentAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> mList = new ArrayList<>(2);

    public FaceFragmentAdapter(FragmentManager fm) {
        super(fm);
        mList.add(new FaceMeFragment());
        mList.add(new FaceMoreFragment());
    }

    @Override
    public Fragment getItem(int i) {
        return mList.get(i);
    }

    @Override
    public int getCount() {
        return mList.size();
    }
}
