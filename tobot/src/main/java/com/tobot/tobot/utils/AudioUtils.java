package com.tobot.tobot.utils;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by YF-04 on 2017/11/29.
 */

public class AudioUtils {

    public static final int MUSIC_MIN_VOLUME_LEVEL=6;

    public static final int SET_MUSIC_VOLUME_SUCCESS=1;
    public static final int SET_MUSIC_VOLUME_FAILED=-1;
    public static final int CURRENT_LEVEL_IS_MIN_VOLUME_LEVEL=-2;
    public static final int CURRENT_LEVEL_IS_MAX_VOLUME_LEVEL=-3;

    private AudioManager manager;

    private Context mContext;

    private int errorCode;

    public AudioUtils(Context context){
        this.mContext=context;
        getManager();
    }

    public AudioManager getManager() {
        if (manager==null){
            manager= (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        }
        return manager;
    }

    public int getCurrentVolume(){
        int volumeLevel=-1;
        volumeLevel=manager.getStreamVolume(AudioManager.STREAM_MUSIC);
        return volumeLevel;
    }

    public int getMaxVolume(){
        int maxVolumeLevel=-1;
        maxVolumeLevel=manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return maxVolumeLevel;
    }

    public int setMusicVolume(int volumeLevel){

        if (!isLegal(volumeLevel)){
            return errorCode;
        }
        //设置音量：当前音量已经是最小的了
        if (volumeLevel==MUSIC_MIN_VOLUME_LEVEL && volumeLevel==getCurrentVolume()){
            errorCode=CURRENT_LEVEL_IS_MIN_VOLUME_LEVEL;
            return errorCode;
        }
        //设置音量：当前音量已经是最大的了
        if (volumeLevel==getMaxVolume() && volumeLevel==getCurrentVolume()){
            errorCode=CURRENT_LEVEL_IS_MAX_VOLUME_LEVEL;
            return errorCode;
        }
        manager.setStreamVolume(AudioManager.STREAM_MUSIC,volumeLevel,AudioManager.FLAG_PLAY_SOUND);
        errorCode=SET_MUSIC_VOLUME_SUCCESS;

        return errorCode;
    }

    public int setMaxVolume(){
        //设置音量：当前音量已经是最大的了
        if (getMaxVolume()==getCurrentVolume()){
            errorCode=CURRENT_LEVEL_IS_MAX_VOLUME_LEVEL;
            return errorCode;
        }
        manager.setStreamVolume(AudioManager.STREAM_MUSIC,getMaxVolume(),AudioManager.FLAG_PLAY_SOUND);
        return SET_MUSIC_VOLUME_SUCCESS;
    }

    public int setMinVolume(){
        //设置音量：当前音量已经是最小的了
        if (MUSIC_MIN_VOLUME_LEVEL == getCurrentVolume()){
            errorCode=CURRENT_LEVEL_IS_MIN_VOLUME_LEVEL;
            return errorCode;
        }
        manager.setStreamVolume(AudioManager.STREAM_MUSIC,MUSIC_MIN_VOLUME_LEVEL,0);
        return SET_MUSIC_VOLUME_SUCCESS;
    }

    public int adjustRaiseMusicVolume(){
        int currentVolumeLevel=getCurrentVolume();
        if (!isLegal(currentVolumeLevel)){
            return errorCode;
        }
        //设置音量：当前音量已经是最大的了
        if (getMaxVolume() ==getCurrentVolume()){
            errorCode=CURRENT_LEVEL_IS_MAX_VOLUME_LEVEL;
            return errorCode;
        }
        return setMusicVolume(getCurrentVolume()+1);

    }

    public int adjustLowerMusicVolume(){
        if (!isLegal(getCurrentVolume())){
            return errorCode;
        }
        //设置音量：当前音量已经是最小的了
        if (MUSIC_MIN_VOLUME_LEVEL ==getCurrentVolume()){
            errorCode=CURRENT_LEVEL_IS_MIN_VOLUME_LEVEL;
            return errorCode;
        }
        return setMusicVolume(getCurrentVolume()-1);
    }

    /**
     * 检验音量值是否合法
     * @param volumeLevel：音量值
     * @return :
     */
    public boolean isLegal(int volumeLevel){
        boolean isLegal=false;
        if (volumeLevel<MUSIC_MIN_VOLUME_LEVEL || volumeLevel>getMaxVolume()){
            errorCode=SET_MUSIC_VOLUME_FAILED;
            return isLegal;
        }

        return true;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
