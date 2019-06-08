package com.ware.face;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.ware.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jianfeng.li on 19-6-5.
 */
public class FaceFragment extends Fragment {
//    @BindView(R.id.mViewPager)
    ViewPager mViewPager;
//    @BindView(R.id.mRadioGroup)
//    RadioGroup mRadioGroup;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_face, container,false);
//        ButterKnife.bind(this,view);

        view.setBackgroundColor(Color.WHITE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);

    }

    protected void initView(View view) {
        mViewPager = view.findViewById(R.id.mViewPager);
        mViewPager.setAdapter(new FaceFragmentAdapter(getFragmentManager()));
//        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int i) {
//                mRadioGroup.check(i == 0 ? R.id.me : R.id.more);
//            }
//        });

//        mRadioGroup.setOnCheckedChangeListener((group, checkedId) -> mViewPager.setCurrentItem(checkedId == R.id.me ? 0 : 1));
    }
}
