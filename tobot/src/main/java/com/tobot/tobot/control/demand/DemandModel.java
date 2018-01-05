package com.tobot.tobot.control.demand;

/**
 * Created by YF-04 on 2017/10/9.
 */

public class DemandModel {

    public String kind;
    private String playUrl32;
    private String track_title;
    private int categoryId;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getPlayUrl32() {
        return playUrl32;
    }

    public void setPlayUrl32(String playUrl32) {
        this.playUrl32 = playUrl32;
    }

    public String getTrack_title() {
        return track_title;
    }

    public void setTrack_title(String track_title) {
        this.track_title = track_title;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setInitialize(){
        setKind(null);
        setPlayUrl32(null);
        setTrack_title(null);
        setCategoryId(0);
    }

}
