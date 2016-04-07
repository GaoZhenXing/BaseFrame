package com.jason.baseframe.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.Uri;
import android.text.TextUtils;

import java.io.File;

/**
 * 跟App相关的辅助类
 *
 * @author zhy
 */
public class AppUtils {

    private AppUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");

    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 安装指定文件路径的apk文件
     *
     * @param path
     */
    public void installApk(Context context, String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(path)),
                "application/vnd.android.package-archive");
        context.startActivity(intent); // 安装新版本
    }

    /**
     * 获取一个应用程序的签名信息
     *
     * @param pkgname 应用程序的包名
     * @return
     */
    public String getSignature(Context context, String pkgname) {
        boolean isEmpty = TextUtils.isEmpty(pkgname);
        if (isEmpty) {
            return null;
        } else {
            try {
                PackageManager manager = context.getPackageManager();
                PackageInfo packageInfo = manager.getPackageInfo(pkgname, PackageManager.GET_SIGNATURES);
                Signature[] signatures = packageInfo.signatures;
                StringBuilder builder = new StringBuilder();
                for (Signature signature : signatures) {
                    builder.append(signature.toCharsString());
                }
                String signature = builder.toString();
                return signature;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
