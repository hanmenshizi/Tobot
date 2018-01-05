package com.tobot.tobot.scene;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tobot.tobot.R;
import com.tobot.tobot.control.demand.DemandUtils;
import com.tobot.tobot.entity.DetailsEntity;
import com.tobot.tobot.entity.SongEntity;
import com.tobot.tobot.entity.StoryEntity;
import com.tobot.tobot.presenter.ICommon.ISceneV;
import com.tobot.tobot.utils.AudioUtils;
import com.tobot.tobot.utils.CommonRequestManager;
import com.tobot.tobot.utils.TobotUtils;
import com.turing123.robotframe.function.tts.ITTSCallback;
import com.turing123.robotframe.function.tts.TTS;
import com.turing123.robotframe.multimodal.Behavior;
import com.turing123.robotframe.scenario.IScenario;
import com.turing123.robotframe.scenario.ScenarioManager;
import com.turing123.robotframe.scenario.ScenarioRuntimeConfig;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.track.SearchTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by YF-03 on 2017/8/31.
 */

public class StoryScenario implements IScenario {
    private static final String TAG = "StoryScenario";
    private static String APPKEY = "os.sys.story";
    private Context mContext;
    private ISceneV mISceneV;
    //    private MediaPlayer mediaPlayer;
    private String interrupt;
    private ScenarioManager scenarioManager;
    private boolean createState;
    private DetailsEntity details;
    private StoryEntity storyEntity;
    private String storyName;//故事名称
    private CommonRequestManager manager;
    private Map<String, String> specificParams;
    private List<Track> tracks = new ArrayList<>();
    private int categoryId;
    private int calcDimension;
    private int successCount;
    private MyHandler myHandler;
    int position = -1;
    private DemandUtils demandUtils;
    private Map<Integer, Integer> musicActionMaps;
    private int currentTimeSum;
    private volatile DoActionThread doActionThread;
    private TTS tts;
    private AudioUtils audioUtils;
    private boolean isWithAction = true;//唱歌 是否有 动作
    private int songDuration;

//    public StoryScenario(Context context){
//        Log.d(TAG, "StoryScenario: ");
//        this.mContext=context;
//        myHandler=new MyHandler();
//        specificParams=new HashMap<>();
//        initXimalaya();
//        initListener();
//    }


    public StoryScenario(ISceneV mISceneV) {
        Log.d(TAG, "StoryScenario: ");
        this.mContext = (Context) mISceneV;
        this.mISceneV = mISceneV;
        myHandler = new MyHandler();
        specificParams = new HashMap<>();
        initXimalaya();
        scenarioManager = new ScenarioManager(mContext);
        tts = new TTS(mContext, new BaseScene(mContext, "os.sys.chat"));
        audioUtils = new AudioUtils(mContext);
        demandUtils = new DemandUtils(mContext);
    }

    /**
     * 初始化 喜马拉雅 环境
     */
    public void initXimalaya() {
        Log.d(TAG, "initXimalaya: ");
        manager = CommonRequestManager.getInstanse(mContext);
        manager.initXimalaya();
    }

    private void initData() {
        currentTimeSum = 0;
    }

