package com.tobot.tobot.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tobot.tobot.MainActivity;
import com.tobot.tobot.presenter.BRealize.BFrame;
import com.tobot.tobot.scene.BaseScene;
import com.turing123.robotframe.RobotFrameManager;
import com.turing123.robotframe.RobotFrameShutdownListener;
import com.turing123.robotframe.function.tts.ITTSCallback;
import com.turing123.robotframe.function.tts.TTS;
import com.turing123.robotframe.multimodal.action.Action;
import com.turing123.robotframe.multimodal.action.EarActionCode;

public class ShutdownReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i("Javen","关机广播");
//        if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)){
            //关机
//            BFrame.Ear(EarActionCode.EAR_MOTIONCODE_5);//关机时间太短了
            BFrame.shutDown();
//        }
    }

}
