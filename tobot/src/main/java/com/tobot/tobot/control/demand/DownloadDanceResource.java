package com.tobot.tobot.control.demand;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by YF-04 on 2017/11/14.
 */

public class DownloadDanceResource implements DownloadBehavior {
    private static final String TAG = "DownloadDanceResource";

    private DanceItemChain mDanceItemChain;
    private Context mContext;

    private DemandUtils demandUtils;
    private DemandTools demandTools;
    /**
     * 下载成功的舞蹈文件包数量
     */
    private int downLoadSuccessCount;
    /**
     * 解压成功的 舞蹈文件包数量
     */
    private int unZipSuccessCount;
    /**
     * 修改（更新）配置文件成功 的数量
     */
    private int updateConfigSuccessCount;

    private MyHandler myHandler;

//    private List<String>zipFileList;
    private List<String> configFileList;
    private String SDPath;

    public DownloadDanceResource(Context context,DanceItemChain danceItemChain){
        Log.d(TAG, "DownloadDanceResource: ");
        this.mContext=context;
        this.mDanceItemChain=danceItemChain;
    }

    @Override
    public void download() throws Exception{
        Log.d(TAG, "download: ");

        initData();

        //TODO 下载
        if (mDanceItemChain.isSuccess()) {

            deleteZipFileList();

            //检查本地sd卡 存储空间是否足够
            Log.d(TAG, "检查本地sd卡 存储空间是否足够: ");
            long availableBytes = demandTools.getAvailableBytes(demandTools.getDanceResourcePath());
            Log.d(TAG, "本地sd卡 存储空间：availableBytes: " + availableBytes);
            // 考虑到文件下载后，还需要解压，故将文件大小乘以2
            if (availableBytes <= mDanceItemChain.getTotalSize() * 2) {
                Log.e(TAG, "sdcard has no enough space!" );
                throw new Exception("sdcard has no enough space!");
            }
            startDownload();

        }else {
            Log.e(TAG, "danceItemChain.isSuccess(): "+mDanceItemChain.isSuccess());
            Log.e(TAG, "请求舞蹈数据列表 失败！" );
            throw new Exception("Request the dance data list failed !");
        }

        //TODO 修改配置文件   考虑 配置文件是否修改的标记 使用sharedperforence 存储

    }


    private void startDownload() throws Exception {
        Log.d(TAG, "startDownload: ");

        List<DanceItem> list = mDanceItemChain.getData();
        //若文件已经存在 则不用下载了
        Iterator<DanceItem> iterator = list.iterator();
        while (iterator.hasNext()) {
            DanceItem item = iterator.next();
            String name = item.getName().trim();

            File file = new File(SDPath, name);
            Log.d(TAG, "file: " + file.getAbsolutePath());
//            Log.d(TAG, "size: "+item.getSize());

            int size = item.getSize();
            //若 文件大小 小于等于零 ，或者 文件已经存在了 ，则不需要下载了
            if (file.exists() || size <= 0) {
                iterator.remove();
            }
        }

        //下载 文件
        Log.d(TAG, "需要下载的文件数量: " + list.size());
        for (int i = 0; i < list.size(); i++) {
            DanceItem danceItem = list.get(i);
            MyCallback myCallback = new MyCallback(danceItem);
            demandTools.download(danceItem.getData(), myCallback);
        }

    }

    /**
     * 初始化 一些变量
     */
    private void initData() {
        if (demandUtils==null){
            demandUtils=new DemandUtils(mContext);
        }
        if (demandTools == null) {
            demandTools = new DemandTools(mContext);
        }
        if (myHandler == null) {
            myHandler=new MyHandler(mContext);
        }
        if (SDPath==null){
            SDPath = demandTools.getDanceResourcePath();
        }
        downLoadSuccessCount = 0;
        unZipSuccessCount=0;
        updateConfigSuccessCount=0;

    }

