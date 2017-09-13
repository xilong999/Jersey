package com.ems.serviceImpl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ems.dao.HistoricalConsumptionDao;
import com.ems.entity.EnergyType;
import com.ems.entity.HisQuery;
import com.ems.entity.HisRising;
import com.ems.entity.HistoricalAmount;
import com.ems.entity.MapHistorical;
import com.ems.entity.RisingLevels;
import com.ems.entity.SpaceMessage;
import com.ems.service.HistoricalConsumptionService;


@Service("historicalConsumptionService")
public class HistoricalConsumptionServiceImpl implements HistoricalConsumptionService {
	@Autowired
	HistoricalConsumptionDao historicalConsumptionDao;
	@Override
	public List<SpaceMessage> getArea() {
		// TODO Auto-generated method stub
		return historicalConsumptionDao.getArea();
	}

	@Override
	public List<SpaceMessage> getBuild(String areaId) {
		// TODO Auto-generated method stub
		return historicalConsumptionDao.getBuild(areaId);
	}

	@Override
	public List<EnergyType> getAllEnergyType(String spaceId) {
		// TODO Auto-generated method stub
		return historicalConsumptionDao.getAllEnergyType(spaceId);
	}

	@Override
	public List<EnergyType> getDevice(String spaceId, String energyType) {
		// TODO Auto-generated method stub
		return historicalConsumptionDao.getDevice(spaceId, energyType);
	}
	


	@Override
	public String getHistoricalConsumptions(HisQuery hisQuery) {
		// TODO Auto-generated method stub
		return historicalConsumptionDao.getHistoricalConsumptions(hisQuery);
	}

	@Override
	public List<HistoricalAmount> getHistoricalConsumption(String area, String build, String energyType, String tagId,
			String deviceName, String startTime, String endTime, int page, int pageSize) {
		// TODO Auto-generated method stub
		return historicalConsumptionDao.getHistoricalConsumption(area, build, energyType, tagId, deviceName, startTime, endTime, page, pageSize);
	}

	@Override
	public RisingLevels getRisingLevels(HisRising hisRising) {
		// TODO Auto-generated method stub
		return historicalConsumptionDao.getRisingLevels(hisRising);
	}
}
