package com.tobot.tobot.presenter.BRealize;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.tobot.tobot.Listener.SensorListener;
import com.tobot.tobot.Listener.SupersonicListener;
import com.tobot.tobot.presenter.ICommon.ISceneV;
import com.tobot.tobot.presenter.IPort.ISensor;
import com.tobot.tobot.scene.BaseScene;
import com.turing123.robotframe.function.tts.TTS;
import com.turing123.robotframe.internal.modbus.protocol.bean.BaseEntity;
import com.turing123.robotframe.internal.modbus.protocol.bean.Sensor;
import com.turing123.robotframe.internal.modbus.protocol.core.OnProtocolListener;
import com.turing123.robotframe.internal.modbus.protocol.core.ProtocolManager;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import static com.turing123.robotframe.internal.modbus.protocol.core.ProtocolManager.getInstance;

/**
 * Created by Javen on 2017/8/31.
 */
public class BSensor implements ISensor{
    private Context mContent;
    private ISceneV mISceneV;
    private ProtocolManager mProtocolManager;
    private SensorListener mSensorListener;

    public BSensor(ISceneV mISceneV){
        this.mISceneV = mISceneV;
        this.mContent = (Context)mISceneV;
        mProtocolManager = getInstance();
//        mProtocolManager.unRegisterSensorListener(Sensor.SENSOR_MERGE_ALL);

        mSensorListener = new SensorListener(mContent);
//        inSensor();
        inShakeFall();
        inSupersonic();
        inInfrared();
    }


    @Override
    public void inSensor() {

    }

    private void inInfrared(){
        mProtocolManager.registerSensorListener(Sensor.SENSOR_INFRARED, new OnProtocolListener() {
            final byte[] mByte = new byte[8];

            @Override
            public void onMotionCompleted(byte[] receiveData) {
//                Log.d("Javen","红外结束时接收到的数据"+ receiveData);
            }

            @Override
            public void onOrderFeedback(byte[] receiveData) {
//                Log.d("Javen","红外反馈接收到的数据长："+ receiveData.length + ";红外数据:"+Arrays.toString(receiveData));
                mByte[0] = receiveData[0];
                mByte[1] = receiveData[1];
                mByte[2] = receiveData[2];
                mByte[3] = receiveData[3];
                mByte[4] = receiveData[4];
                mByte[5] = receiveData[5];
//                mByte[6] = receiveData[6];
                mByte[7] = 0x02;
                mSensorListener.onOrderFeedback(mByte);
            }

            @Override
            public void onMotionError(byte[] errorMessage) {
//                Log.d("Javen","红外出错时接收到的数据"+ errorMessage);
            }

            @Override
            public void onMotionInterrupt() {

            }

            @Override
            public void onMotionStart(byte[] receiveData) {
//                Log.d("Javen","红外开始时接收到的数据"+ receiveData);
            }
        });
    }

    private void inSupersonic(){
//        final SensorListener mSensorListener = new SensorListener();

        mProtocolManager.registerSensorListener(Sensor.SENSOR_SUPERSONIC_WAVE, new OnProtocolListener() {
            final byte[] mByte = new byte[8];

            @Override
            public void onMotionCompleted(byte[] receiveData) {
//                Log.d("Javen","超声波结束时接收到的数据"+ receiveData);
            }

            @Override
            public void onOrderFeedback(byte[] receiveData) {
//                Log.d("Javen","超声波反馈接收到的数据长："+ receiveData.length + ";超声波数据:"+Arrays.toString(receiveData));
                mByte[0] = receiveData[0];
                mByte[1] = receiveData[1];
                mByte[2] = receiveData[2];
                mByte[3] = receiveData[3];
                mByte[4] = receiveData[4];
                mByte[5] = receiveData[5];
//                mByte[6] = receiveData[6];
                mByte[7] = 0x01;
                mSensorListener.onOrderFeedback(mByte);
            }

            @Override
            public void onMotionError(byte[] errorMessage) {
//                Log.d("Javen","超声波出错时接收到的数据"+ errorMessage);
            }

            @Override
            public void onMotionInterrupt() {

            }

            @Override
            public void onMotionStart(byte[] receiveData) {
//                Log.d("Javen","超声波开始时接收到的数据"+ receiveData);
            }
        });
    }

    private void inShakeFall(){
//        final SensorListener mSensorListener = new SensorListener();

        mProtocolManager.registerSensorListener(Sensor.SENSOR_SHAKE_FALL, new OnProtocolListener() {
            final byte[] mByte = new byte[8];

            @Override
            public void onMotionCompleted(byte[] receiveData) {
//                Log.d("Javen","摇晃结束时接收到的数据"+ receiveData);
            }

            @Override
            public void onOrderFeedback(byte[] receiveData) {
//                Log.d("Javen","摇晃反馈接收到的数据长："+ receiveData.length + ";摇晃数据:"+Arrays.toString(receiveData));
                mByte[0] = receiveData[0];
                mByte[1] = receiveData[1];
                mByte[2] = receiveData[2];
                mByte[3] = receiveData[3];
                mByte[4] = receiveData[4];
                mByte[5] = receiveData[5];
//                mByte[6] = receiveData[6];
                mByte[7] = 0x00;
                mSensorListener.onOrderFeedback(mByte);
            }

            @Override
            public void onMotionError(byte[] errorMessage) {
//                Log.d("Javen","摇晃出错时接收到的数据"+ errorMessage);
            }

            @Override
            public void onMotionInterrupt() {
            }

            @Override
            public void onMotionStart(byte[] receiveData) {
//                Log.d("Javen","摇晃开始时接收到的数据"+ receiveData);
            }
        });
    }


}
