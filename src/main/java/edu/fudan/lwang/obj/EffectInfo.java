package edu.fudan.lwang.obj;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EffectInfo {
	protected int camId;
	protected int playerId;
	protected String rtmpUrl;
	protected String effectTye;
	protected HashMap<String, Double> parameters;
	
	public EffectInfo(int camId, int playerId, String rtmpUrl, String effectTye, HashMap<String, Double> parameters) {
		this.camId = camId;
		this.playerId = playerId;
		this.rtmpUrl = rtmpUrl;
		this.effectTye = effectTye;
		this.parameters = parameters;
	}
	
	public EffectInfo() {
		
	}
	
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
	
	public void setEffectType(String effectTye) {
		this.effectTye = effectTye;
	}
	
	@XmlElement(name = "effectType")
	public String getEffectType() {
		return this.effectTye;
	}
	
	public void setParameters(HashMap<String, Double> parameters) {
		this.parameters = parameters;
	}
	
	@XmlElement(name = "parameters")
	public HashMap<String, Double> getParameters() {
		return this.parameters;
	}
	
}
