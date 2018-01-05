package com.tobot.tobot.control.demand;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.tobot.tobot.utils.CommonRequestManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by YF-04 on 2017/11/3.
 */

public class DemandUtils {
    private static final String TAG = "DemandUtils";


    public static final String DANCE_BACKGROUND_MUSIC_Dir="TuubaDanceBackgroundMusic";
    public static final String DANCE_CONFIG_DIR="TuubaDanceConfig";
    public static final String DANCE_CONFIG_FILE_NAME="danceActionConfig";
    public static final String DANCE_RESOURCE_PATH="TuubaDanceResourcePath";
    public static final String DANCE_ACTION_FILES_PATH="TuubaDanceActionFiles";

    public static final String CUSTOM_SCENE_DIR="TuubaCustomScene";
    public static final String MUSIC_ACTION_FILE_NAME="musicActionInfo";
    public static final String STORY_ACTION_FILE_NAME="storyActionInfo";
    public static final String DANCE_ACTION_FILE_NAME="danceActionInfo";

    private volatile Map<Integer ,String> actionMap;
    private volatile Map<Integer ,Integer> actionInfoMaps;

    private CommonRequestManager manager;
    private Context mContext;

    private Handler handler;

    private static boolean isConfigChange;

    public DemandUtils(Context context){
        this.mContext=context;
        manager= CommonRequestManager.getInstanse(mContext);

    }

