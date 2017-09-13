package com.ems.entity;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SpaceMessage implements Serializable {
	private static final long serialVersionUID = -1412430916706441199L;

	private String spaceId;
	private String spaceName;
	public String getSpaceId() {
		return spaceId;
	}
	public void setSpaceId(String spaceId) {
		this.spaceId = spaceId;
	}
	public String getSpaceName() {
		return spaceName;
	}
	public void setSpaceName(String spaceName) {
		this.spaceName = spaceName;
	}
	public SpaceMessage(String spaceId, String spaceName) {
		super();
		this.spaceId = spaceId;
		this.spaceName = spaceName;
	}
	public SpaceMessage() {
		super();
	}
	@Override
	public String toString() {
		return "SpaceMessage [spaceId=" + spaceId + ", spaceName=" + spaceName + "]";
	}
}
