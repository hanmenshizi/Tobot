package com.tobot.tobot.Listener;

import android.content.Context;

import com.tobot.tobot.MainActivity;
import com.tobot.tobot.control.Demand;
import com.tobot.tobot.utils.socketblock.SocketConnectCoherence;
import com.turing123.robotframe.function.tts.ITTSCallback;

/**
 * Created by Javen on 2017/12/14.
 */

public class TTSCallback implements ITTSCallback {
    private final String TAG = "Javen TTSCallback";
    private Context context;
    private static TTSCallback mTTSCallback;

    private TTSCallback(Context context){
       this.context = context;
    }

    public static synchronized TTSCallback instance(Context context) {
        if (mTTSCallback == null) {
            mTTSCallback = new TTSCallback(context);
        }
        return mTTSCallback;
    }

    @Override
    public void onStart(String s) {

    }

    @Override
    public void onPaused() {

    }

    @Override
    public void onResumed() {

    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(String s) {

    }
}
