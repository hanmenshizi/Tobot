package com.tobot.tobot.Listener;

import android.os.Bundle;

import com.turing123.robotframe.function.cloud.ICloudCallback;
import com.turing123.robotframe.internal.function.asr.IFrameASRCallback;
import com.turing123.robotframe.internal.function.assemble.IFrameAssembleOutputCallback;
import com.turing123.robotframe.internal.function.motor.IFrameMotorCallback;
import com.turing123.robotframe.internal.function.tts.IFrameTTSCallback;
import com.turing123.robotframe.multimodal.Behavior;
import com.turing123.robotframe.scenario.IMainScenario;
import com.turing123.robotframe.scenario.ScenarioRuntimeConfig;

/**
 * Created by Javen on 2017/9/20.
 */

public class MainScenarioCallback implements IMainScenario{

    @Override
    public IFrameASRCallback getMainScenarioAsrCallback() {
        return null;
    }

    @Override
    public IFrameTTSCallback getMainScenarioTtsCallback() {
        return null;
    }

    @Override
    public IFrameMotorCallback getMainScenarioMotorCallback() {
        return null;
    }

    @Override
    public IFrameAssembleOutputCallback getMainScenarioAssembleCallback() {
        return null;
    }

    @Override
    public void setCloudCallback(ICloudCallback iCloudCallback) {

    }

    @Override
    public void onScenarioLoad() {

    }

    @Override
    public void onScenarioUnload() {

    }

    @Override
    public boolean onStart() {
        return false;
    }

    @Override
    public boolean onExit() {
        return false;
    }

    @Override
    public boolean onTransmitData(Behavior behavior) {
        return false;
    }

    @Override
    public boolean onUserInterrupted(int i, Bundle bundle) {
        return false;
    }

    @Override
    public String getScenarioAppKey() {
        return null;
    }

    @Override
    public ScenarioRuntimeConfig configScenarioRuntime(ScenarioRuntimeConfig scenarioRuntimeConfig) {
        return null;
    }
}
