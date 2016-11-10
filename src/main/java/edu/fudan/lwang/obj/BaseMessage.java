package edu.fudan.lwang.obj;

import java.io.Serializable;

public class BaseMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String msg;
	private String addr;
	
	public BaseMessage(String msg, String addr) {
		this.msg = msg;
		this.addr = addr;
	}
	
	public String getMsg() {
		return msg;
	}
	
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public String getAddr() {
		return this.addr;
	}
	
	public void setAddr(String addr) {
		this.addr = addr;
	}

}
