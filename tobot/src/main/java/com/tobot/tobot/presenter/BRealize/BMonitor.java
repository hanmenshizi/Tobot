package com.tobot.tobot.presenter.BRealize;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.tobot.tobot.MainActivity;
import com.tobot.tobot.R;
import com.tobot.tobot.base.Constants;
import com.tobot.tobot.presenter.ICommon.ISceneV;
import com.tobot.tobot.presenter.IPort.IMonitor;
import com.turing123.robotframe.event.AppEvent;
import com.turing123.robotframe.function.asr.IASRFunction;
import com.turing123.robotframe.notification.Notification;
import com.turing123.robotframe.notification.NotificationActions;
import com.turing123.robotframe.notification.NotificationFilter;
import com.turing123.robotframe.notification.NotificationManager;
import com.turing123.robotframe.notification.Receiver;

import java.util.Timer;

/**
 * Created by Javen on 2017/8/8.
 */

public class BMonitor implements IMonitor{
    private Context mContent;
    private ISceneV mISceneV;

    public BMonitor(ISceneV mISceneV){
        this.mISceneV = mISceneV;
        this.mContent = (Context)mISceneV;
        inMonitor();
    }

    @Override
    public void inMonitor() {
        //1. 获取NotificationManager 类对象
        NotificationManager notificationManager = NotificationManager.get();
        //2. 定义通知的Receive
        Receiver receiver = new Receiver() {
            //2.1 接收到消息
            @Override
            public void onReceive(Notification notification) {
                if (notification != null) {
                    Message message = Message.obtain();
                    message.what = Constants.NOTIFICATION_MSG;
//                    message.obj = notification;
                    Log.i("BMonitor",notification.toString());
                    Bundle bundle = new Bundle();
                    bundle.putInt("arg1", notification.arg1);
                    bundle.putString("action", notification.action);
                    try{
                        bundle.putString("arg2",  notification.arg2.toString()) ;
//                        Log.i("Javen","arg2:"+notification.arg2.toString());
                    }catch (NullPointerException e) {

                    }
                    message.obj = bundle;
                    mISceneV.getResult(message);
//                    mainHandler.sendMessage(message);
                }
            }
        };

        //3. 定义要接收的notification的过滤器(监听asr是否开启)
        NotificationFilter notificationFilter = new NotificationFilter(NotificationActions.NOTIFICATION_ACTION_ASR_STATUS);
        //4. 添加其他要监听的action(监听网络变化).
        notificationFilter.addAction(NotificationActions.NOTIFICATION_ACTION_CONNECTION_STATUS);
        // 监听TTS变化
        notificationFilter.addAction(NotificationActions.NOTIFICATION_ACTION_TTS_STATUS);
        // 监听语言唤醒
        notificationFilter.addAction(NotificationActions.NOTIFICATION_ACTION_WEAKEUP_STATUS);
        // 监听机器人状态变化
        notificationFilter.addAction(NotificationActions.NOTIFICATION_ACTION_ROBOT_STATE);
        // 监听机器人电池状态变化
        notificationFilter.addAction(NotificationActions.NOTIFICATION_ACTION_BATTERY_STATE);
        //5. 注册receiver
        notificationManager.registerNotificationReceiver(notificationFilter, receiver);
    }

}
