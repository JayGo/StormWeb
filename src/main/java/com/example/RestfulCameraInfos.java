package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import edu.fudan.lwang.obj.CameraInfo;
import edu.fudan.lwang.obj.CommonResponse;
import edu.fudan.lwang.obj.EffectInfo;
import edu.fudan.lwang.obj.RawRtmpInfo;
import edu.fudan.lwang.service.TCPClient;

import java.sql.*;
//import org.apache.thrift.transport.TTransport;

/*
 * @author jliu
 */

@Path("camera")
public class RestfulCameraInfos {

	static private ArrayList<CameraInfo> camList = null;
	private static Map<String, List<String>> videoMap = new HashMap<String, List<String>>();
	private static Map<String, List<String>> effectMap = new HashMap<String, List<String>>();
	private static SqlManager sm = SqlManager.getInstance();
	private static CameraPageGenerator cpg = CameraPageGenerator.getInstance();

//	static private String dirString = "/home/jliu/workspace/simple-service-webapp/src/main/webapp/";

	private final String[] cameraStringList = { 
			"rtsp://admin:123456qaz@10.134.141.176:554/h264/ch1/main/av_stream",
			"rtsp://admin:123456qaz@10.134.141.177:554/h264/ch1/main/av_stream",
			"rtsp://admin:123456qaz@10.134.141.178:554/h264/ch1/main/av_stream",
			"file:///home/nfs/videos/classroom.mp4",
			"file:///home/nfs/videos/classroom_baofeng.mp4",
			"file:///home/nfs/videos/grass720.mp4",
			"file:///home/nfs/videos/grass1080.mp4"};
	
	private final String serverIp = "10.134.142.100";
	private final int msgPort = 8967;

	public RestfulCameraInfos() {
	}
	
	private void print(String msg) {
		System.out.println(msg);
	}

	@GET
	@Path("/allCamerasList")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<CameraInfo> allCameraLists() {	
		return (ArrayList<CameraInfo>) sm.getCameraList();
	}
	
	@GET
	@Path("/rawRtmp")
	@Produces(MediaType.APPLICATION_JSON)
	public RawRtmpInfo getRawRtmpRecord(@QueryParam("id") int id) {
		return sm.getRawRtmpRecord(id);
	}
	
	@POST
	@Path("/addEffect")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public CommonResponse addEffect(EffectInfo effectInfo) {
		CommonResponse mCommonResponse = new CommonResponse();
		print("============Add effect info:==============");
		int camId = effectInfo.getCamId();
		print("camId: "+camId);
		int playerId = effectInfo.getPlayerId();
		print("playerId: "+playerId);
		// String rtmpUrl = effectInfo.getRtmpUrl();
		String rtmpUrl = effectInfo.getRtmpUrl();
		print("rtmpUrl: "+rtmpUrl);
		String effectType = effectInfo.getEffectType();
		print("effetType: "+effectType);
		
		HashMap<String, Double> parameters = effectInfo.getParameters();
		print("parameters' size: "+parameters.size());
		mCommonResponse.setResult(true);
		sm.addEffect(camId, playerId, rtmpUrl, effectType, parameters);
		
		
		return mCommonResponse;
	}
	
	@POST
	@Path("/add")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public CommonResponse addCamera(CameraInfo cam) {
		
		CommonResponse mCommonResponse = new CommonResponse();
		String addr = cam.getCameraAddr();
		
		// make sure the new comer's id is the biggest.
		List<CameraInfo> cameraList = sm.getCameraList();
		cam.setCameraId(cameraList.size());
		int id = cam.getCameraId();
		
		// print("Message out:"+"valid," + addr);
		
		cpg.createPage(id);
		sm.addCamera(id,addr);
		String rtmpUrl = "rtmp://"+id;
		sm.recordRawRtmp(id, 0, rtmpUrl);
		mCommonResponse.setResult(true);
		// cpg.createPage(id);
		
//		TCPClient mTCPClient = new TCPClient();
//		String isVideoAddrValid = mTCPClient.sendMsg("valid," + addr, serverIp, msgPort);
//		
//		if(isVideoAddrValid.equals("true")) {
//			sm.addCamera(id,addr);
//			cpg.createPage(id);
//			mCommonResponse.setResult(true);
//			mCommonResponse.setMessage("succeed");
//			print("Add successfully!");
//		}
//		else {
//			print("Result returned from Server: "+isVideoAddrValid);
//			mCommonResponse.setResult(false);
//			mCommonResponse.setMessage("Video Address is not valid");
//			print("Add failed!");
//		}
		return mCommonResponse;
	}
	
