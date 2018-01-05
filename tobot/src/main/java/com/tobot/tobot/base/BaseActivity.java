package com.tobot.tobot.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;

import com.turing123.robotframe.function.FunctionState;
import com.turing123.robotframe.function.IFunctionStateObserver;
import com.turing123.robotframe.function.keyin.IKeyInputFunction;
import com.turing123.robotframe.function.keyin.IKeyInputObserver;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.ButterKnife;

public abstract class BaseActivity extends Activity {

    public abstract void isKeyDown(int keyCode, KeyEvent event);
    public abstract int getGlobalLayout();
    public abstract void initial(Bundle savedInstanceState);
    private boolean isLongPressKey;//是否长按
    private boolean lockLongPressKey;
    private int KEY_BACK_LONG = 100000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getGlobalLayout());
        ButterKnife.bind(this);
        initial(savedInstanceState);
    }

    public void Log(String msg) { android.util.Log.i("Javen MainActivity", msg); }
    public void Toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        Log.e("Javen BaseActivity onKeyDown","==>keyCode:"+keyCode+"==>keyEvent:"+event);
        switch(keyCode){
//            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_BACK://需要识别长按事件
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                Log("开始摸头时间:"+dateFormat.format(new Date()));
                if (event.getRepeatCount() < 30) {
//                    if(event.getRepeatCount() == 0){
//                        isKeyDown(keyCode,event);
//                        isLongPressKey = false;
//                    }
                    event.startTracking();
                    isLongPressKey = false;
//                    Log("中间循环时间:"+dateFormat.format(new Date()));
                }else if (event.getRepeatCount() > 30){
//                    isKeyDown(KeyEvent.FLAG_LONG_PRESS,event);
                    isLongPressKey = true;
//                    Log("结束摸头时间:"+dateFormat.format(new Date()));
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
//        Log.e("Javen BaseActivity onKeyLongPress","==>keyCode:"+keyCode+"==>keyEvent:"+event);
        lockLongPressKey = true;
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        Log.e("Javen BaseActivity onKeyUp","==>keyCode:"+keyCode+"==>keyEvent:"+event);
        //Javen_BaseActivity_onKeyUp: keyCode:4
        // keyEvent:KeyEvent { action=ACTION_UP, keyCode=KEYCODE_BACK, scanCode=158, metaState=0, flags=0x208, repeatCount=0, eventTime=12233791, downTime=12231429, deviceId=1, source=0x101 }
        switch(keyCode){
//            case KeyEvent.KEYCODE_MENU:
//            case KeyEvent.KEYCODE_ENTER:
//            case KeyEvent.KEYCODE_DPAD_CENTER:
//            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_BACK:
                if (isLongPressKey == true) {
                    if (lockLongPressKey) {
                            isKeyDown(KeyEvent.FLAG_LONG_PRESS,event);
                        Log.e("Javen BaseActivity onKeyUp","进入长按事件处理");
                    }
                } else if (isLongPressKey == false) {
                    if (!lockLongPressKey) {
                            isKeyDown(keyCode,event);
                    }
                }

                if(lockLongPressKey){
                    lockLongPressKey = false;
                    return true;
                }

                return true;//中断
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_DPAD_UP:

                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_DPAD_DOWN:

                return true;
        }
//        switch(keyCode){
//            case KeyEvent.KEYCODE_BACK:
//                Log("onKeyUp............."+event);
//                if(lockLongPressKey){
//                    lockLongPressKey = false;
//                    return true;
//                }
//                return true;
//        }
        return super.onKeyUp(keyCode, event);
    }



}
