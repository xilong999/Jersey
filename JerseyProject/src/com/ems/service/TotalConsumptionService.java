package com.ems.service;

import com.ems.entity.LastOneDayList;
import com.ems.entity.NowData;
import com.ems.entity.RisingLevels;

public interface TotalConsumptionService {
	public NowData getNowdata(String type);
	public LastOneDayList getElectricity(String type);
	public RisingLevels risingLevels(String type);
}
