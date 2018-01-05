package com.tobot.tobot.control.demand;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import com.tobot.tobot.Listener.SimpleFrameCallback;
import com.tobot.tobot.scene.BaseScene;
import com.tobot.tobot.scene.CustomScenario;
import com.tobot.tobot.utils.CommonRequestManager;
import com.turing123.robotframe.function.motor.Motor;
import com.turing123.robotframe.function.tts.TTS;
import com.turing123.robotframe.multimodal.action.Action;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by YF-04 on 2017/10/9.
 */

public class DemandDance implements DemandBehavior {
    private static final String TAG = "DemandDance";

    private DemandModel demandModel;
    private CommonRequestManager manager;
    private Context context;

    private TTS tts;
    private Motor motor;

    private MediaPlayer mediaPlayer;

    private String playUrl;
    private int bodyActionCode;

    private DemandUtils demandUtils;
    private Map<Integer,String> actionMap;

    public DemandDance(Context context,DemandModel danceModel){
        this.context=context;
        this.demandModel=danceModel;


        this.manager=CommonRequestManager.getInstanse(context);
        demandUtils =new DemandUtils(context);

        motor = new Motor(context, new CustomScenario(context));
        tts = new TTS(context,new BaseScene(context,"os.sys.chat"));

        //TODO  mohuaiyuan 20171009: 初始化 playUrl 和 bodyActionCode
        try {
            initData();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "DemandDance: "+e.getMessage() );
        }

    }

    private void initData() throws Exception {

        String playUrl32 = demandModel.getPlayUrl32();
        if (playUrl32 == null) {
            throw new Exception("Init Dance Info error :playUrl or bodyActionCode init error!");
        }
        String[] result = playUrl32.split(Constants.SEPARATOR_BETWEEN_PLAYURL_ACTION);

        if (result == null) {
            throw new Exception("Init Dance Info error :playUrl or bodyActionCode init error!");
        }
        Log.d(TAG, "result.length(): " + result.length);

        if (result.length == 1) {
            bodyActionCode = Integer.valueOf(result[0].trim());
            //init playUrl
            //play music file in local
            boolean initState = initPlayUrl();
            if (!initState) {
                throw new Exception("Init Dance Info error :playUrl  init error!");
            }
        } else if (result.length == 2) {
            playUrl = result[0].trim();
            bodyActionCode = Integer.valueOf(result[1].trim());
        } else {
            throw new Exception("Init Dance Info error :playUrl or bodyActionCode init error!");
        }

    }

    /**
     * init playUrl
     * @return true when the file is exist,others return false
     */
    private boolean initPlayUrl()throws Exception {

        //读取配置文件
       if (demandUtils.isConfigChange() || actionMap==null || actionMap.isEmpty()){
           actionMap= demandUtils.initActionConfig();
           demandUtils.setIsConfigChange(false);
       }else {
           Log.d(TAG, "There is no need to re-read the configuration file: ");
       }
        Log.d(TAG, "actionMap: "+actionMap);

        // 根据舞蹈序列号 找到对应的背景音乐的文件名
        String backgroundMusicName=actionMap.get(bodyActionCode);
        Log.d(TAG, "backgroundMusicName: "+backgroundMusicName);

        // 根据文件名 获取完整的文件路径
        File playUrlFile=manager.getSDcardFile(DemandUtils.DANCE_BACKGROUND_MUSIC_Dir+File.separator+backgroundMusicName);
        Log.d(TAG, "playUrlFile: "+playUrlFile.getAbsolutePath());
        if (playUrlFile.exists()){
            playUrl=playUrlFile.getAbsolutePath();
            Log.d(TAG, "playUrl: "+playUrl);
            return true;
        }
       return false;

    }


    public DemandModel getDemandModel() {
        return demandModel;
    }

    public void setDemandModel(DemandModel demandModel) {
        this.demandModel = demandModel;
    }

    @Override
    public void executeDemand() {

        //播放背景音乐
        try {
            manager.playMusic(playUrl, new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer=mp;
                    //发送舞蹈指令
                    sendBodyAction();
                }
            }, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 发送舞蹈指令 ，即机器人开始跳舞
     */
    private void sendBodyAction() {
        motor.doAction(Action.buildBodyAction(bodyActionCode,Action.PRMTYPE_EXECUTION_TIMES,1),new SimpleFrameCallback(){
            @Override
            public void onStarted() {
                super.onStarted();
                Log.d(TAG, "onStarted: ");
//                try{
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                Thread.sleep(1700);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                            //开始播放背景音乐
                            mediaPlayer.start();
//
//                        }
//                    }).start();
//                }catch (Exception e){
//                    e.printStackTrace();
//                    Log.e(TAG, "onStarted: "+e.getMessage());
//                }
            }

            @Override
            public void onStopped() {
                super.onStopped();
                Log.d(TAG, "onStopped: ");
            }

            @Override
            public void onPaused() {
                super.onPaused();
                Log.d(TAG, "onPaused: ");
            }

            @Override
            public void onResumed() {
                super.onResumed();
                Log.d(TAG, "onResumed: ");
            }

            @Override
            public void onInterrupted() {
                super.onInterrupted();
                Log.d(TAG, "onInterrupted: ");
            }

            @Override
            public void onCompleted() {
                super.onCompleted();
                Log.d(TAG, "onCompleted: ");
            }

            @Override
            public void onError(String s) {
                super.onError(s);
                Log.d(TAG, "onError: "+s);
            }
        });
    }
}