	@GET
	@Path("/allEffects")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public ArrayList<EffectInfo> allEffects(@QueryParam("id") int id) {
		List<EffectInfo> res = sm.getEffectVideoList(id);
		for(EffectInfo e : res) {
			System.out.println(e.getCamId()+" : "+e.getEffectType());
		}
		
		return (ArrayList<EffectInfo>) sm.getEffectVideoList(id);
	}

	@GET
	@Path("/play")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public CommonResponse playCamera(@QueryParam("addr") String videoAddr) {
		
		CommonResponse mCommonResponse = new CommonResponse();
		if(!videoMap.containsKey(videoAddr)) {
			TCPClient mTCPClient = new TCPClient();
			String resultMsg = mTCPClient.sendMsg("start," + videoAddr, serverIp, msgPort);
			
			if(resultMsg.startsWith("Succeed")) {
				String[] resultMsgs = resultMsg.split(",");
				String originRTMPAddr = resultMsgs[1];
				mCommonResponse.setMessage(originRTMPAddr);
				mCommonResponse.setResult(true);
				List<String> rtmpAddrs = new ArrayList<String>();
				rtmpAddrs.add(originRTMPAddr);
				videoMap.put(videoAddr, rtmpAddrs);
			}
			else {
				mCommonResponse.setMessage(resultMsg);
				mCommonResponse.setResult(false);
			}
		}
		else {
			List<String> rtmpAddrs = videoMap.get(videoAddr);
			String allRTMPAddrs = "";
			for(String rtmpAddr : rtmpAddrs) {
				allRTMPAddrs += rtmpAddr + ",";
			}
			allRTMPAddrs = allRTMPAddrs.substring(0, allRTMPAddrs.length()-1);
			mCommonResponse.setMessage(allRTMPAddrs);
			mCommonResponse.setResult(true);
		}

		return mCommonResponse;
	}
	
	@GET
	@Path("/startEffect")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public CommonResponse effect(@QueryParam("effect") String effect, @QueryParam("addr") String videoAddr) {
		CommonResponse mCommonResponse = new CommonResponse();
		if(!effectMap.containsKey(videoAddr) || !effectMap.get(videoAddr).contains(effect)) {
			TCPClient mTCPClient = new TCPClient();
			String resultMsg = mTCPClient.sendMsg("startEffect," + videoAddr + "," + effect, serverIp, msgPort);
			if(resultMsg.startsWith("Succeed")) {
				String[] resultMsgs = resultMsg.split(",");
				String rtmpAddr = resultMsgs[1];
				mCommonResponse.setMessage(rtmpAddr);
				mCommonResponse.setResult(true);
				
				List<String> effects = effectMap.get(videoAddr);
				if(effects == null) {
					effects = new ArrayList<String>();
				}
				effects.add(effect);

				List<String> rtmpAddrs = videoMap.get(videoAddr);
				rtmpAddrs.add(rtmpAddr);
				
				return mCommonResponse;
			}
			else {
				mCommonResponse.setMessage(resultMsg);
			}
		}
		mCommonResponse.setMessage("Effect already exist");
		mCommonResponse.setResult(false);
		
		return mCommonResponse;
	}
	
