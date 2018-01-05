package com.tobot.tobot.Listener;

import android.util.Log;

import com.turing123.robotframe.internal.modbus.protocol.core.OnProtocolListener;

import java.io.UnsupportedEncodingException;

/**
 * Created by Javen on 2017/8/31.
 */

public class SupersonicListener implements OnProtocolListener {
    @Override
    public void onMotionStart(byte[] bytes) {
        Log.i("Javen","onMotionStart..."+bytes.length);
    }

    @Override
    public void onMotionCompleted(byte[] bytes) {
        Log.i("Javen","onMotionCompleted..."+bytes.length);
    }

    @Override
    public void onOrderFeedback(byte[] bytes) {
        String s = null;

            s = new String(bytes);

        Log.i("Javen","onOrderFeedback..."+s.toString().substring(0,bytes.length));
//        Log.i("Javen","onOrderFeedback..."+s);
//        for (int i=0;i < bytes.length;i++){
//            Log.i("Javen","寄存器的数据..."+bytes[i]);
//        }
    }

    @Override
    public void onMotionError(byte[] bytes) {
        Log.i("Javen","onMotionError..."+bytes.length);
    }

    @Override
    public void onMotionInterrupt() {
        Log.i("Javen","onMotionInterrupt");
    }
}
