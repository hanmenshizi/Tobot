package com.tobot.tobot.presenter.BRealize;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.tobot.tobot.presenter.ICommon.ISceneV;
import com.tobot.tobot.presenter.IPort.IArmtouch;
import com.tobot.tobot.scene.BaseScene;
import com.tobot.tobot.scene.SongScenario;
import com.tobot.tobot.base.TouchResponse;
import com.turing123.robotframe.function.keyin.IKeyInputObserver;
import com.turing123.robotframe.function.keyin.KeyIn;
import com.turing123.robotframe.function.keyin.KeyInputEvent;
import com.turing123.robotframe.function.tts.TTS;

/**
 * Created by Javen on 2017/8/30.
 */

public class BArmtouch implements IArmtouch{
    private Context mContent;
    private ISceneV mISceneV;
    private String scenario = "scoff";//取得进入的场景
    private SongScenario mSongScenario;
    AudioManager mAudioManager;
    int maxVolume; //最大音量
    int currentVolume;//当前音量
    TTS tts;


    public BArmtouch(ISceneV mISceneV){
        this.mISceneV = mISceneV;
        this.mContent = (Context)mISceneV;
        this.mAudioManager = (AudioManager) mContent.getSystemService(Context.AUDIO_SERVICE);;
        maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        tts = new TTS(mContent,new BaseScene(mContent,"os.sys.chat"));
        armtouch();
    }


    @Override
    public void armtouch() {
        //手臂触摸监听机制
        KeyIn mKeyIn = new KeyIn(mContent,new BaseScene(mContent,"os.sys.chat"));
        mKeyIn.observeKeyInput(new IKeyInputObserver() {
            @Override
            public void onKeyInputEvent(KeyInputEvent keyInputEvent) {
//                Log.i("Javen","手臂触摸事件"+keyInputEvent.toString());
                switch (keyInputEvent.getKeycode()){
                    case KeyInputEvent.KEYCODE_LEFT_HAND://左手
                        switch (keyInputEvent.getEventType()){
                            case KeyInputEvent.EVENT_TYPE_SHORT://短按 --> 降低
                                entranceScenario(scenario,"LEFT");
                                break;
                            case KeyInputEvent.EVENT_TYPE_LONG://长按

                                break;
                        }
                        break;
                    case KeyInputEvent.KEYCODE_RIGHT_HAND://右手
                        switch (keyInputEvent.getEventType()){
                            case KeyInputEvent.EVENT_TYPE_SHORT://短按 -->调高
                                entranceScenario(scenario,"RIGHT");
                                break;
                            case KeyInputEvent.EVENT_TYPE_LONG://长按

                                break;
                        }
                        break;

                    default:
                        break;
                }

            }
        });
    }

    public void getScenario(String scenario){
        this.scenario = scenario;
    }

    public void getSongScenario(Object song) {
        this.mSongScenario = (SongScenario)song;
    }

    private void entranceScenario(String scenario,String direction){//短按控制
        Log.i("Javen","当前进入场景..."+scenario);
        switch (scenario){
            case "os.sys.song":
                volumeControl(direction);
                break;
//            case "os.sys.song_stop":
//                break;
            case "os.sys.story":
                volumeControl(direction);
                break;
//            case "os.sys.story_stop":
//                break;
            case "os.sys.dance":
                volumeControl(direction);
                break;
//            case "os.sys.dance_stop":
//                break;

            default:
                Log.i("Javen","未进入场景进入调侃");
                tts.speak(TouchResponse.getResponse(mContent));
                break;
        }
    }

    private void volumeControl(String direction){
        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        switch (direction){
            case "LEFT"://降低
                if(currentVolume > 6){
                    mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER,AudioManager.FX_FOCUS_NAVIGATION_UP);
                } else {
                    tts.speak("再小声你就听不到我说话了");
                }
                break;
            case "RIGHT"://调高
                if(maxVolume > currentVolume){
                    mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE,AudioManager.FX_FOCUS_NAVIGATION_UP);
                } else {
                    tts.speak("已经是最大声了");
                }
                break;
        }
    }

}
