package com.tobot.tobot.utils.socketblock;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.util.Log;

/**
 * <b>类功能描述：</b>
 * 日志管理工具(非多线程...会阻塞...) </div>
 *
 * @author
 * @version 1.0 </p> 修改时间：</br> 修改备注：</br>
 */
public class CLog {
    private static boolean enable = true;// 总闸
    private static boolean writetosd = true;// 记得加写SD卡权限
    private static boolean showLogInfo = false;
    private static boolean printout = true;
    private static String LogTag = CLog.class.getSimpleName();
    private static String packageName = "com.onevo";// 修改包名,
    private static int maxSize = 1000;// KB
    private static File folder = new File("/sdcard/" + CLog.class.getSimpleName());
    private static File save = new File(folder.getPath() + "//" + "SaveString.txt");
    private static File log = new File(folder.getPath() + "//" + "Log.txt");
    private static File temp = new File(folder.getPath() + "//" + "Temp.txt");
    private static final String FORMATSTR = "yyyy-MM-dd hh:mm:ss";
    private static int move = 1;

    /**
     * <b>类功能描述：</b><div style="margin-left:40px;margin-top:-10px">
     * 日志输出类型,与传统Log一致(V,D,I,W,E)</br> 当类型为E时,日志文件记录类型为Error,其他为Infos </div>
     *
     * @author
     * @version 1.0 </p> 修改时间：</br> 修改备注：</br>
     */
    public enum CLogType {
        D(0), W(1), E(2), I(3), V(4);
        private int value;

        CLogType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /*--此工具类无需实例化--*/
    private CLog() {
    }

    /**
     * 保存字符串到文本(建议信息时量大时使用)<br/>
     *
     * @param toSave 欲保存的文本
     * @return true-成功 </br>false-失败
     */
    public static boolean saveStringToTxt(String toSave) {
        if (!enable) {
            return false;
        }
        DateFormat sdf = new SimpleDateFormat(FORMATSTR);
        String t = sdf.format(new Date());
        StackTraceElement element = new Exception().getStackTrace()[1];
        StringBuilder sb = new StringBuilder();
        sb.append("\n-------------------- ");
        sb.append(t);
        sb.append(" --------------------");
        sb.append(element.getFileName() == null ? "" : "\nCaller:\n"
                + element.getFileName().substring(0,
                element.getFileName().indexOf(".")) + "_"
                + element.getMethodName() + ":" + element.getLineNumber());
        sb.append("\nString:\n");
        sb.append(toSave);
        sb.append("\n--------------------------   End   --------------------------");
        return save(save, sb.toString());
    }

    /**
     * LogCat中显示方法的调用者相关信息
     *
     * @return 方法调用者类与方法名
     */
    public static String showCallerInfo() {
        if (!enable) {
            return "";
        }
        StackTraceElement element = new Exception().getStackTrace()[2];
        move = 2;
        if (element.getClassName().indexOf(packageName) != -1) {
            out(CLogType.D, "<-Callee    ↙Caller,Double Click Goto Source");
        } else {
            out(CLogType.D, "<-Callee    ↙Caller(Nonnative)");
        }
        move = 2;
        out(CLogType.D,
                "at "
                        + element.getClassName()
                        + "."
                        + element.getMethodName()
                        + (element.getFileName() == null ? "(UnKnow Source)"
                        : ("(" + element.getFileName() + ":"
                        + element.getLineNumber() + ")")));
        return element.getClassName() + "." + element.getMethodName();
    }

    ;

    /**
     * 控制台LogCat输出信息(不放入日志文件)
     */
    public static void out(Object infos) {
        move = 2;
        if (infos == null) {
            infos = "";
        }
        out(CLogType.D, infos.toString());
    }

