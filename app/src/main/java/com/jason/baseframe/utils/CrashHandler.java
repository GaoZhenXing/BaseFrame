package com.jason.baseframe.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.os.StatFs;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.jason.baseframe.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * jason
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告.
 */
public class CrashHandler implements UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    //防止异常信息
    private Map<String, String> infos = new HashMap<String, String>();
    // 系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    // CrashHandler实例
    private static CrashHandler instance;
    // 程序的Context对象
    private Context mContext;
    // 异常时间
    private String time;
    //err文件名
    private String fileName;
    // 路径

    //保证只有一个CrashHandler实例
    private CrashHandler() {
    }

    //获取CrashHandler实例 ,单例模式
    public static CrashHandler getInstance() {
        if (instance == null)
            instance = new CrashHandler();
        return instance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该重写的方法来处理
     */
    public void uncaughtException(Thread thread, Throwable ex) {
        //用于格式化日期,作为日志文件名的一部分
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        time = dateFormat.format(new Date());

        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果自定义的没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        }

    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex 异常信息
     * @return true 如果处理了该异常信息 true;否则返回false.
     */
    public boolean handleException(Throwable ex) {
        if (ex == null || mContext == null) {
            return false;
        }
        final String crashReport = getCrashReport(mContext, ex);
        final File file = save2File(crashReport);
//        showToast(file);
        new Thread() {
            public void run() {
                Looper.prepare();
                sendAppCrashReport(mContext, crashReport, file);
                Looper.loop();
            }

        }.start();
        return true;
    }

    private File save2File(String crashReport) {

        StringBuffer sb = new StringBuffer().append("Error").append(time).append(".txt");
        fileName = sb.toString();
        // 收集设备硬件信息
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + " ";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());

            } catch (Exception e) {
            }
        }

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            try {
                //存储路径，是sd卡的crash文件夹
                File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "LOG");
                if (!dir.exists())
                    dir.mkdir();
                File file = new File(dir, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(crashReport.toString().getBytes());
                fos.close();
                return file;
            } catch (Exception e) {
                //sd卡存储，记得加上权限，不然这里会抛出异常

                Log.e(TAG, "save2File error" + e.getMessage());
            }
        }
        return null;
    }

    private void sendAppCrashReport(final Context context, final String crashReport, final File file) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(R.string.app_error)
                .setMessage(R.string.app_error_message)
                .setPositiveButton(R.string.submit_report,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    //TODO 上传到服务器
                                    if (file != null) {
                                        Uri uri = Uri.fromFile(file);
                                        L.i(TAG, "Error URL: " + uri.toString());

                                    }

                                } catch (Exception e) {
                                    L.e(TAG, "error" + ":" + e.getMessage());
                                } finally {
                                    dialog.dismiss();
                                    // 退出
                                    android.os.Process.killProcess(Process.myPid());
                                    System.exit(1);
                                }
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                // 退出
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        });
        AlertDialog dialog = builder.create();
        //需要的窗口句柄方式，没有这句会报错的
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }
   private void showToast(final File file){
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext,"错误日志保存在"+Uri.fromFile(file).toString(),Toast.LENGTH_LONG).show();
//                Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
    }

    /**
     * 获取APP崩溃异常报告
     *
     * @param ex
     * @return
     */
    private String getCrashReport(Context context, Throwable ex) {

        Log.e("===剩余内存===》：", "" + getAvailaleSize() + "M");
        System.out.println("*************程序异常**************");
        System.out.println("异常捕获：" + ex.getMessage());
        System.out.println("异常捕获：" + ex.getCause());
        System.out.println("异常捕获：" + ex.getClass());
        System.out.println("异常捕获：" + ex.getStackTrace());
        System.out.println("异常捕获：" + ex.getLocalizedMessage());
        ex.printStackTrace();
        // 收集设备硬件信息
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                infos.put("versionName", versionName);
                infos.put("versionCode", pi.versionCode + "");
            }
        } catch (NameNotFoundException e) {
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
            }
        }
        // 保存错误信息到文件
        StringBuffer exceptionStr = new StringBuffer();
        exceptionStr.append("★★★★★★=====错误时间=====" + time + "\n");
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            exceptionStr.append(key + "=" + value + "\n");
        }
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        exceptionStr.append(result);


        return exceptionStr.toString();
    }

    /**
     * 获取App安装包信息
     *
     * @return
     */
    private PackageInfo getPackageInfo(Context context) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }


    /**
     * 判断SD卡剩余空间
     */
    public long getAvailaleSize() {
        File path = Environment.getExternalStorageDirectory(); // 取得sdcard文件路径
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize / 1024 / 1024;
        // (availableBlocks * blockSize)/1024 KIB 单位
        // (availableBlocks * blockSize)/1024 /1024 MIB单位
    }


}