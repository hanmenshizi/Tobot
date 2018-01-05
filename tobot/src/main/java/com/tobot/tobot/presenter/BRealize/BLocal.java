package com.tobot.tobot.presenter.BRealize;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.ViewGroup;

import com.tobot.tobot.MainActivity;
import com.tobot.tobot.R;
import com.tobot.tobot.base.Constants;
import com.tobot.tobot.control.Demand;
import com.tobot.tobot.presenter.ICommon.ISceneV;
import com.tobot.tobot.presenter.IPort.ILocal;
import com.tobot.tobot.scene.BaseScene;
import com.tobot.tobot.utils.SHA1;
import com.tobot.tobot.utils.TobotUtils;
import com.tobot.tobot.utils.Transform;
import com.tobot.tobot.utils.okhttpblock.OkHttpUtils;
import com.tobot.tobot.utils.okhttpblock.callback.StringCallback;
import com.tobot.tobot.utils.photograph.CameraInterface;
import com.tobot.tobot.utils.photograph.CameraSurfaceView;
import com.tobot.tobot.utils.photograph.DisplayUtil;
import com.tobot.tobot.utils.socketblock.SocketConnectCoherence;
import com.turing123.robotframe.RobotFrameManager;
import com.turing123.robotframe.function.asr.ASR;
import com.turing123.robotframe.function.asr.IASRHotWordUploadCallback;
import com.turing123.robotframe.localcommand.LocalCommand;
import com.turing123.robotframe.localcommand.LocalCommandCenter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Javen on 2017/8/9.
 */

public class BLocal implements ILocal,CameraInterface.CamOpenOverCallback {

    private CameraSurfaceView surfaceView;
    private static BLocal mBLocal;
    private Context mContent;
    private ISceneV mISceneV;
    private LocalCommandCenter localCommandCenter;
    private LocalCommand localCommand;
    private RobotFrameManager mRobotFrameManager;
    private MainActivity mainActivity;
    private String phty;
    float previewRate = -1f;
    private String sn = "";//流水号
    private String uuid;

    public static synchronized BLocal instance(ISceneV mISceneV) {
        if (mBLocal == null) {
            mBLocal = new BLocal(mISceneV);
        }
        return mBLocal;
    }

    private  BLocal(ISceneV mISceneV){
        this.mISceneV = mISceneV;
        this.mContent = (Context) mISceneV;
        this.mainActivity = (MainActivity) mISceneV;
        setHotWord();
        disposeLocal();
//        Thread openThread = new Thread(){//开启预览
//            @Override
//            public void run() {
//                renderScreen();
//            }
//        };
//        openThread.start();
        initViewParams();//初始化屏幕
    }


    @Override
    public void setHotWord() {
        //1. 创建ASR 对象。
        ASR asr = new ASR(mContent, new BaseScene(mContent,"os.sys.chat"));
        //2. 创建热词列表。
        ArrayList<String> words = new ArrayList<String>();
        words.add("拍照");
        words.add("快进");
        words.add("暂停");
        words.add("好了");
        words.add("可以了");
        words.add("不想听");
        words.add("拍张照片吧");
        words.add("帮我拍张照片吧");
        //3. 设置热词
        asr.uploadHotWords(words, new IASRHotWordUploadCallback() {
            @Override
            public void onSuccess() {
                Log.i("Javen", "热词设置成功");
            }

            @Override
            public void onError() {
                Log.i("Javen", "热词设置失败");
            }
        });
    }

    @Override
    public void disposeLocal() {
        //1. 获取LocalCommandCenter 对象
        localCommandCenter = LocalCommandCenter.getInstance(mContent);
        //2. 定义本地命令的名字
        String name = "local";
        //3. 定义匹配该本地命令的关键词，包含这些关键词的识别结果将交由该本地命令处理。
        List<String> keyWords = new ArrayList<String>();
        keyWords.add("拍照");
        keyWords.add("拍张照");
        keyWords.add("拍张照吧");
        keyWords.add("帮我拍张照吧");
        //4. 定义本地命令对象
        localCommand = new LocalCommand(name, keyWords) {
            //4.1. 在process 函数中实现该命令的具体动作。
            @Override
            protected void process(String name, String s) {
                //4.1.1. 本示例中，当喊关键词中配置的词时将使机器人进入拍照
                carryThrough("");
                //5. 命令执行完成后需明确告诉框架，命令处理结束，否则无法继续进行主对话流程。
                this.localCommandComplete.onComplete();
            }

            //4.2. 执行命令前的处理
            @Override
            public void beforeCommandProcess(String s) {

            }

            //4.3. 执行命令后的处理
            @Override
            public void afterCommandProcess() {

            }
        };
        //5. 将定义好的local command 加入 LocalCommandCenter中。
        localCommandCenter.add(localCommand);
    }

