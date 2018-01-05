package com.tobot.tobot.control.demand;

import android.content.Context;

import com.tobot.tobot.utils.CommonRequestManager;

/**
 * Created by YF-04 on 2017/10/9.
 */

public class DemandMusic implements DemandBehavior {
    private DemandModel demandModel;
    private CommonRequestManager manager;
    private Context context;

    public DemandMusic(Context context,DemandModel musicModel){
        this.context=context;
        this.manager=CommonRequestManager.getInstanse(context);
        this.demandModel=musicModel;
    }

    public DemandModel getDemandModel() {
        return demandModel;
    }

    public void setDemandModel(DemandModel demandModel) {
        this.demandModel = demandModel;
    }

    @Override
    public void executeDemand() {
        try {
            manager.playMusic(demandModel.getPlayUrl32(),null,null,null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
