package com.tobot.tobot.presenter.BRealize;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.iflytek.cloud.thirdparty.E;
import com.tobot.tobot.MainActivity;
import com.tobot.tobot.base.Constants;
import com.tobot.tobot.base.Frequency;
import com.tobot.tobot.base.OkHttpClientManager;
import com.tobot.tobot.db.bean.UserDBManager;
import com.tobot.tobot.db.model.User;
import com.tobot.tobot.entity.ActionEntity;
import com.tobot.tobot.entity.ConnectionEntity;
import com.tobot.tobot.presenter.ICommon.ISceneV;
import com.tobot.tobot.presenter.IPort.IConnect;
import com.tobot.tobot.utils.AppTools;
import com.tobot.tobot.utils.socketblock.Joint;
import com.tobot.tobot.utils.SHA1;
import com.tobot.tobot.utils.TobotUtils;
import com.tobot.tobot.utils.Transform;
import com.tobot.tobot.utils.okhttpblock.OkHttpUtils;
import com.tobot.tobot.utils.okhttpblock.callback.StringCallback;
import com.tobot.tobot.utils.socketblock.SocketThreadManager;
import com.turing123.libs.android.connectivity.ConnectionManager;
import com.turing123.libs.android.connectivity.ConnectionStatus;
import com.turing123.libs.android.connectivity.ConnectionStatusCallback;
import com.turing123.libs.android.connectivity.DataReceiveCallback;
import com.turing123.libs.android.connectivity.wifi.ap.ApConfiguration;
import com.turing123.robotframe.multimodal.action.Action;
import com.turing123.robotframe.multimodal.action.EarActionCode;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;


/**
 * Created by Javen on 2017/8/8.
 */

public class BConnect implements IConnect{
    private String TAG = "Javen BConnect";
    private Context mContent;
    private ISceneV mISceneV;
    private MainActivity mainActivity;
    private boolean isLoad = true;//是否加载框架
    private WifiConfiguration wc;
    private ApConfiguration apc;
    private User user = new User();
    private Timer networkTime = new Timer(true);
    private OkHttpClientManager mOkHttp;
    private ConnectionEntity mConnectionEntity;
    private String uuid;
    private int bind = 0;
    private String phone;
    private boolean isBing = true,isSucceed;


    public BConnect(ISceneV mISceneV){
        this.mISceneV = mISceneV;
        this.mContent = (Context)mISceneV;
        this.mainActivity = (MainActivity)mISceneV;
        mOkHttp = OkHttpClientManager.getInstance();
//        if (TobotUtils.isEmploy()){
//            //首次使用录音播放
//        }
        if (!AppTools.netWorkAvailable(mContent)) {
            initialize(false);
            networkTime.schedule(new networkTimeTask(),9000);
        }
    }

    @Override
    public void shunt(){
        try{
            if (TobotUtils.isNotEmpty(wc,apc)){
                link();
            }else {
                initialize(true);
            }
        }catch (NullPointerException e){

        }
    }

    @Override
    public void shuntVoice() {
        Frequency.start("/sdcard/.TuringResource/audio/first_employ.mp3");
        shunt();
    }

    @Override
    public void initialize(boolean toggle) {
        //1. 配置WifiConfiguration对象。
        wc = new WifiConfiguration();
        wc.SSID = TobotUtils.getDeviceId(Constants.DeviceId,Constants.Path);//设置热点
        wc.preSharedKey = "1008666666";
        wc.hiddenSSID = false;
        wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        //2. 配置ApConfiguration, 端口号为22334
        apc = new ApConfiguration(22334, wc);
        if (toggle){
            link();
        }else{
            shut();
        }
    }

