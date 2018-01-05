package com.tobot.tobot.scene;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.tobot.tobot.presenter.BRealize.BFrame;
import com.tobot.tobot.utils.TobotUtils;
import com.turing123.robotframe.function.motor.Motor;
import com.turing123.robotframe.function.tts.TTS;
import com.turing123.robotframe.multimodal.Behavior;
import com.turing123.robotframe.scenario.IScenario;
import com.turing123.robotframe.scenario.ScenarioRuntimeConfig;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.turing123.robotframe.multimodal.action.Action.PRMTYPE_EXECUTION_TIMES;

/**
 * Created by Jvaen on 2017/8/8.
 */

public class CustomActionScenario implements IScenario {
    private String TAG = "Javen CustomActionScenario";
    private String APPKEY;
    private Context mContext;
    private TTS tts;


    public CustomActionScenario(Context context,String appkey){
        this.mContext = context;
        this.APPKEY = appkey;
        tts = new TTS(mContext, new BaseScene(mContext,"os.sys.chat"));
    }

    @Override
    public void onScenarioLoad() {

    }

    //removerscenario()
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
        tts.speak("好的");
        if (TobotUtils.isNotEmpty(behavior)){
            Pattern p = Pattern.compile("[^0-9]");
            Matcher matcher = p.matcher(behavior.getIntent().getIntentName());
            Log.i("TAG","运动代号:"+ matcher.replaceAll("").trim());
            BFrame.motion(Integer.parseInt(matcher.replaceAll("").trim()),true);
        }
        Log.i("TAG","onTransmitData behavior:" + behavior.toString());
        return true;
    }

    @Override
    public boolean onUserInterrupted(int i, Bundle bundle) {
        return true;
    }

    @Override
    public String getScenarioAppKey() {
        return APPKEY;
    }

    @Override
    public ScenarioRuntimeConfig configScenarioRuntime(ScenarioRuntimeConfig scenarioRuntimeConfig) {
        scenarioRuntimeConfig.allowDefaultChat = true;
        //为场景添加打断语，asr 识别到打断语时将产生打断事件，回调到场景的onUserInterrupted() 方法。
        return scenarioRuntimeConfig;
    }


}
