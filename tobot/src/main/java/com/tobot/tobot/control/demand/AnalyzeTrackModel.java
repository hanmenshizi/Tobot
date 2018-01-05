package com.tobot.tobot.control.demand;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by YF-04 on 2017/10/9.
 */

public class AnalyzeTrackModel implements AnalyzeBehavior {
    private DemandModel demandModel;
    private String json;
    private Gson gson;

    public AnalyzeTrackModel(String json){
        this.json=json;
        gson= new GsonBuilder().create();
    }

    public DemandModel getDemandModel() {
        return demandModel;
    }

    public void setDemandModel(DemandModel demandModel) {
        this.demandModel = demandModel;
    }

    @Override
    public DemandModel toModel() {
        demandModel=gson.fromJson(json,TrackModel.class);
        return demandModel;
    }
}
