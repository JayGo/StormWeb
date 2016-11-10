package edu.fudan.lwang.obj;

import java.util.HashMap;

public class EffectMessage extends BaseMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String effectType;
	private HashMap<String, Double> parameters;
	
	public EffectMessage(String msg, String addr, String effectType, HashMap<String, Double> parameters) {
		super(msg, addr);
		this.effectType = effectType;
		this.parameters = parameters;
	}
	
	public String getEffectType() {
		return effectType;
	}
	
	public void setEffectType(String effectType) {
		this.effectType = effectType;
	}
	
	public HashMap<String, Double> getParameters() {
		return parameters;
	}
	
	public void setParameters(HashMap<String, Double> parameters) {
		this.parameters = parameters;
	}

}
