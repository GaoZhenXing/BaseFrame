package com.jason.baseframe.manager;

import android.app.Activity;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * © 2012 amsoft.cn
 * 名称：ActivityManager.java
 * 描述：用于处理退出程序时可以退出所有的activity，而编写的通用类
 *
 * @author 还如一梦中
 * @version v1.0
 * @date 2015年4月10日 下午6:10:28
 */
public class ActivityManager {

    private Stack<Activity> activityList = new Stack<Activity>();
    private Stack<Activity> orderList = new Stack<Activity>();

    private static ActivityManager instance;

    private ActivityManager() {
    }

    /**
     * 单例模式中获取唯一的AbActivityManager实例.
     *
     * @return
     */
    public static ActivityManager getInstance() {
        if (null == instance) {
            instance = new ActivityManager();
        }
        return instance;
    }

    /**
     * 添加Activity到容器中.
     *
     * @param activity
     */
    public void addOrderActivity(Activity activity) {
        orderList.add(activity);
    }

    /**
     * 移除Activity从容器中.
     *
     * @param activity
     */
    public void removeOrderActivity(Activity activity) {
        orderList.remove(activity);
    }

    /**
     * 遍历所有Activity并finish.
     */
    public void clearOrderActivity() {
        for (Activity activity : orderList) {
            if (activity != null) {
                activity.finish();
            }
        }
    }

    /**
     * 添加Activity到容器中.
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    /**
     * 移除Activity从容器中.
     *
     * @param activity
     */
    public void removeActivity(Activity activity) {
        activityList.remove(activity);
    }

    /**
     * 遍历所有Activity并finish.
     */
    public void clearAllActivity() {
        for (Activity activity : activityList) {
            if (activity != null) {
                activity.finish();
            }
        }
    }


}