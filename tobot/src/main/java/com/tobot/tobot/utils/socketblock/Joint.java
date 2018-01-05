package com.tobot.tobot.utils.socketblock;

import android.util.Log;

import com.tobot.tobot.base.Constants;
import com.tobot.tobot.utils.CRC;
import com.tobot.tobot.utils.TobotUtils;
import com.tobot.tobot.utils.Transform;

/**
 * Created by Javen on 2017/10/18.
 */
public class Joint {
    public final static String REGISTER= "5B3231";//注册请求
    public final static String  COMMA = "2C";
    public final static String RESPONSE = "5D";
    public final static String SUCCEED_RESPONSE = "2C31";//成功响应
    public final static String FAILURE_RESPONSE = "2C30";//失败响应
    public final static String PHOTO = "5B3233";//拍照响应头
    public final static String DEMAND = "5B3234";//点播响应头
    public final static String ROLE = "5B323530303030";//点播响应头
    public final static String RESTORE = "5B3236";//恢复出厂设置响应头
    public final static String DANCE = "5B3238";//舞蹈响应头


    /**
     * tcp注册请求
     * @param
     * @return
     */
    public static String setRegister(){
        String message = Transform.stringToHex(TobotUtils.getDeviceId(Constants.DeviceId,Constants.Path)) + COMMA + Transform.stringToHex(TobotUtils.getDeviceId(Constants.Ble_Name,Constants.Path));
        String register = REGISTER + Transform.stringToHex(countDataLen(message.length()/2)) + message;
        return register + Transform.stringToHex(CRC.CRC_16_UP_HEX(Transform.HexString2Bytes(register))) + RESPONSE;
    }

    /**
     * tcp通用响应
     * @param s
     * @return
     */
    public static String setResponse(String top,String s){
        String response = top + Transform.stringToHex(countDataLen(getDataLen(s) + 2)) + Transform.stringToHex(getSpecialRunning(s)) + SUCCEED_RESPONSE;
        String succeed = response + Transform.stringToHex(CRC.CRC_16_UP_HEX(Transform.HexString2Bytes(response))) + RESPONSE;
        return succeed;
    }

    /**
     *
     *
     *
     * 数据区：{sn},{categoryId},{title},{url}

     例：sn=00001,data=2,标题,http://www.ximalaya.com/play.mp3
     5B 31 34 30 30 34 33 73 6E 30 30 31 2C 32 2C B1 EA CC E2 2C 68 74 74 70 3A 2F 2F 77 77 77 2E 78 69 6D 61 6C 61 79 61 2E 63 6F 6D 2F 70 6C 61 79 2E 6D 70 33 38 46 42 31 5D


     数据区：{sn},{success}
     success：0x31 成功；0x30 失败

     例：成功
     5B 32 34 30 30 30 37 73 6E 30 30 31 2C 31 30 37 37 31 5D

     */

    /**
     * 点播响应
     * @param s
     * @return
     */
    public static String setDemandResponse(String s){
        String data = Transform.stringToHex(getRunning(s)) + SUCCEED_RESPONSE;
        String response = DEMAND + Transform.stringToHex(countDataLen(data.length()/2)) + data;
        String succeed = response + Transform.stringToHex(CRC.CRC_16_UP_HEX(Transform.HexString2Bytes(response))) + RESPONSE;
        return succeed;
    }

    /**
     * 舞蹈响应
     * @param s
     * @return
     */
    public static String setDanceResponse(String s){
        String data = Transform.stringToHex(getRunning(s)) + SUCCEED_RESPONSE;
        String response = DANCE + Transform.stringToHex(countDataLen(data.length()/2)) + data;
        String succeed = response + Transform.stringToHex(CRC.CRC_16_UP_HEX(Transform.HexString2Bytes(response))) + RESPONSE;
        return succeed;
    }

    /**
     * 角色修改响应
     * @param
     * @return
     */
    public static String setRoleResponse(){
        return  ROLE + Transform.stringToHex(CRC.CRC_16_UP_HEX(Transform.HexString2Bytes(ROLE))) + RESPONSE;
    }


//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    //计算data长度
    public static String countDataLen(int i){
        String leng = String.valueOf(i);
        StringBuffer sb = new StringBuffer();
        for (i=leng.length();i<4;i++ ){
            sb.append("0");
        }
        char[] strChar = leng.toCharArray();
        for (char a:strChar){
            Log.i("Javen","....................."+sb.toString());
            sb.append(a);
        }
        return sb.toString();
    }

    //截data长度
    public static int getDataLen(String s){
        return Integer.parseInt(s.substring(3,7));
    }

    //取功能码
    public static String getFunCMD(String s){
        return s.substring(2,3);
    }

    //取拍照流水号 [1300026798D9]
    public static String getSpecialRunning(String sn){ return sn.substring(7,sn.length()-5); }

    //取流水号
    public static String getRunning(String sn){
        String capture = sn.substring(7,sn.length());
        char[] strChar = capture.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (char a:strChar){
            if (a == ','){
                break;
            }
            sb.append(a);
        }
        return sb.toString();
    }

    /**
     * 数据分割
     例：sn=00001,data=2,标题,http://www.ximalaya.com/play.mp3
     5B 31 34 30 30 34 33 73 6E 30 30 31 2C 32 2C B1 EA CC E2 2C 68 74 74 70 3A 2F 2F 77 77 77 2E 78 69 6D 61 6C 61 79 61 2E 63 6F 6D 2F 70 6C 61 79 2E 6D 70 33 38 46 42 31 5D
     */
    public static String getCommaAmong(String str,int i){
        String[] strArray = null;
        strArray = str.split(",");
        if (i < strArray.length){
            return  strArray[i];
        }
        return null;
    }

    /**
     * 最后剥离
     * @param str
     * @return
     */
    public static String getPeelVerify(String str){
        String[] strArray = null;
        strArray = str.split(",");
        String peel = strArray[strArray.length-1];
        return  peel.substring(0,peel.length()-5);
    }


}