    private synchronized void updateDanceActionConfig()throws Exception {
        Log.d(TAG, "updateDanceActionConfig: ");
        String path=demandTools.getDanceResourcePath();
        File file=new File(path);
        String[]files = null;
        if (file.exists()) {
            files = file.list();
        }
        if (files==null || files.length<1){
            Log.e(TAG, "(files==null || files.length<1: " );
            throw new Exception("The folder has no files");
        }
        String fileName=null;
        for (int i=0;i<files.length;i++){
            fileName=files[i];
            Log.d(TAG, "fileName: "+fileName);
            if (fileName.contains(Constants.CONFIG_FILE_NAME)){
                fileName=path+File.separator+fileName;
                File configFile=new File(fileName);
                Log.d(TAG, "configFile: "+configFile.getAbsolutePath());
                Map<Integer,String> map=null;
                try {
                    map=demandUtils.initActionConfig(configFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                demandUtils.setHandler(myHandler);
                demandUtils.updateActionConfig(map);
            }
        }

    }

    private void unzipAllFile(){
        Log.d(TAG, "unzipAllFile: ");
        int size=mDanceItemChain.getData().size();
        for (int i=0;i<size;i++){
            DanceItem danceItem=mDanceItemChain.getData().get(i);
            String name=danceItem.getName();
            String suffix=danceItem.getSuffix();
            String path=SDPath+File.separator+name+Constants.PUNCTUATION_POINT+suffix;
            try {
                unzipFile(path);
            } catch (Exception e) {
                Log.e(TAG, "unzipAllFile: 解压 Exception e: "+e.getMessage());
                //解压失败 就删除 所有下载的文件 及其解压的文件
                deleteZipFileList();
                e.printStackTrace();
            }
        }
        
    }

    /**
     * 解压zip文件
     * @param path
     * @throws Exception
     */
    private  void unzipFile(String path)throws Exception {
        Log.d(TAG, "unzipFile: ");
        if (path==null || path.length()<1 || path.trim().length()<1){
            Log.e(TAG, "path==null || path.length()<1  : " );
            throw new Exception("path==null || path.length()<1");
        }
        Log.d(TAG, "path: "+path);
        File file=new File(path);
        if (!file.exists()){
            Log.e(TAG, "The file in this directory does not exist !" );
            throw new Exception("The file in this directory does not exist!");
        }
        demandTools.setHandler(myHandler);
        demandTools.unZip(path);

    }

    /**
     * 删除 解压之后的配置文件以及下载的文件
     */
    private void deleteZipFileList() {
        Log.d(TAG, "deleteZipFileList: ");
        String path = demandTools.getDanceResourcePath();
        demandTools.deleteDir(path);

    }
    

    class MyCallback implements Callback {

        private DanceItem mDanceItem;

        public MyCallback(DanceItem danceItem){
            this.mDanceItem=danceItem;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            Log.e(TAG, "下载失败。。。 " );
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            InputStream is = null;
            byte[] buf = new byte[2048];
            int len = 0;
            FileOutputStream fos = null;
            Log.d(TAG, "SDPath: "+SDPath);
            try {
                is = response.body().byteStream();
                long total = response.body().contentLength();
                String completeFileName=mDanceItem.getName()+"."+mDanceItem.getSuffix();
                File file = new File(SDPath, completeFileName);
                fos = new FileOutputStream(file);
                long sum = 0;
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                    sum += len;
                    int progress = (int) (sum * 1.0f / total * 100);//下载进度的百分比
//                    Log.d(TAG, "progress=" + progress);

                }
                fos.flush();
                Log.d(TAG, "文件下载成功");
                Message message=new Message();
                message.what=DOWNLOAD_SUCCESS;
                myHandler.sendMessage(message);

            } catch (Exception e) {
                Log.e(TAG, "文件下载失败");
                Message message=new Message();
                message.what=DOWNLOAD_FAILED;
                myHandler.sendMessage(message);
                e.printStackTrace();
            } finally {
                try {
                    if (is != null){
                        is.close();
                    }
                } catch (IOException e) {
                }
                try {
                    if (fos != null){
                        fos.close();
                    }
                } catch (IOException e) {
                }
            }
        }
    }


    public static final int UPDATE_DANCE_ACTION_CONFIG=548641;
    public static final int UNZIP_FILE=3544;

    class MyHandler extends Handler {

        private Context context;

        public MyHandler(Context context){
            this.context=context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "handleMessage: ");
            Log.d(TAG, "msg.what: "+msg.what);
            switch (msg.what){
                case UPDATE_DANCE_ACTION_CONFIG:
                    synchronized (this){
                        Log.d(TAG, "handleMessage:  UPDATE_DANCE_ACTION_CONFIG  开始修改（更新配置文件）");
                        try {
                            updateDanceActionConfig();
                        } catch (Exception e) {
                            Log.d(TAG, "handleMessage Exception e: "+e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    break;

                case DOWNLOAD_SUCCESS:
                    synchronized (this){
                        Log.d(TAG, "handleMessage:  DOWNLOAD_SUCCESS  下载成功 ");
                        downLoadSuccessCount++;
                        if (downLoadSuccessCount==mDanceItemChain.getData().size()){
                            Message message=new Message();
                            message.what=UNZIP_FILE;
                            this.sendMessage(message);
                        }
                    }
                    break;

                case UNZIP_FILE:
                    Log.d(TAG, "handleMessage: UNZIP_FILE  开始解压文件");
                    unzipAllFile();
                    break;

                case Constants.UNZIP_SUCCESS:
                    Log.d(TAG, "handleMessage: UNZIP_SUCCESS  解压成功");
                    //若全部解压完成
                    synchronized (this){
                        unZipSuccessCount++;
                        if (unZipSuccessCount==mDanceItemChain.getData().size()){
                            Message message=new Message();
                            message.what=UPDATE_DANCE_ACTION_CONFIG;
                            this.sendMessage(message);
                        }
                    }
                    break;

                case Constants.UNZIP_FAILED:
                    Log.e(TAG, "handleMessage: UNZIP_FAILED  解压失败");

                    break;

                case Constants.UPDATE_CONFIG_SUCCESS:
                    Log.d(TAG, "handleMessage: UPDATE_CONFIG_SUCCESS  修改配置文件成功");
                    synchronized (this){
                        updateConfigSuccessCount++;
                        if (updateConfigSuccessCount==mDanceItemChain.getData().size()){
                            //mohuaiyuan 测试  20171123
                            deleteZipFileList();
                        }
                    }
                    break;

                case Constants.UPDATE_CONFIG_FAILED:
                    Log.e(TAG, "handleMessage: UPDATE_CONFIG_FAILED  修改配置文件失败");
                    break;


                default:
                    break;
            }

        }

    }

   

}
