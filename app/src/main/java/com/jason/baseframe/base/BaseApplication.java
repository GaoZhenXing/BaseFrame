package com.jason.baseframe.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.jason.baseframe.utils.CrashHandler;
import com.jason.baseframe.utils.NetUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.Stack;

/**
 * Created by Administrator on 2016/2/13.
 */
public class BaseApplication extends Application {
    private static BaseApplication mApplication;
    /*Activity堆*/
    private Stack<Activity> activityStack = new Stack<Activity>();

    public static int mNetWorkState;
    public synchronized static BaseApplication getInstance() {
        return mApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initData();
    }

    private void initData() {
        if (mApplication == null) {
            mApplication = this;
        }
        mNetWorkState = NetUtil.getNetworkState(this);
        //当程序发生Uncaught异常的时候,由该类来接管程序,一定要在这里初始化
        CrashHandler.getInstance().init(this);
        initImageLoader(mApplication);
    }

    //    初始化ImageLoader
    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }







    public void addActivity(final Activity curAT) {
        if (null == activityStack) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(curAT);
    }

    public void removeActivity(final Activity curAT) {
        if (null == activityStack) {
            activityStack = new Stack<Activity>();
        }
        activityStack.remove(curAT);
    }

    //获取最后一个Activity
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    //返回寨内Activity的总数
    public int howManyActivities() {
        return activityStack.size();
    }

    //关闭所有Activity
    public void finishAllActivities() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

}
