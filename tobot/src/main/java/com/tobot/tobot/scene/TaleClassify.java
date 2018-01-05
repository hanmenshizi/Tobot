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

/**
 * Created by Javen on 2017/12/27.
 */

public class TaleClassify implements IScenario {
    private String TAG = "Javen TaleClassify";
    private String APPKEY;
    private Context mContext;


    public TaleClassify(Context context, String appkey) {
        this.mContext = context;
        this.APPKEY = appkey;
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
        if (TobotUtils.isNotEmpty(behavior)) {
            Pattern p = Pattern.compile("[^0-9]");
            Matcher matcher = p.matcher(behavior.getIntent().getIntentName());
            Log.i("TAG", "故事分类代号:" + matcher.replaceAll("").trim());
        }
        Log.i("TAG", "onTransmitData behavior:" + behavior.toString());
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