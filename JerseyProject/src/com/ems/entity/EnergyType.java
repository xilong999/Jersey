package com.ems.entity;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EnergyType implements Serializable{

	private static final long serialVersionUID = 8889912126388288194L;
	
	private String id;
	private String energyType;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEnergyType() {
		return energyType;
	}
	public void setEnergyType(String energyType) {
		this.energyType = energyType;
	}
	public EnergyType(String id, String energyType) {
		super();
		this.id = id;
		this.energyType = energyType;
	}
	public EnergyType() {
		super();
	}
	@Override
	public String toString() {
		return "EnergyType [id=" + id + ", energyType=" + energyType + "]";
	}
}
