package com.example;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.fudan.lwang.obj.CameraInfo;
import edu.fudan.lwang.obj.EffectInfo;
import edu.fudan.lwang.obj.EffectType;
import edu.fudan.lwang.obj.RawRtmpInfo;

/**
 * Root resource (exposed at "myresource" path)
 */

public class SqlManager {

	private static SqlManager instance = null;

	private final String CAMERA_LIST_TABLE = "CAMERA_LIST";
	private final String RAW_RTMP_LIST_TABLE = "RAW_RTMP_LIST";
	private final String CAMERA_LIST_COLS = "(ID,URL)";
	private final String RAW_RTMP_LIST_COLS = "(CAM_ID,PLAYER_ID,RTMP_URL)";
	private final String GRAY_EFFECT_LIST_TABLE = "GRAY_EFFECT_LIST";
	private final String GRAY_EFFECT_LIST_COLS = "(CAM_ID,PLAYER_ID,RTMP_URL,R_WEIGHT,G_WEIGHT,B_WEIGHT)";
	private final String CANNY_EFFECT_LIST_TABLE = "CANNY_EFFECT_LIST";
	private final String CANNY_EFFECT_LIST_COLS = "(CAM_ID,PLAYER_ID,RTMP_URL,L_THRESHOLD,H_THRESHOLD)";
	private final String[] EFFECT_TABLE_SET = { GRAY_EFFECT_LIST_TABLE, CANNY_EFFECT_LIST_TABLE };

	private Connection connection = null;