    private void initListener() {
        Log.d(TAG, "initListener: ");
        manager.setSearchTrackListIDataCallBack(new CommonRequestManager.SearchTrackListIDataCallBack() {
            @Override
            public void onSuccess(List<Track> tracks) {
                Log.d(TAG, "manager.setSearchTrackListIDataCallBack onSuccess(List<Track> tracks): ");
                Log.d(TAG, "tracks.size(): " + tracks.size());
                try {
                    initVoice();
                } catch (Exception e) {
                    Log.e(TAG, "Exception e.getMessage(): " + e.getMessage());
                    return;
                }
                Message message = new Message();
                message.what = TO_EXECUTE_STORY;
                myHandler.sendMessage(message);
            }

            @Override
            public void onSuccess(SearchTrackList searchTrackList) {
                Log.d(TAG, "manager.setSearchTrackListIDataCallBack onSuccess(SearchTrackList searchTrackList): ");
                List<Track> tempTrack = searchTrackList.getTracks();
                List<Track> resultTrack = new ArrayList<Track>();
                Log.d(TAG, "tempTrack.size(): " + tempTrack.size());
                Iterator iterator = tempTrack.iterator();
                while (iterator.hasNext()) {
                    Track track = (Track) iterator.next();
                    int duration = track.getDuration();
                    //mohuaiyuan 过滤掉  时间小于1 分钟 的故事
                    if (duration > 60) {
                        resultTrack.add(track);
                    }
                }
                Log.d(TAG, "resultTrack.size(): " + resultTrack.size());
                tracks.addAll(resultTrack);
//                tracks.addAll(searchTrackList.getTracks());
            }

            @Override
            public void onError(int code, String message) {
                Log.d(TAG, " manager.setSearchTrackListIDataCallBack  onError: ");
                Log.d(TAG, "code = [" + code + "], message = [" + message + "]");
            }
        });

        manager.setTrackListIDataCallBack(new CommonRequestManager.TrackListIDataCallBack() {
            @Override
            public void onSuccess(List<Track> tracks) {
                Log.d(TAG, " manager.setTrackListIDataCallBack  onSuccess(List<Track> tracks): ");
                Log.d(TAG, "tracks.size(): " + tracks.size());
                Random random = new Random();
                position = random.nextInt(tracks.size());
                try {
                    initVoice(position);
                    Log.d(TAG, "position: " + position);
                    Log.d(TAG, "track title: " + tracks.get(position).getTrackTitle());
                    Log.d(TAG, "duration: " + tracks.get(position).getDuration());
                } catch (Exception e) {
                    Log.e(TAG, "Exception e: " + e.getMessage());
                }
                Message message = new Message();
                message.what = TO_EXECUTE_STORY;
                myHandler.sendMessage(message);
            }

            @Override
            public void onSuccess(TrackList trackList) {
                Log.d(TAG, "manager.setTrackListIDataCallBack  onSuccess(TrackList trackList): ");
                tracks.addAll(trackList.getTracks());
            }

            @Override
            public void onError(int code, String message) {
                Log.d(TAG, "manager.setTrackListIDataCallBack  onError: ");
                Log.d(TAG, "code = [" + code + "], message = [" + message + "]");
            }
        });
    }


    @Override
    public void onScenarioLoad() {
    }

    @Override
    public void onScenarioUnload() {
        mISceneV.getScenario("os.sys.story_stop");
    }

    @Override
    public boolean onStart() {
        return true;
    }

    @Override
    public boolean onExit() {
        Log.d(TAG, "onExit: ");
        Log.d(TAG, "退出故事场景 ");
        mISceneV.getScenario("os.sys.story_stop");
//        manager.mediaPlayonExit(getMediaPlayer());//Javen注释
        if (getMediaPlayer() != null && getMediaPlayer().isPlaying()) {
            getMediaPlayer().stop();
        }
        manager.setMediaPlayer(null);

//        manager.mediaPlayonExit(mediaPlayer);

        // 调用quitCurrentScenario 退出当前场景，恢复NLP处理。
         scenarioManager.quitCurrentScenario();
//        manager.backMainScenario();
        return true;
    }

    private static final int TO_EXECUTE_STORY = 23;

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TO_EXECUTE_STORY:
                    executeVoice();
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public boolean onTransmitData(Behavior behavior) {
        Log.d(TAG, "onTransmitData: ");
        if (behavior.results != null) {
            Log.d(TAG, "进入故事场景:");
            if (getMediaPlayer() != null && getMediaPlayer().isPlaying()) {
                Log.d(TAG, "正在讲故事: ");
                return false;
            } else {
                Log.d(TAG, "没有 在 讲故事: ");
            }
            initData();
            initListener();
            //用于跟踪代码
            manager.setTAG(TAG);

            mISceneV.getScenario("os.sys.story");
            Behavior.IntentInfo intent = behavior.intent;
            JsonObject parameters = intent.getParameters();
            storyEntity = new Gson().fromJson(parameters, StoryEntity.class);

            //mohuaiyuan 201708 根据故事名 搜索故事播放资源(playUrl)
            if (storyEntity == null) {
                Log.e(TAG, "songEntity==null: ");
                return false;
            }
            storyName = storyEntity.getStory();
            //mohuaiyuan 先用图灵的资源 20170922
            executeStory();

//            if (!tracks.isEmpty()) {
//                tracks.clear();
//            }
//            Log.d(TAG, "storyName: " + storyName);
//            try {
//                if (storyName == null || storyName.length() < 1) {
//                    searchVoiceByName();
//                } else {
//                    searchVoiceByName(storyName);
//                }
//            } catch (Exception e) {
//                Log.e(TAG, "Exception e: " + e.getMessage());
//                return false;
//            }
        }
        return true;
    }


