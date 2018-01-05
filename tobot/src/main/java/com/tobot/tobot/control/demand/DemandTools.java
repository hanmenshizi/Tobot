package com.tobot.tobot.control.demand;


import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

//import org.apache.tools.zip.ZipEntry;
//import org.apache.tools.zip.ZipFile;
//import org.apache.tools.zip.ZipOutputStream;

/**
 * Created by YF-04 on 2017/11/10.
 */

public class DemandTools {
    private static final String TAG = "DemandTools";

    private static final int buffer = 2048;

    /**
     * okHttpClient对象
     */
    private OkHttpClient mOkHttpClient;
    private Callback mCallback;

    private Context mContext;
    private Handler handler;


    private List<String> zipFileList;
    private String danceRespath;
    private String danceActionFilesPath;
    private String danceBackgroundMusicPath;

    public DemandTools(Context context) {
        this.mContext=context;
        if (mOkHttpClient == null) {
            mOkHttpClient = new OkHttpClient();
        }
    }


    public void download(String url)throws Exception {
        boolean downloadResult = false;
        if (url==null || url.length()<1 || url.trim().length()<1){
            throw new Exception("Illegal argument of  url !");
        }

        Request request = new Request.Builder().url(url).build();
        mOkHttpClient.newCall(request).enqueue(mCallback);

    }

    /**
     * 调用 okhttp3 框架 下载文件
     * @param url :待下载文件的url
     * @param callback :下载方法的回调方法 ，后续的事情都通过callback处理，包括文件保存的位置、保存的过程
     * @throws Exception :下载过程若出现异常，则抛出异常
     */
    public void download(String url,Callback callback)throws Exception {
        Log.d(TAG, "download: ");
        if (url==null || url.length()<1 || url.trim().length()<1){
            throw new Exception("Illegal argument of  url !");
        }

        Request request = new Request.Builder().url(url).build();
        mOkHttpClient.newCall(request).enqueue(callback);

    }



    public long getAvailableBytes(String path) {
        Log.d(TAG, "getAvailableBytes: ");
        Log.d(TAG, "path: "+path);
        StatFs fs = new StatFs(path);
        int blockSize = fs.getBlockSize();
        return blockSize * fs.getAvailableBlocks();
    }

    public String getDanceResourcePath() {
        Log.d(TAG, "getDanceResourceDownloadDir: ");
        String path=Environment.getExternalStorageDirectory().getPath()+ File.separator+DemandUtils.DANCE_RESOURCE_PATH;
        File file=new File(path);
        if (!file.exists()){
            Log.d(TAG, "getDanceResourcePath:文件不存在 ");
              boolean mkdirResult= file.mkdir();
            Log.d(TAG, "getDanceResourcePath mkdirResult :"+mkdirResult);
        }
        return  path;
    }

    public String getDanceActionPath(){
        Log.d(TAG, "getDanceActionPath: ");
        String path=Environment.getExternalStorageDirectory().getPath()+File.separator+DemandUtils.DANCE_ACTION_FILES_PATH;
        File file=new File(path);
        if (!file.exists()){
            Log.d(TAG, "getDanceActionPath:文件不存在 ");
            boolean mkdirResult= file.mkdir();
            Log.d(TAG, "getDanceActionPath mkdirResult :"+mkdirResult);
        }
        return path;
    }

