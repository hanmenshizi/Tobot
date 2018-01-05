package com.tobot.tobot.presenter.BRealize;

import android.content.Context;

import com.tobot.tobot.MainActivity;
import com.tobot.tobot.Listener.SimpleFrameCallback;
import com.tobot.tobot.presenter.ICommon.ISceneV;
import com.tobot.tobot.presenter.IPort.IBattery;
import com.tobot.tobot.scene.BaseScene;
import com.turing123.robotframe.function.bodystate.BodyState;
import com.turing123.robotframe.function.tts.TTS;
import com.turing123.robotframe.multimodal.action.Action;
import com.turing123.robotframe.multimodal.action.BodyActionCode;

import java.util.Timer;
import java.util.TimerTask;

import static com.turing123.robotframe.multimodal.action.Action.PRMTYPE_EXECUTION_TIMES;

/**
 * Created by Javen on 2017/8/29.
 */

public class BBattery implements IBattery{
    private Context mContent;
    private ISceneV mISceneV;
    private BodyState mBodyState;
    private TTS tts;
    private Timer mTimer = new Timer(true);
    private long T1 = 3000, T2 = 12000;
    private int FULL,CHARGING,LOW,DISCHARGE,NATURE_DISCHARGE;//播报次数
    //0:冲满电;;1:充电状态;2:低电量状态;3:自然放电状态;4:拔电时低电量提示5:拔电时电量不足提示
//    private int FULL_STATE,CHARGING_STATE,LOW_STATE,NATURE_DISCHARGE = -1,PLUCK_LOW,PLUCK_INSUFFCIENT;
    private int Battery_pull;//拔电标志  0:非拔电;1:拔电


    public BBattery(ISceneV mISceneV){
        this.mISceneV = mISceneV;
        this.mContent = (Context)mISceneV;
        mBodyState = new BodyState(mContent,new BaseScene(mContent, "os.sys.chat"));
        tts = new TTS(mContent, new BaseScene(mContent, "os.sys.chat"));
        mTimer.schedule(new BatteryTimer(),T1,T2);//电量查看
    }

    @Override
    public void energy() {
//        Log.i("Javen", "电量:" + mBodyState.getBatteryLevel() + "电池状态:" +  mBodyState.getBatteryState());
        if (mBodyState.getBatteryState() == BodyState.BATTERY_STATE_FULL){
            if (mBodyState.getBatteryLevel() == 100 && setFrequency("FULL") < 3){
                mTimer.cancel();
                mTimer = new Timer();
                mTimer.schedule(new BatteryTimer(),600000,12000);
                tts.speak("满电壮态");
            }
        }
        if (mBodyState.getBatteryState() == BodyState.BATTERY_STATE_CHARGING){
            if (mBodyState.getBatteryLevel() < 100 && setFrequency("CHARGING") < 1){
                Battery_pull = 1;
                tts.speak("插入电源,充血中");
            }
        }
        if (mBodyState.getBatteryState() == BodyState.BATTERY_STATE_LOW){
            if ((mBodyState.getBatteryLevel() == 19 && setFrequency("LOW") < 3) || (mBodyState.getBatteryLevel() == 9) && setFrequency("LOW") < 5){
                mTimer.cancel();
                mTimer = new Timer();
                mTimer.schedule(new BatteryTimer(),9000,12000);
                tts.speak("电量过低，快饿死了,赶紧给我充电吧");
            }
        }
        if (mBodyState.getBatteryState() == BodyState.BATTERY_STATE_DISCHARGE && setFrequency("DISCHARGE") > 0){

            if ((mBodyState.getBatteryLevel() < 30 && Battery_pull == 0) && setFrequency("NATURE_DISCHARGE") < 2){
                tts.speak("亲,我有点饿了,帮我充电吧");
            }else if((mBodyState.getBatteryLevel() < 30 && Battery_pull == 1)) {
                Battery_pull = 0;
                tts.speak("我还没吃饱呢,你确定要这么做");
            }else if((mBodyState.getBatteryLevel() < 60 && Battery_pull == 1)) {
                Battery_pull = 0;
                tts.speak("我才吃到半饱,你真小气");
            }
        }

    }

    @Override
    public void balance() {
        if (mBodyState.getBatteryLevel() >= 70) {
            BFrame.motion(BodyActionCode.ACTION_120);
            tts.speak("还有百分之" + mBodyState.getBatteryLevel() + "体力充足,小子放马过来吧.");
        } else if (mBodyState.getBatteryLevel() >= 40){
            BFrame.motion(BodyActionCode.ACTION_31);
            tts.speak("还剩百分之" + mBodyState.getBatteryLevel() + "的电量,继续嗨没问题啦.");
        } else if (mBodyState.getBatteryLevel() >= 20){
            tts.speak("仅剩百分之" + mBodyState.getBatteryLevel() + "的电量了,感觉自己有点晕,巴拉巴拉巴拉.");
        } else if (mBodyState.getBatteryLevel() >= 6){
            tts.speak("糟糕只剩不到百分" + mBodyState.getBatteryLevel() + "了,一不小心透支了,你扶我起来我还能继续嗨.");
        } else {
            tts.speak("只有不到百分" + mBodyState.getBatteryLevel() + "的电,我现在四肢无力快给我充电");
        }
    }


    private class BatteryTimer extends TimerTask {
        public void run() {
            energy();
        }
    }

    private int setFrequency(String frequency){
        switch (frequency) {
            case "FULL":
                CHARGING = 0; LOW = 0; DISCHARGE = 0;
                return FULL++;
            case "CHARGING":
                FULL = 0; LOW = 0; DISCHARGE = 0;
                return CHARGING++;
            case "LOW":
                CHARGING = 0; FULL = 0; DISCHARGE = 0;
                return LOW++;
            case "DISCHARGE":
                CHARGING = 0;LOW = 0;FULL = 0;
                return DISCHARGE++;
            case "NATURE_DISCHARGE":
                CHARGING = 0;LOW = 0;FULL = 0; DISCHARGE = 0;
                return NATURE_DISCHARGE++;

            default:
                return 0;
        }
    }

}
