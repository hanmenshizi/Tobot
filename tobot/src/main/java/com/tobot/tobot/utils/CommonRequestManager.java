package com.tobot.tobot.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.tobot.tobot.Listener.SimpleFrameCallback;
import com.tobot.tobot.R;
import com.tobot.tobot.presenter.BRealize.BFrame;
import com.tobot.tobot.scene.CustomScenario;
import com.turing123.robotframe.function.motor.Motor;
import com.turing123.robotframe.multimodal.action.Action;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.track.SearchTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * Created by YF-04 on 2017/8/21.
 */

public class CommonRequestManager {

    private static  String TAG = "Javen CommonRequestManager";

    private static CommonRequestManager manager=new CommonRequestManager();
    private CommonRequest commonRequest;

    private Map<String, String> specificParams;
//    private Map<Integer,String>errorInfo=new HashMap<>();
//    private List<Category> categories=new ArrayList<>();

    private List<Album> albums=new ArrayList<>();
    private List<Track> tracks=new ArrayList<>();
    private int successCount;
    private long albumId;
    private MyHandler myHandler;

    private static int defaultPageCount=50;

    private TrackListIDataCallBack trackListIDataCallBack;
    private AlbumListIDataCallBack albumListIDataCallBack;
    private SearchTrackListIDataCallBack searchTrackListIDataCallBack;

    private static Context mContext;

    private String mAppSecret;
    private MediaPlayer mediaPlayer;


    private Motor motor;

    private CommonRequestManager(){
        commonRequest= CommonRequest.getInstanse();
        specificParams=new HashMap<>();
        myHandler=new MyHandler();

    }

    public static CommonRequestManager getInstanse(Context context){
        mContext=context;
        return manager;
    }

    /**
     * 初始化 喜马拉雅 环境
     */
    public void initXimalaya() {
        Log.d(TAG, "initXimalaya: ");
        mAppSecret = mContext.getResources().getString(R.string.app_secret);
        init(mContext,mAppSecret);
        setDefaultPagesize(defaultPageCount);

    }

    public void init(Context context, String appsecret){
        commonRequest.init(context,appsecret);
    }

    public void setDefaultPagesize(int size) {
        commonRequest.setDefaultPagesize(size);
        this.defaultPageCount=size;
    }

    public static String getTAG() {
        return TAG;
    }

    public static void setTAG(String TAG) {
        CommonRequestManager.TAG = TAG;
    }

    public TrackListIDataCallBack getTrackListIDataCallBack() {
        return trackListIDataCallBack;
    }

    public void setTrackListIDataCallBack(TrackListIDataCallBack trackListIDataCallBack) {
        this.trackListIDataCallBack = trackListIDataCallBack;
    }

    public AlbumListIDataCallBack getAlbumListIDataCallBack() {
        return albumListIDataCallBack;
    }

    public void setAlbumListIDataCallBack(AlbumListIDataCallBack albumListIDataCallBack) {
        this.albumListIDataCallBack = albumListIDataCallBack;
    }

    public SearchTrackListIDataCallBack getSearchTrackListIDataCallBack() {
        return searchTrackListIDataCallBack;
    }