    /**
     * 控制台LogCat输出信息(不放入日志文件)
     */
    public static void out(CLogType logType, Object infos) {
        if (!enable) {
            return;
        }
        if (!printout) {
            return;
        }
        StackTraceElement element = new Exception().getStackTrace()[move];
        move = 1;
        String tag = LogTag;
        if (element.getFileName() != null) {
            tag = LogTag
                    + "_"
                    + element.getFileName().substring(0,
                    element.getFileName().indexOf(".")) + "_"
                    + element.getMethodName() + ":" + element.getLineNumber();
        }
        if (logType == CLogType.D) {
            Log.d(tag, infos.toString());
        } else if (logType == CLogType.W) {
            Log.w(tag, infos.toString());
        } else if (logType == CLogType.I) {
            Log.i(tag, infos.toString());
        } else if (logType == CLogType.E) {
            Log.e(tag, infos.toString());
        } else if (logType == CLogType.V) {
            Log.v(tag, infos.toString());
        }
    }

    /**
     * 保持与传统Log一致的使用
     *
     * @param tag   标志
     * @param infos 信息
     */
    public static void d(String tag, Object infos) {
        if (!enable) {
            return;
        }
        Log.d(tag, infos.toString());
    }

    /**
     * 保持与传统Log一致的使用
     *
     * @param tag   标志
     * @param infos 信息
     */
    public static void e(String tag, Object infos) {
        if (!enable) {
            return;
        }
        Log.e(tag, infos.toString());
    }

    /**
     * 保持与传统Log一致的使用
     *
     * @param tag   标志
     * @param infos 信息
     */
    public static void v(String tag, Object infos) {
        if (!enable) {
            return;
        }
        Log.v(tag, infos.toString());
    }

    /**
     * 保持与传统Log一致的使用
     *
     * @param tag   标志
     * @param infos 信息
     */
    public static void w(String tag, Object infos) {
        if (!enable) {
            return;
        }
        Log.w(tag, infos.toString());
    }

    /**
     * 保持与传统Log一致的使用
     *
     * @param tag   标志
     * @param infos 信息
     */
    public static void i(String tag, Object infos) {
        if (!enable) {
            return;
        }

        if (infos == null) {
            Log.i(tag, "null");
        } else {
            Log.i(tag, infos.toString());
        }

    }

    /**
     * 控制台LogCat输出信息(同时保存到日志文件)<br/>
     */
    public static void record(Object infos) {
        move = 2;
        out(CLogType.D, infos.toString());
        saveInfoToLog(infos.toString(), false);
    }

    /**
     * 控制台LogCat输出信息(同时保存到日志文件)<br/>
     */
    public static void record(CLogType logType, Object infos) {
        move = 2;
        out(logType, infos.toString());
        saveInfoToLog(infos.toString(), logType == CLogType.E);
    }

    /**
     * 保存信息到日志文件
     *
     * @param infos 信息
     * @param error 是否使用错误信息标志 </br> 标志为true-[Error] or false-[Infos]
     */
    public static void write(Object infos, boolean error) {
        saveInfoToLog(infos.toString(), error);
    }

    /*--保存信息到日志文件--*/
    private static boolean saveInfoToLog(String infos, boolean error) {
        DateFormat sdf = new SimpleDateFormat(FORMATSTR);
        String t = sdf.format(new Date());
        infos = t + (error ? "  [Error]  " : "  [Infos]  ") + infos;
        return save(log, infos);
    }

