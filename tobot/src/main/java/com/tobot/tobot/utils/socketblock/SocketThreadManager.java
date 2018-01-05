package com.tobot.tobot.utils.socketblock;

import android.os.Handler;

import static java.lang.Thread.sleep;


/**
 * Socket 管理类
 *
 * @author Javen
 */
public class SocketThreadManager {

    private static SocketThreadManager s_SocketManager = null;

    private SocketInputThread mInputThread = null;

    private SocketOutputThread mOutThread = null;

    private SocketHeartThread mHeartThread = null;

    private SocketConnectCoherence mConnectTest = null;


    // 获取单例
    public static SocketThreadManager sharedInstance() {
        if (s_SocketManager == null) {
            s_SocketManager = new SocketThreadManager();
//            try {
//                sleep(Const.SOCKET_SLEEP_SECOND * 1000);
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            s_SocketManager.startThreads();
        }
        return s_SocketManager;
    }

    // 单例，不允许在外部构建对象
    private SocketThreadManager() {
//        mHeartThread = SocketHeartThread.instance();
//        mInputThread = new SocketInputThread();
//        mOutThread = new SocketOutputThread();
        mConnectTest = SocketConnectCoherence.instance();
    }

    // 启动线程
    private void startThreads() {
        mHeartThread.start();
        mInputThread.start();
        mInputThread.setStart(true);
        mOutThread.start();
        mOutThread.setStart(true);
    }

    /**
     * stop线程
     */
    public void stopThreads() {
        mHeartThread.stopThread();
        mInputThread.setStart(false);
        mOutThread.setStart(false);
    }

    public static void releaseInstance() {
        if (s_SocketManager != null) {
            s_SocketManager.stopThreads();
            s_SocketManager = null;
        }
    }

    public void sendMsg(byte[] buffer, Handler handler) {
        MsgEntity entity = new MsgEntity(buffer, handler);
        mOutThread.addMsgToSendList(entity);
    }

    public void sendMsg(byte[] buffer) {
//        MsgEntity entity = new MsgEntity(buffer, null);
//        mOutThread.addMsgToSendList(entity);
        mConnectTest.sendData();
    }


}
