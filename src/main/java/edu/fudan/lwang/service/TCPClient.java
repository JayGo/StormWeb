package edu.fudan.lwang.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.fudan.lwang.obj.BaseMessage;
import edu.fudan.lwang.obj.EffectMessage;

/**
 * send: "start,[videoAddress]" ---> received:
 * "succeed,[jpeg stream ip address and port] <strong>OR</strong> Error,[reason]"
 * <br>
 * send: "startByMaster,[videoAddress]" ---> received:
 * "succeed,[jpeg stream ip address and port] <strong>OR</strong> Error,[reason]"
 * <br>
 * send: "end,[videoAddress]" ---> received "Succeed" <strong>OR</strong> "Error,[reason]"
 * <br>
 * send: "endByMaster,[videoAddress]" ---> received "Succeed" <strong>OR</strong> "Error,[reason]"
 * 
 * @author lwang
 *
 */
public class TCPClient {

	private Socket socket;

	public static void main(String args[]) {

		TCPClient client = new TCPClient();
		
		BaseMessage bmsg = new BaseMessage("Start", "rtsp://1223a.dwoa");
		BaseMessage result = client.sendMsg(bmsg);
		
		System.out.println("Message: "+result.getMsg());
		System.out.println("Addr: "+result.getAddr());

	}
	
	public TCPClient() {
		try {
			socket = new Socket("10.134.142.100", 8999);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public BaseMessage sendMsg(BaseMessage msg) {
		BaseMessage result = null;
		try {
			
			// BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
			// ObjectOutputStream oos = new ObjectOutputStream(bos);
			ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(msg);
			
			// BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
			// ObjectInputStream ois = new ObjectInputStream(bis);
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			result = (BaseMessage) ois.readObject();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * send message to certain server
	 * 
	 * @param msg message ready to send to server
	 * @param serverAddr server ip address
	 * @param port server port
	 * @return server replay
	 */
	public String sendMsg(String msg, String serverAddr, int port) {
		String receivedMsg = "";
		try {
			socket = new Socket(serverAddr, port);
			byte[] data = msg.getBytes("utf-8");

			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.write(data);

			char[] receivedData = new char[1024];
			// receive
			
			int numRead = in.read(receivedData);
			receivedMsg = new String(receivedData, 0, numRead);
			
			System.out.println("Client Received:" + receivedMsg);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return receivedMsg;
	}

}
