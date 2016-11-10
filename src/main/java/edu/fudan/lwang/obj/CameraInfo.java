package edu.fudan.lwang.obj;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CameraInfo {
	private int id;
	private String addr;

	public CameraInfo() {
	}

	public CameraInfo(int id, String addr) {
		this.id = id;
		this.addr = addr;
	}
	
	public void setCameraId(int id) {
		this.id = id;
	}
	
	@XmlElement(name = "id")
	public int getCameraId() {
		return this.id;
	}

	public void setCameraAddr(String addr) {
		this.addr = addr;
	}

	@XmlElement(name = "addr")
	public String getCameraAddr() {
		return this.addr;
	}	
}