    public void setSearchTrackListIDataCallBack(SearchTrackListIDataCallBack searchTrackListIDataCallBack) {
        this.searchTrackListIDataCallBack = searchTrackListIDataCallBack;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public static final String TRACK_KIND="track";
    public static final String PLAY_URL_32="play_url_32";
    public static final String PLAY_URL_64="play_url_64";
    public static final String PLAY_URL_24_M4a="play_url_24_m4a";
    public static final String PLAY_URL_64_M4a="play_url_64_m4a";

    /**
     *init a base track
     * @param options:play_url_32 ,play_url_64 ,play_url_24_m4a and play_url_64_m4a,those options can not be all null.
     * @return track if is success,null while those options are all null.
     */
    public Track initTrack(Map<String,String> options){

        if (options==null || options.isEmpty()){
            return null;
        }

        Track track=new Track();
        track.setKind(TRACK_KIND);

        boolean initState=false;

        String play_url_32=options.get(PLAY_URL_32);
        if(play_url_32!=null){
            initState=true;
            track.setPlayUrl32(play_url_32);
        }

        String play_url_64=options.get(PLAY_URL_64);
        if(play_url_64!=null){
            initState=true;
            track.setPlayUrl64(play_url_64);
        }

        String play_url_24_m4a=options.get(PLAY_URL_24_M4a);
        if(play_url_24_m4a!=null){
            initState=true;
            track.setPlayUrl24M4a(play_url_24_m4a);
        }

        String play_url_64_m4a=options.get(PLAY_URL_64_M4a);
        if(play_url_64_m4a!=null){
            initState=true;
            track.setPlayUrl64M4a(play_url_64_m4a);
        }

        if(!initState){
            return null;
        }

        return track;
    }

    /**
     *
     * @param isGetTrack :是否去获取声音列表。是：获取声音列表；否，仅仅获取专辑列表。
     * @param categoryId:音乐分类 ,为0时表示热门分类. eg. 2-音乐 6-儿童
     * @param calcDimension：计算维度，现支持最火（1），最新（2），经典或播放最多（3）
     * @param tagName
     */
    public void getVoiceList(boolean isGetTrack,int categoryId , int calcDimension ,String tagName){
        Log.d(TAG, "getVoiceList(boolean isGetTrack,int categoryId , int calcDimension ,String tagName): ");
        if (!specificParams.isEmpty()) {
            specificParams.clear();
        }

        if (categoryId<0){
            Log.e(TAG, "cagetory<0 :");
            return;
        }
        if (calcDimension!=1 && calcDimension!=2 && calcDimension!=3){
            Log.e(TAG, "calcDimension!=1 && calcDimension!=2 && calcDimension!=3 :");
            return;
        }
        Log.d(TAG, "isGetTrack: "+isGetTrack);
        Log.d(TAG, "categoryId: "+categoryId);
        Log.d(TAG, "calcDimension: "+calcDimension);
        Log.d(TAG, "tagName: "+tagName);


        //音乐分类
//        categoryId = 2;
        //计算维度，现支持最火（1），最新（2），经典或播放最多（3）
//        calcDimension = 1;
        //分类下对应的专辑标签，不填则为热门分类
//        tagName = "歌单";

        //返回第几页，必须大于等于1，不填默认为1
//        page = 3000;
        specificParams.put(DTransferConstants.CATEGORY_ID, String.valueOf(categoryId));
        specificParams.put(DTransferConstants.CALC_DIMENSION, String.valueOf(calcDimension));
//        specificParams.put(DTransferConstants.PAGE, String.valueOf(page));
        if(tagName!=null && tagName.length()>0){
            specificParams.put(DTransferConstants.TAG_NAME, tagName);
        }

        getVoiceList(specificParams,isGetTrack);

    }

    private void getVoiceList(final Map<String, String> specificParams, final boolean isGetTrack){
        Log.d(TAG, "getVoiceList(final Map<String, String> specificParams, final boolean isGetTrack): ");
        if (!albums.isEmpty()){
            albums.clear();
        }
        //init
        successCount=0;

        String tempId=specificParams.get(DTransferConstants.CATEGORY_ID);
        String tempCalcDimension=specificParams.get(DTransferConstants.CALC_DIMENSION);
        if (tempId==null || tempCalcDimension ==null){
            Log.e(TAG, "Illegal parameter of specificParams:category is null or calcDimension is null ! ");
            return;
        }


        CommonRequest.getAlbumList(specificParams, new IDataCallBack<AlbumList>() {

            @Override
            public void onSuccess(AlbumList albumList) {
                Log.d(TAG, "CommonRequest.getAlbumList onSuccess: ");
                //获取第一页的专辑列表
                albums.addAll(albumList.getAlbums());
                Log.d(TAG, "albumList = [" + albumList + "]");

                if (albumListIDataCallBack!=null){
                    albumListIDataCallBack.onSuccess(albumList);
                }

                if (isGetTrack){
                    Message message = new Message();
                    message.what = TO_GET_TRICK;
                    myHandler.sendMessage(message);

                    if (albumListIDataCallBack!=null){
                        albumListIDataCallBack.onSuccess(albums);
                    }
                }



//                Log.d(TAG, "albumListToString: " + MyUtils.getInstance().albumListToString(albumList));
                //获取所有的专辑列表
//                final int totalPage= albumList.getTotalPage();
//
//                for(int i=0;i<totalPage;i++){
//                    specificParams.put(DTransferConstants.PAGE,String.valueOf(i+1));
//                    CommonRequest.getAlbumList(specificParams, new IDataCallBack<AlbumList>() {
//                        @Override
//                        public void onSuccess(AlbumList albumList) {
//                            albums.addAll(albumList.getAlbums());
////                            Log.d(TAG, "albumList = [" + albumList + "]");
//
//                            if (albumListIDataCallBack != null) {
//                                albumListIDataCallBack.onSuccess(albumList);
//                            }
//
//                            successCount++;
////                            Log.d(TAG, "successCount: " + successCount);
//                            if (isGetTrack && successCount == totalPage && successCount != 0) {
//                                Message message = new Message();
//                                message.what = TO_GET_TRICK;
//                                myHandler.sendMessage(message);
//
//                                if (albumListIDataCallBack!=null){
//                                    albumListIDataCallBack.onSuccess(albums);
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onError(int code, String message) {
//                            Log.d(TAG, "code = [" + code + "], message = [" + message + "]");
//                            if (albumListIDataCallBack!=null){
//                                albumListIDataCallBack.onError(code,message);
//                            }
//                        }
//                    });
//                }
            }

            @Override
            public void onError(int code, String message) {
                Log.d(TAG, "code = [" + code + "], message = [" + message + "]");
                if (albumListIDataCallBack!=null){
                    albumListIDataCallBack.onError(code,message);
                }
            }
        });

    }



    private void getTracks(long albumId){

        Album album=null;
        boolean isContains=false;
        for(int i=0;i<albums.size();i++){
            if(albums.get(i).getId()==albumId){
                album=albums.get(i);
                isContains=true;

                break;
            }
        }

        if (!isContains){
            album=albums.get(0);
        }

        getTracksByAlbum(album);

    }

    private int successGetTrack;
    private int pageCount;
    public   void  getTracksByAlbum(Album album){
        Log.d(TAG, "getTracksByAlbum: ");
        if(album==null){
            Log.e(TAG, "album==null: ");
            return;
        }
        if(!tracks.isEmpty()){
            tracks.clear();
        }

        successGetTrack=0;
        pageCount=0;
        int includeTrackCount=(int)album.getIncludeTrackCount();
        pageCount=includeTrackCount/defaultPageCount;
        if(includeTrackCount%defaultPageCount!=0){
            pageCount++;
        }
        Log.d(TAG, "pageCount: "+pageCount);

        for(int page=0;page<pageCount;page++){

            if (!specificParams.isEmpty()) {
                specificParams.clear();
            }
            specificParams.put(DTransferConstants.ALBUM_ID, String.valueOf(albumId));
            specificParams.put(DTransferConstants.PAGE,String.valueOf(page+1) );

            CommonRequest.getTracks(specificParams, new IDataCallBack<TrackList>() {

                @Override
                public void onSuccess(TrackList trackList) {
                    tracks.addAll(trackList.getTracks());
//                    Log.d(TAG, "trackList = [" + trackList + "]");
//                    Log.d(TAG, "trackListToString: " + MyUtils.getInstance().trackListToString(trackList));
                    successGetTrack++;

                    if (trackListIDataCallBack!=null){
                        trackListIDataCallBack.onSuccess(trackList);
                    }

                    if(pageCount==successGetTrack){
//                        Message message=new Message();
//                        message.what=TO_EXECUTE_SONG;
//                        myHandler.sendMessage(message);
                        if (trackListIDataCallBack!=null){
                            trackListIDataCallBack.onSuccess(tracks);
                        }
                    }
                }

                @Override
                public void onError(int code, String message) {
//                    Log.d(TAG, "code = [" + code + "], message = [" + message + "]");
                    if (trackListIDataCallBack!=null){
                        trackListIDataCallBack.onError(code,message);
                    }
                }
            });
        }

    }

    public void searchVoice(String voiceName,int categoryId,int calcDimension){
        Log.d(TAG, "searchSong: ");
        if(!tracks.isEmpty()){
            tracks.clear();
        }
        if (!specificParams.isEmpty()) {
            specificParams.clear();
        }
        specificParams.put(DTransferConstants.SEARCH_KEY, voiceName);
        specificParams.put(DTransferConstants.CATEGORY_ID, String.valueOf(categoryId));
        specificParams.put(DTransferConstants.CALC_DIMENSION,String.valueOf(calcDimension));
        successCount=0;

        CommonRequest.getSearchedTracks(specificParams, new IDataCallBack<SearchTrackList>() {

            @Override
            public void onSuccess(SearchTrackList searchTrackList) {
                Log.d(TAG, "searchTrackList.getCategoryId: "+searchTrackList.getCategoryId());
                Log.d(TAG, "searchTrackList.getTagName: "+searchTrackList.getTagName());
                Log.d(TAG, "searchTrackList.getParams: "+searchTrackList.getParams());
                Log.d(TAG, "searchTrackList.getTotalPage: "+searchTrackList.getTotalPage());
                Log.d(TAG, "tracks.size(): " + tracks.size());

                final int totalPage=searchTrackList.getTotalPage();
                for(int page=0;page<totalPage;page++){
                    specificParams.put(DTransferConstants.PAGE,String.valueOf(page+1));
                    CommonRequest.getSearchedTracks(specificParams, new IDataCallBack<SearchTrackList>() {
                        @Override
                        public void onSuccess(SearchTrackList searchTrackList) {

                            if (searchTrackListIDataCallBack!=null){
                                searchTrackListIDataCallBack.onSuccess(searchTrackList);
                            }

                            successCount++;
//                            Log.d(TAG, "successCount: "+successCount);
                            if( successCount==totalPage && successCount!=0 ) {

                                if (searchTrackListIDataCallBack!=null){
                                    searchTrackListIDataCallBack.onSuccess(tracks);
                                }
                            }
                        }

                        @Override
                        public void onError(int code, String message) {
//                            Log.d(TAG, "code = [" + code + "], message = [" + message + "]");
                            if (searchTrackListIDataCallBack!=null){
                                searchTrackListIDataCallBack.onError(code,message);
                            }

                        }
                    });
                }
            }

            @Override
            public void onError(int code, String message) {
                Log.d(TAG, "code = [" + code + "], message = [" + message + "]");
                if (searchTrackListIDataCallBack!=null){
                    searchTrackListIDataCallBack.onError(code,message);
                }
            }
        });

    }

    public interface  AlbumListIDataCallBack{
        void onSuccess(List<Album> albums);
        void onSuccess(AlbumList albumList);
        void onError(int code, String message);

    }

    public interface  TrackListIDataCallBack{
        void onSuccess(List<Track> tracks);
        void onSuccess(TrackList trackList);
        void onError(int code, String message);

    }

    public interface SearchTrackListIDataCallBack{
        void onSuccess(List<Track> tracks);
        void onSuccess(SearchTrackList searchTrackList);
        void onError(int code, String message);

    }

//    private static final int TO_EXECUTE_SONG=23;
    private static final int TO_GET_TRICK=343;
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
//                case TO_EXECUTE_SONG:
//                    executeSong();
//
//                    break;
                case TO_GET_TRICK:
                    Log.d(TAG, "MyHandler  TO_GET_TRICK : ");
                    Random random=new Random();
                    int index=random.nextInt(albums.size());
                    //mohuaiyuan 201708
//                    index=0;
                    albumId =  albums.get(index).getId();
                    getTracks(albumId);

                    break;
                default:
            }
        }
    }

    public void mediaPlayonExit(MediaPlayer mediaPlayer){
        Log.d(TAG, "mediaPlayonExit(MediaPlayer mediaPlayer): ");

        try {
            if (mediaPlayer!=null && mediaPlayer.isPlaying()) {
                Log.d(TAG, "mediaPlayonExit: ediaPlayer!=null && mediaPlayer.isPlaying()");
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Log.d(TAG, "IllegalStateException e: " + e.getMessage());
            mediaPlayer = new MediaPlayer();
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }

    /**
     * 创建网络mp3
     * @return
     */
    public MediaPlayer createNetMp3(String url){
        Log.d(TAG, "createNetMp3: ");
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


    /**
     * 创建本地（assets）mp3
     * @return
     */
    public MediaPlayer createNetMp3(AssetFileDescriptor fd) {
        Log.d(TAG, "createNetMp3: ");
        MediaPlayer mp = new MediaPlayer();
        try {
            long offset=fd.getStartOffset();
            long length=fd.getLength();
            long declarelength=fd.getDeclaredLength();
            Log.d(TAG, "offset: "+offset);
            Log.d(TAG, "length: "+length);
            Log.d(TAG, "declarelength: "+declarelength);

            mp.setDataSource(fd.getFileDescriptor(),offset,length);

        } catch (IllegalArgumentException e) {
            return null;
        } catch (IllegalStateException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        return mp;
    }

    /**
     * 根据文件名 提供完整的SD Card 文件路径
     * @param fileName
     * @return
     */
    public File getSDcardFile(String fileName){
        Log.d(TAG, "getSDcardFile: ");
        File file=null;
        File sdCardDir = Environment.getExternalStorageDirectory();
        file= new File(sdCardDir.getPath()+ File.separator + fileName);
        if (file==null || !file.exists()){
            Log.e(TAG, "file==null || !file.exists(): ");
            return null;
        }
        Log.d(TAG, "file: "+file);
        return file;
    }

    /**
     * 初始化音乐播放器
     * @param track:音乐信息类，详见喜马拉雅文档model
     * @throws Exception:track 为空 ，或者track 中的播放地址 playUrl都为空
     */
    public void playMusic(Track track,
                          final MediaPlayer.OnPreparedListener  onPreparedListener,
                          final MediaPlayer.OnCompletionListener onCompletionListener,
                          final MediaPlayer.OnErrorListener  onErrorListener)throws  Exception{
        Log.d(TAG, "playMusic(Track track): ");
        if (track==null){
            throw  new Exception("Illegal parameter:track is null");
        }
        String playUrl=null;
        playUrl=track.getPlayUrl32();
        if (playUrl==null || playUrl.length()<1){
            playUrl=track.getPlayUrl64();
        }
        if (playUrl==null || playUrl.length()<1){
            playUrl=track.getPlayUrl24M4a();
        }
        if (playUrl==null || playUrl.length()<1){
            playUrl=track.getPlayUrl64M4a();
        }
        if (playUrl==null || playUrl.length()<1){
            throw  new Exception("PlayUrl init error :playUrl is all null !");
        }else {
            playMusic(playUrl,onPreparedListener,onCompletionListener,onErrorListener);
        }
    }

    /**
     *初始化音乐播放器
     * @param playUrl:音乐文件路径（本地目录或者网络地址）
     * @throws Exception：playUrl 为空，或者初始化音乐播放器出现问题
     */
    public void playMusic(String playUrl,
                                final MediaPlayer.OnPreparedListener  onPreparedListener,
                                final MediaPlayer.OnCompletionListener onCompletionListener,
                                final MediaPlayer.OnErrorListener  onErrorListener) throws Exception {

        Log.d(TAG, "playMusic(String playUrl): ");
        if (playUrl==null || playUrl.length()<1){
            throw  new Exception("Illegal parameter:playUrl is null");
        }
        if (TobotUtils.isEmpty(mediaPlayer)) {
            mediaPlayer = createNetMp3(playUrl);
        } else{
            mediaPlayer.release();//释放音频资源
            mediaPlayer = createNetMp3(playUrl);
        }

        Log.d(TAG, "mediaPlayer==null: "+(mediaPlayer==null));

        if (mediaPlayer==null){
            throw  new Exception("Init mediaPlay error !");
        }

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.prepareAsync();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (onCompletionListener!=null){
                    onCompletionListener.onCompletion(mp);
                }
                Log.i(TAG,"释放资源 回到主场景");
                BFrame.disparkChat();
            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "onPrepared: ");
                if (onPreparedListener!=null){
                    onPreparedListener.onPrepared(mp);
                }else {
                    mp.start();
                }
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (onErrorListener!=null){
                    onErrorListener.onError(mp,what,extra);
                }
                Log.i(TAG,"播放错误 回到主场景");
                BFrame.disparkChat();
                return false;
            }
        });

    }

    /**
     * 暂停或者开始播放音乐
     */
    public void pauseOrstartPlayMusic(){
        if (mediaPlayer!=null){
            if (mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                Log.i(TAG,"暂停/播放 回到主场景");
                BFrame.disparkChat();
            }else {
                Log.i(TAG,"暂停/播放 脱离主场景");
                BFrame.shutChat();
                mediaPlayer.start();
            }
        }
    }

    /**
     *  暂停播放音乐
     * @throws Exception
     */
    public void pausePlayMusic()throws Exception{
        if (mediaPlayer!=null ){
            mediaPlayer.pause();
            Log.i(TAG,"暂停 回到主场景");
            BFrame.disparkChat();
        }
    }

    /**
     * 开始播放音乐
     */
    public void startPlayMusic(){
        if (mediaPlayer!=null){
            mediaPlayer.start();
            Log.i(TAG,"开始播放 脱离主场景");
            BFrame.shutChat();
        }
    }

    /**
     *  停止播放音乐
     * @throws Exception
     */
    public void stopPlayMusic()throws Exception{
        if (mediaPlayer!=null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer=null;
            Log.i(TAG,"停止播放 回到主场景");
            BFrame.disparkChat();
        }
    }

    /**
     * 给机器人发送动作命令
     * @param action ：动作 编号
     * @param simpleFrameCallback
     */
    public void doAction(int action, SimpleFrameCallback simpleFrameCallback) {
        Log.d(TAG, "doAction:");
        if (motor==null){
            motor = new Motor(mContext, new CustomScenario(mContext));
        }
        motor.doAction(Action.buildBodyAction(action, Action.PRMTYPE_EXECUTION_TIMES, 1), simpleFrameCallback);
    }

    public String getString(int id){
        String string=mContext.getResources().getString(id);
        return string;
    }



}
