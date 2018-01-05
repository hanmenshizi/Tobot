package com.tobot.tobot.utils.socketblock;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


/**
 * 客户端写消息线程
 *
 * @author Javen
 */
public class SocketOutputThread extends Thread {
	private boolean isStart = true;
	private static String TAG = "Javen_socketOutputThread";
	private List<MsgEntity> sendMsgList;

	public SocketOutputThread() {
		sendMsgList = new CopyOnWriteArrayList<MsgEntity>();
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
		synchronized (this) {
			notify();
		}
	}

	// 使用socket发送消息
	public boolean sendMsgOut(byte[] msg) throws Exception {
		if (msg == null) {
			CLog.e(TAG, "sendMsg is null");
			return false;
		}
		try {
			TCPClient.instance().sendMsg(msg);//TCPClient
//            TCPClientSocket.instance().sendMsg(msg);
		} catch (Exception e) {
			throw (e);
		}
		return true;
	}

	// 使用socket发送消息
	public void addMsgToSendList(MsgEntity msg) {
		synchronized (this) {
			this.sendMsgList.add(msg);
			notify();
		}
	}

	@Override
	public void run() {
		while (isStart) {
			// 锁发送list
			synchronized (sendMsgList) {
				// 发送消息
				for (MsgEntity msg : sendMsgList) {
					Handler handler = msg.getHandler();
					try {
						boolean send = sendMsgOut(msg.getBytes());
						sendMsgList.remove(msg);
						// 成功消息，通过hander回传
						if (handler != null) {
							Message message = new Message();
							message.obj = msg.getBytes();
							message.what = 101;
							handler.sendMessage(message);
							//	handler.sendEmptyMessage(1);
						}
					} catch (Exception e) {
						e.printStackTrace();
						CLog.e(TAG, e.toString());
						// 错误消息，通过hander回传
						if (handler != null) {
							Message message = new Message();
							message.obj = msg.getBytes();
							message.what = 102;
							handler.sendMessage(message);
						}
					}
				}
			}

			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}// 发送完消息后，线程进入等待状态
			}
		}
	}


}
