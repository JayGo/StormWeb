package com.example;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CameraAddrHashCode {
	private String addrHashCode;
	
	public CameraAddrHashCode() {
		
	}
	
	public void setAddrHashCode(String addr) {
		this.addrHashCode = addr.hashCode()+"";
	}
	
	@XmlElement(name="addrHashCode")
	public String getAddrHashCode() {
		return this.addrHashCode;
	}
}
