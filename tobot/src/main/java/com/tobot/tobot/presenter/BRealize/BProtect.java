package com.tobot.tobot.presenter.BRealize;

import android.content.Context;
import android.util.Log;

import com.tobot.tobot.MainActivity;
import com.tobot.tobot.presenter.ICommon.ISceneV;
import com.tobot.tobot.presenter.IPort.IProtect;
import com.tobot.tobot.scene.BaseScene;
import com.tobot.tobot.utils.AppTools;
import com.turing123.robotframe.function.keyin.KeyInputEvent;
import com.turing123.robotframe.function.selfprotect.ISelfProtectObserver;
import com.turing123.robotframe.function.selfprotect.SelfProtect;
import com.turing123.robotframe.function.selfprotect.SelfProtectEvent;
import com.turing123.robotframe.function.tts.TTS;

/**
 * Created by Javen on 2017/8/28.
 */

public class BProtect implements IProtect{
    private Context mContent;
    private ISceneV mISceneV;
    private SelfProtect mSelfProtect;
    private TTS tts;

    public BProtect(ISceneV mISceneV){
        this.mISceneV = mISceneV;
        this.mContent = (Context)mISceneV;
        tts = new TTS(mContent,new BaseScene(mContent,"os.sys.chat"));
//        protect();
    }

    @Override
    public void protect() {
        //框架保护机制
        mSelfProtect = new SelfProtect(mContent,new BaseScene(mContent,"os.sys.chat"));
//        mSelfProtect.setNotifyInterval(8000);
        mSelfProtect.observeSlefProtect(new ISelfProtectObserver() {
            @Override
            public void onSelfProtectEvent(SelfProtectEvent selfProtectEvent) {
//                Log.i("Javen", "框架保护机制"+selfProtectEvent.toString());
                //框架保护机制
                switch (selfProtectEvent.type){
                    case SelfProtectEvent.EVENT_TYPE_ATTITUDE://姿势
                        switch (selfProtectEvent.attitude){
                            case SelfProtectEvent.ATTITUDE_FALL_BACKWARD://向后倒
                                tts.speak("啊,摔倒了");
                                break;
                            case SelfProtectEvent.ATTITUDE_FALL_FORWARD://向前倒
                                tts.speak("哎呀!好疼啊");
                                break;
                            case SelfProtectEvent.ATTITUDE_NORMAL://正常

                                break;
                            case SelfProtectEvent.ATTITUDE_OTHER://其他
//                                tts.speak("其他,我也不知道摔成什么样");
                                break;
                            case SelfProtectEvent.ATTITUDE_SHAKE://摇晃
                                tts.speak("我没喝醉,是地震了");
                                break;
                        }
                        break;
                    case SelfProtectEvent.EVENT_TYPE_INFRARED://红外
                        switch (selfProtectEvent.infrared1){
                            case 30://红外距离
                                tts.speak("你已被红外锁定");
                                break;
                            case 50://
                                tts.speak("红外解除锁定");
                                break;
                        }
                        break;
                    case SelfProtectEvent.EVENT_TYPE_SUPERSONIC://超声波
                        switch (selfProtectEvent.frontSupersonic){
                            case 15://最短障碍距离
                                tts.speak("你跟个傻瓜式的仵在这干嘛");
                                break;
                            case 60://最远感应距离
                                tts.speak("等等我,你跑那么快干嘛");
                                break;
                        }
                        break;

                    default:
                        break;
                }
            }
        });
    }
}