    /**
     *
     * @return
     * @throws Exception
     */
    public synchronized Map<Integer,Integer> initMusciActionInfo() throws Exception {
        Log.d(TAG, "initBasicActionInfo(): ");
        String configFileName=CUSTOM_SCENE_DIR+ File.separator+MUSIC_ACTION_FILE_NAME;
        File file=manager.getSDcardFile(configFileName);
        Log.d(TAG, "file.getAbsolutePath(): "+file.getAbsolutePath());
        return initBasicActionInfo(file);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public synchronized Map<Integer,Integer> initStoryActionInfo() throws Exception {
        Log.d(TAG, "initBasicActionInfo(): ");
        String configFileName=CUSTOM_SCENE_DIR+ File.separator+STORY_ACTION_FILE_NAME;
        File file=manager.getSDcardFile(configFileName);
        Log.d(TAG, "file.getAbsolutePath(): "+file.getAbsolutePath());
        return initBasicActionInfo(file);
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public synchronized Map<Integer,Integer> initDanceActionInfo() throws Exception {
        Log.d(TAG, "initBasicActionInfo(): ");
        String configFileName=CUSTOM_SCENE_DIR+ File.separator+DANCE_ACTION_FILE_NAME;
        File file=manager.getSDcardFile(configFileName);
        Log.d(TAG, "file.getAbsolutePath(): "+file.getAbsolutePath());
        return initBasicActionInfo(file);
    }


    /**
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public synchronized Map<Integer,Integer> initBasicActionInfo(File fileName) throws Exception {
        if (fileName == null) {
            throw new Exception("Illegal fileName: fileName is null!");
        }
        if (!fileName.exists()) {
            throw new Exception("Illegal fileName:  fileName is not exist!");
        }
        if (actionInfoMaps == null) {
            actionInfoMaps = new HashMap<>();
        }
        if (!actionInfoMaps.isEmpty()) {
            actionInfoMaps.clear();
        }

        FileInputStream is = null;
        BufferedReader br = null;
        String line = "";
        try {
            is = new FileInputStream(fileName);
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                String[] temp = line.split(":");
                if (temp != null && temp.length == 2) {
                    actionInfoMaps.put(Integer.valueOf(temp[0].trim()), Integer.valueOf(temp[1].trim()));
                } else {
                    throw new Exception("There are some errors in your configuration file:" + fileName.getName());
                }
            }
            br.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return actionInfoMaps;
    }

    /**
     * 初始化（读取）配置文件：配置文件中包含背景音乐与舞蹈动作的对应关系;
     * 默认的文件路径：内置sdcard卡中的 TuubaDanceConfig/danceActionConfig
     */
    public Map<Integer, String> initActionConfig()throws Exception{
        Log.d(TAG, "initActionConfig: ");
        String configFileName=DANCE_CONFIG_DIR+ File.separator+DANCE_CONFIG_FILE_NAME;
        File file=manager.getSDcardFile(configFileName);
        Log.d(TAG, "file.getAbsolutePath(): "+file.getAbsolutePath());
        return initActionConfig(file);
    }

    /**
     * 初始化（读取）配置文件：配置文件中包含背景音乐与舞蹈动作的对应关系.
     * eg: 卓依婷-生日歌.mp3 ,131
     * @param fileName :The name of the configuration file
     * @return
     * @throws Exception
     */
    public synchronized Map<Integer,String> initActionConfig(File fileName) throws Exception {
        if (fileName == null) {
            throw new Exception("Illegal fileName: fileName is null!");
        }
        if (!fileName.exists()) {
            throw new Exception("Illegal fileName:  fileName is not exist!");
        }
        if (actionMap == null) {
            actionMap = new HashMap<>();
        }
        if (!actionMap.isEmpty()) {
            actionMap.clear();
        }

        FileInputStream is = null;
        BufferedReader br = null;
        String line = "";
        try {
            is = new FileInputStream(fileName);
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                String[] temp = line.split(",");
                if (temp != null && temp.length == 2) {
                    actionMap.put(Integer.valueOf(temp[1].trim()), temp[0].trim());
                } else {
                    throw new Exception("There are some errors in your configuration file:" + DANCE_CONFIG_FILE_NAME);
                }
            }
            br.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return actionMap;
    }



    /**
     * 修改（更新）配置文件:写入到文件的末尾
     * 默认的路径：内置sdcard卡中的 TuubaDanceConfig/danceActionConfig
     * @param map
     */
    public synchronized void updateActionConfig(Map<Integer,String> map)throws Exception{
        Log.d(TAG, "updateActionConfig(Map<Integer,String> map): ");
        if (map==null ){
            throw new Exception("updateActionConfig :map is null !");
        }
        String configFileName=DANCE_CONFIG_DIR+ File.separator+DANCE_CONFIG_FILE_NAME;
        File file=manager.getSDcardFile(configFileName);
        Log.d(TAG, "file.getAbsolutePath(): "+file.getAbsolutePath());
        updateActionConfig(file,map,true);
    }

    private synchronized void updateActionConfig(File file ,Map<Integer,String> map,boolean append)throws Exception{
        Log.d(TAG, "updateActionConfig(File file ,Map<Integer,String> map): ");
        if (file == null) {
            throw new Exception("Illegal fileName: fileName is null!");
        }
        if (!file.exists()) {
            throw new Exception("Illegal fileName:  fileName is not exist!");
        }
        FileOutputStream fos=null;
        BufferedWriter bw=null;
        try {
            //写到文件的末尾
            fos=new FileOutputStream(file,append);
            bw=new BufferedWriter(new OutputStreamWriter(fos));

            Set<Integer> keySet = map.keySet();
            Iterator<Integer>iterator=keySet.iterator();
            while (iterator.hasNext()){
                Integer key= iterator.next();
                String value=map.get(key);
                StringBuffer sb=new StringBuffer();
                sb.append(value);
                sb.append(Constants.SEPARATOR_COMMA);
                sb.append(key);
                bw.write(sb.toString());
                bw.newLine();
            }
            bw.flush();
            bw.close();
            fos.close();

            //修改（更新）配置文件成功
            if (handler!=null){
                Message message=new Message();
                message.what=Constants.UPDATE_CONFIG_SUCCESS;
                handler.sendMessage(message);
            }
        } catch (IOException e) {
            //修改（更新）配置文件失败
            if(handler!=null){
                Message message=new Message();
                message.what=Constants.UPDATE_CONFIG_FAILED;
                handler.sendMessage(message);
            }
            e.printStackTrace();
        }finally {
            try {
                if (bw!=null){
                    bw.close();
                }
                if (fos!=null){
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


    public static synchronized boolean isConfigChange() {
        return isConfigChange;
    }

    public static synchronized void setIsConfigChange(boolean isConfigChange) {
        DemandUtils.isConfigChange = isConfigChange;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }
}
