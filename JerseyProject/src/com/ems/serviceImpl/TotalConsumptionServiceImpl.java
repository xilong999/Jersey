package com.ems.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ems.dao.QueryBeforeDao;
import com.ems.dao.TotalConsumptionDao;
import com.ems.entity.LastOneDayList;
import com.ems.entity.NowData;
import com.ems.entity.RisingLevels;
import com.ems.service.TotalConsumptionService;
@Service("totalConsumptionService")
public class TotalConsumptionServiceImpl implements TotalConsumptionService {

	
	@Autowired
	TotalConsumptionDao totalConsumptionDao;

	@Override
	public LastOneDayList getElectricity(String type) {
		// TODO Auto-generated method stub
		return totalConsumptionDao.getElectricity(type);
	}

	@Override
	public NowData getNowdata(String type) {
		// TODO Auto-generated method stub
		return totalConsumptionDao.getNowdata(type);
	}

	@Override
	public RisingLevels risingLevels(String type) {
		// TODO Auto-generated method stub
		return totalConsumptionDao.risingLevels(type);
	}

}
