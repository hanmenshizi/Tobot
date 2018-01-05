package com.tobot.tobot.control.demand;

/**
 * Created by YF-04 on 2017/11/9.
 */

public class ActionItem {
    private int id;
    private String name;
    private String code;
    private String thumb;

    private String data;
    private String createTime;
    private boolean valid;
    /**
     * data 路径对应的文件的大小，单位（字节：Byte）
     */
    private int size;
    /**
     * suffix 文件后缀
     */
    private String suffix;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append("{");
        stringBuffer.append("id:"+this.id+",");
        stringBuffer.append("name:"+this.name+",");
        stringBuffer.append("code:"+this.code+",");
        stringBuffer.append("thumb:"+this.thumb+",");

        stringBuffer.append("data:"+this.data+",");
        stringBuffer.append("createTime:"+this.createTime+",");
        stringBuffer.append("valid:"+this.valid+",");
        stringBuffer.append("size:"+this.size);

        stringBuffer.append("suffix:"+this.suffix);

        stringBuffer.append("}");
        return stringBuffer.toString();
    }
}
