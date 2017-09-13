package com.ems.entity;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class HisRising implements Serializable{

	private static final long serialVersionUID = 1248151907252075614L;
	
	private String area;
	private String building;
	private String energyType;
	private String startTime;
	private String endTime;
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getBuilding() {
		return building;
	}
	public void setBuilding(String building) {
		this.building = building;
	}
	public String getEnergyType() {
		return energyType;
	}
	public void setEnergyType(String energyType) {
		this.energyType = energyType;
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
	public HisRising(String area, String building, String energyType, String startTime, String endTime) {
		super();
		this.area = area;
		this.building = building;
		this.energyType = energyType;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	public HisRising() {
		super();
	}
	@Override
	public String toString() {
		return "HisRising [area=" + area + ", building=" + building + ", energyType=" + energyType + ", startTime="
				+ startTime + ", endTime=" + endTime + "]";
	}
	
}
