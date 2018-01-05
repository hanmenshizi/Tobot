/*
* Copyright (c) TuringOS 
*/
package com.tobot.tobot.function;

import android.content.Context;

import com.tobot.tobot.base.CustomTTSEngine;
import com.turing123.libs.android.utils.Logger;
import com.turing123.robotframe.event.AppEvent;
import com.turing123.robotframe.function.FunctionState;
import com.turing123.robotframe.function.IFunctionStateObserver;
import com.turing123.robotframe.function.IInitialCallback;
import com.turing123.robotframe.function.tts.ITTSFunction;
import com.turing123.robotframe.internal.function.tts.FrameTTSConfig;
import com.turing123.robotframe.internal.function.tts.IFrameSynthPCMCallback;
import com.turing123.robotframe.internal.function.tts.IFrameTTSCallback;

public class TTSFunction implements ITTSFunction {
    private static final String TAG = "TTSFunction";
    private Context mContext;
    private CustomTTSEngine customTTSEngine;

    public TTSFunction(Context context) {
        mContext = context;
        customTTSEngine = new CustomTTSEngine(context);
    }

    @Override
    public void initTTS(IInitialCallback callback) {
        customTTSEngine.initEngine(callback);
    }

    @Override
    public void speak(String content, IFrameTTSCallback callback) {
        Logger.d(TAG, "[CUSTOM_TTS] speak: " + content);
        customTTSEngine.speak(content, callback);
    }

    @Override
    public void stop() {
        customTTSEngine.stop();
    }

    @Override
    public void pause() {
        customTTSEngine.pause();
    }

    @Override
    public void resume() {
        customTTSEngine.resume();
    }

    @Override
    public void config(FrameTTSConfig config) {
        customTTSEngine.config(config);
    }



    @Override
    public void onFunctionLoad() {

    }

    @Override
    public void onFunctionUnload() {

    }

    @Override
    public void onFunctionInterrupted() {

    }

    @Override
    public int getFunctionType() {
        return AppEvent.FUNC_TYPE_TTS;
    }

    @Override
    public FunctionState getState() {
        return customTTSEngine.getState();
    }

    @Override
    public void setStateObserver(IFunctionStateObserver iFunctionStateObserver) {

    }

    @Override
    public void choiceProcessor(int i) {

    }

    @Override
    public void resetFunction() {

    }

    @Override
    public void synth(String s, IFrameSynthPCMCallback iFrameSynthPCMCallback) {
        
    }
}