    public void searchVoiceByName(String voiceName) throws Exception {
        Log.d(TAG, "searchVoiceByName(String voiceName): ");

        if (voiceName == null || voiceName.length() < 1) {
            Log.e(TAG, "voiceName==null || voiceName.length()<1: ");
            return;
        }
//        Log.d(TAG, "voiceName: "+voiceName);

        //分类ID，不填或者为0检索全库
        categoryId = 0;
        //排序条件：2-最新，3-最多播放，4-最相关（默认）
        calcDimension = 4;
        manager.searchVoice(voiceName, categoryId, calcDimension);
    }

    public void searchVoiceByName() throws Exception {
        Log.d(TAG, "searchVoiceByName(): ");
        if (!tracks.isEmpty()) {
            tracks.clear();
        }
        manager.getVoiceList(true, 6, 1, "故事");
    }

    private void initVoice() throws Exception {
        Log.d(TAG, "initVoice() : ");


//        //mohuaiyuan 按播放次数 降序排列
        Collections.sort(tracks, new Comparator<Track>() {
            @Override
            public int compare(Track o1, Track o2) {
                if (o1.getPlayCount() > o2.getPlayCount()) {
                    return -1;
                } else if (o1.getPlayCount() == o2.getPlayCount()) {
                    return 0;
                } else {
                    return 1;
                }
            }
        });

        //TODO mohuaiyuan 在这里添加 筛选故事
        for (int i = 0; i < tracks.size(); i++) {
            if (tracks.get(i).getTrackTitle().toLowerCase().contains(storyName.toLowerCase())) {
                position = i;
                break;
            }
        }

        if (position == -1) {
            position = 0;
        }

        Log.d(TAG, "position: " + position);
        Log.d(TAG, "track title: " + tracks.get(position).getTrackTitle());
        Log.d(TAG, "duration: " + tracks.get(position).getDuration());

        initVoice(position);
    }

    private void initVoice(int position) throws Exception {
        Log.d(TAG, "initVoice(int position): ");

        String playUrl = "";
        try {
            playUrl = tracks.get(position).getPlayUrl32();
            if (playUrl == null) {
                playUrl = tracks.get(position).getPlayUrl64();
            }
            if (playUrl == null) {
                playUrl = tracks.get(position).getPlayUrl24M4a();
            }
            if (playUrl == null) {
                playUrl = tracks.get(position).getPlayUrl64M4a();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "onSuccess: " + e.getMessage());
        }

        if (playUrl == null) {
            throw new Exception("init voice error ,all of the playUrl is null! ");
        }
        storyEntity.setUrl(playUrl);
    }


