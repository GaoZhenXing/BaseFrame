package com.jason.baseframe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jason.baseframe.R;


public class HeadLayout extends RelativeLayout {
    private RelativeLayout mHeadGeneralLayout;
    private RelativeLayout mBackLayout;
    private TextView mMidleTitle;
    private RelativeLayout mHeadRightLayout;
    private TextView mRightTitle;

    public HeadLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeadLayout(Context context) {
        super(context);
    }

    public HeadLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public HeadLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
//		通用布局设置
        mHeadGeneralLayout = (RelativeLayout) findViewById(R.id.head_general_layout);
        mMidleTitle = (TextView) findViewById(R.id.head_midle_title);
        mRightTitle = (TextView) findViewById(R.id.right_title);
        mBackLayout = (RelativeLayout) findViewById(R.id.head_btn_back);
        mHeadRightLayout = (RelativeLayout) findViewById(R.id.head_right_Layout);
        super.onFinishInflate();
    }

}