package com.jason.baseframe;

import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.jason.baseframe.adapter.PagerAdapter;
import com.jason.baseframe.base.BaseActivity;
import com.jason.baseframe.fragment.MainFragment;
import com.jason.baseframe.fragment.TabFragment;
import com.jason.baseframe.utils.T;
import com.jason.baseframe.view.TabButton;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MainActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, BaseActivity.OnBackButtonClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ViewPager mViewPager;
    private ImageView imageView;
    private List<Fragment> mFragmentList;
    private String[] mTitles = new String[]{"First Fragment!", "Second Fragment!", "Third Fragment!", "Fourth Fragment!"};
    private List<TabButton> mTabButtonList = new ArrayList<TabButton>();
    PagerAdapter adapter;


    @Override
    protected int headLayoutResId() {
        return R.layout.head_layout;
    }

    @Override
    protected int layoutResID() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreate(FragmentManager manager, Bundle savedInstanceState) {
        intView();
        setListener();
        initEvent();
        pagerAdapter();
        Log.d(TAG, "onCreate: ");

    }

    @Override
    protected void handler(Message msg) {

    }

    protected void intView() {
        imageView = (ImageView) findViewById(R.id.imageView);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mTabButtonList.add((TabButton) findViewById(R.id.tab_first));
        mTabButtonList.add((TabButton) findViewById(R.id.tab_second));
        mTabButtonList.add((TabButton) findViewById(R.id.tab_third));
        mTabButtonList.add((TabButton) findViewById(R.id.tab_fourth));
        mTabButtonList.get(0).setAlpha(1.0f);


    }


    protected void setListener() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTabButtonList.get(mViewPager.getCurrentItem()).addMessageNumber(1);
            }
        });
        setOnBackButtonListener(this);
    }

    @Override
    public void onBackPressed() {
        if (mLoadingView.getVisibility() == View.VISIBLE) {
            mLoadingView.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }


    private void pagerAdapter() {
        mFragmentList = new ArrayList<Fragment>();


        for (String title : mTitles) {
            TabFragment tabFragment = new TabFragment();
            Bundle args = new Bundle();
            args.putString("title", title);
            tabFragment.setArguments(args);
            mFragmentList.add(tabFragment);
        }

        adapter = new PagerAdapter(fragmentManager, mFragmentList);
        mViewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void initEvent() {
        for (int i = 0; i < mTabButtonList.size(); i++) {
            mTabButtonList.get(i).setOnClickListener(this);
            mTabButtonList.get(i).setTag(i);
        }
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        int number = (Integer) v.getTag();
        changeAlpha(number);
        mViewPager.setCurrentItem(number, false);
    }

    public void changeAlpha(int number) {
        for (TabButton btn : mTabButtonList) {
            btn.setAlpha(0f);
        }
        mTabButtonList.get(number).setAlpha(1.0f);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffsetPixels != 0) {
            mTabButtonList.get(position).setAlpha(1 - positionOffset);
            mTabButtonList.get(position + 1).setAlpha(positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void OnBackButtonClick(View v) {
        T.show(this, "点击了返回按钮");
        String [] d=null;
        String s = d[3];
    }


}
