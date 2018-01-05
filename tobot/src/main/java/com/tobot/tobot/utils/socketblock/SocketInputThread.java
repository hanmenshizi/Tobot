package com.tobot.tobot.utils.socketblock;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.Arrays;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.tobot.tobot.MainActivity;
import com.tobot.tobot.control.Demand;
import com.tobot.tobot.control.demand.DemandModel;
import com.tobot.tobot.utils.TobotUtils;
import com.tobot.tobot.utils.Transform;

/**
 * 客户端读消息线程
 *
 * @author Javen
 */
public class SocketInputThread extends Thread {
    private boolean isStart = true;

    private static String tag = "Javen_SocketInputThread";

    // private MessageListener messageListener;// 消息监听接口对象

    public SocketInputThread() { }

    public void setStart(boolean isStart) {
        this.isStart = isStart;
    }

    @Override
    public void run() {
        while (isStart) {
            // 能联网，读socket数据
            if (NetManager.instance().isNetworkConnected()) {
                if (!TCPClient.instance().isConnect()) {//TCPClient
//                if (!TCPClientSocket.instance().isConnect()) {
                    CLog.e(tag, "TCPClientSocket connet server is fail read thread sleep second" + Const.SOCKET_SLEEP_SECOND);
                    TCPClient.instance().reConnect();//TCPClient
//                    TCPClientSocket.instance().reConnect();
                    try {
                        sleep(Const.SOCKET_SLEEP_SECOND * 1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                readSocketChannel();//TCPClient
//                readSocket();
                // 如果连接服务器失败,服务器连接失败，sleep固定的时间，能联网，就不需要sleep
//                CLog.e(tag, "TCPClientSocket.instance().isConnect() " + TCPClientSocket.instance().isConnect());
            }
        }
    }

    public void readSocketChannel() {
        Selector selector = TCPClient.instance().getSelector();
        if (selector == null) {
            return;
        }
        try {
            // 如果没有数据过来，一直阻塞
            while (selector.select() > 0) {
                for (SelectionKey sk : selector.selectedKeys()) {
                    // 如果该SelectionKey对应的Channel中有可读的数据
                    if (sk.isReadable()) {
                        // 使用NIO读取Channel中的数据
                        SocketChannel sc = (SocketChannel) sk.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        try {
                            sc.read(buffer);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            // continue;
                        }
                        buffer.flip();
                        String receivedString = "";
                        // 打印收到的数据
                        try {
                            receivedString = Charset.forName("UTF-8").newDecoder().decode(buffer).toString();
                            if (TobotUtils.isNotEmpty(receivedString)) {
                                CLog.e(tag, "nio读取到的数据:" + receivedString);
                                //处理收到服务器过来的消息
                                if (receivedString.equals("[12]")) {//处理心跳回复

                                } else if (receivedString.equals("[10]")) {
                                    TCPClient.instance().reConnect();
                                } else if (receivedString.equals("[1100011960B]")) {//注册成功

                                } else if (receivedString.equals("[1100010160E]")) {//注册失败
                                    SocketThreadManager.sharedInstance().sendMsg(Transform.HexString2Bytes(Joint.setRegister()));
                                } else if (receivedString.substring(2, 3).equals("3")) {//拍照 31 33 30 30 30 35 30 30 30 30 31 46 42 39 46
                                    SocketThreadManager.sharedInstance().sendMsg(Transform.HexString2Bytes(Joint.setResponse(Joint.PHOTO, receivedString)));
//                                    MainActivity.mBLocal.carryThrough(Joint.getSpecialRunning(receivedString));
                                } else if (receivedString.substring(2, 3).equals("4")) {//点播
                                    SocketThreadManager.sharedInstance().sendMsg(Transform.HexString2Bytes(Joint.setDemandResponse(receivedString)));
                                    Message Msg = Message.obtain();
                                    Msg.what = 4;
                                    Msg.obj = receivedString;
                                    handler.sendMessage(Msg);
                                } else if (receivedString.substring(2, 3).equals("5")) {//角色定义修改通知
                                    SocketThreadManager.sharedInstance().sendMsg(Transform.HexString2Bytes(Joint.setRoleResponse()));
                                    //还没做
                                } else if (receivedString.substring(2, 3).equals("6")) {//出厂设置
                                    SocketThreadManager.sharedInstance().sendMsg(Transform.HexString2Bytes(Joint.setResponse(Joint.RESTORE, receivedString)));
                                    //还没做
                                } else if (receivedString.substring(2, 3).equals("8")) {//舞蹈
                                    SocketThreadManager.sharedInstance().sendMsg(Transform.HexString2Bytes(Joint.setResponse(Joint.DANCE, receivedString)));
                                    //还没做
                                }
//                                //广播发送
//                                Intent intent = new Intent(Const.BC);
//                                intent.putExtra("response", receivedString);
//                                MainActivity.mContext.sendBroadcast(intent);
                            }
                        } catch (CharacterCodingException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        buffer.clear();
                        buffer = null;
                        try {
                            // 为下一次读取作准备
                            sk.interestOps(SelectionKey.OP_READ);
                            // 删除正在处理的SelectionKey
                            selector.selectedKeys().remove(sk);
                        } catch (CancelledKeyException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            // selector.close();
            // TCPClient.instance().repareRead();

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ClosedSelectorException e2) {
        }
    }

    public void readSocket() {
        Socket socket = TCPClientSocket.instance().getSocket();
        if (socket == null) {
            return;
        }
        try {
            InputStream mInputStream = socket.getInputStream();
            byte[] mbyte = new byte[1024 * 2];
            int length = 0;
            while (!socket.isClosed() && !socket.isInputShutdown() && isStart && ((length = mInputStream.read(mbyte)) != -1)) {
                if (length > 0) {
                    String message = new String(Arrays.copyOf(mbyte, length),"GB2312");
                    Log.e("Javen", "SocketInputThread回应消息..." + message);
                    //处理收到服务器过来的消息
                    if (message.equals("[12]")) {//处理心跳回复

                    } else  if (message.equals("[10]")){
                        TCPClientSocket.instance().reConnect();
                    } else if (message.equals("[1100011960B]")) {//注册成功

                    } else if(message.equals("[1100010160E]")){//注册失败
                        SocketThreadManager.sharedInstance().sendMsg(Transform.HexString2Bytes(Joint.setRegister()));
                    } else if (message.substring(2,3).equals("3")) {//拍照 31 33 30 30 30 35 30 30 30 30 31 46 42 39 46
                        SocketThreadManager.sharedInstance().sendMsg(Transform.HexString2Bytes(Joint.setResponse(Joint.PHOTO,message)));
//                        MainActivity.mBLocal.carryThrough(Joint.getSpecialRunning(message));
                    } else if (message.substring(2,3).equals("4")) {//点播
                        SocketThreadManager.sharedInstance().sendMsg(Transform.HexString2Bytes(Joint.setDemandResponse(message)));
                        Message Msg = Message.obtain();
                        Msg.what = 4;
                        Msg.obj = message;
                        handler.sendMessage(Msg);
                    } else if (message.substring(2,3).equals("5")) {//角色定义修改通知
                        SocketThreadManager.sharedInstance().sendMsg(Transform.HexString2Bytes(Joint.setRoleResponse()));
                        //还没做
                    } else if (message.substring(2,3).equals("6")) {//出厂设置
                        SocketThreadManager.sharedInstance().sendMsg(Transform.HexString2Bytes(Joint.setResponse(Joint.RESTORE,message)));
                        //还没做
                    } else if (message.substring(2,3).equals("8")) {//舞蹈
                        SocketThreadManager.sharedInstance().sendMsg(Transform.HexString2Bytes(Joint.setResponse(Joint.DANCE,message)));
                        //还没做
                    }
                }
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (ClosedSelectorException e2) {
            e2.printStackTrace();
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                case 4:
                    String message = (String) msg.obj;
                    DemandModel demandModel = new DemandModel();
                    demandModel.setCategoryId(Integer.parseInt(Joint.getCommaAmong(message, 1)));
                    demandModel.setTrack_title(Joint.getCommaAmong(message, 2));
                    demandModel.setPlayUrl32(Joint.getPeelVerify(message));
//                    Demand.instance(MainActivity.mContext).setResource(demandModel);
                    break;
                case 5:
                    break;
                case 6:
                    break;
            }
        }
    };

}
