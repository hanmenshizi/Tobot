package com.tobot.tobot.base;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tobot.tobot.MainActivity;
import com.tobot.tobot.db.bean.MemoryDBManager;
import com.tobot.tobot.db.bean.UserDBManager;
import com.tobot.tobot.db.model.User;

/**
 * Created by Javen on 2017/7/10.
 */

public class TobotApplication extends Application {

    private static TobotApplication instance;
    private User currentUser;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.i("Javen","TobotApplication onCreate");
        instance = this;
        // 程序崩溃时触发线程  以下用来捕获程序崩溃异常
        CrashHandler crashHandler = CrashHandler.getInstance();//打印log后重启
        crashHandler.init(instance);
//        Thread.setDefaultUncaughtExceptionHandler(handler);//直接重启
//        StartOtherApplications();
        MemoryDBManager.getManager().clear();
    }

    public synchronized static TobotApplication getInstance() {
        if (null == instance) {
            instance = new TobotApplication();
        }
        return instance;
    }

    public User getCurrentUser() {
        if (currentUser == null) {
            currentUser = UserDBManager.getManager().getCurrentUser();
        }
        return currentUser;
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        super.onTerminate();
        Log.i("Javen","TobotApplication onTerminate");
        MemoryDBManager.getManager().clear();
    }

    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        super.onLowMemory();
        System.gc();
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        super.onTrimMemory(level);
    }

//    private Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
//        @Override
//        public void uncaughtException(Thread t, Throwable e) {
//            restartApp(); //发生崩溃异常时,重启应用
//        }
//    };
//
//    private void restartApp() {
//        Intent intent = new Intent(this, MainActivity.class);
//        PendingIntent restartIntent = PendingIntent.getActivity(instance.getApplicationContext(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
//        //退出程序
//        AlarmManager mgr = (AlarmManager)instance.getSystemService(Context.ALARM_SERVICE);
//        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
//        //结束进程之前可以把程序的注销或者退出代码放在这段代码之前
//        android.os.Process.killProcess(android.os.Process.myPid());
//    }
//
//    private  void StartOtherApplications(){
//        Intent intent = getPackageManager().getLaunchIntentForPackage("com.robot.bridge");
//        if (intent != null) {
//            Log.i("Javen","已启动应用");
//            instance.startActivity(intent);
//        } else {
//            // 没有安装要跳转的app应用，提醒一下
//            Log.i("Javen","没有要启动的应用");
//        }
//    }

}
