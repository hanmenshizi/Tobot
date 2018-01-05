package com.tobot.tobot.Listener;

import android.content.Context;
import android.util.Log;

import com.tobot.tobot.scene.BaseScene;
import com.tobot.tobot.scene.CustomScenario;
import com.turing123.robotframe.function.motor.Motor;
import com.turing123.robotframe.function.selfprotect.SelfProtectEvent;
import com.turing123.robotframe.function.tts.TTS;
import com.turing123.robotframe.internal.modbus.protocol.core.OnProtocolListener;
import com.turing123.robotframe.multimodal.action.Action;
import com.turing123.robotframe.multimodal.action.BodyActionCode;

import java.util.Arrays;

import static com.turing123.robotframe.multimodal.action.Action.PRMTYPE_EXECUTION_TIMES;

/**
 * Created by Javen on 2017/9/21.
 */

public class SensorListener implements OnProtocolListener {
    private Context mContext;
    private TTS tts;
    private Motor motor;

    public SensorListener(Context context){
        this.mContext = context;
        tts = new TTS(mContext,new BaseScene(mContext,"os.sys.chat"));
        motor =  new Motor(mContext, new CustomScenario(mContext));
    }
    @Override
    public void onMotionStart(byte[] bytes) {
        Log.i("Javen","onMotionStart..."+bytes.length);
    }

    @Override
    public void onMotionCompleted(byte[] bytes) {
        Log.i("Javen","onMotionCompleted..."+bytes.length);
    }

    @Override
    public void onOrderFeedback(byte[] d) {
//        Log.i("Javen","识别位:"+bytesToInt(d,7)+"数据位:"+bytesToInt(d,4));
        switch (bytesToInt(d,7)){
            case 0:
                switch (bytesToInt(d,4)){
                    case SelfProtectEvent.ATTITUDE_FALL_BACKWARD://向后倒
                        tts.speak("哎呀!好疼啊");
                        motor.doAction(Action.buildBodyAction(BodyActionCode.ACTION_11,PRMTYPE_EXECUTION_TIMES,1),new SimpleFrameCallback());
                        break;
                    case SelfProtectEvent.ATTITUDE_FALL_FORWARD://向前倒
                        tts.speak("刚那个瓜娃子推我");
                        motor.doAction(Action.buildBodyAction(BodyActionCode.ACTION_13,PRMTYPE_EXECUTION_TIMES,1),new SimpleFrameCallback());
                        break;
                    case SelfProtectEvent.ATTITUDE_NORMAL://正常

                        break;
                    case SelfProtectEvent.ATTITUDE_OTHER://其他

                        break;
                    case SelfProtectEvent.ATTITUDE_SHAKE://摇晃
                        tts.speak("别晃了,我都快被你摇散架了");
                        break;
                }
                break;
            case 1:
                switch (bytesToInt(d,4)){
                    case 15://最短障碍距离
                        tts.speak("你跟个傻瓜式的挡在这干嘛");
                        break;
                    case 60://最远感应距离
                        tts.speak("等等我,你跑那么快干嘛");
                        break;
                }
                break;
            case 2:
                switch (bytesToInt(d,4)){
                    case 30://红外距离
                        tts.speak("你已被红外锁定");
                        break;
                    case 50://
                        tts.speak("红外解除锁定");
                        break;
                }
                break;
        }

    }

    @Override
    public void onMotionError(byte[] bytes) {
        Log.i("Javen","onMotionError..."+bytes.length);
    }

    @Override
    public void onMotionInterrupt() {
        Log.i("Javen","onMotionInterrupt");
    }





    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) ((src[offset] & 0xFF));
        return value;
    }


}
