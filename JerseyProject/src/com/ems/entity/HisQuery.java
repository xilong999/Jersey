package com.ems.entity;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HisQuery implements Serializable{

	private static final long serialVersionUID = 4864469067025002598L;
	
	private int pageNumber;
	private int pageSize;
	private String buildingValue;
	private String typeValue;
	private String deviceId;
	private String deviceValue;
	private String startTime;
	private String endTime;

	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public String getBuildingValue() {
		return buildingValue;
	}
	public void setBuildingValue(String buildingValue) {
		this.buildingValue = buildingValue;
	}
	public String getTypeValue() {
		return typeValue;
	}
	public void setTypeValue(String typeValue) {
		this.typeValue = typeValue;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getDeviceValue() {
		return deviceValue;
	}
	public void setDeviceValue(String deviceValue) {
		this.deviceValue = deviceValue;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public HisQuery(int pageNumber, int pageSize, String buildingValue, String typeValue, String deviceId, String deviceValue,
			String startTime, String endTime) {
		super();
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.buildingValue = buildingValue;
		this.typeValue = typeValue;
		this.deviceId = deviceId;
		this.deviceValue = deviceValue;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	public HisQuery() {
		super();
	}
	@Override
	public String toString() {
		return "HisQuery [pageNumber=" + pageNumber + ", pageSize=" + pageSize + ", buildingValue=" + buildingValue + ", typeValue=" + typeValue
				+ ", deviceId=" + deviceId + ", deviceValue=" + deviceValue + ", startTime=" + startTime + ", endTime="
				+ endTime + "]";
	}

}