    /*--抽取出的写入文件方法--*/
    private static boolean save(File f, String s) {
        if (!(writetosd && enable)) {
            return true;
        }
        boolean result = true;
        FileInputStream input = null;
        BufferedInputStream inBuff = null;
        FileOutputStream output = null;
        BufferedOutputStream outBuff = null;
        FileWriter fw = null;
        BufferedWriter bw = null;
        PrintWriter pw = null;
        try {
            folder.mkdirs();
            /*--获取原文件以保存的信息--*/
            if (f.exists()) {
                input = new FileInputStream(f);
                inBuff = new BufferedInputStream(input);
            }
			/*--End--*/
			/*--写入新的信息--*/
            fw = new FileWriter(temp);
            bw = new BufferedWriter(fw);
            pw = new PrintWriter(bw);
            if (f == log) {
                pw.write(s.replace("\n", "\n                              ")
                        + "\n");
            } else {
                pw.write(s + "\n");
            }
			/*--End--*/
            try {
                if (temp.exists()) {
                    pw.close();
                    bw.close();
                    fw.close();
                }
            } catch (Exception e) {
                if (showLogInfo)
                    out("Exception Occur!");
                result = false;
            }
			/*--追加原有文件信息--*/
            int currentSize = (int) ((temp.length() / 1024) + 1);
            if (f.exists()) {
                output = new FileOutputStream(temp, true);
                outBuff = new BufferedOutputStream(output);
                byte[] b = new byte[1024];
                int len;
                while ((len = inBuff.read(b)) != -1) {
                    currentSize++;
                    if (currentSize > maxSize)
                        break;
                    outBuff.write(b, 0, len);
                }
            }
			/*--End--*/
            move = 4;
            if (showLogInfo)
                out(CLogType.D, ("Successfully to Save " + (f == log ? "Log"
                        : "String")));
        } catch (Exception e) {
            move = 4;
            if (showLogInfo)
                out(CLogType.D, ("Successfully to Save " + (f == log ? "Log"
                        : "String")));
            result = false;
        } finally {
            try {
                if (f.exists()) {
                    outBuff.flush();
                    inBuff.close();
                    outBuff.close();
                    output.close();
                    input.close();
                }
            } catch (Exception e) {
                if (showLogInfo)
                    out("Exception Occur!");
                result = false;
            }
        }
        return result && temp.renameTo(f);
    }

    /*--递归删除日志文件夹--*/
    private static void deleteFolder(File f) {
        File[] files = folder.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                files[i].delete();
            } else {
                deleteFolder(files[i]);
            }
        }
        folder.delete();
    }

    /**
     * 日志功能是否已启用
     *
     * @return true-启用 <br/>
     * false-关闭
     */
    public static boolean isEnable() {
        return enable;
    }

    /**
     * 设置日志功能开关
     *
     * @param enable true-启用 or false-关闭
     */
    public static void setEnable(boolean enable) {
        CLog.enable = enable;
        if (!enable && folder.exists()) {
            deleteFolder(folder);
        }
    }

    /**
     * 日志功能是否能将信息写入文件
     *
     * @return true-能 </br> false-不能
     */
    public static boolean canWritetosd() {
        return writetosd;
    }

    /**
     * 设置日志功能写文件开关
     *
     * @param writetosd enable true-启用 or false-关闭
     */
    public static void setWriteToSD(boolean writetosd) {
        CLog.writetosd = writetosd;
    }

    /**
     * 日志功能是否显示相关日志操作信息
     *
     * @return true-是 </br> false-否
     */
    public static boolean isShowLogInfo() {
        return showLogInfo;
    }

    /**
     * 设置日志功能设置是否显示相关日志操作信息
     *
     * @param showLogInfo
     */
    public static void setShowLogInfo(boolean showLogInfo) {
        CLog.showLogInfo = showLogInfo;
    }

    /**
     * 是否允许输出信息到LogCat
     *
     * @return true-是 </br> false-否
     */
    public static boolean isPrintout() {
        return printout;
    }

    /**
     * 是否允许输出信息到LogCat
     *
     * @param printout
     */
    public static void setPrintout(boolean printout) {
        CLog.printout = printout;
    }

    /**
     * 日志功能可写入的最大文件大小
     *
     * @return N kb
     */
    public static int getMaxSize() {
        return maxSize;
    }

    /**
     * 设置日志功能设置可写入的最大文件大小
     *
     * @param maxSize N kb
     */
    public static void setMaxSize(int maxSize) {
        CLog.maxSize = maxSize;
    }

    /**
     * 当前用来匹配的项目包名
     *
     * @return
     */
    public static String getPackageName() {
        return packageName;
    }

    /**
     * 设置用来匹配当前项目的包名
     *
     * @param packageName
     */
    public static void setPackageName(String packageName) {
        CLog.packageName = packageName;
    }

}
