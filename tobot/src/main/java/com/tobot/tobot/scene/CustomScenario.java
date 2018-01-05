package com.tobot.tobot.scene;

import android.content.Context;
import android.os.Bundle;

import com.turing123.robotframe.config.SystemConfig;
import com.turing123.robotframe.function.tts.ITTSCallback;
import com.turing123.robotframe.function.tts.TTS;
import com.turing123.robotframe.multimodal.Behavior;
import com.turing123.robotframe.scenario.IScenario;
import com.turing123.robotframe.scenario.ScenarioRuntimeConfig;

/**
 * 特别注意： 进入该场景的关键词是唱首歌，用户开发属于自己的场景，需结合自定意图使用。
 * <p>
 * 自定义场景示例, 说明：
 * 1. 自定义场景需要实现 IScenario 接口
 * 2. 当场景被加载和卸载时，会调用 onScenarioLoad 和 onScenarioUnload.
 * 3. 当场景启动和退出时， 会调用 onStart 和 onExit.
 * 4. 使用默认主场景时， 主场景通过 onTransmitData 将属于该场景的云端协议传递给该场景。
 * 5. 当发生打断事件时， 会调用onUserInterrupted，并将发生打断的类型和一些附加信息传进来。
 * 6. 通过设置configScenarioRuntime方法中传入的参数ScenarioRuntimeConfig的值，可改变该场景运行时框架的默认环境。
 * 7. 语音打断的配置，可通过自定义意图配置应用内交互意图，也可以调用ScenarioRuntimeConfig.addInterruptCmd 添加一条打断指令
 *    当asr识别打断指令后，将产生打断事件。
 */
public class CustomScenario implements IScenario {
    private static final String DEMO_APPKEY = "唱歌";

    Context mContext;
    TTS tts;

    public CustomScenario(Context context) {
        this.mContext = context;
        tts = new TTS(mContext, this);
    }

    @Override
    public void onScenarioLoad() {
        tts.speak("CustomScenario 正在加载", ittsCallback);
    }

    @Override
    public void onScenarioUnload() {
        tts.speak("CustomScenario 正在卸载", ittsCallback);
    }

    @Override
    public boolean onStart() {
        tts.speak("CustomScenario 正在启动", ittsCallback);
        return true;
    }

    @Override
    public boolean onExit() {
        // 返回值说明，如果返回true, 表示立即退出；如果返回false,表示异步退出
        // 返回false时，框架会停止将ASR识别结果上传云端，直到调用了ScenarioManager.quitCurrentScenario()
        // 此处以返回false为例，模拟退出前要做一些操作。
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                TTS tts = new TTS(context, CustomScenario.this);
//                tts.speak("开始做退出处理....10秒后退出...", ittsCallback);
//                // 此处模拟一个耗时的过程。
//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                tts.speak("现在退出", ittsCallback);
//                ScenarioManager scenarioManager = new ScenarioManager(context);
//                // 调用quitCurrentScenario 退出当前场景，恢复NLP处理。
//                scenarioManager.quitCurrentScenario();
//            }
//        }).start();

        // 返回false, 框架不在处理NLP,等待场景退出中...
        return true;
    }

    @Override
    public boolean onTransmitData(Behavior behavior) {
        if (behavior.results != null) {
            tts.speak(behavior.results.get(0).values.getText(), ittsCallback);
        }
        return true;
    }

    @Override
    public boolean onUserInterrupted(int type, Bundle extra) {
        String content = "";
        if (extra != null) {
            extra.getString(SystemConfig.INTERRUPT_EXTRA_VOICE_CMD);
        }
        tts.speak("CustomScenario 收到打断事件" + content, ittsCallback);
        return true;
    }

    @Override
    public String getScenarioAppKey() {
        return DEMO_APPKEY;
    }

    @Override
    public ScenarioRuntimeConfig configScenarioRuntime(ScenarioRuntimeConfig scenarioRuntimeConfig) {
        scenarioRuntimeConfig.allowDefaultChat = true;
        //为场景添加打断语，asr 识别到打断语时将产生打断事件，回调到场景的onUserInterrupted() 方法。
        scenarioRuntimeConfig.addInterruptCmd("鸭蛋鸭蛋");
        scenarioRuntimeConfig.addInterruptCmd("不想听了");
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
