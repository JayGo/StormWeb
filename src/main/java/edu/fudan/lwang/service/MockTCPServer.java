package edu.fudan.lwang.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import edu.fudan.lwang.obj.BaseMessage;
import edu.fudan.lwang.obj.EffectMessage;

public class MockTCPServer {
	private final int serverMsgPort = 8999;
	private ServerSocket sc;
	private Runnable runnable;
	
	public static void main(String args[]) {
		MockTCPServer server = new MockTCPServer();
		server.startListen();
	}
	
	public void startListen() {
		new Thread(runnable).start();
	}
	
	public MockTCPServer() {
		try {
			sc = new ServerSocket(serverMsgPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		runnable = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true) {
					try {
						System.out.println("Server is listenning...");
						Socket socket = sc.accept();
						
//						BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
//						ObjectInputStream ois = new ObjectInputStream(bis);
						ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
						BaseMessage msg = (BaseMessage) ois.readObject();
						
						BaseMessage reslut = handleMessage(msg);
						
						// BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
						// ObjectOutputStream oos = new ObjectOutputStream(bos);
						ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
						oos.writeObject(reslut);
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		};
	}
	

	private BaseMessage handleMessage(BaseMessage msg) {
		BaseMessage result = null;
		EffectMessage eMsg = null;
		System.out.println("Message: "+msg.getMsg());
		System.out.println("Addr: "+msg.getAddr());
		
		if(msg instanceof EffectMessage) {
			eMsg = (EffectMessage) msg;

			System.out.println("Effect: "+eMsg.getEffectType());
			System.out.println("Parameters' size: "+eMsg.getParameters().size());
		} else {
			// do something else.
		}
		
		result = new BaseMessage("server is OK", "rtmp://10.134.142.111");
		return result;
	}
	
	
	
}
