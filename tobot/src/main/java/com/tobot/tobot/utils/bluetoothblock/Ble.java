package com.tobot.tobot.utils.bluetoothblock;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.tobot.tobot.Listener.SimpleFrameCallback;
import com.tobot.tobot.MainActivity;
import com.tobot.tobot.base.*;
import com.tobot.tobot.control.demand.DemandFactory;
import com.tobot.tobot.control.demand.DemandModel;
import com.tobot.tobot.presenter.BRealize.BFrame;
import com.tobot.tobot.scene.BaseScene;
import com.tobot.tobot.scene.CustomScenario;
import com.turing123.robotframe.RobotFrameManager;
import com.turing123.robotframe.function.motor.Motor;
import com.turing123.robotframe.function.tts.ITTSCallback;
import com.turing123.robotframe.function.tts.TTS;
import com.turing123.robotframe.multimodal.action.Action;
import com.turing123.robotframe.multimodal.action.BodyActionCode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import static com.turing123.robotframe.multimodal.action.Action.PRMTYPE_EXECUTION_TIMES;
import static java.lang.Thread.sleep;

/**
 * Created by xx on 2017/10/17.
 */

public class Ble {
    private static final String TAG = "MainActivity";
    private int sdkInt = -1;
    private BluetoothDeviceAdapter bluetoothDeviceAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothUtils bluetoothUtils;
    private BluetoothCommunication communication;
    private BluetoothDevice bluetoothDevice;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private Set<BluetoothDevice> devices;
    private Context context;
    private TTS tts;
    private Motor motor;

    public Ble(Context context) {
        this.context = context;
        tts = new TTS(context, new BaseScene(context,"os.sys.chat"));
        motor = new Motor(context, new CustomScenario(context));
        //蓝牙通讯 初始化
        sdkInt = Build.VERSION.SDK_INT;
        communication = new BluetoothCommunication();
        bluetoothUtils = new BluetoothUtils();
        checkBleSupportAndInitialize();
        bluetoothUtils.setDiscoverableTimeout(120);
        getBondedDevices();
        onStaticSrvice();//启动服务；机器人端为服务端
    }

