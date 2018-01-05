package com.tobot.tobot.utils.socketblock;

import android.util.Log;

import com.tobot.tobot.utils.Transform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import static com.tobot.tobot.utils.socketblock.Const.HEART;

/**
 * NIO TCP 客户端
 *
 * @author Javen
 */
public class TCPClient {
    // 信道选择器
    private Selector selector;

    // 与服务器通信的信道
    private SocketChannel socketChannel;

    // 要连接的服务器Ip地址
    private String hostIp;

    // 要连接的远程服务器在监听的端口
    private int hostListenningPort;

    private static TCPClient s_Tcp = null;

    public boolean isInitialized = false;

    public static synchronized TCPClient instance() {
        if (s_Tcp == null) {
            s_Tcp = new TCPClient(Const.SOCKET_SERVER, Const.SOCKET_PORT);
        }
        return s_Tcp;
    }

    /**
     * 构造函数
     *
     * @param HostIp
     * @param HostListenningPort
     * @throws IOException
     */
    public TCPClient(String HostIp, int HostListenningPort) {
        this.hostIp = HostIp;
        this.hostListenningPort = HostListenningPort;
        try {
            separateThread();
            this.isInitialized = true;
        } catch (Exception e) {
            this.isInitialized = false;
            e.printStackTrace();
        }
    }

    private void separateThread() throws Exception{
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    initialize();
                } catch (IOException e) {
                    isInitialized = false;
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 初始化
     *
     * @throws IOException
     */
    private void initialize() throws IOException {
        boolean done = false;
        try {
            // 创建一个套接字通道，注意这里必须使用有参形式
            socketChannel = SocketChannel.open(new InetSocketAddress(hostIp, hostListenningPort));
            if (socketChannel != null) {
                //是否延时
                socketChannel.socket().setTcpNoDelay(false);
                //保持连接
                socketChannel.socket().setKeepAlive(true);
                // 设置 读socket的timeout时间
                socketChannel.socket().setSoTimeout(Const.SOCKET_READ_TIMOUT);
                //设置为非阻塞模式
                socketChannel.configureBlocking(false);
                // 打开并注册选择器到信道
                selector = Selector.open();
                if (selector != null) {
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    done = true;
                }
                sendMsg(Joint.setRegister());

//            //非自动连接模式
//            // 创建一个套接字通道，注意这里必须使用无参形式
//            socketChannel = SocketChannel.open();
//            // 设置为非阻塞模式，这个方法必须在实际连接之前调用(所以open的时候不能提供服务器地址，否则会自动连接)
//            socketChannel.configureBlocking(false);
//            // 连接服务器，由于是非阻塞模式，这个方法会发起连接请求，并直接返回false(阻塞模式是一直等到链接成功并返回是否成功)
//           boolean connect = socketChannel.connect(new InetSocketAddress(hostIp, hostListenningPort));
//            if (socketChannel != null) {
//                //是否延时
//                socketChannel.socket().setTcpNoDelay(false);
//                //保持连接
//                socketChannel.socket().setKeepAlive(true);
//                // 设置 读socket的timeout时间
//                socketChannel.socket().setSoTimeout(Const.SOCKET_READ_TIMOUT);
//                // 打开并注册选择器到信道
//                selector = Selector.open();
//                if (selector != null) {
//                    socketChannel.register(selector, SelectionKey.OP_READ);//OP_CONNECT
//                    done = true;
//                }
//                sendMsg(Joint.setRegister());
            }
        } finally {
            if (!done && selector != null) {
//                selector.close();
            }
            if (!done){
//                socketChannel.close();
            }
        }
    }

    /**
     *
     * @param key
     * @param timeout
     * @throws IOException
     */
    static void blockUntil(SelectionKey key, long timeout) throws IOException {
        int nkeys = 0;
        if (timeout > 0) {
            nkeys = key.selector().select(timeout);
        } else if (timeout == 0) {
            nkeys = key.selector().selectNow();
        }
        if (nkeys == 0) {
            throw new SocketTimeoutException();
        }
    }

    /**
     * 发送字符串到服务器
     *
     * @param message
     * @throws IOException
     */
    public void sendMsg(String message){
//        ByteBuffer writeBuffer = ByteBuffer.wrap(message.getBytes("utf-8"));
        ByteBuffer writeBuffer = ByteBuffer.wrap(Transform.HexString2Bytes(message));
        try{
            if (socketChannel != null && socketChannel.isConnected()) {
                socketChannel.write(writeBuffer);
                CLog.e("Javen","数据发送成功");
            }else{
                CLog.e("Javen","数据发送失败");
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 发送数据
     *
     * @param bytes
     * @throws IOException
     */
    public boolean sendMsg(byte[] bytes){
        ByteBuffer writeBuffer = ByteBuffer.wrap(bytes);
        try{
            if (socketChannel != null && socketChannel.isConnected()) {
                socketChannel.write(writeBuffer);
                CLog.e("Javen","数据发送成功");
            }else{
                CLog.e("Javen","数据发送失败");
                return false;
            }
        }catch (IOException e){
            return false;
        }
        return true;
    }

    /**
     * @return
     */
    public synchronized Selector getSelector() {
        return this.selector;
    }

    /**
     * Socket连接是否是正常的
     *
     * @return
     */
    public boolean isConnect() {
        boolean isConnect = false;
//        CLog.e("Javen","连接是否正常....isInitialized:" + isInitialized);
        if (this.isInitialized) {
            isConnect = this.socketChannel.isConnected();
        }
//        CLog.e("Javen","连接是否正常....isConnect:" + isInitialized);
        return isConnect;
    }

    /**
     * 关闭socket 重新连接
     *
     * @return
     */
    public boolean reConnect() {
        closeTCPSocket();
        try {
            separateThread();
            isInitialized = true;
        } catch (Exception e) {
            isInitialized = false;
            e.printStackTrace();
        }
        return isInitialized;
    }

    /**
     * 服务器是否关闭，通过发送一个socket信息
     *
     * @return
     */
    public boolean canConnectToServer() {
        try {
            if (socketChannel != null && socketChannel.isConnected()) {
//                socketChannel.socket().sendUrgentData(0xff);//数据不一致
                boolean heart = sendMsg(Transform.HexString2Bytes(HEART));
            }else{
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 关闭socket
     */
    public void closeTCPSocket() {
        try {
            if (socketChannel != null) {
                socketChannel.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (selector != null) {
                selector.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 每次读完数据后，需要重新注册selector，读取数据
     */
    public synchronized void repareRead() {
        if (socketChannel != null) {
            try {
                selector = Selector.open();
                socketChannel.register(selector, SelectionKey.OP_READ);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
