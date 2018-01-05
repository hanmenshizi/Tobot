package com.tobot.tobot.function;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.tobot.tobot.Listener.ExpressionCallback;
import com.tobot.tobot.Listener.SimpleFrameCallback;
import com.tobot.tobot.presenter.BRealize.BFrame;
import com.tobot.tobot.scene.BaseScene;
import com.tobot.tobot.scene.CustomScenario;
import com.tobot.tobot.utils.TobotUtils;
import com.turing123.robotframe.event.AppEvent;
import com.turing123.robotframe.function.FunctionState;
import com.turing123.robotframe.function.IFunctionStateObserver;
import com.turing123.robotframe.function.IInitialCallback;
import com.turing123.robotframe.function.assemble.IAssembleOutputFunction;
import com.turing123.robotframe.function.expression.Expression;
import com.turing123.robotframe.function.tts.ITTSCallback;
import com.turing123.robotframe.function.tts.TTS;
import com.turing123.robotframe.internal.function.assemble.IFrameAssembleOutputCallback;
import com.turing123.robotframe.multimodal.AssembleData;
import com.turing123.robotframe.multimodal.Behavior;
import com.turing123.robotframe.multimodal.action.Action;
import com.turing123.robotframe.multimodal.expression.FacialExpression;
import com.turing123.robotframe.multimodal.voice.Voice;
import com.turing123.robotframe.scenario.IScenario;
import com.turing123.robotframe.scenario.ScenarioRuntimeConfig;

import java.util.List;

import static com.turing123.robotframe.multimodal.action.Action.PRMTYPE_EXECUTION_TIMES;

/**
 * Created by Javen on 2017/9/22.
 * 创建AssembleFunction类，用来处理各种表现。
 */
public class AssembleFunction implements IAssembleOutputFunction {
    private static final String TAG = "Javen AssembleFunction";

    private Context mContext;
    private TTS tts;
    private IFrameAssembleOutputCallback iFrameAssembleOutputCallback;
    private int index;
    private int size;
    private List<AssembleData> currentDatas;
    private FunctionState state = FunctionState.UNREADY;
    private Voice voice;
    private Action action;
    private FacialExpression facialExpression;


    public AssembleFunction(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void init(IInitialCallback iInitialCallback) {
        tts = new TTS(mContext, tempScenario);
        // 初始化成功需要回调onSuccess.
        iInitialCallback.onSuccess();
        state = FunctionState.IDLE;
        Interrupt();
    }

    /**
     * 实现此方法用来处理输出
     *
     * @param list
     * @param iFrameAssembleOutputCallback 当start处理开始和完成需要回调其onStart 和 onStop 方法。
     *                                     特别注意：处理过程有可能是异步的，例如tts的输出，需在异步执行完成后调用onStop.
     */
    @Override
    public void start(List<AssembleData> list, IFrameAssembleOutputCallback iFrameAssembleOutputCallback, boolean last) {
//        Log.d(TAG, "[ASSEMBLE] start with list:" + list + ", last:" + last);
        index = 0;
        //1、先保存好传来的回调
        this.iFrameAssembleOutputCallback = iFrameAssembleOutputCallback;
        //2、此处已打印log的方式示范，再次强调：实际情况需要异步执行时，需要在执行完成后回调onStop
        iFrameAssembleOutputCallback.onStart();
        //3、开始处理需要将状态置为RUNNING.
        size = list.size();
        if (size > 0) {
            state = FunctionState.RUNNING;
            currentDatas = list;
            try {
                UnifiedIssued(index);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void UnifiedIssued(int index) throws Exception{
        AssembleData data = currentDatas.get(index);
        Log.d(TAG, "start: " + data);
        voice = data.voice;
        action = data.action;
        facialExpression = data.expression;
        //如下tts,action和expression的输出。
        if (voice != null || action != null || facialExpression != null) {

            try{
                if(action != null && TobotUtils.isNotEmpty(action.actionInfoList.get(0).actionCode)){
                    BFrame.motion(action.actionInfoList.get(0).actionCode);
                }
                if(facialExpression != null && TobotUtils.isNotEmpty(facialExpression.emoj)){
                    BFrame.Facial(facialExpression.emoj);
                }
                if (voice != null && TobotUtils.isNotEmpty(voice.text)) {
                    tts.speak(voice.text, ittsCallback);
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }else {
            // 一组输出全部处理完成，这时需要回调 onStop, 表示做完了。
            if (iFrameAssembleOutputCallback != null) {
                iFrameAssembleOutputCallback.onStop();
                //3、处理完成将状态置为IDLE;
                state = FunctionState.IDLE;
            }
        }
    }

    @Override
    public void stop() {
        state = FunctionState.IDLE;
    }

    @Override
    public void onFunctionLoad() { }

    @Override
    public void onFunctionUnload() { }

    @Override
    public void onFunctionInterrupted() {
        state = FunctionState.IDLE;
    }

    @Override
    public int getFunctionType() {
        return AppEvent.FUNC_TYPE_ASSEMBLE;
    }

    @Override
    public FunctionState getState() {
        return state;
    }

    @Override
    public void setStateObserver(IFunctionStateObserver iFunctionStateObserver) { }

    @Override
    public void choiceProcessor(int i) { }

    @Override
    public void resetFunction() { }

    private ITTSCallback ittsCallback = new ITTSCallback() {

        @Override
        public void onStart(String s) {
            mAssemble.Permit(true);
        }

        @Override
        public void onPaused() {
            mAssemble.Permit(false);
        }

        @Override
        public void onResumed() {
            mAssemble.Permit(true);
        }

        @Override
        public void onCompleted() {
            mAssemble.Permit(false);
            if (index == size - 1) {
                // 一组输出全部处理完成，这时需要回调 onStop, 表示做完了。
                if (iFrameAssembleOutputCallback != null) {
                    iFrameAssembleOutputCallback.onStop();
                    //3、处理完成将状态置为IDLE;
                    state = FunctionState.IDLE;
                }
            } else {
                index++;
                //输出一组中的下一个assemble数据。
                try {
                    UnifiedIssued(index);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
           BFrame.hint();
        }

        @Override
        public void onError(String s) {
            mAssemble.Permit(false);
            if (index == size - 1) {
                // 一组输出全部处理完成，这时需要回调 onStop, 表示做完了。
                if (iFrameAssembleOutputCallback != null) {
                    iFrameAssembleOutputCallback.onStop();
                    //3、处理完成将状态置为IDLE;
                    state = FunctionState.IDLE;
                }
            } else {
                index++;
                //输出一组中的下一个assemble数据。
                try {
                    UnifiedIssued(index);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private IScenario tempScenario = new IScenario() {
        @Override
        public void onScenarioLoad() { }

        @Override
        public void onScenarioUnload() { }

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
        public boolean onUserInterrupted(int type, Bundle extra) {
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
    };


    private IAssembleFunction mAssemble;

    public IAssembleFunction getAssembleFunction() {
        return mAssemble;
    }

    public void setAssembleFunction(IAssembleFunction mAssemble) {
        this.mAssemble = mAssemble;
    }

    public interface IAssembleFunction {
//        void Assemble(Object dispose);
        void Permit(Object interrupt);
    };

    private void Interrupt(){
        BFrame.setFrameThing(new BFrame.IFrameThing() {

            @Override
            public void setAssemble(Object dispose) {
                if ((boolean) dispose) {
                    tts.speak(" ", ittsCallback);
                }else {
                    tts.speak(" ");
                }
            }
        });
    }



}
