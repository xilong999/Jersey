package com.ems.entity;

import java.io.Serializable;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class MapHistorical implements Serializable{

	private static final long serialVersionUID = -7289227486919695979L;
	
	private Map<String, Object> map ;

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

	public MapHistorical(Map<String, Object> map) {
		super();
		this.map = map;
	}

	public MapHistorical() {
		super();
	}

	@Override
	public String toString() {
		return "MapHistorical [map=" + map + "]";
	}
}
