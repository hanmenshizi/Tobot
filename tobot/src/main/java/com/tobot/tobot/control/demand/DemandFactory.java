package com.tobot.tobot.control.demand;

/**
 * Created by YF-04 on 2017/10/9.
 */

import android.content.Context;
import android.util.Log;

import com.tobot.tobot.control.demand.decorate.DemandMusicDecorate;

/**
 * 点播功能 的工厂类
 */
public class DemandFactory {
    private static final String TAG = "DemandFactory";

    /**
     * 音乐
     */
    public static final int DEMAND_MUSIC=2;
    /**
     * 故事
     */
    public static final int DEMAND_STORY=6;
    /**
     * 国学
     */
    public static final int DEMAND_SINOLOGY=40;
    /**
     * 舞蹈
     */
    public static final int DEMAND_DANCE=88;

    private static DemandFactory demandFactory=new DemandFactory();

    private DemandBehavior demandBehavior;
    private DownloadBehavior downloadBehavior;

    private  AnalyzeTrackModel analyzeTrackModel;
    private  DemandModel demandModel;

    private AnalyzeDanceItemChain analyzeDanceItemChain;
    private DanceItemChain danceItemChain;

    private  int categoryId;

    private  static Context mContext;
    private String json;


    private DemandFactory(){

    }

    public static DemandFactory getInstance(Context context){
        mContext=context;
        return demandFactory;
    }

    private void initDemandBehavior(int categoryId) {

        switch (categoryId){
            case DEMAND_MUSIC:

            case DEMAND_STORY:

            case DEMAND_SINOLOGY:
                demandBehavior=new DemandMusic(mContext,demandModel);
                break;

            case DEMAND_DANCE:
                demandBehavior=new DemandDance(mContext,demandModel);
                break;

            default:
                demandBehavior=null;
                break;
        }

    }

    public void demands(String jsonString)throws Exception {

        if (jsonString==null || jsonString.length()<1 || jsonString.trim().length()<1){
           throw new Exception("Json format is  illegal!");
        }
        json=jsonString.trim();
        try {
            analyzeTrackModel=new AnalyzeTrackModel(json);
            analyzeTrackModel.toModel();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Json format is  illegal!");
        }
        demandModel=analyzeTrackModel.getDemandModel();
        categoryId=demandModel.getCategoryId();

        demands();
    }


    public void demands(DemandModel demandModel)throws Exception {
        if (demandModel==null){
            throw new Exception("demandModel==null ,demandModel is illegal!");
        }
        this.demandModel=demandModel;
        categoryId=demandModel.getCategoryId();

        demands();
    }

    private void demands()throws Exception {
        initDemandBehavior(categoryId);

        if (demandBehavior==null){
            throw new Exception("Init DemandFactory error:please  check the data your provided");
        }
        demandBehavior.executeDemand();
    }

    /**
     * 暂停播放音乐
     */
    public void pausePlayMusic() throws Exception {
        DemandMusicDecorate demandMusicDecorate= new DemandMusicDecorate(mContext,demandBehavior);
        demandMusicDecorate.pause();
    }
    //停止播放音乐
    public void stopPlayMusic() throws Exception {
        DemandMusicDecorate demandMusicDecorate= new DemandMusicDecorate(mContext,demandBehavior);
        demandMusicDecorate.stop();
    }

    /**
     * 下载 舞蹈及其相关的资源
     * @param data: json 格式的 用于下载舞蹈及其相关资源 的列表
     * @throws Exception
     */
    public void downloadDanceResource(String data)throws Exception{
        Log.d(TAG, "downloadDanceResource: ");

        //mohuaiyuan 测试 20171117
//        data="{\"success\":true,\"desc\":null,\"data\":[{\"id\":18,\"name\":\"java_zip\",\"code\":\"17\",\"thumb\":null,\"data\":\"http://39.108.134.20:80/tubarobot/data/dance/20171123144046\",\"suffix\":\"zip\",\"size\":1291734,\"time\":0,\"createTime\":\"2017-11-23 14:40:46\",\"valid\":false}]}";
        Log.d(TAG, "data: "+data);
        // 解析 json
        initDanceItemChain(data);
        //下载 文件
        downloadBehavior = new DownloadDanceResource(mContext,danceItemChain);
        downloadBehavior.download();
    }

    /**
     * 解析 下载舞蹈文件包的 json字符串
     * @param data
     * @throws Exception
     */
    private  void initDanceItemChain(String data)throws Exception{
        Log.d(TAG, "initDanceItemChain: ");
        if (data==null || data.length()<1 || data.trim().length()<1){
            throw new Exception("Json format is  illegal!");
        }
        try {
            analyzeDanceItemChain=new AnalyzeDanceItemChain(data);
            analyzeDanceItemChain.toModel();
        } catch (Exception e) {
            Log.e(TAG,"Json format is  illegal!" );
            e.printStackTrace();
            throw new Exception("Json format is  illegal!");
        }
        danceItemChain=analyzeDanceItemChain.getDanceItemChain();
        Log.d(TAG, "danceItemChain: "+danceItemChain.toString());
    }

    /**
     * 下载 动作及其相关的资源
     * @param data:json 格式的  用于下载动作及其相关资源 的列表
     * @throws Exception
     */
    public void downloadActionResource(String data)throws Exception{

        //TODO 解析 json

        //TODO 下载

        //TODO 修改配置文件

    }

}
