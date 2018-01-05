package com.tobot.tobot.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.tobot.tobot.base.Constants;
import com.tobot.tobot.db.bean.UserDBManager;
import com.tobot.tobot.presenter.BRealize.BFrame;
import com.turing123.libs.android.resourcemanager.ResourceManager;
import com.turing123.libs.android.resourcemanager.ResourceMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Javen on 2017/8/3.
 */

public class TobotUtils {
    /**
     * 判断是否为空，或者全部空格
     * @return
     */
    public static boolean isEmpty(Object obj) {
        return null == obj || "".equals(obj.toString().trim());
    }

    /**
     * 判断是否空白
     * @return
     */
    public static boolean isBlank(Object obj) {
        return null == obj || "".equals(obj.toString());
    }

    /**
     * 判断是否不为空
     * @return
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 判断是否不为空
     * @return
     */
    public static boolean isNotEmpty(Object obj1,Object obj2) {
        if (!isEmpty(obj1) && !isEmpty(obj2)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 判断是否不为空
     * @return
     */
    public static boolean isEqual(Object obj1,Object obj2) {
        if (obj1.equals(obj2) || obj1 == obj2){
            return true;
        }else{
            return false;
        }
    }



    /**
     * 机器人是否首次使用 true:首次/false:非首次
     * @return
     */
    public static boolean isEmploy(){
        String Ultr = null;
        try {
            Ultr = UserDBManager.getManager().getCurrentUser().getUltr();
        } catch (Exception e) {
            // TODO: handle exception
        }
        if (TobotUtils.isEmpty(Ultr)) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * 机器人是否首次使用 true:首次/false:非首次
     * @return
     */
    public static boolean isEmployFack(){
        String UltrFack = null;
        try {
            UltrFack = UserDBManager.getManager().getCurrentUser().getUltrFack();
        } catch (Exception e) {
            // TODO: handle exception
        }
        if (TobotUtils.isEmpty(UltrFack)) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * 是否在场景
     * @param Scenario
     * @return
     */
    public static boolean isInScenario(String Scenario){
        if (Scenario.equals("os.sys.song") || Scenario.equals("os.sys.story") || Scenario.equals("os.sys.dance")){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 在哪个场景
     * @param Scenario
     * @return
     */
    public static boolean whichScenario(String Scenario){
        if (Scenario.equals("os.sys.song") || Scenario.equals("os.sys.story")){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 是否需要执行动作记忆
     * @param action
     * @return
     */
    public static boolean isMemory(int action){
        if (action == 6 || action == 8 || action == 10 || action == 12 || action == 14 || action == 28){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 是否需要执行动作记忆
     * @param action
     * @return
     */
    public static boolean isReset(int action){
        if (action == 1 || action == 7 || action == 9 || action == 11 || action == 13 || action == 15 || action == 29){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 模糊唤醒
     * @return
     * @param discernASR
     */
    public static boolean isAwaken(String discernASR){
        if (discernASR.contains("小猪小猪") || discernASR.contains("小图小图") || discernASR.contains("小偷小偷")
                || discernASR.contains("晓彤晓彤") || discernASR.contains("小兔小兔") || discernASR.contains("下图下图")
                || discernASR.contains("海豚海豚") || discernASR.contains("插头插头") || discernASR.contains("呷哺呷哺")
                || discernASR.contains("下途下途")){
            return true;
        }else{
            return false;
        }
    }


    /**
     * 机器人联网状态
     * @return
     */
    public static String isEmployAP(){
        try {
            return UserDBManager.getManager().getCurrentUser().getUltrAP();
        } catch (Exception e) {
            // TODO: handle exceptionre
            return "0";
        }
    }


    /**
     * 获取当前日期 格式：yyyy/MM/dd HH:mm:ss
     * @return
     */
    public static String getCurrentlyDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    /**
     * 两个时间相减 格式：yyyy-MM-dd HH:mm:ss
     * @param time1
     * @param time2
     * @return
     */
    public static long DateMinusTime(String time1, String time2) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1 = new Date();
        Date d2 = new Date();
        try {
            d1 = dateFormat.parse(time1);
            d2 = dateFormat.parse(time2);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        long diff = d2.getTime() - d1.getTime();// 这样得到的差值是微秒级别
        long days = diff / (1000 * 60 * 60 * 24 * 7);//24小时 *7天
        return days;
    }


    /**
     *读取文本文件中的内容 I/O
     * @param strFilePath
     * @return
     */
    public static String ReadTxtFile (String strFilePath) throws Exception{
        String path = strFilePath;
        String content = ""; //文件内容字符串
        //打开文件
        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            Log.d("TestFile", "The File doesn't not exist.");
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        content += line + "\n";
                    }
                    instream.close();
                }
            } catch (java.io.FileNotFoundException e) {
                Log.d("TestFile", "The File doesn't not exist.");
            } catch (IOException e) {
                Log.d("TestFile", e.getMessage());
            }
        }
        return content;
    }

    /**
     * 按指定行读取文本文件中的内容 I/O
     * @param strFilePath
     * @return
     */
    public static String ReadTxtFile (String strFilePath,int row) throws Exception{
        String path = strFilePath;
        String content = ""; //文件内容字符串
        int currentLine = 0;//当前行
        //打开文件
        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory()) {
            Log.d("TestFile", "The File doesn't not exist.");
        } else {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while ((line = buffreader.readLine()) != null) {
                        currentLine++;
                        if (currentLine==row) {
                            content += line + "\n";
                        }
                    }
                    instream.close();
                }
            } catch (java.io.FileNotFoundException e) {
                Log.d("TestFile", "The File doesn't not exist.");
            } catch (IOException e) {
                Log.d("TestFile", e.getMessage());
            }
        }
        Log.i("Javen","readFile:"+content);
        return content;
    }

    /**
     * 按指定行读取文本文件中的内容 FILE
     * @param strFilePath
     * @return
     */
    public static String AssignReadTxtFile (String strFilePath,int row) throws Exception{
        String music;
        StringBuffer stringBuffer = null;
        BufferedReader bufferedReader;
        int currentLine = 0;//当前行
        try {
            stringBuffer = new StringBuffer();
            bufferedReader = new BufferedReader(new FileReader(strFilePath));
            while ((music = bufferedReader.readLine()) != null) {
                currentLine++;
                if (currentLine == row) {
                    stringBuffer.append(music);
                    break;
                }
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("Javen","指定行内容:"+stringBuffer.toString());
        return stringBuffer.toString();
    }

    /**
     * 取设备ID
     * @param strFilePath
     * @return
     */
    public static String getDeviceId(String matching, String strFilePath){
        String text = null;
        StringBuffer stringBuffer = null;
        try {
            text = ReadTxtFile(strFilePath);
            String regEx = matching+">(.+)<";
            Pattern pat = Pattern.compile(regEx);
            Matcher mat = pat.matcher(text);
            boolean rs = mat.find();
            stringBuffer = new StringBuffer();
            for(int i=1;i<=mat.groupCount();i++){
                Log.e("Javen","取设备ID:"+i);
                stringBuffer.append(mat.group(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

    /**
     * 取统计
     * @param strFilePath
     * @return
     */
    public static String getGross(String strFilePath){
        String text = null;
        StringBuffer stringBuffer = null;
        try {
            text = AssignReadTxtFile(strFilePath,1);
            String regEx = ":(.+)";
            Pattern pat = Pattern.compile(regEx);
            Matcher mat = pat.matcher(text);
            boolean rs = mat.find();
            stringBuffer = new StringBuffer();
            for(int i=1;i<=mat.groupCount();i++){
                stringBuffer.append(mat.group(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("Javen","取总量"+stringBuffer.toString());
        return stringBuffer.toString();
    }

    /**
     * 取歌名
     * @param strFilePath
     * @return
     */
    public static String getMusic(String strFilePath) throws Exception{
        String gross,text;
        StringBuffer stringBuffer = null;
        try {
            gross = getGross(strFilePath);
            int assign = (Math.abs(new Random().nextInt())%Integer.parseInt(gross))+2;
            Log.i("Javen","指定行:" + assign);
            text = AssignReadTxtFile(strFilePath,assign);
            String regEx = "\\d+\\s+(.*)";
            Pattern pattern = Pattern.compile(regEx);
            Matcher matcher = pattern.matcher(text);
            stringBuffer = new StringBuffer();
            if (matcher.find()) {
                Log.i("Javen","matcher.group(1):" + matcher.group(1));
                stringBuffer.append(matcher.group(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

//    /**
//     * 取歌名
//     * @param strFilePath
//     * @return
//     */
//    public static String getMusic(String strFilePath) throws Exception{
//        String gross,music;
//        StringBuffer stringBuffer = null;
//        BufferedReader bufferedReader;
//        int currentLine = 0;//当前行
//        try {
//            gross = getGross(strFilePath);
//            int assign = (Math.abs(new Random().nextInt())%Integer.parseInt(gross))+2;
//            Log.i("Javen","指定行:" + assign);
//            stringBuffer = new StringBuffer();
//            bufferedReader = new BufferedReader(new FileReader(strFilePath));
//            String regEx = "\\d+\\s+(.*)";
//            Pattern pattern = Pattern.compile(regEx);
//            while ((music = bufferedReader.readLine()) != null) {
//                currentLine++;
//                if (currentLine == assign) {
//                    Matcher matcher = pattern.matcher(music);
//                    if (matcher.find()) {
//                        Log.i("Javen","matcher.group(1):" + matcher.group(1));
//                        stringBuffer.append(matcher.group(1));
//                    }
//                    break;
//                }
//            }
//            bufferedReader.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return stringBuffer.toString();
//    }





    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                BFrame.TTS("本机当前IP地址为:"+inetAddress.getHostAddress());
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                BFrame.TTS("本机当前IP地址为:"+ipAddress);
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }



}
