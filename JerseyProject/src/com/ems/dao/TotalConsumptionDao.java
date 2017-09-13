package com.ems.dao;

import com.ems.entity.LastOneDayList;
import com.ems.entity.NowData;
import com.ems.entity.RisingLevels;

public interface TotalConsumptionDao {
	
	public LastOneDayList getElectricity(String type);
	public NowData getNowdata(String type);
	public RisingLevels risingLevels(String type);
}