    @Override
    public void renderScreen() {
        try {
            CameraInterface.getInstance().doOpenCamera(BLocal.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void carryThrough(String var) {
        this.sn = var;
        new Thread(new Runnable() {
            @Override
            public void run() {
                renderScreen();
                Photograph();
            }
        }). start();
    }

    private void initViewParams(){
        surfaceView = (CameraSurfaceView)mainActivity.findViewById(R.id.camera_surfaceview);
        ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
        Point p = DisplayUtil.getScreenMetrics(mContent);
        params.width = p.x;
        params.height = p.y;
        previewRate = DisplayUtil.getScreenRate(mContent); //默认全屏的比例预览
        surfaceView.setLayoutParams(params);
    }

    @Override
    public void cameraHasOpened() {
        SurfaceHolder holder = surfaceView.getSurfaceHolder();
        CameraInterface.getInstance().doStartPreview(holder, previewRate);//预览
    }

    public void Photograph(){//拍照
        CameraInterface.getInstance().doTakePicture();
    }


    public void upload(String path){
        uuid = Transform.getGuid();
        OkHttpUtils.post()
                .url(Constants.IMAGE_UPLOAD + uuid + "/" + SHA1.gen(Constants.identifying + uuid))
                .addParams("nonce", uuid)//伪随机数
                .addParams("sign", SHA1.gen(Constants.identifying + uuid))//签名
                .addParams("robotId", TobotUtils.getDeviceId(Constants.DeviceId,Constants.Path))//机器人设备ID
                .addParams("sn", sn)
                .addFile("data", path, new File(path))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i("Javen","照片发送失败:"+call.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i("Javen","照片发送成功:"+response);
                    }
                });
    }


}




//        public void photograph(){
//        Log.i("Javen","执行拍照");
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        File out = new File(getPhotopath());
//        intent.putExtra("return-data", out);
////        Log.i("Javen","路径"+Uri.fromFile(out));
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(out));
////        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
////        intent.putExtra("noFaceDetection", true);
//        mISceneV.getImgpath(Uri.fromFile(out));
//        mainActivity.startActivityForResult(intent, Constants.FOR_RESULT);
//    }
//
//
//    /**
//     * 获取原图片存储路径
//     * @return
//     */
//    private String getPhotopath() {
//        // 文件夹路径
//        String thepath = Environment.getExternalStorageDirectory() + "/";
//        thepath += mContent.getString(R.string.app_name) + "/";
//        Calendar ca = Calendar.getInstance();
//        int year = ca.get(Calendar.YEAR);// 获取年份
//        int month = ca.get(Calendar.MONTH) + 1;// 获取月份
//        int day = ca.get(Calendar.DATE);// 获取日
//        int hour = ca.get(Calendar.HOUR_OF_DAY);// 小时
//        int minute = ca.get(Calendar.MINUTE);// 分
//        int second = ca.get(Calendar.SECOND);// 秒
//        int milliSecond = ca.get(Calendar.MILLISECOND);
//        String Suffix = "png";
//        // 照片全路径
//        String fileName = thepath + year + "-" + (month < 10 ? "0" : "") + month + "-" + (day < 10 ? "0" : "") + day
//                + "_" + (hour < 10 ? "0" : "") + hour + "-" + (minute < 10 ? "0" : "") + minute + "-"
//                + (second < 10 ? "0" : "") + second + "_" + (milliSecond < 10 ? "00" : (milliSecond < 100 ? "0" : ""))
//                + milliSecond + (Suffix == null ? "" : ("." + Suffix));
//        File file = new File(thepath);
//        if (!file.exists()) {
//            file.mkdirs();// 创建文件夹
//        }
//        return fileName;
//    }