	private SqlManager() {

		String dbPath = FileUtil.getProjectPath() + "/db/";
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath + "CameraAddr.db");
			connection.setAutoCommit(false);
		} catch (Exception e) {
			System.out.println("Class not found!");
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		System.out.println("Opened database successfully");
	}

	public static synchronized SqlManager getInstance() {
		if (instance == null) {
			instance = new SqlManager();
		}
		return instance;
	}

	// Get the left bar's camera list.
	public synchronized List<CameraInfo> getCameraList() {
		Statement stmt = null;
		ResultSet rs = null;
		List<CameraInfo> result = new ArrayList<CameraInfo>();

		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM " + CAMERA_LIST_TABLE + ";");

			while (rs.next()) {
				int id = rs.getInt("ID");
				String url = rs.getString("URL");
				result.add(new CameraInfo(id, url));
				// System.out.println(id + "||" + url);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	// Get all the effect videos in the mainbody's video box.
	public synchronized List<EffectInfo> getEffectVideoList(int camId) {
		List<EffectInfo> result = new ArrayList<>();
		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = connection.createStatement();

			for (String table : EFFECT_TABLE_SET) {
				rs = stmt.executeQuery("SELECT * FROM " + table + " WHERE CAM_ID=" + camId + ";");
				while (rs.next()) {
					camId = rs.getInt("CAM_ID");
					int playerId = rs.getInt("PLAYER_ID");
					String rtmpUrl = rs.getString("RTMP_URL");
					HashMap<String, Double> parameters = new HashMap<>();
					String effect = "";
					switch (table) {
					case GRAY_EFFECT_LIST_TABLE: {
						parameters.put("r", rs.getDouble("R_WEIGHT"));
						parameters.put("g", rs.getDouble("G_WEIGHT"));
						parameters.put("b", rs.getDouble("B_WEIGHT"));
						effect = EffectType.GRAY_EFFECT;
						break;
					}
					case CANNY_EFFECT_LIST_TABLE: {
						parameters.put("l_th", rs.getDouble("L_THRESHOLD"));
						parameters.put("h_th", rs.getDouble("H_THRESHOLD"));
						effect = EffectType.CANNY_EFFECT;
						break;
					}
					}

					result.add(new EffectInfo(camId, playerId, rtmpUrl, effect, parameters));
				}
				rs.close();
				stmt.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	// Add effect
	public synchronized boolean addEffect(int camId, int playerId, String rtmpUrl, String effectType,
			HashMap<String, Double> parameters) {
		Statement stmt = null;
		String table = "";
		String cols = "";
		String paraStr = camId + ", " + playerId + ", '" + rtmpUrl + "', ";

		// if(effectType.equals(EffectType.GRAY_EFFECT)) {
		// table = GRAY_EFFECT_LIST_TABLE;
		// cols = GRAY_EFFECT_LIST_COLS;
		// paraStr += parameters.get("r") + ", " + parameters.get("g") + ", " +
		// parameters.get("b");
		// } else if(effectType.equals(EffectType.CANNY_EFFECT)) {
		// table = CANNY_EFFECT_LIST_TABLE;
		// cols = CANNY_EFFECT_LIST_COLS;
		// paraStr += parameters.get("l_th") + ", " + parameters.get("h_th");
		// }

		switch (effectType) {
		case EffectType.GRAY_EFFECT: {
			table = GRAY_EFFECT_LIST_TABLE;
			cols = GRAY_EFFECT_LIST_COLS;
			paraStr += parameters.get("r") + ", " + parameters.get("g") + ", " + parameters.get("b");
			break;
		}
		case EffectType.CANNY_EFFECT: {
			table = CANNY_EFFECT_LIST_TABLE;
			cols = CANNY_EFFECT_LIST_COLS;
			paraStr += parameters.get("l_th") + ", " + parameters.get("h_th");
			break;
		}
		default:
			break;
		}

		String sql = "INSERT INTO " + table + " " + cols + " " + "VALUES (" + paraStr + ");";

		// System.out.println("add effect sql is: " + sql);
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate(sql);
			connection.commit();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	// Add source camera(only rtsp)
	public synchronized boolean addCamera(int camId, String url) {
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			String sql = "INSERT INTO " + CAMERA_LIST_TABLE + " " + CAMERA_LIST_COLS + " " + "VALUES (" + camId + ", '"
					+ url + "');";
			// System.out.println("insert sql is:" + sql);
			stmt.executeUpdate(sql);
			connection.commit();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	public synchronized boolean recordRawRtmp(int camId, int playerId, String rtmpUrl) {
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			String sql = "INSERT INTO " + RAW_RTMP_LIST_TABLE + " " + RAW_RTMP_LIST_COLS + " " + "VALUES (" + camId
					+ "," + playerId + ", '" + rtmpUrl + "');";
			// System.out.println("insert raw rtmp sql is:" + sql);
			stmt.executeUpdate(sql);
			connection.commit();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public synchronized RawRtmpInfo getRawRtmpRecord(int camId) {
		Statement stmt = null;
		ResultSet rs = null;
		RawRtmpInfo res = new RawRtmpInfo();

		try {
			stmt = connection.createStatement();
			String sql = "SELECT * FROM " + RAW_RTMP_LIST_TABLE + " WHERE CAM_ID = " + camId + ";";

			// System.out.println("insert raw rtmp sql is:" + sql);
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				res.setCamId(rs.getInt("CAM_ID"));
				res.setPlayerId(rs.getInt("PLAYER_ID"));
				res.setRtmpUrl(rs.getString("RTMP_URL"));
			}
			// System.out.println(res.getCamId() + ":" + res.getPlayerId() + ":" + res.getRtmpUrl());
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	public synchronized boolean clearAll() {
		clearCameraList();
		clearEffectList();
		clearRawList();
		return true;
	}

	// Delete all items in EFFECT_TABLE_SET
	public boolean clearEffectList() {
		String sql = "";
		try {
			Statement stmt = connection.createStatement();
			for (String t : EFFECT_TABLE_SET) {
				sql = "DELETE FROM " + t + ";";
				stmt.executeUpdate(sql);
				connection.commit();
				// System.out.println(t + " has been cleared!");
			}
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	public boolean clearRawList() {
		String sql = "";
		try {
			Statement stmt = connection.createStatement();

			sql = "DELETE FROM " + RAW_RTMP_LIST_TABLE + ";";
			stmt.executeUpdate(sql);
			connection.commit();

			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return true;
	}

	// Delete all items in CAMERA_LIST_TABLE
	public boolean clearCameraList() {
		String sql = "DELETE FROM " + CAMERA_LIST_TABLE + ";";
		Statement stmt = null;

		try {
			stmt = connection.createStatement();
			stmt.executeUpdate(sql);
			connection.commit();
			stmt.close();
			// System.out.println(CAMERA_LIST_TABLE + " has been cleared!");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public static void main(String args[]) {
		SqlManager sm = SqlManager.getInstance();
		sm.clearAll();
		sm.addCamera(0, "rtsp://0");
		sm.recordRawRtmp(0, 0, "rtmp://0");
		sm.addCamera(1, "rtsp://1");
		sm.recordRawRtmp(1, 0, "rtmp://1");

		HashMap<String, Double> map0 = new HashMap<>();
		map0.put("r", 0.33);
		map0.put("g", 0.34);
		map0.put("b", 0.33);

		HashMap<String, Double> map1 = new HashMap<>();
		map1.put("l_th", 0.33);
		map1.put("h_th", 0.66);

		sm.addEffect(0, 1, "rtmp://0-1", "gray", map0);
		sm.addEffect(0, 2, "rtmp://0-2", "canny_edge", map1);

		sm.addEffect(1, 1, "rtmp://1-1", "gray", map0);
		sm.addEffect(1, 2, "rtmp://1-2", "canny_edge", map1);

		System.out.println("All cameras:");
		List<CameraInfo> cameras = sm.getCameraList();
		for (CameraInfo c : cameras) {
			System.out.println(c.getCameraId() + " : " + c.getCameraAddr());
		}

		System.out.println("Effects list on camera 1:");
		List<EffectInfo> effects1 = sm.getEffectVideoList(1);
		for (EffectInfo e : effects1) {
			System.out.println(
					e.getCamId() + " : " + e.getPlayerId() + " : " + e.getRtmpUrl() + " : " + e.getEffectType());
		}
		sm.clearAll();
		// sm.addCamera(4, "rtmp://255.255.253.1");
	}

}