    @Override
    public boolean onUserInterrupted(int i, Bundle bundle) {
        try {
            Log.d(TAG, "onUserInterrupted: ");
            Log.d(TAG, "StoryScenario bundle: " + bundle + "==>i:" + i);
            interrupt = bundle.getString("interrupt_extra_voice_cmd");
            if (TobotUtils.isNotEmpty(interrupt) && i == 1) {
                try {
                    if (interrupt.contains("暂停")) {
                        Log.d(TAG, "暂停: ");
                        getMediaPlayer().pause();
                        //mohuaiyuan 线程 中止
                        mISceneV.getScenario("os.sys.story_stop");
                        isWithAction = false;
                    }
                    if (interrupt.contains("不想听了") || interrupt.contains("好了") || interrupt.contains("可以了")) {
                        Log.d(TAG, "不想听了 or 好了 or 可以了: ");
                        getMediaPlayer().stop();
                        mISceneV.getScenario("os.sys.story_stop");
                        //mohuaiyuan 注释：不要退出场景
                        //退出当前场景
                      onExit();
                    }
                    if (interrupt.contains("你好小图")) {
                        Log.d(TAG, "图巴 :关键词------这里调用onexit方法了 ");
                        this.onExit();
                        mISceneV.getScenario("os.sys.song_stop");
                    }
                    if (interrupt.contains("继续") && !getMediaPlayer().isPlaying()) {
                        Log.d(TAG, "继续: ");
                        getMediaPlayer().start();
                        mISceneV.getScenario("os.sys.story");
                        //mohuaiyuan 线程
                        isWithAction = true;
                        doAction();
                    }
                    //mohuaiyuan 暂时不用
                    if (interrupt.contains("退出")) {
                        Log.d(TAG, "退出: ");
                        onExit();
                    }
                    //mohuaiyuan 什么也不做 20170914
                    if (interrupt.contains("推出")) {
                        Log.d(TAG, "推出: ");
                        onExit();
                    }
//                if (interrupt.contains("快进") || interrupt.contains("前进")) {
//                    int percentage = Integer.parseInt(details.getDuration());
//                    int speed = percentage / 100;
//                    Pattern pattern = Pattern.compile("[^0-9]");
//                    Matcher isNum = pattern.matcher(interrupt);
//                    Log.i("Javen","运动代号..."+ isNum.replaceAll("").trim());
//
////                Pattern pattern = Pattern.compile("[0-9]*");
////                Log.i("Javen", interrupt.substring(2, interrupt.length() - 1));
////                Matcher isNum = pattern.matcher(interrupt.substring(2, interrupt.length() - 1));
////                Log.i("Javen", isNum.matches() + "11");
//                    if (isNum.matches()) {
//                        Log.i("Javen", "快进" + Integer.parseInt(isNum.replaceAll("").trim()) * speed);
//                        mediaPlayer.seekTo(Integer.parseInt(isNum.replaceAll("").trim()) * speed);
//                    }
//                }

                    if (interrupt.contains("大声点")
                            || interrupt.contains("大点声")
                            || interrupt.contains("声音大一点")
                            || interrupt.contains("音量大一点")) {

                        int currentVolumeLevel = audioUtils.getCurrentVolume();
                        Log.d(TAG, "currentVolumeLevel: " + currentVolumeLevel);
                        int result = audioUtils.adjustRaiseMusicVolume();
                        if (result < 0) {
                            switch (result) {
                                case AudioUtils.CURRENT_LEVEL_IS_MAX_VOLUME_LEVEL:
                                    tts.speak(manager.getString(R.string.maxMusicVolume), null);
                                    break;

                                default:
                                    break;

                            }
                        } else {
                            tts.speak(manager.getString(R.string.raiseMusicVolume), null);
                        }
                        currentVolumeLevel = audioUtils.getCurrentVolume();
                        Log.d(TAG, "currentVolumeLevel: " + currentVolumeLevel);
                    }

                    if (interrupt.contains("小声点")
                            || interrupt.contains("小点声")
                            || interrupt.contains("声音小一点")
                            || interrupt.contains("音量小一点")) {
                        int currentVolumeLevel = audioUtils.getCurrentVolume();
                        Log.d(TAG, "currentVolumeLevel: " + currentVolumeLevel);
                        int result = audioUtils.adjustLowerMusicVolume();
                        if (result < 0) {
                            switch (result) {
                                case AudioUtils.CURRENT_LEVEL_IS_MIN_VOLUME_LEVEL:
                                    tts.speak(manager.getString(R.string.minMusicVolume), null);
                                    break;

                                default:
                                    break;
                            }
                        } else {
                            tts.speak(manager.getString(R.string.lowerMusicVolume), null);
                        }
                        currentVolumeLevel = audioUtils.getCurrentVolume();
                        Log.d(TAG, "currentVolumeLevel: " + currentVolumeLevel);
                    }
                } catch (IllegalStateException e) {

                }
            } else if (TobotUtils.isNotEmpty(bundle.getString("interrupt_extra_touch_keyEvent")) && i == 2) {
                Log.i(TAG, "头部打断");
                this.onExit();
            }
        } catch (NullPointerException e) {
            if (TobotUtils.isEmpty(bundle) && i == 2) {
                Log.d(TAG, "进入打断处理:catch");
                this.onExit();
            }
        }
        return true;
    }

    @Override
    public String getScenarioAppKey() {
        return APPKEY;
    }

