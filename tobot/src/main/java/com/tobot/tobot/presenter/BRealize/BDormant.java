package com.tobot.tobot.presenter.BRealize;

import android.content.Context;

import com.tobot.tobot.presenter.ICommon.ISceneV;
import com.tobot.tobot.presenter.IPort.IDormant;
import com.turing123.robotframe.RobotFrameManager;
import com.turing123.robotframe.localcommand.LocalCommand;
import com.turing123.robotframe.localcommand.LocalCommandCenter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Javen on 2017/8/8.
 */

public class BDormant implements IDormant {
    private Context mContent;
    private ISceneV mISceneV;
    private LocalCommandCenter localCommandCenter;
    private LocalCommand sleepCommand;
//    private RobotFrameManager mRobotFrameManager;//20171211-previous code

    public BDormant(ISceneV mISceneV){
        this.mISceneV = mISceneV;
        this.mContent = (Context)mISceneV;
        inDormant();
    }


    @Override
    public void inDormant() {
        //1. 获取LocalCommandCenter 对象
        localCommandCenter = LocalCommandCenter.getInstance(mContent);
        //2. 定义本地命令的名字
        String name = "sleep";
        //3. 定义匹配该本地命令的关键词，包含这些关键词的识别结果将交由该本地命令处理。
        List<String> keyWords = new ArrayList<String>();
        keyWords.add("去睡觉");
        keyWords.add("去休息");
        //4. 定义本地命令对象
        sleepCommand = new LocalCommand(name, keyWords) {
            //4.1. 在process 函数中实现该命令的具体动作。
            @Override
            protected void process(String name, String s) {
                //4.1.1. 本示例中，当喊关键词中配置的词时将使机器人进入睡眠状态
                //注意： 若要唤醒机器人，可调用wakeup,或者使用语言唤醒词唤醒。
                mISceneV.getDormant(false);
//                mRobotFrameManager = (RobotFrameManager) mISceneV.setInfluence(); //20171211-previous code
//                mRobotFrameManager.sleep();
//                //5.2 命令执行完成后需明确告诉框架，命令处理结束，否则无法继续进行主对话流程。
//                this.localCommandComplete.onComplete();
                BFrame.FallAsleep();
            }

            //4.2. 执行命令前的处理
            @Override
            public void beforeCommandProcess(String s) {

            }

            //4.3. 执行命令后的处理
            @Override
            public void afterCommandProcess() {

            }
        };
        //5. 将定义好的local command 加入 LocalCommandCenter中。
        localCommandCenter.add(sleepCommand);
    }
}
