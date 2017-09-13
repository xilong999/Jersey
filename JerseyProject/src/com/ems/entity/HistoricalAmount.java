package com.ems.entity;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HistoricalAmount implements Serializable{

	private static final long serialVersionUID = 774341976116415773L;

	private String buildName;
	private String deviceName;
	private String time;
	private String amount;
	private String energyType;
	
	public HistoricalAmount(String buildName, String deviceName, String time, String amount, String energyType
			) {
		super();
		this.buildName = buildName;
		this.deviceName = deviceName;
		this.time = time;
		this.amount = amount;
		this.energyType = energyType;
		
	}
	public HistoricalAmount() {
		super();
	}
	@Override
	public String toString() {
		return "HistoricalAmount [buildName=" + buildName + ", deviceName=" + deviceName + ", time=" + time
				+ ", amount=" + amount + ", energyType=" + energyType + "]";
	}
	public String getBuildName() {
		return buildName;
	}
	public void setBuildName(String buildName) {
		this.buildName = buildName;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getEnergyType() {
		return energyType;
	}
	public void setEnergyType(String energyType) {
		this.energyType = energyType;
	}
}