    @Override
    public ScenarioRuntimeConfig configScenarioRuntime(ScenarioRuntimeConfig scenarioRuntimeConfig) {
        scenarioRuntimeConfig.allowDefaultChat = false;
        scenarioRuntimeConfig.interruptMatchMode = scenarioRuntimeConfig.INTERRUPT_CMD_MATCH_MODE_FUZZY;
        //为场景添加打断语，asr 识别到打断语时将产生打断事件，回调到场景的onUserInterrupted() 方法。
//        scenarioRuntimeConfig.addInterruptCmd("暂停");
//        scenarioRuntimeConfig.addInterruptCmd("继续");
//        scenarioRuntimeConfig.addInterruptCmd("不想听了");
//        scenarioRuntimeConfig.addInterruptCmd("好了");
//        scenarioRuntimeConfig.addInterruptCmd("可以了");
////        scenarioRuntimeConfig.addInterruptCmd("快进");
////        scenarioRuntimeConfig.addInterruptCmd("前进");
//        scenarioRuntimeConfig.addInterruptCmd("你好小图");
//        scenarioRuntimeConfig.addInterruptCmd("退出");
//        scenarioRuntimeConfig.addInterruptCmd("推出");

        scenarioRuntimeConfig.addInterruptCmd("大声点");
        scenarioRuntimeConfig.addInterruptCmd("小声点");
        scenarioRuntimeConfig.addInterruptCmd("大点声");
        scenarioRuntimeConfig.addInterruptCmd("小点声");
        scenarioRuntimeConfig.addInterruptCmd("声音大一点");
        scenarioRuntimeConfig.addInterruptCmd("声音小一点");
        scenarioRuntimeConfig.addInterruptCmd("音量大一点");
        scenarioRuntimeConfig.addInterruptCmd("音量小一点");
        return scenarioRuntimeConfig;
    }


    /**
     * 播放喜马拉雅的资源
     */
    private void executeVoice() {
        Log.d(TAG, "executeVoice(): ");

        try {
            String url = storyEntity.getUrl();
            Log.d(TAG, "url: " + url);
            executeSong(url);
        } catch (IOException e) {
            tts.speak(manager.getString(R.string.noExistStory));
            e.printStackTrace();
        }
    }

    /**
     * 播放 图灵的资源
     */
    private void executeStory(){
        Log.d(TAG, "executeSong(): ");

        boolean isExecuteSuccess=true;
        //mohuaiyuan  20171207 这段代码 没有用的 ，故事实体类 中并没有playlist
/*        try {
            Log.d(TAG, "准备。。播放喜马拉雅的故事: ");
            if (TobotUtils.isNotEmpty(storyEntity.getPlayList().get(0))) {
                details = songEntity.getPlayList().get(0);
                Log.d(TAG, "播放喜马拉雅的故事: ");
                Log.d(TAG, "歌曲playUrl: "+details.getTrack_url());

                executeSong(details.getTrack_url());
            }
        } catch (Exception e) {
            e.printStackTrace();
            isExecuteSuccess=false;
            Log.d(TAG, "Exception e.getMessage(): "+e.getMessage());
            Log.d(TAG, "Exception e.getCause(): "+e.getCause());
        }*/

        //mohuaiyuan 先用图灵的资源 20170922
//        if (!isExecuteSuccess){
        try {
            Log.d(TAG, "准备播放图灵默认的故事: ");
            if (TobotUtils.isNotEmpty(storyEntity.getUrl())) {
                Log.d(TAG, "播放图灵默认的故事: ");
                Log.d(TAG, "故事playUrl: "+storyEntity.getUrl());
                executeSong(storyEntity.getUrl());
            }else {
                //退出当前场景
                Log.d(TAG, "退出当前场景4756: ");
                //mohuaiyuan 20170925 调用onexit方法
                tts.speak(manager.getString(R.string.noExistStory));
                onExit();
            }
        } catch (Exception e) {
            Log.d(TAG, "Exception e.getMessage(): "+e.getMessage());
            Log.d(TAG, "Exception e.getCause(): "+e.getCause());
            //退出当前场景
            Log.d(TAG, "退出当前场景9395: ");
            //mohuaiyuan 20170925 调用onexit方法
            tts.speak(manager.getString(R.string.noExistStory));
            onExit();
        }
//        }

    }

    public void executeSong(String url) throws IOException {
        Log.d(TAG, "executeSong(String url): ");
        MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "MediaPlayer.OnPreparedListener onPrepared: ");

