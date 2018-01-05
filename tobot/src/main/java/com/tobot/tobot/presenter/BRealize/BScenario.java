package com.tobot.tobot.presenter.BRealize;

import android.content.Context;
import android.media.AudioManager;

import com.tobot.tobot.presenter.ICommon.ISceneV;
import com.tobot.tobot.scene.CustomActionScenario;
import com.tobot.tobot.scene.DanceScenario;
import com.tobot.tobot.scene.MusicClassify;
import com.tobot.tobot.scene.SongScenario;
import com.tobot.tobot.scene.StoryScenario;
import com.tobot.tobot.scene.TaleClassify;
import com.tobot.tobot.scene.VolumeScenario;
import com.tobot.tobot.presenter.IPort.IScene;
import com.turing123.robotframe.scenario.ScenarioManager;

/**
 * Created by Javen on 2017/8/8.
 */

public class BScenario implements IScene {
    private Context mContent;
    private AudioManager mAudioManager;
    private ISceneV mISceneV;

    public BScenario(ISceneV mISceneV){
        this.mISceneV = mISceneV;
        this.mContent = (Context)mISceneV;
        inScene();
   }

    @Override
    public void inScene() {
        //1. 获取Scenario 管理类对象
        ScenarioManager scenarioManager = new ScenarioManager(mContent);
        mAudioManager = (AudioManager) mContent.getSystemService(Context.AUDIO_SERVICE);
        //2. 添加自定义的Scenario
        //3. 对机器人说出进入场景的词语，进入场景。

//        SongScenario mSongScenario = new SongScenario(mISceneV);
//        scenarioManager.addScenario(mSongScenario);
//        mISceneV.getSongScenario(mSongScenario);
//        StoryScenario mStoryScenario = new StoryScenario(mISceneV);
//        scenarioManager.addScenario(mStoryScenario);
//        DanceScenario mDanceScenario = new DanceScenario(mISceneV);
//        scenarioManager.addScenario(mDanceScenario);

        scenarioManager.addScenario(SongScenario.instance(mISceneV));
        scenarioManager.addScenario(new StoryScenario(mISceneV));
        scenarioManager.addScenario(new DanceScenario(mISceneV));
        scenarioManager.addScenario(new VolumeScenario(mContent,mAudioManager));
        scenarioManager.addScenario(new CustomActionScenario(mContent,"胳膊控制"));
        scenarioManager.addScenario(new CustomActionScenario(mContent,"连贯动作"));
        scenarioManager.addScenario(new MusicClassify(mContent,"原地动作"));
        scenarioManager.addScenario(new CustomActionScenario(mContent,"动作控制"));
        scenarioManager.addScenario(new TaleClassify(mContent,"走动作"));
    }
}
