package com.tobot.tobot.scene;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.JsonObject;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.tobot.tobot.MainActivity;
import com.tobot.tobot.base.Constants;
import com.tobot.tobot.presenter.BRealize.BFrame;
import com.turing123.robotframe.function.tts.ITTSCallback;
import com.turing123.robotframe.function.tts.TTS;
import com.turing123.robotframe.multimodal.Behavior;
import com.turing123.robotframe.scenario.IScenario;
import com.turing123.robotframe.scenario.ScenarioRuntimeConfig;

import org.json.JSONObject;

/**
 * Created by Javen on 2017/7/25.
 */

public class VolumeScenario implements IScenario{

    private String APPKEY = "os.sys.setting";
    private SpeechSynthesizer mSpeechSynthesizer;
    AudioManager mAudioManager;
    Context mContext;


    public VolumeScenario(Context context,AudioManager mAudioManager) {
        this.mContext = context;
        this.mAudioManager = mAudioManager;
    }

    @Override
    public void onScenarioLoad() {

    }

    @Override
    public void onScenarioUnload() {

    }

    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public boolean onExit() {
        return true;
    }

    @Override
    public boolean onTransmitData(Behavior behavior) {
        if (behavior.results != null) {
            TTS tts = new TTS(mContext, this);
            String direction = behavior.intent.getOperateState();
            if (direction.equals("1011") || direction.equals("1010")){
//              int before = Integer.parseInt(Constants.DEFAULT_VOLUME);
                int before = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                try{
                    int now = Integer.parseInt(behavior.intent.getParameters().get("setting_level").getAsString());
                    int gap = 0;
                    if (direction.equals("1011")){
                        gap = before - now;
                    }else if (direction.equals("1010")){
                        gap = before + now;
                    }
//            Log.i("Javen","最大音量"+mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
//            Log.i("Javen","当前音量"+mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
//            Log.i("Javen","保存音量"+Constants.DEFAULT_VOLUME);
                    if (gap > 1 && gap <= mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)){
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, gap, 0);
//                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER , AudioManager.FLAG_SHOW_UI);
//                mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(mContext, new InitListener() {
//                    @Override
//                    public void onInit(int resultCode) {
//
//                    }
//                });
                        Constants.DEFAULT_VOLUME = String.valueOf(gap);
//                mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, String.valueOf(gap));
                        tts.speak(behavior.results.get(0).values.getText(), ittsCallback);
                    }else if (gap < 2){
                        tts.speak("已经是最小声了哦", ittsCallback);
                    }else if (gap >= mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)){
                        tts.speak("已经是最大声了哦", ittsCallback);
                    }
                }catch (NullPointerException e){

                }
            }else if (direction.equals("1030")){
                BFrame.getBBattery().balance();
            }

        }
        return true;
    }

    @Override
    public boolean onUserInterrupted(int i, Bundle bundle) {
        return false;
    }

    @Override
    public String getScenarioAppKey() {
        return APPKEY;
    }

    @Override
    public ScenarioRuntimeConfig configScenarioRuntime(ScenarioRuntimeConfig scenarioRuntimeConfig) {
        scenarioRuntimeConfig.allowDefaultChat = true;
        //为场景添加打断语，asr 识别到打断语时将产生打断事件，回调到场景的onUserInterrupted() 方法。
        scenarioRuntimeConfig.addInterruptCmd("好了");
        scenarioRuntimeConfig.addInterruptCmd("可以了");
        return scenarioRuntimeConfig;
    }

    private ITTSCallback ittsCallback = new ITTSCallback() {
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
    };
}
