/*
* Copyright (c) TuringOS 
*/
package com.tobot.tobot.base;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.turing123.robotframe.function.FunctionState;
import com.turing123.robotframe.function.IInitialCallback;
import com.turing123.robotframe.internal.function.tts.FrameTTSConfig;
import com.turing123.robotframe.internal.function.tts.IFrameTTSCallback;

import java.util.ArrayList;
import java.util.List;

public class CustomTTSEngine {
    private static final String TAG = "CustomTTSEngine";

    private static final int RESULT_CODE_SUCCESS = 0;

    private static final String APPID = "=562c8bae";
    /**
     * DEFAULT_VOLUME 默认音量30
     */
//    public static String DEFAULT_VOLUME = Constants.DEFAULT_VOLUME;
    /**
     * DEFAULT_ROLE 默认角色
     */
    protected String DEFAULT_ROLE = "xiaokun";
    protected static final int DEFAULT_SPEED = 5;
    protected static final int DEFAULT_PITCH = 5;

    private static final int MIN_SPEED = 0;
    private static final int MAX_SPEED = 9;

    private static final List<String> ROLES = new ArrayList<>();

    static {
        ROLES.add("nannan");
        ROLES.add("xiaoxin");
        ROLES.add("xiaokun");
        ROLES.add("xiaoqian");
        ROLES.add("xiaoyan");
        ROLES.add("xiaoyu");
        ROLES.add("vixy");
        ROLES.add("vixf");
        ROLES.add("xiaoqi");
        ROLES.add("xiaomei");
        ROLES.add("xiaolin");
        ROLES.add("xiaorong");
        ROLES.add("vixying");
        ROLES.add("vils");
        ROLES.add("catherine");
        ROLES.add("henry");
        ROLES.add("vimar");
    }

    private Context mContext;
    private FunctionState state;

    private String content;

    private IInitialCallback initialCallback;
    private IFrameTTSCallback callback;

    private SpeechSynthesizer mSpeechSynthesizer;

    private InitListener initListener = new InitListener() {
        @Override
        public void onInit(int resultCode) {
            if (resultCode == RESULT_CODE_SUCCESS) {
                state = FunctionState.IDLE;
                if (initialCallback != null) {
                    initialCallback.onSuccess();
                }
            } else {
                state = FunctionState.ERROR;
                if (initialCallback != null) {
                    initialCallback.onError("MotorFunctionErrorCode: " + resultCode);
                }
            }
        }
    };

    private SynthesizerListener synthesizerListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
            state = FunctionState.RUNNING;
            if (callback != null) {
                callback.onStart(content);
            }
        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }

        @Override
        public void onSpeakPaused() {
            state = FunctionState.PAUSE;
            if (callback != null) {
                callback.onPaused();
            }
        }

        @Override
        public void onSpeakResumed() {
            state = FunctionState.RUNNING;
            if (callback != null) {
                callback.onResumed();
            }
        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {
            if (speechError == null) {
                state = FunctionState.IDLE;
                if (callback != null) {
                    callback.onCompleted();
                }
            } else {
                if (callback != null) {
                    callback.onError(speechError.getPlainDescription(true));
                }
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    public CustomTTSEngine(Context context) {
        mContext = context;
        SpeechUtility.createUtility(mContext, SpeechConstant.APPID + APPID);
        state = FunctionState.UNREADY;
    }

    public void initEngine(IInitialCallback initialCallback) {
        this.initialCallback = initialCallback;
        mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(mContext, initListener);
        mSpeechSynthesizer
                .setParameter(SpeechConstant.VOICE_NAME, DEFAULT_ROLE);
        mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, DEFAULT_SPEED * 10 + "");
//        mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, DEFAULT_VOLUME);
        mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, Constants.DEFAULT_VOLUME);
        Log.i("CHAT","音量"+Constants.DEFAULT_VOLUME);
        mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        mSpeechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/tts.pcm");
    }

    public void speak(String content, IFrameTTSCallback callback) {
        this.content = content;
        this.callback = callback;
        mSpeechSynthesizer.startSpeaking(content, synthesizerListener);
    }

    public void pause() {
        mSpeechSynthesizer.pauseSpeaking();
    }

    public void resume() {
        mSpeechSynthesizer.resumeSpeaking();
    }

    public void stop() {
        if (FunctionState.RUNNING == state) {
            mSpeechSynthesizer.stopSpeaking();
        }
    }

    public void config(FrameTTSConfig config) {
        if (config != null) {
            if (config.getSpeed() >= MIN_SPEED && config.getSpeed() <= MAX_SPEED) {
                mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, DEFAULT_SPEED * 10 + "");
            }

            if (!TextUtils.isEmpty(config.getEffect()) && ROLES.contains(config.getEffect())) {
                mSpeechSynthesizer
                        .setParameter(SpeechConstant.VOICE_NAME, config.getEffect());
            }
        }
    }

    public FunctionState getState() {
        return state;
    }
}
