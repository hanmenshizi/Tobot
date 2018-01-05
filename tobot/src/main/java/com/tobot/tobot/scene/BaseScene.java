package com.tobot.tobot.scene;

import android.content.Context;
import android.os.Bundle;
import com.turing123.robotframe.multimodal.Behavior;
import com.turing123.robotframe.scenario.IScenario;
import com.turing123.robotframe.scenario.ScenarioRuntimeConfig;

/**
 * Created by Javen on 2017/7/21.
 */
public class BaseScene implements IScenario{

    private Context mContext;
    private String APPKEY;

    public BaseScene(Context context,String appkey){
        this.mContext = context;
        this.APPKEY = appkey;
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
        return scenarioRuntimeConfig;
    }
}
