package edu.fudan.lwang.obj;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CommonResponse {
	
	private boolean result;
	public String message;
	
	public void setResult(boolean result) {
		this.result = result;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	@XmlElement(name = "result")
	public boolean getResult() {
		return this.result;
	}
	
	@XmlElement(name = "message")
	public String getMeassage() {
		return this.message;
	}
}