    @Override
    public void link() {
        //3. 启动AP和接收客户端信息的服务，选择联网方式为TYPE_WIFI_AP.
        ConnectionManager.startReceiveAndConnect(mContent, ConnectionManager.TYPE_WIFI_AP, apc,
                new ConnectionStatusCallback() {
                    @Override
                    public void onConnectionCompleted(int status) {
                        //4. 接收数据完成，返回参数代表是否成功接收了数据。连接成功关闭ap.
                        Log.i(TAG,"onConnectionCompleted status: " + status);
                        if (status == ConnectionStatus.WIFI_CONNECTED_SUCCESS) {
                            ConnectionManager.stopConnection(mContent, ConnectionManager.TYPE_WIFI_AP);//关闭ap
                        }
                        Message message = Message.obtain();
                        message.what = Constants.NET_MSG;
                        message.obj = status;
//                        mISceneV.getResult(message);
                        networkHandler.sendMessage(message);
                    }
                },
                new DataReceiveCallback() {//收到客户端数据回调
                    @Override
                    public void onReceiveData(String s) {
                        isBing = false;
                        mConnectionEntity = new Gson().fromJson(s,ConnectionEntity.class);
                        try{
                            phone = mConnectionEntity.getCustomData();
                        }catch (NullPointerException e){}
                        Log.i(TAG,"onReceiveData() called with: " + "s = [" + s + "]");
                    }
                }, null);
    }

    @Override
    public void shut(){
        ConnectionManager.stopConnection(mContent, ConnectionManager.TYPE_WIFI_AP);//关闭ap
        Message message = Message.obtain();
        message.what = Constants.NET_MSG;
        message.obj = Constants.CLOSE_AP;
//        mISceneV.getResult(message);
        networkHandler.sendMessage(message);
    }

    @Override
    public void isLoad(boolean load) {
        isLoad = load;
    }

