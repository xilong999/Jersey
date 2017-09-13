package com.ems.entity;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HisValue implements Serializable{
	private static final long serialVersionUID = 7346273392429411346L;
	private String nameKey;
	private String nameValue;
	private int total;
	
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public String getNameKey() {
		return nameKey;
	}
	public void setNameKey(String nameKey) {
		this.nameKey = nameKey;
	}
	public String getNameValue() {
		return nameValue;
	}
	public void setNameValue(String nameValue) {
		this.nameValue = nameValue;
	}
	public HisValue(String nameKey, String nameValue) {
		super();
		this.nameKey = nameKey;
		this.nameValue = nameValue;
	}
	public HisValue() {
		super();
	}
	@Override
	public String toString() {
		return "HisValue [nameKey=" + nameKey + ", nameValue=" + nameValue + ", total=" + total + "]";
	}
}
