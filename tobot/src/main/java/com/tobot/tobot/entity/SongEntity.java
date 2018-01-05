package com.tobot.tobot.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Javen on 2017/7/25.
 */

public class SongEntity implements Serializable {

    private String song;
    private String text_spare;
    private String singer;
    private String duration;
    private String code;
    private List<DetailsEntity> playList;
    private String url;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getText_spare() {
        return text_spare;
    }

    public void setText_spare(String text_spare) {
        this.text_spare = text_spare;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<DetailsEntity> getPlayList() {
        return playList;
    }

    public void setPlayList(List<DetailsEntity> playList) {
        this.playList = playList;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
