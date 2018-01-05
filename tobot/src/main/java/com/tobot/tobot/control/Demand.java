package com.tobot.tobot.control;

import android.content.Context;
import android.util.Log;

import com.tobot.tobot.MainActivity;
import com.tobot.tobot.control.demand.DemandFactory;
import com.tobot.tobot.control.demand.DemandModel;
import com.tobot.tobot.presenter.BRealize.BFrame;
import com.tobot.tobot.utils.TobotUtils;
import com.tobot.tobot.utils.socketblock.SocketConnectCoherence;

/**
 * Created by Javen on 2017/10/19.
 */
public class Demand {
    private static final String TAG = "Javen_MusicDemand";
    private static Demand demand;
    private static Context context;
    private static SocketConnectCoherence mCoherence;

    public Demand(Context context){
        this.context = context;
    }

    public static synchronized Demand instance(Context context) {
        if (demand == null) {
            demand = new Demand(context);
        }
        return demand;
    }

    public void setDemand(SocketConnectCoherence mCoherence){
        this.mCoherence = mCoherence;
        setResource();
    }

    public void setResource(){
        if (mCoherence != null) {
            mCoherence.setDemandListener(new SocketConnectCoherence.DemandListener() {
                @Override
                public void setDemandResource(DemandModel demand) {
                    //功能实现
                    DemandFactory demandFactory = DemandFactory.getInstance(context);
                    try {
                        Log.i(TAG,"进入点播关闭聊天");
                        BFrame.shutChat();
                        demandFactory.demands(demand);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void stopDemand() {
                    stopDemand();
                }
            });
        }
    }

//    public void setResource(DemandModel demandModel){
//        DemandFactory demandFactory = DemandFactory.getInstance(context);
//        try {
//            if (TobotUtils.isNotEmpty(MainActivity.mRobotFrameManager)){
//                MainActivity.mRobotFrameManager.toLostScenario();
//            }
//            demandFactory.demands(demandModel);
//            Log.i("Javen","点播:"+demandModel.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void stopDemand(){
        DemandFactory demandFactory = DemandFactory.getInstance(context);
        try {
            demandFactory.stopPlayMusic();
            Log.i("Javen","点播停止:");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
