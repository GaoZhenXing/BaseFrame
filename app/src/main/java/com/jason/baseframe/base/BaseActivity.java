package com.jason.baseframe.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jason.baseframe.R;
import com.jason.baseframe.constant.Constant;
import com.jason.baseframe.entity.MData;
import com.jason.baseframe.handler.UIHandler;
import com.jason.baseframe.intface.RespHandleListener;
import com.jason.baseframe.utils.NetUtil;
import com.jason.baseframe.view.HeadLayout;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by Jason on 2016/2/16.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private final static String TAG = BaseActivity.class.getClass().getSimpleName();
    protected FragmentManager fragmentManager;
    protected BaseApplication baseApplication;
    protected Context mContext;    //全局的Context
    private TextView mMsgAlert;//消息提醒
    private RelativeLayout parentLayout;//把父类activity和子类activity的view都add到这里
    protected RelativeLayout mContentLayout;
    protected LinearLayout mHeadGroup;
    protected static UIHandler handler = new UIHandler(Looper.getMainLooper());
    protected AVLoadingIndicatorView mLoadingView;
    private HeadLayout headLayout;
    private RelativeLayout mHeadBtnBack;
    public OnBackButtonClickListener mOnBackButtonClickListener;

    /**
     * 布局文件ID
     *
     * @return
     */
    protected abstract int layoutResID();


    ;

    /**
     * 头部布局文件ID
     *
     * @return
     */
    protected abstract int headLayoutResId();

    /**
     * @param msg 消息
     */
    protected abstract void handler(Message msg);

    /**
     * Activity的生命周期onCreate()方法
     *
     * @param manager            管理Fragment的对象，如果使用Fragment可以跟本框架做到无缝的结合
     * @param savedInstanceState Activity的状态保存
     */
    protected abstract void onCreate(FragmentManager manager, Bundle savedInstanceState);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        initParameter();//初始化参数
        initView();//初始化数据
        int headLayoutResId = headLayoutResId();//子类实现的方法
        int layoutResID = layoutResID();//子类实现的方法
        setHeadLayout(headLayoutResId);
        setContentView(layoutResID);//设置内容视图

        setHandler();//设置handler
        onCreate(fragmentManager, savedInstanceState);//子类实现的onCreate 方法
    }

    private void setHeadLayout(int headLayoutResId) {
        if (headLayoutResId != 0) {
            if (layoutResID() == R.layout.head_layout) {
                LayoutInflater.from(this).inflate(headLayoutResId, mHeadGroup, true);
            }
        } else {
            mHeadGroup.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 你可以添加多个Action捕获
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_NETWORK_CHANGE);
        filter.addAction(Constant.ACTION_PUSH_DATA);
        filter.addAction(Constant.ACTION_NEW_VERSION);
        registerReceiver(receiver, filter);
        //还可能发送统计数据，比如第三方的SDK 做统计需求
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        //还可能发送统计数据，比如第三方的SDK 做统计需求
    }

    @Override
    public void setContentView(int layoutResID) {
        Log.i(TAG, "setContentView: ");
        if (layoutResID != 0) {
            LayoutInflater.from(this).inflate(layoutResID, mContentLayout, true);
        }
    }

    @Override
    public void setContentView(View view) {
        parentLayout.addView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        parentLayout.addView(view, params);
    }

    /**
     * 退出
     */
    @Override
    public void finish() {
        baseApplication.removeActivity(this);
        super.finish();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    // 横竖屏切换，键盘等
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        try {
            super.onRestoreInstanceState(savedInstanceState);
        } catch (Exception e) {
        }
    }

    private void setHandler() {
        handler.setHandler(new UIHandler.IHandler() {
            public void handleMessage(Message msg) {
                handler(msg);//有消息就提交给子类实现的方法
            }
        });
    }

    private void initParameter() {
        fragmentManager = getSupportFragmentManager();
        mContext = BaseActivity.this;
        baseApplication = BaseApplication.getInstance();
        baseApplication.addActivity(this);
    }

    private void initView() {
        initContentView(R.layout.activity_base);
        mMsgAlert = (TextView) findViewById(R.id.tvAlert);
        mContentLayout = (RelativeLayout) findViewById(R.id.layoutContent);
        mHeadGroup = (LinearLayout) findViewById(R.id.headGroup);
        mLoadingView = (AVLoadingIndicatorView) findViewById(R.id.loadingView);

        headLayout = (HeadLayout) mHeadGroup.findViewById(R.id.layout_head);
        mHeadBtnBack = (RelativeLayout) headLayout.findViewById(R.id.head_btn_back);
        mHeadBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnBackButtonClickListener != null) {
                    mOnBackButtonClickListener.OnBackButtonClick(v);
                }
            }
        });
    }


    protected void addLeftMenu(boolean enable) {
        // 如果你的项目有侧滑栏可以处理此方法
        if (enable) { // 是否能有侧滑栏

        } else {

        }
    }

    /**
     * 初始化contentview
     */
    private void initContentView(int layoutResId) {
        Log.i(TAG, "initContentView: ");
        ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
        viewGroup.removeAllViews();
        parentLayout = new RelativeLayout(this);
        viewGroup.addView(parentLayout);
        LayoutInflater.from(this).inflate(layoutResId, parentLayout, true);
    }

    /**
     * 获取全局的Context
     */
    public Context getContext() {
        return mContext;
    }


    /**
     * 默认退出
     */
    public void defaultFinish() {
        super.finish();
    }


    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 处理各种情况
            String action = intent.getAction();
            if (Constant.ACTION_NETWORK_CHANGE.equals(action)) { // 网络发生变化
                // 处理网络问题
                int networkState = NetUtil.getNetworkState(context);
                switch (networkState) {
                    case NetUtil.NETWORN_NONE:
                        Log.e(TAG, "onReceive:无网络 ");
                        mMsgAlert.setVisibility(View.VISIBLE);
                        mMsgAlert.setText("无网络");
                        mMsgAlert.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                NetUtil.openSetting(BaseActivity.this);
                            }
                        });
                        break;
                    case NetUtil.NETWORN_MOBILE:
                        mMsgAlert.setVisibility(View.GONE);
                        mMsgAlert.setText("移动网络");
                        Log.e(TAG, "onReceive: 移动网络");
                        mMsgAlert.setOnClickListener(null);
                        break;
                    case NetUtil.NETWORN_WIFI:
                        mMsgAlert.setVisibility(View.GONE);
                        mMsgAlert.setText("WIFI");
                        Log.e(TAG, "onReceive: wifi");
                        mMsgAlert.setOnClickListener(null);
                        break;
                }

            } else if (Constant.ACTION_PUSH_DATA.equals(action)) { // 可能有新数据
                Bundle b = intent.getExtras();
                MData<BaseEntity> mdata = (MData<BaseEntity>) b.get("data");
            } else if (Constant.ACTION_NEW_VERSION.equals(action)) { // 可能发现新版本
                // VersionDialog 可能是版本提示是否需要下载的对话框
            }


        }
    };


    //网络错误种类
    protected int getNetworkErrorTip(int code) {

        Log.d(TAG, "getNetworkErrorTip - code = \"" + code + "\"");

        int textResId = R.string.error_network_time_out;
        switch (code) {

            case RespHandleListener.ErrCode.ERR_NETWORK_NOT_AVAILABLE:
                textResId = R.string.error_network_not_available;
                break;

            case RespHandleListener.ErrCode.ERR_SERVER_ERROR:
                textResId = R.string.error_network_server_busy;
                break;

            case RespHandleListener.ErrCode.ERR_TIME_OUT:
            case RespHandleListener.ErrCode.ERR_CLIENT_ERROR:
            case RespHandleListener.ErrCode.ERR_UNKNOWN_ERROR:
                break;
            default:
                break;
        }
        Log.d(TAG, "getNetworkErrorTip - textResId = \"" + textResId + "\"");
        return textResId;

    }

//返回按钮点击监听
    public void setOnBackButtonListener(OnBackButtonClickListener onBackButtonClickListener) {
        this.mOnBackButtonClickListener = onBackButtonClickListener;
    }

    public interface OnBackButtonClickListener {
        void OnBackButtonClick(View v);
    }
}
