package edu.fudan.lwang.obj;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RawRtmpInfo {
	private int camId;
	private int playerId;
	private String rtmpUrl;
	
	public RawRtmpInfo(int camId, int playerId, String rtmpUrl) {
		this.camId = camId;
		this.playerId = playerId;
		this.rtmpUrl = rtmpUrl;
	}
	
	public RawRtmpInfo() {};
	
	public void setCamId(int camId) {
		this.camId = camId;
	}
	
	@XmlElement(name = "camId")
	public int getCamId() {
		return this.camId;
	}
	
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	
	@XmlElement(name = "playerId")
	public int getPlayerId() {
		return this.playerId;
	}
	
	public void setRtmpUrl(String rtmpUrl) {
		this.rtmpUrl = rtmpUrl;
	}
	
	@XmlElement(name = "rtmpUrl")
	public String getRtmpUrl() {
		return this.rtmpUrl;
	}
}