                //mohuaiyuan 增加语音播报 20171207
                Log.d(TAG, "storyName: "+storyName);
                String speech=manager.getString(R.string.beforePlayStory)+":"+storyName;
                tts.speak(speech, new ITTSCallback() {
                    @Override
                    public void onStart(String s) {
                        Log.d(TAG, "tts onStart: ");

                    }

                    @Override
                    public void onPaused() {
                        Log.d(TAG, "tts onPaused: ");

                    }

                    @Override
                    public void onResumed() {
                        Log.d(TAG, "tts onResumed: ");

                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "tts onCompleted: ");
                        //开始播放音频
                        try{
                            getMediaPlayer().start();
                            songDuration = getMediaPlayer().getDuration();
                            Log.d(TAG, "duration: " + songDuration);
                            if (isWithAction) {
                                doAction();
                            }
                        }catch (NullPointerException e){

                        }
                    }

                    @Override
                    public void onError(String s) {
                        Log.d(TAG, "tts onError: ");
                    }
                });


            }
        };

        MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "MediaPlayer.OnCompletionListener onCompletion: ");
                onExit();
            }
        };

        MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.d(TAG, "MediaPlayer.OnErrorListener onError: ");
                return false;
            }
        };

        try {
            manager.playMusic(url, onPreparedListener, onCompletionListener, onErrorListener);
        } catch (Exception e) {
            Log.d(TAG, "讲故事 出现 Exception e : " + e.getMessage());
            e.printStackTrace();
        }

    }

    private MediaPlayer getMediaPlayer() {
        return manager.getMediaPlayer();
    }

    private void doAction() {
        Log.d(TAG, "doAction: ");
        doActionThread = new DoActionThread();
        doActionThread.start();
    }


    /**
     * 播放当前的故事
     */
    private void replayStory() {
        Log.d(TAG, "replayStory: ");
        Message message = new Message();
        message.what = TO_EXECUTE_STORY;
        myHandler.sendMessage(message);
    }

    /**
     * 播放下一个故事
     */
    private void playNextStory() {
        Log.d(TAG, "playNextStory: ");

        int size = tracks.size();
        if (size == 1) {

        } else {
            position = position + 1;
            if (position >= size) {
                position -= size;
            }
        }
        try {
            initVoice(position);

            Log.d(TAG, "position: " + position);
            Log.d(TAG, "track title: " + tracks.get(position).getTrackTitle());
            Log.d(TAG, "duration: " + tracks.get(position).getDuration());
        } catch (Exception e) {
            e.printStackTrace();

            Log.e(TAG, "Exception e.getMessage(): " + e.getMessage());
            return;
        }

        Message message = new Message();
        message.what = TO_EXECUTE_STORY;
        myHandler.sendMessage(message);

    }

    public void setStoryName(String storyName) {
        this.storyName = storyName;
    }

    public boolean isWithAction() {
        return isWithAction;
    }

    public void setWithAction(boolean withAction) {
        isWithAction = withAction;
    }

    class DoActionThread extends Thread {

        private volatile int count = 0;

        public DoActionThread() {

        }

        @Override
        public void run() {
            super.run();
            int sleepTime = 0;
            try {
                if (musicActionMaps == null) {
                    musicActionMaps = demandUtils.initStoryActionInfo();
                }
            } catch (Exception e) {
                Log.e(TAG, "初始化 音乐动作的信息 出现 Exception e: " + e.getMessage());
                e.printStackTrace();
            }

            Set<Integer> set = musicActionMaps.keySet();
            Iterator<Integer> iterator = set.iterator();

            List<Integer> keyLists = new ArrayList<Integer>();
            while (iterator.hasNext()) {
                keyLists.add(iterator.next());
            }
            Random random = new Random();

            synchronized (this) {
                while (isWithAction && getMediaPlayer() != null && getMediaPlayer().isPlaying()) {

                    Log.d(TAG, "count: " + count);
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    int index = random.nextInt(keyLists.size());
                    int action = keyLists.get(index);
                    sleepTime = musicActionMaps.get(action);
                    currentTimeSum += sleepTime;
                    Log.d(TAG, "action: " + action);
                    Log.d(TAG, "sleepTime: " + sleepTime);
                    Log.d(TAG, "sleepTimeSum: " + currentTimeSum);
                    Log.d(TAG, "songDuration: " + songDuration);
                    if (currentTimeSum >= (songDuration - 5 * 1000)) {
                        Log.d(TAG, "机器人 不在做动作了。。。。。。。。: ");
                        break;
                    }
                    if (isWithAction) {
                        manager.doAction(action, null);
                    } else {
                        break;
                    }
                    count++;
                }
            }
        }
    }


}
