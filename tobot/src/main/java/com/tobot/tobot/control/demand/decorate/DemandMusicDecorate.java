package com.tobot.tobot.control.demand.decorate;

import android.content.Context;
import android.util.Log;

import com.tobot.tobot.control.Demand;
import com.tobot.tobot.control.demand.DemandBehavior;
import com.tobot.tobot.control.demand.DemandMusic;
import com.tobot.tobot.utils.CommonRequestManager;

/**
 * Created by YF-04 on 2017/11/27.
 */

public class DemandMusicDecorate implements DemandBehaviorDecorate,DemandBehavior{
    private static final String TAG = "DemandMusicDecorate";

    private DemandBehavior mDemandBehavior;
    private CommonRequestManager manager;
    private Context mContext;

    public DemandMusicDecorate(Context context,DemandBehavior demandBehavior ){
        this.mContext=context;
        this.mDemandBehavior=demandBehavior;
        if (this.manager==null){
            this.manager=CommonRequestManager.getInstanse(mContext);
        }

    }


    @Override
    public void pause() throws Exception {
        manager.pauseOrstartPlayMusic();

    }

    @Override
    public void stop() throws Exception {
        manager.stopPlayMusic();

    }

    @Override
    public void executeDemand() {
        mDemandBehavior.executeDemand();

    }
}