	@GET
	@Path("/close")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public CommonResponse close(@QueryParam("addr") String videoAddr) {
		CommonResponse mCommonResponse = new CommonResponse();
		if(videoMap.containsKey(videoAddr)) {
			TCPClient mTCPClient = new TCPClient();
			String resultMsg = mTCPClient.sendMsg("end," + videoAddr, serverIp, msgPort);
			if(!resultMsg.contains("Error")) {
				videoMap.remove(videoAddr);
				if(effectMap.containsKey(videoAddr)) {
					effectMap.remove(videoAddr);
				}
				mCommonResponse.setMessage(resultMsg);
				mCommonResponse.setResult(true);
				return mCommonResponse;
			}
			else {
				mCommonResponse.setMessage(resultMsg);
			}
		}
		mCommonResponse.setMessage("Error, failed to close");
		mCommonResponse.setResult(false);
		
		return mCommonResponse;
	}

//	@POST
//	@Path("/play")
//	@Produces(MediaType.APPLICATION_JSON)
//	@Consumes(MediaType.APPLICATION_JSON)
//	public CommonResponse playCamera(CameraInfo cam) {
//
//		String topic = cam.getCameraTopic();
//
//		// // If topic not equals to nowTopic, kill the nowTpic if it isn't
//		// null.
//		if (!topic.equals(nowTopic)) {
//			//
//			// // Kill the nowTpic
//			// if(nowTopic!=null) {
//			// String killCommand = "storm kill kafka-raw-"+ nowTopic + " -w 1";
//			//
//			// Process killProcess=null;
//			// try {
//			// killProcess = Runtime.getRuntime().exec(killCommand);
//			// } catch (IOException e1) {
//			// // TODO Auto-generated catch block
//			// e1.printStackTrace();
//			// }
//			// try {
//			// killProcess.waitFor();
//			// } catch (InterruptedException e) {
//			// // TODO Auto-generated catch block
//			// e.printStackTrace();
//			// }
//			// System.out.println("kafka-raw-"+ nowTopic);
//			// }
//
//			String command = "storm jar /home/jliu/workspace/stormcv/target/stormcv-0.8-jar-with-dependencies.jar nl.tno.stormcv.topology.KafkaReadTopology -t "
//					+ topic + " -cl";
//
//			Process addUrlProcess = null;
//			try {
//				addUrlProcess = Runtime.getRuntime().exec(command);
//				System.out.println("Submitt: " + topic);
//
//				final InputStream is = addUrlProcess.getInputStream();
//				new Thread(new Runnable() {
//					public void run() {
//						BufferedReader br = new BufferedReader(new InputStreamReader(is));
//						StringBuilder buf = new StringBuilder();
//						String line = null;
//						try {
//							while ((line = br.readLine()) != null) {
//								buf.append(line + "\n");
//							}
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//						System.out.println("output:" + buf);
//						try {
//							is.close();
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
//				}).start();
//				try {
//					addUrlProcess.waitFor();
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					System.out.println("addUrlProcess exception!");
//					e.printStackTrace();
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				System.out.println("play camera failed!");
//				e.printStackTrace();
//			}
//			System.out.println("Finish Submitt: " + topic);
//
//			nowTopic = topic;
//
//		}
//
//		return cam;
//	}
//
//	@POST
//	@Path("/process")
//	@Produces(MediaType.APPLICATION_JSON)
//	@Consumes(MediaType.APPLICATION_JSON)
//	public CameraInfo processCamera(CameraInfo cam) {
//		String topic = cam.getCameraTopic();
//		String ops = cam.getCameraAddr();
//
//		String command = "storm jar /home/jliu/workspace/stormcv/target/stormcv-0.8-jar-with-dependencies.jar nl.tno.stormcv.topology.KafkaTopologyWithOpts -t "
//				+ topic + " -cl -op " + ops;
//
//		Process proProcess = null;
//		try {
//			proProcess = Runtime.getRuntime().exec(command);
//			System.out.println("Submitt: " + topic);
//
//			final InputStream is = proProcess.getInputStream();
//			new Thread(new Runnable() {
//				public void run() {
//					BufferedReader br = new BufferedReader(new InputStreamReader(is));
//					StringBuilder buf = new StringBuilder();
//					String line = null;
//					try {
//						while ((line = br.readLine()) != null) {
//							buf.append(line + "\n");
//						}
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//					System.out.println("output:" + buf);
//					try {
//						is.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			}).start();
//
//			try {
//				proProcess.waitFor();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				System.out.println("proProcess exception!");
//				e.printStackTrace();
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			System.out.println("process camera failed!");
//			e.printStackTrace();
//		}
//		System.out.println("Finish Submitt: " + topic);
//
//		return cam;
//	}
}
