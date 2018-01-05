package com.tobot.tobot.base;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.tobot.tobot.control.Demand;
import com.tobot.tobot.presenter.BRealize.BFrame;
import com.tobot.tobot.utils.TobotUtils;

import java.io.IOException;

import static com.tobot.tobot.MainActivity.ACTIVATESIGN;

/**
 * Created by Javen on 2017/9/4.
 */

public class Frequency {
    private static String TAG = "Javen Frequency";
    private static Context mContext;
    private static MediaPlayer mediaPlayer;
    private static boolean createState;
    private static Frequency frequency;



    private Frequency(){
        new Frequency(TobotApplication.getInstance());
    }

    private  Frequency(Context mContext){
        this.mContext = mContext;
    }

    public static synchronized Frequency instance() {
        if (frequency == null) {
            frequency = new Frequency();
        }
        return frequency;
    }

    public static synchronized Frequency instance(Context context) {
        if (frequency == null) {
            frequency = new Frequency(context);
        }
        return frequency;
    }

    public static void hint(){
        try {
            executeMP3(Constants.HINT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void start(String url){
        try {
            if (ACTIVATESIGN){
                Log.i(TAG,"ACTIVATESIGN:" + ACTIVATESIGN);
                BFrame.Interrupt();
            }
            executeMP3(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pause(){
        if (mediaPlayer !=null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public static void stop(){
        if (mediaPlayer !=null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    /**
     * 创建本地mp3
     * @return
     */
    private static MediaPlayer createLocalMp3(String url){
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource(url);
        } catch (IllegalArgumentException e) {
            return null;
        } catch (IllegalStateException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        return mp;
    }

    private  static void executeMP3(String url) throws IOException {
        if (TobotUtils.isEmpty(mediaPlayer)) {
            mediaPlayer = createLocalMp3(url);
            createState = true;
        } else{
            mediaPlayer.release();//释放音频资源
            mediaPlayer = createLocalMp3(url);
            createState = true;
        }
        //当播放完音频资源时，会触发onCompletion事件，可以在该事件中释放音频资源，
        //以便其他应用程序可以使用该资源:
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();//释放音频资源
//                mp = null;
                Log.i("Javen_Frequency", "资源已经被释放了");
            }
        });
        //在播放音频资源之前，必须调用Prepare方法完成些准备工作
        if (createState) {
            mediaPlayer.prepare();
        }
        //开始播放音频
        mediaPlayer.start();
    }


}
