package com.tobot.tobot.function;

import android.content.Context;
import com.turing123.robotframe.function.FunctionState;
import com.turing123.robotframe.function.IFunctionStateObserver;
import com.turing123.robotframe.function.IInitialCallback;
import com.turing123.robotframe.function.wakeup.IWakeUpFunction;
import com.turing123.robotframe.internal.function.wakeup.ILocalWakeUpCallback;

/**
 * Created by YF-03 on 2017/7/21.
 */
public class AwakenFuntion implements IWakeUpFunction{

    private Context context;
    private String resource;

    @Override
    public void initWakeUp(IInitialCallback iInitialCallback) {

    }

    @Override
    public void initWakeUp(String s, IInitialCallback iInitialCallback) {

    }

    @Override
    public void start(ILocalWakeUpCallback iLocalWakeUpCallback) {

    }

    @Override
    public void stop() {

    }

    @Override
    public void configWakeUp(String s) {

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
        return 0;
    }

    @Override
    public void choiceProcessor(int i) {

    }

    @Override
    public void resetFunction() {

    }

    @Override
    public FunctionState getState() {
        return null;
    }

    @Override
    public void setStateObserver(IFunctionStateObserver iFunctionStateObserver) {

    }
}
