package com.tobot.tobot.db.model;

import android.util.Log;

import com.tobot.tobot.base.Constants;
import com.tobot.tobot.db.bean.MemoryDBManager;
import com.tobot.tobot.sqlite.annotation.Column;
import com.tobot.tobot.sqlite.annotation.Id;
import com.tobot.tobot.sqlite.annotation.Table;
import com.tobot.tobot.utils.TobotUtils;

import java.io.Serializable;

/**
 * Created by Javen on 2017/12/15.
 */
@Table(name = "tab_tobot_memory")
public class Memory implements Serializable {
//    private static Memory memory;
//
//    private Memory(){}
//
//    public static synchronized Memory instance() {
//        if (memory == null) {
//            memory = new Memory();
//        }
//        return memory;
//    }

    @Id(name = "keyId")
    public String keyId = "memory";

    @Column(name = "global")
    private String global = "0";//当true对全局动作进行阻拦

    @Column(name = "motion")
    private String motion = "0";//保存当前需记忆的动作

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getGlobal() {
        return global;
    }

    public void setGlobal(String global) {
        this.global = global;
    }

    public String getMotion() { return motion; }

    public void setMotion(String motion) {
        this.motion = motion;
    }

//    @Column(name = "squat")
//    private int squat;//蹲下
//
//    @Column(name = "sitDown")
//    private int sitDown;//坐下
//
//    @Column(name = "lieDown")
//    private int lieDown;//躺下
//
//    @Column(name = "goProne")
//    private int goProne;//趴下
//
//    @Column(name = "SitBack")
//    private int SitBack;//向后坐下
//
//    @Column(name = "SideDown")
//    private int SideDown;//侧摔倒
//
//    public int getSquat() {
//        return squat;
//    }
//
//    public void setSquat(int squat) {
//        this.squat = squat;
//    }
//
//    public int getSitDown() {
//        return sitDown;
//    }
//
//    public void setSitDown(int sitDown) {
//        this.sitDown = sitDown;
//    }
//
//    public int getLieDown() {
//        return lieDown;
//    }
//
//    public void setLieDown(int lieDown) {
//        this.lieDown = lieDown;
//    }
//
//    public int getGoProne() {
//        return goProne;
//    }
//
//    public void setGoProne(int goProne) {
//        this.goProne = goProne;
//    }
//
//    public int getSitBack() {
//        return SitBack;
//    }
//
//    public void setSitBack(int sitBack) {
//        SitBack = sitBack;
//    }
//
//    public int getSideDown() {
//        return SideDown;
//    }
//
//    public void setSideDown(int sideDown) {
//        SideDown = sideDown;
//    }

//    //检索连贯动作
//    public int IsContinue(){
//        int must = 0;
//        try{
//            Memory memory = MemoryDBManager.getManager().queryById(keyId);
//            Log.w("Javen","Global:"+memory.global);
//            if(memory.global){
//                Log.i("Javen","getMotion():");
//                switch (memory.getMotion()){
//                    case Constants.squat:
//                        must = Constants.squat_stand;
//                        break;
//                    case Constants.sitDown:
//                        must = Constants.sitDown_stand;
//                        break;
//                    case Constants.lieDown:
//                        must = Constants.lieDown_stand;
//                        break;
//                    case Constants.goProne:
//                        must = Constants.goProne_stand;
//                        break;
//                    case Constants.SitBack:
//                        must = Constants.SitBack_stand;
//                        break;
//                    case Constants.SideDown:
//                        must = Constants.SideDown_stand;
//                        break;
//                }
//            }
//        }catch (NullPointerException e){
//            return must;
//        }
//        Log.i("Javen","must:"+must);
//        return must;
//    }
//
//    //检索当前状态
//    public String nowState(int action) {
//        String state = "";
//        switch (action) {
//            case Constants.squat:
//                state = "蹲着";
//                break;
//            case Constants.sitDown:
//                state = "坐在地上";
//                break;
//            case Constants.lieDown:
//                state = "躺在地上";
//                break;
//            case Constants.goProne:
//                state = "趴在地上";
//                break;
//            case Constants.SitBack:
//                state = "坐着";
//                break;
//            case Constants.SideDown:
//                state = "倒在地上";
//                break;
//        }
//        return state;
//    }
//
//    //重置状态
//    public int resetState(int action){
//        int reset = 0;
//        try{
//            if (TobotUtils.isReset(action)) {
//                IsContinue();
//            }
////            if(Global){
////                switch (action){
////                    case Constants.squat_stand:
////                        must = Constants.squat_stand;
////                        break;
////                    case Constants.sitDown_stand:
////                        must = Constants.sitDown_stand;
////                        break;
////                    case Constants.lieDown_stand:
////                        must = Constants.lieDown_stand;
////                        break;
////                    case Constants.goProne_stand:
////                        must = Constants.goProne_stand;
////                        break;
////                    case Constants.SitBack_stand:
////                        must = Constants.SitBack_stand;
////                        break;
////                    case Constants.SideDown_stand:
////                        must = Constants.SideDown_stand;
////                        break;
////                }
////            }
//        }catch (Exception e){
//            return reset;
//        }
//        return reset;
//    }
//
//    //检索是否记忆
//    public boolean IsMemory(int action) {
//        Memory memory = new Memory();
//        if (TobotUtils.isMemory(action)) {
//            memory.setMotion(action);
//            memory.setGlobal(true);
//            Log.w("Javen","要记忆动作 action:" + action);
//        }else {
//            memory.setMotion(0);
//            memory.setGlobal(false);
//            Log.w("Javen","不要记忆动作 action:" + action);
//        }
//        MemoryDBManager.getManager().insert(memory);
//        return getGlobal();
//    }





}