    /**
     * 如果蓝牙功能被禁用，请获得蓝牙适配器并打开蓝牙
     */
    private void checkBleSupportAndInitialize() {
        Log.d(TAG, "checkBleSupportAndInitialize: ");
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.d(TAG, "device_ble_not_supported ");
            return;
        }
        // Initializes a Blue tooth adapter.
//        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "device_ble_not_supported ");
            return;
        }

        //打开蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "open bluetooth ");
            bluetoothUtils.openBluetooth();
        }
    }

    private void getBondedDevices() {
        prepareGetBondedDevices();
        devices = mBluetoothAdapter.getBondedDevices();
        Log.d(TAG, "bonded device size =" + devices.size());
        for (BluetoothDevice bonddevice : devices) {
            Log.d(TAG, "bonded device: name ==" + bonddevice.getName() + " address--" + bonddevice.getAddress());
            if (bonddevice.getName().contains("BT")) {
                bluetoothDevice = bonddevice;
            }
        }
    }

    private void prepareGetBondedDevices() {
        String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        Log.d(TAG, "Build.VERSION.SDK_INT: " + Build.VERSION.SDK_INT);
        if (sdkInt >= 25 && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Android M Permission check ");
            Log.d(TAG, "ask for Permission... ");
            ActivityCompat.requestPermissions((MainActivity) context, permissions, PERMISSION_REQUEST_COARSE_LOCATION);
        } else {
//            startScan();
        }

    }

    private void onStaticSrvice() {
        AcceptThread.HandleMessage handleMessage = new AcceptThread.HandleMessage() {
            @Override
            public void handleMessage(String msg) {
//                operation(msg);
                //mohuaiyuan 测试

                Log.d(TAG, "msg: " + msg);
                dealWithMsg(msg);
            }
        };
        communication.startAcceptThread(handleMessage);
    }

    private void dealWithMsg(String msg) {
        Log.d(TAG, "dealWithMsg: "+msg);
        if (msg.contains(Constants.SEPARATOR_BETWEEN_KIND_AND_BYDY_TEXT)){
            String[] temp=msg.split(Constants.SEPARATOR_BETWEEN_KIND_AND_BYDY_TEXT);
            String kind=temp[0];
            String data=temp[1];
            Log.d(TAG, "kind: "+kind);
            Log.d(TAG, "data: "+data);
            switch (kind){
                case Constants.KIND_FUNCTION:
                    dealFunction(data);
                    break;

                case Constants.KIND_ACTION:
                    dealAction(data);
                    break;

                case Constants.KIND_DANCE:
                    dealDance(data);
                    break;

                case Constants.KIND_ACTION+"#"+Constants.KIND_SPEECH:
                    dealSpeech(data);
                    break;


                default:
                    break;

            }
        }

    }

    private void dealDance(String msg){
        Log.d(TAG, "dealDance: ");
        Log.d(TAG, "msg: "+msg);
        String []temp =null;
        if (msg.contains(Constants.SEPARATOR_BETWEEN_SPEECH_AND_ACTION)){
            temp=msg.split(Constants.SEPARATOR_BETWEEN_SPEECH_AND_ACTION);
        }
        DemandFactory demandFactory=DemandFactory.getInstance(context);
        DemandModel demandModel=new DemandModel();
        demandModel.setCategoryId(88);
        String playUrl32=temp[1];
        Log.d(TAG, "playUrl32: "+playUrl32);
        demandModel.setPlayUrl32(playUrl32);
        try {
            demandFactory.demands(demandModel);
        } catch (Exception e) {
            Log.e(TAG, "机器人 舞蹈 出现错误 Exception e： "+e.getMessage());
            e.printStackTrace();
        }


    }

    private void dealAction(String msg){
        Log.d(TAG, "dealAction: ");
        Log.d(TAG, "msg: "+msg);
        String[] temp=null;
        if (msg.contains(Constants.SEPARATOR_BETWEEN_SPEECH_AND_ACTION)){
            temp=msg.split(Constants.SEPARATOR_BETWEEN_SPEECH_AND_ACTION);
        }
        int action=Integer.valueOf(temp[1]);
        Log.d(TAG, "action: "+action);
        doAction(action);
    }

    private void dealFunction(String msg){
        Log.d(TAG, "dealFunction: ");
        Log.d(TAG, "msg: "+msg);

        switch (msg){
            case "关闭机器人聊天":
                Log.i("Javen","关闭机器人聊天");
                BFrame.shutChat();
                break;
            case "休眠机器人":
                Log.i("Javen","休眠机器人");
                motor.doAction(Action.buildBodyAction(BodyActionCode.ACTION_8, PRMTYPE_EXECUTION_TIMES, 1), new SimpleFrameCallback());
                BFrame.FallAsleep();
                break;
            case "开放机器人聊天":
                Log.i("Javen","开放机器人聊天");
                BFrame.disparkChat();
                break;
            case "同桌的阿达":
                Log.i("Javen","同桌的阿达");
                Frequency.start("/sdcard/.TuubaResource/song/同桌的阿达.mp3");
                break;
            case "幸运的小灰象":
                Log.i("Javen","幸运的小灰象");
                Frequency.start("/sdcard/.TuubaResource/song/幸运的小灰象.mp3");
                break;
            case "暂停播放歌曲":
                Log.i("Javen","暂停播放歌曲");
                Frequency.pause();
                break;
            case "停止播放歌曲":
                Log.i("Javen","停止播放歌曲");
                Frequency.stop();
                break;
            case "小苹果":
                Log.i("Javen","小苹果");
                motor.doAction(Action.buildBodyAction(123,PRMTYPE_EXECUTION_TIMES,1),new SimpleFrameCallback());
                Frequency.start("/sdcard/TuubaDanceBackgroundMusic/小苹果0107演示.mp3");
                break;
            case "你好，老徐！有事吗？":
                //唤醒机器人
                BFrame.Wakeup();
                break;


            default:
        }

    }

    private void dealSpeech(final String msg){
        Log.d(TAG, "dealSpeech: ");
        Log.d(TAG, "msg: "+msg);
//        boolean isContains=msg.contains("--");

        if (msg.contains(Constants.SEPARATOR_BETWEEN_SPEECH_AND_ACTION)){

            new Thread(new Runnable() {
                @Override
                public void run() {

                    int sleepTime=0;

                    String[] temp=msg.split(Constants.SEPARATOR_BETWEEN_SPEECH_AND_ACTION);
                    for (int i=0;i<temp.length;i++){
                        if (i==0){
                            speak(temp[i]);
                        }else {
                            String tempMsg=temp[i];
                            if (tempMsg.contains(":")){
                                String[] containsTimes=tempMsg.split(":");
                                Log.d(TAG, "sleepTime: "+sleepTime);
                                try {
                                    Thread.sleep(sleepTime);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                doAction(Integer.valueOf(containsTimes[0]));
                                sleepTime=Integer.valueOf(containsTimes[1]);
                            }else {
                                doAction(Integer.valueOf(tempMsg));
                            }

                        }
                    }

                }
            }).start();

        }else {
            speak(msg);
        }

//        String[] temp=msg.split(Constants.SEPARATOR_BETWEEN_SPEECH_AND_ACTION);
//        if (temp!=null){
//            Log.d(TAG, "length:"+temp.length);
//        }
//        if (temp!=null){
//
//            switch (temp.length){
//                case 0:
//
//                    break;
//                case 1:
//                    speak(temp[0]);
//                    break;
//                case 2:
//                    speak(temp[0]);
//                    doAction(Integer.valueOf(temp[1]));
//                    break;
//                case 3:
//                    speak(temp[0]);
//                    doAction(Integer.valueOf(temp[1]));
//                    doAction(Integer.valueOf(temp[2]));
//                    break;
//                case 4:
//                    speak(temp[0]);
//                    doAction(Integer.valueOf(temp[1]));
//                    doAction(Integer.valueOf(temp[2]));
//                    doAction(Integer.valueOf(temp[3]));
//                    break;
//                case 5:
////                    if (isContains){
////                        speak(temp[0]);
////                        doAction(Integer.valueOf(temp[1]));
////                        doAction(Integer.valueOf(temp[2]));
////                        doAction(Integer.valueOf(temp[3]));
////
////                    }else {
////
//                        speak(temp[0]);
//                        doAction(Integer.valueOf(temp[1]));
//                        doAction(Integer.valueOf(temp[2]));
//                        doAction(Integer.valueOf(temp[3]));
//                        doAction(Integer.valueOf(temp[4]));
////                    }
//
//                    break;
//
//                case 6:
//                    speak(temp[0]);
//                    doAction(Integer.valueOf(temp[1]));
//                    doAction(Integer.valueOf(temp[2]));
//                    doAction(Integer.valueOf(temp[3]));
//                    doAction(Integer.valueOf(temp[4]));
//                    doAction(Integer.valueOf(temp[5]));
//
//                    break;
//
//
//                default:
//                    speak(temp[0]);
//                    break;
//            }
//        }
    }

    private void speak(String string){
        Log.d(TAG, "speak:");
        tts.speak(string,ittsCallback);
    }

    private void doAction(int action){
        Log.d(TAG, "doAction:");
        motor.doAction(Action.buildBodyAction(action, PRMTYPE_EXECUTION_TIMES, 1), new SimpleFrameCallback());
    }

    String serialNumber = "0";
    ITTSCallback ittsCallback = new ITTSCallback() {

        @Override
        public void onStart(String s) {
            Log.d(TAG, "onStart: ");
            Log.d(TAG, "s: "+s);
//            if (s.equals("哎呀，小朋友都好可爱啊。我给大家唱首儿歌吧！")){
//                serialNumber = "1";
//            }else {
//                serialNumber = "0";
//            }
            if (s.contains("要不给你秀下我的肌肉吧")){
                serialNumber="2";
            }else {
                serialNumber="0";
            }

        }

        @Override
        public void onPaused() { }

        @Override
        public void onResumed() { }

        @Override
        public void onCompleted() {//结束
            Log.d(TAG, "onCompleted: ");
            Log.d(TAG, "serialNumber: "+serialNumber);
            switch (serialNumber){
                case "1":
                    Frequency.start("/sdcard/.TuubaResource/song/读书郎.mp3");
                    break;
                case "2":
                    doAction(58);
                    break;
                case "3":

                    break;

                default:
                    break;
            }
        }

        @Override
        public void onError(String s) { }

    };



}
