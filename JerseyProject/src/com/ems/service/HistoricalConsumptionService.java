package com.ems.service;

import java.util.List;
import java.util.Map;

import com.ems.entity.EnergyType;
import com.ems.entity.HisQuery;
import com.ems.entity.HisRising;
import com.ems.entity.HistoricalAmount;
import com.ems.entity.MapHistorical;
import com.ems.entity.RisingLevels;
import com.ems.entity.SpaceMessage;

public interface HistoricalConsumptionService {
	public List<SpaceMessage> getArea();
	public List<SpaceMessage> getBuild(String areaId);
	public List<EnergyType> getAllEnergyType(String spaceId);
	public List<EnergyType> getDevice(String spaceId,String energyType);
	public List<HistoricalAmount> getHistoricalConsumption(String area,String build,String energyType,String tagId,String deviceName,String startTime,String endTime,int page, int pageSize );
	public String getHistoricalConsumptions(HisQuery hisQuery);
	public RisingLevels getRisingLevels(HisRising hisRising);

}