    Handler networkHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Log.e(TAG,"msg:"+msg.obj);
            switch (msg.what){
                case Constants.NET_MSG:
                    int connect = (int) msg.obj;
                    if (connect == ConnectionStatus.WIFI_CONNECTED_SUCCESS) {
                        mISceneV.getFeelHead(true);//摸头三秒判断
                        mISceneV.getInitiativeOff(false);//主动断开关闭
                        Frequency.start("/sdcard/.TuringResource/audio/networking_succeed.mp3");
                        SocketThreadManager.sharedInstance().sendMsg(Transform.HexString2Bytes(Joint.setRegister()));
                        bindRobot();//绑定
                        if(TobotUtils.isEmploy()){//首次使用
                            BFrame.instance(mISceneV).onInitiate(true);
                            user.setUltr("1");
                        }else if (!isLoad){//加载失败
                            BFrame.instance(mISceneV).onInitiate(true);
                        }
                        user.setMobile(phone);
                        user.setUltrAP("1");
                        mainActivity.tvConnResult.setText("已连接到网络");
                    } else if (connect == ConnectionStatus.AP_START_SERVER) {
                        try{
                            BFrame.Ear(EarActionCode.EAR_MOTIONCODE_6);//断网-灯圈
                        }catch (Exception e){

                        }
                        mainActivity.tvConnResult.setText("启动ap server, 并准备好ap连接网");
//                    } else if(connect == ConnectionStatus.WIFI_CONNECTED_FAIL){
//                        user.setUltrAP("2");
//                        Frequency.start("/sdcard/.TuringResource/audio/networking_failure.mp3");
//                        mainActivity.tvConnResult.setText("AP连接失败");
//                        link();
//                    } else if(connect == ConnectionStatus.WIFI_CONNECTED_SSID_NOT_FOUND){
//                        user.setUltrAP("2");
//                        Frequency.start("/sdcard/.TuringResource/audio/networking_failure.mp3");
//                        mainActivity.tvConnResult.setText("连接的wifi失败");
//                    } else if(connect == ConnectionStatus.WIFI_ENABLE_NETWORK_ERROR){
//                        user.setUltrAP("2");
//                        Frequency.start("/sdcard/.TuringResource/audio/networking_failure.mp3");
//                        mainActivity.tvConnResult.setText("启用网络错误");
//                    } else if(connect == ConnectionStatus.WIFI_CONNECTED_AUTHENTICATING_ERROR){
//                        user.setUltrAP("2");
//                        Frequency.start("/sdcard/.TuringResource/audio/networking_failure.mp3");
//                        mainActivity.tvConnResult.setText("AP验证失败");
//                        link();
                    } else if(connect == ConnectionStatus.WIFI_CONNECTED_READY){
                        user.setUltrAP("0");
                        Frequency.start("/sdcard/.TuringResource/audio/networking_waiting.mp3");
                        mainActivity.tvConnResult.setText("联网中...请稍等!");
                    } else if(connect == Constants.CLOSE_AP){
                        user.setUltrAP("3");
//                        Frequency.start("/sdcard/.TuringResource/audio/close_networking.mp3");
                        mainActivity.tvConnResult.setText("关闭AP连接!");
                    } else if (connect != ConnectionStatus.AP_SOCKET_ERROR){
                        Log.i(TAG,"联网失败:");
                        user.setUltrAP("2");
                        mainActivity.tvConnResult.setText("AP联网失败");
                        networkTime.schedule(new networkFailureTimeTask(),15000);
                    }
                    try{
                        UserDBManager.getManager().insertOrUpdate(user);
                    }catch(Exception e){}
                    break;

                default:
                    super.handleMessage(msg);
            }
        }
    };

    private class networkTimeTask extends TimerTask {
        public void run() {
            Log.i(TAG,"延时进入联网:");
            if (!AppTools.netWorkAvailable(mContent)) {
                shuntVoice();
            }
        }
    }

    private class networkFailureTimeTask extends TimerTask {
        public void run() {
            Log.i(TAG,"联网失败延时开启ap:");
            if (!AppTools.netWorkAvailable(mContent)) {
                Frequency.start("/sdcard/.TuringResource/audio/networking_failure.mp3");
                link();
            } else {
                ConnectionManager.stopConnection(mContent, ConnectionManager.TYPE_WIFI_AP);//关闭ap
                Message message = Message.obtain();
                message.what = Constants.NET_MSG;
                message.obj = ConnectionStatus.WIFI_CONNECTED_SUCCESS;
                networkHandler.sendMessage(message);
            }
        }
    }

    @Override
    public void onAgain() {
        if (!isBing && isSucceed){
            bindRobot();
        }
    }

    private void bindRobot() {
//        if (!TobotUtils.isEqual(UserDBManager.getManager().getCurrentUser().getMobile(),mConnectionEntity.getCustomData()) && TobotUtils.isNotEmpty(phone)) {
            uuid = Transform.getGuid();
            OkHttpUtils.get()
                    .url(Constants.ROBOT_BOUND + uuid + "/" + SHA1.gen(Constants.identifying + uuid)
                            + "/" + TobotUtils.getDeviceId(Constants.DeviceId,Constants.Path)
                            + "/" + TobotUtils.getDeviceId(Constants.Ble_Name,Constants.Path)
                            + "/" + phone)
                    .addParams("nonce", uuid)//伪随机数
                    .addParams("sign", SHA1.gen(Constants.identifying + uuid))//签名
                    .addParams("robotId", TobotUtils.getDeviceId(Constants.DeviceId,Constants.Path))//机器人设备ID
                    .addParams("bluetooth", TobotUtils.getDeviceId(Constants.Ble_Name,Constants.Path))//蓝牙名称
                    .addParams("mobile", phone)//手机号
                    .build()
                    .execute(new StringCallback() {

                        @Override
                        public void onError(Call call, Exception e, int id) {
                            bind ++;
                            if (bind < 3) {
                                bindRobot();
                            } else {
                                bind = 0;
                            }
                            Log.i("Javen", "绑定失败===>call:" + call + "bind:" + bind);
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.i("Javen", "绑定===>response:" + response + "id:" + id);//{"success":false,"desc":"绑定失败","data":null}id:0
                            ActionEntity json = new Gson().fromJson(response,ActionEntity.class);
                            if (json.getSuccess()) {
                                isSucceed = true;
                            }
                        }
                    });
//        }
    }

}
