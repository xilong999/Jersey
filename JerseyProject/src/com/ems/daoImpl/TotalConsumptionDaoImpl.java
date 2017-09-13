package com.ems.daoImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ems.dao.TotalConsumptionDao;
import com.ems.entity.LastOneDayList;
import com.ems.entity.NowData;
import com.ems.entity.RisingLevels;
import com.ems.util.MongoDBUtil;

@Repository("totalConsumptionDao")
public class TotalConsumptionDaoImpl implements TotalConsumptionDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public LastOneDayList getElectricity(String type) {
		// TODO Auto-generated method stub
		String timeStart = MongoDBUtil.getTimeStart(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		System.out.println("type==="+type);
		List<String> listTagId = new ArrayList<String>();
		
		String sql = "select a.tagId from device_propertie_tag as a inner join device_propertie as c on a.devicePropertieID=c.devicePropertieID where c.energyConsumptionType=?";
	
		listTagId = jdbcTemplate.queryForList(sql, new Object[] { type }, String.class);
		System.out.println("listTagId==="+listTagId.toString());
		
		LastOneDayList lastOneDayList = MongoDBUtil.getElectricityLastOneDayTotalFormStatstics("statistics", timeStart,listTagId);

		System.out.println(lastOneDayList.toString());
		return lastOneDayList;
	}



	@Override
	public NowData getNowdata(String type) {
		// TODO Auto-generated method stub
		String timeStart = MongoDBUtil.getTimeStart(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		System.out.println("type==="+type);
		List<String> listTagId = new ArrayList<String>();
		String sql = "select a.tagId from device_propertie_tag as a inner join device_propertie as c on a.devicePropertieID=c.devicePropertieID where c.energyConsumptionType=?";
		
		listTagId = jdbcTemplate.queryForList(sql, new Object[] { type }, String.class);
		System.out.println("listTagId==="+listTagId.toString());
		
		String timeLastHour = MongoDBUtil.getLastOneHour(timeStart, 1);
        System.out.println(timeLastHour);
		
		NowData nowData = MongoDBUtil.getElectricityNowTotalFormStatstics("statistics", timeLastHour,listTagId);
		
		System.out.println("nowData:::"+nowData.toString());
		return nowData;
	}

	@Override
	public RisingLevels risingLevels(String type) {
		// TODO Auto-generated method stub
		
		System.out.println("type==="+type);
		List<String> listTagId = new ArrayList<String>();
		String sql = "select a.tagId from device_propertie_tag as a inner join device_propertie as c on a.devicePropertieID=c.devicePropertieID where c.energyConsumptionType=?";
		
		listTagId = jdbcTemplate.queryForList(sql, new Object[] { type }, String.class);
		System.out.println("listTagId==="+listTagId.toString());
		
		String timeStart = MongoDBUtil.getTimeStart(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        String lastOneMonth = MongoDBUtil.getLastOneMonth(timeStart, 1);
        System.out.println("====================分割线1================================");

        String lastTwoMonth = MongoDBUtil.getLastOneMonth(timeStart, 2);
        System.out.println("====================分割线2================================");

        String SlastOneMonthTotal = MongoDBUtil.getLastOneMonthTotalFormStatstics("statistics", lastOneMonth,listTagId);
        String SlastTwoMonthTotal = MongoDBUtil.getLastOneMonthTotalFormStatstics("statistics", lastTwoMonth,listTagId);

        Double DlastOneMonthTotal = Double.parseDouble(SlastOneMonthTotal);
        Double DlastTwoMonthTotal = Double.parseDouble(SlastTwoMonthTotal);
        System.out.println("DlastOneMonthTotal ======"+ DlastOneMonthTotal);
        System.out.println("DlastTwoMonthTotal ======"+ DlastTwoMonthTotal);
        Double risingLevelsOfAMonthAgo = ((DlastOneMonthTotal - DlastTwoMonthTotal) / DlastTwoMonthTotal);
        String SrisingLevelsOfAMonthAgo = String.valueOf(risingLevelsOfAMonthAgo);
        System.out.println("risingLevelsOfAMonthAgo ======"+ risingLevelsOfAMonthAgo);
        System.out.println("SrisingLevelsOfAMonthAgo ======"+ SrisingLevelsOfAMonthAgo);
        BigDecimal bd = new BigDecimal(SrisingLevelsOfAMonthAgo);//建议使用String参数
        // BigDecimal bd_half_even = bd.setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal bd_half_up = bd.setScale(2,RoundingMode.HALF_UP);

        System.out.println("bd_half_up========"+bd_half_up);

        System.out.println("===========同比==========================");
        // 同比

        String lastOneYear = MongoDBUtil.getLastOneYear(timeStart, 1);
        String lastOneMonth2 = MongoDBUtil.getLastOneMonth(lastOneYear, 1);
        System.out.println("同比的lastOneMonth2======="+ lastOneMonth2);

        String SlastOneYearTotal = MongoDBUtil.getLastOneMonthTotalFormStatstics("statistics", lastOneMonth2,listTagId);
        Double DlastOneYearTotal = Double.parseDouble(SlastOneYearTotal);
        if(DlastOneYearTotal != 0){
			Double risingLevelsOfAYearAgo = ((DlastOneMonthTotal - DlastOneYearTotal) / DlastOneYearTotal);
			String DrisingLevelsOfAYearAgo = String.valueOf(risingLevelsOfAYearAgo);
			System.out.println("DrisingLevelsOfAYearAgo=========" + DrisingLevelsOfAYearAgo);

			BigDecimal bd2 = new BigDecimal(risingLevelsOfAYearAgo);// 建议使用String参数
			BigDecimal bd_half_up2 = bd2.setScale(2, RoundingMode.HALF_UP);
			System.out.println("bd_half_up2========" + bd_half_up2);

			RisingLevels risingLevels = new RisingLevels(DlastOneMonthTotal, bd_half_up2, bd_half_up);
			return risingLevels;
		}else{
			RisingLevels risingLevels = new RisingLevels(0.00,new BigDecimal("0.00"), new BigDecimal("0.00"));
			return risingLevels;
		}
	}

}