    public String getBackgroundMusicPath(){
        Log.d(TAG, "getBackgroundMusicPath: ");
        String path=Environment.getExternalStorageDirectory().getPath()+File.separator+DemandUtils.DANCE_BACKGROUND_MUSIC_Dir;
        File file=new File(path);
        if (!file.exists()){
            Log.d(TAG, "getBackgroundMusicPath:文件不存在 ");
            boolean mkdirResult= file.mkdir();
            Log.d(TAG, "getBackgroundMusicPath mkdirResult :"+mkdirResult);
        }
        return path;
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir :将要删除的文件目录
     * @return  true if all deletions were successful ,others return false .
     */
    public   boolean deleteDir(File dir) {
        Log.d(TAG, "deleteDir: ");
        if (dir==null || !dir.exists()){
            return false;
        }
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    public boolean deleteDir(String path){
        return deleteDir(new File(path));
    }

    /**
     * 解压Zip文件
     * @param path 文件目录
     */
    public void unZip(String path)throws Exception {

        Log.d(TAG, "unZip: ");
        Log.d(TAG, "path: "+path);
        String configPath=path.substring(path.lastIndexOf("/")+1,path.lastIndexOf("."));
        Log.d(TAG, "configPath: "+configPath);
        if (zipFileList==null){
            zipFileList=new ArrayList<>();
        }
        if (!zipFileList.isEmpty()){
            zipFileList.clear();
        }
        String savePath="";
        int count = -1;

        File file = null;
        InputStream is = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;

        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(path); // 解决中文乱码问题
//            zipFile= new ZipFile(path,Charset.forName("GBK"));//解决中文文件夹乱码
//            zipFile= new ZipFile(path,"GBK");//解决中文文件夹乱码
            Enumeration<?> entries = zipFile.entries();
//            Enumeration<?> entries = zipFile.getEntries();

            while (entries.hasMoreElements()) {
                byte buf[] = new byte[buffer];
                ZipEntry entry = (ZipEntry) entries.nextElement();

                String filename = entry.getName();
                boolean ismkdir = false;
                if (filename.lastIndexOf("/") != -1) { // 检查此文件是否带有文件夹
                    ismkdir = true;
                }
                //音乐文件 直接解压到 TuubaDanceBackgroundMusic 文件夹
                //config 文件解压到TuubaDanceBackgroundMusic 文件夹
                //.prj文件 解压到 TuubaDanceActionFiles 文件夹
                String downloadPath=null;
                if (filename.trim().endsWith(".prj")){//动作文件
                    downloadPath=(this.danceActionFilesPath==null)?getDanceActionPath():this.danceActionFilesPath;
                }else if (filename.contains(Constants.CONFIG_FILE_NAME)){//配置文件
                    downloadPath=(this.danceRespath==null?getDanceResourcePath():this.danceRespath);
                    filename+=configPath;
                }else { //音乐文件
                    downloadPath=(this.danceBackgroundMusicPath==null?getBackgroundMusicPath():this.danceBackgroundMusicPath);
                }
                if (downloadPath!=null){
                    savePath=downloadPath;
                }
                if (savePath==null){
                    savePath=getDanceResourcePath();
                }
                savePath+=File.separator;
                new File(savePath).mkdir();// 创建保存目录
                filename = savePath+ filename;
//                filename=new String(filename.getBytes("GB2312"), "utf-8");
                zipFileList.add(filename);
                Log.d(TAG, "filename: "+filename);

                if (entry.isDirectory()) { // 如果是文件夹先创建
                    file = new File(filename);
                    file.mkdirs();
                    continue;
                }
                file = new File(filename);
                if (!file.exists()) { // 如果是目录先创建
                    if (ismkdir) {
                        new File(filename.substring(0,
                                filename.lastIndexOf("/"))).mkdirs(); // 目录先创建
                    }
                }else {
                    //若文件存在 则删除 旧文件
                    file.delete();
                }
                file.createNewFile(); // 创建文件

                is = zipFile.getInputStream(entry);
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos, buffer);

                while ((count = is.read(buf)) > -1) {
                    bos.write(buf, 0, count);
                }
                bos.flush();
                bos.close();
                fos.close();

                is.close();
            }

            zipFile.close();
            //解压成功
            if (handler!=null){
                Message message=new Message();
                message.what=Constants.UNZIP_SUCCESS;
                handler.sendMessage(message);
            }

        } catch (IOException ioe) {
            //解压失败
            if (handler!=null){
                Message message=new Message();
                message.what=Constants.UNZIP_FAILED;
                handler.sendMessage(message);
            }
            ioe.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (is != null) {
                    is.close();
                }
                if (zipFile != null) {
                    zipFile.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    public Callback getmCallback() {
        return mCallback;
    }

    public void setmCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public List<String> getZipFileList() {
        return zipFileList;
    }

    public void setZipFileList(List<String> zipFileList) {
        this.zipFileList = zipFileList;
    }

    public String getDanceRespath() {
        return danceRespath;
    }

    public void setDanceRespath(String danceRespath) {
        this.danceRespath = danceRespath;
    }

    public String getDanceActionFilesPath() {
        return danceActionFilesPath;
    }

    public void setDanceActionFilesPath(String danceActionFilesPath) {
        this.danceActionFilesPath = danceActionFilesPath;
    }

    public String getDanceBackgroundMusicPath() {
        return danceBackgroundMusicPath;
    }

    public void setDanceBackgroundMusicPath(String danceBackgroundMusicPath) {
        this.danceBackgroundMusicPath = danceBackgroundMusicPath;
    }
}
