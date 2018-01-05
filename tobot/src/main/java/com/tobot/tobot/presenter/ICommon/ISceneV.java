package com.tobot.tobot.presenter.ICommon;

import android.net.Uri;

/**
 * Created by Javen on 2017/8/8.
 */

public interface ISceneV {
//    Object setInfluence();//BDormant
    void getResult(Object result);//公用
    void getDormant(boolean dormant);//BDormant
    void getInitiativeOff(boolean initiative);//BConnect--主动联网
    void getFeelHead(boolean feel);//BConnect--摸头
    void getConnectFailure(boolean failure);//BConnect--联网失败回调
    void getScenario(String scenario);//BArmtouch
    void getSongScenario(Object song);//BArmtouch 废弃
//    void getImgpath(Uri path);//废弃
    void FrameLoadSuccess(boolean whence);//框架加载成功
    void FrameLoadFailure();//框架加载失败
//    void Interrupt(boolean isInterrupt);//废弃
}
