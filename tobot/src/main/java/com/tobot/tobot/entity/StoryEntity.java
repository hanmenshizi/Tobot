package com.tobot.tobot.entity;

import java.io.Serializable;

/**
 * Created by YF-04 on 2017/8/31.
 */

public class StoryEntity implements Serializable {
    private String id;//20171207-Javen(long改string)
    private String kind;
    /**
     * 故事名称(storyTitle)
     */
    private String story;
    private String text_spare;
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getText_spare() {
        return text_spare;
    }

    public void setText_spare(String text_spare) {
        this.text_spare = text_spare;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public String toString() {
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append("[");
        stringBuffer.append("id:"+id+",");
        stringBuffer.append("kind:"+kind+",");
        stringBuffer.append("story(storyTitle):"+story+",");
        stringBuffer.append("text_spare:"+text_spare+",");
        stringBuffer.append("url:"+url);

        stringBuffer.append("]");
        return stringBuffer.toString();
    }
}
