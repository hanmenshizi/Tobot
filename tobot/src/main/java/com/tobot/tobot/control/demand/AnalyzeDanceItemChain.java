package com.tobot.tobot.control.demand;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by YF-04 on 2017/11/13.
 */

public class AnalyzeDanceItemChain implements AnalyzeBehavior {

    private DanceItemChain danceItemChain;
    private String json;
    private Gson gson;

    public AnalyzeDanceItemChain(String json){
        this.json=json;
        gson= new GsonBuilder().create();
    }

    public DanceItemChain getDanceItemChain() {
        return danceItemChain;
    }

    public void setDanceItemChain(DanceItemChain danceItemChain) {
        this.danceItemChain = danceItemChain;
    }


    @Override
    public DanceItemChain toModel() {
        danceItemChain=gson.fromJson(json,DanceItemChain.class);
        return danceItemChain;
    }
}
