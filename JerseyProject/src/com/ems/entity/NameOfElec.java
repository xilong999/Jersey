package com.ems.entity;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NameOfElec implements Serializable{
	private static final long serialVersionUID = 7346273392429411346L;
	private String nameKey;
	private String nameValue;
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
	public NameOfElec(String nameKey, String nameValue) {
		super();
		this.nameKey = nameKey;
		this.nameValue = nameValue;
	}
	public NameOfElec() {
		super();
	}
	@Override
	public String toString() {
		return "NameOfElec [nameKey=" + nameKey + ", nameValue=" + nameValue + "]";
	}
}
