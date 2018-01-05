package com.tobot.tobot.utils.socketblock;

import android.util.Log;

import com.tobot.tobot.utils.Transform;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import static com.tobot.tobot.utils.socketblock.Const.HEART;

/**
 * Created by Javen on 2017/10/10.
 */
public class TCPClientSocket {
    //socket 软引用
    private WeakReference<Socket> mWeakReference;

    private static TCPClientSocket s_Tcp = null;

    public boolean isInitialized = false;

    public static synchronized TCPClientSocket instance() {
        if (s_Tcp == null) {
            s_Tcp = new TCPClientSocket();
        }
        return s_Tcp;
    }

    /**
     * 构造函数
     * @throws IOException
     */
    private TCPClientSocket() { initialize(); }

    /**
     * 初始化
     * @throws IOException
     */
    private void initialize() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket mSocket = new Socket(Const.SOCKET_SERVER, Const.SOCKET_PORT);
                    if (mSocket.isConnected()) {
                        //是否延时
                        mSocket.setTcpNoDelay(false);
                        //保持连接
                        mSocket.setKeepAlive(true);
                        //设置 读socket的timeout时间
                        mSocket.setSoTimeout(Const.SOCKET_READ_TIMOUT);
                        //弱引用
                        mWeakReference = new WeakReference<Socket>(mSocket);
                        //注册
                        sendMsg(Joint.setRegister());
                        isInitialized = true;
                    } else {
                      reConnect();
                    }
                } catch (SocketException e) {
                    isInitialized = false;
                } catch (UnknownHostException e) {
                    isInitialized = false;
                } catch (IOException e) {
                    isInitialized = false;
                }
            }
        }).start();
    }

    /**
     * 发送数据byte[]
     * @param bytes
     * @throws IOException
     */
    public boolean sendMsg(byte[] bytes){
        if (null == mWeakReference || mWeakReference.get().isClosed()) {
            return false;
        }
        Socket socket = mWeakReference.get();
        try {
            if (socket.isConnected() && !socket.isOutputShutdown()) {
                OutputStream os = socket.getOutputStream();
                os.write(bytes);
                os.flush();
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 发送数据String
     * @param msg
     * @throws IOException
     */
    public boolean sendMsg(String msg){
        if (null == mWeakReference || mWeakReference.get().isClosed()) {
            return false;
        }
        Socket socket = mWeakReference.get();
        try {
            if (socket.isConnected() && !socket.isOutputShutdown()) {
                OutputStream os = socket.getOutputStream();
                os.write(Transform.HexString2Bytes(msg));
                os.flush();
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     *
     * @return
     */
    public synchronized Socket getSocket() {
        return mWeakReference.get();
    }

    /**
     * Socket连接是否是正常的
     * @return
     */
    public boolean isConnect() {
        boolean isConnect = false;
        if (this.isInitialized && mWeakReference.get() != null) {
            isConnect = this.mWeakReference.get().isConnected();
        }
        return isConnect;
    }

    /**
     * 关闭socket 重新连接
     * @return
     */
    public boolean reConnect() {
        closeTCPSocket();
        initialize();
        return isInitialized;
    }

    /**
     * 释放/关闭socket
     * @param
     */
    private void closeTCPSocket() {
        try {
            if (null != mWeakReference) {
                Socket socket = mWeakReference.get();
                if (!socket.isClosed()) {
                    socket.close();
                }
                socket = null;
                mWeakReference = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 服务器是否关闭，通过发送一个socket信息
     * @return
     */
    public boolean canConnectToServer() {
        try {
            if (mWeakReference.get() != null) {
                boolean heart = sendMsg(HEART);
                CLog.e("Javen","心跳包发送" + heart);
                return heart;
            }else{
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }




}
