package com.tobot.tobot.control.demand;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YF-04 on 2017/11/10.
 */

public class DanceItemChain {
    private static final String TAG = "DanceItemChain";
    private boolean success;
    private String desc;
    private List<DanceItem> data;

    public DanceItemChain(){
        initData();
    }

    private void initData() {
        if (data==null){
            data=new ArrayList<>();
        }

    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<DanceItem> getData() {
        return data;
    }

    public void setData(List<DanceItem> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        Log.d(TAG, "toString: ");
        Log.d(TAG, "success: "+this.success);
        Log.d(TAG, "desc: "+this.desc);
        Log.d(TAG, "data.size(): "+data.size());
        StringBuffer stringBuffer=new StringBuffer();
        stringBuffer.append("{");

        stringBuffer.append("success:"+this.success+",");
        stringBuffer.append("desc:"+this.desc+",");
        stringBuffer.append("data:");
        stringBuffer.append("[");
        for (int i=0;i<this.data.size();i++){
            stringBuffer.append(data.get(i).toString());
            stringBuffer.append(",");
        }
        stringBuffer.append("]");
        stringBuffer.append("}");
        return stringBuffer.toString();
    }

    /**
     * 获取 所有文件 的 文件大小总和 ,单位（字节：Byte）
     * @return
     */
    public int getTotalSize()throws Exception{
        int totalSize=0;
        int tempSize=-1;
        for (int i=0;i<this.data.size();i++){
            DanceItem danceItem=this.data.get(i);
            tempSize=danceItem.getSize();
            if (tempSize<0){
                throw new Exception("The file size is less than zero! -- "+"id:"+danceItem.getId()+"  name:"+danceItem.getName());
            }
            totalSize+=tempSize;
        }

        return totalSize;
    }

}
