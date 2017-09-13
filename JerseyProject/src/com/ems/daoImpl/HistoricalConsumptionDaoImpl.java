package com.ems.daoImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.ems.dao.HistoricalConsumptionDao;
import com.ems.entity.EnergyType;
import com.ems.entity.HisQuery;
import com.ems.entity.HisRising;
import com.ems.entity.HisValue;
import com.ems.entity.HistoricalAmount;
import com.ems.entity.Pagination;
import com.ems.entity.RisingLevels;
import com.ems.entity.SpaceMessage;
import com.ems.util.MongoDBUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Repository("historicalConsumptionDao")
public class HistoricalConsumptionDaoImpl implements HistoricalConsumptionDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<SpaceMessage> getArea() {
		// TODO Auto-generated method stub
		String sql = "select spaceId,spaceName from equipment_space_parent where space_device_Type='空间' and parentSpaceId='1'";

		List<SpaceMessage> spaceList = jdbcTemplate.query(sql, new RowMapper<SpaceMessage>() {
			public SpaceMessage mapRow(java.sql.ResultSet rs, int rowNum) throws SQLException {
				SpaceMessage sm = new SpaceMessage();
				sm.setSpaceId(rs.getString("spaceId"));
				sm.setSpaceName(rs.getString("spaceName"));
				return sm;
			}
		});
		System.out.println("spaceList.size():" + spaceList.size());
		return spaceList;
	}

	@Override
	public List<SpaceMessage> getBuild(String areaId) {
		// TODO Auto-generated method stub
		String sql = "select spaceId,spaceName from equipment_space_parent where space_device_Type='空间' and parentSpaceId='"
				+ areaId + "'";

		List<SpaceMessage> spaceList = jdbcTemplate.query(sql, new RowMapper<SpaceMessage>() {
			public SpaceMessage mapRow(java.sql.ResultSet rs, int rowNum) throws SQLException {
				SpaceMessage sm = new SpaceMessage();
				sm.setSpaceId(rs.getString("spaceId"));
				sm.setSpaceName(rs.getString("spaceName"));
				return sm;
			}
		});
		System.out.println("spaceList.size():" + spaceList.size());

		return spaceList;
	}

	@Override
	public List<EnergyType> getAllEnergyType(final String spaceId) {
		// TODO Auto-generated method stub
		List<String> listspaceId = new ArrayList<String>();
		List<EnergyType> elecs = new ArrayList<EnergyType>();
		List<Map<String, Object>> elecList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> tagIds = new ArrayList<Map<String, Object>>();
		String sql1 = "select spaceId from equipment_space_parent where space_device_Type = '空间' and parentSpaceId=?";

		listspaceId = jdbcTemplate.queryForList(sql1, new Object[] { spaceId }, String.class);

		System.out.println("listspaceId.size==" + listspaceId.size());

		if (listspaceId.size() == 0) {
			String sql = "select b.tagId,c.energyConsumptionChildType from (select deviceId,parentSpaceId from equipment_space_parent where space_device_Type = '设备' and parentSpaceId='"
					+ spaceId
					+ "') a,device_propertie_tag b,device_propertie c  where a.deviceId = b.deviceId and b.devicePropertieID=c.devicePropertieID ";
			System.out.println(sql);

			elecList = jdbcTemplate.queryForList(sql);

			System.out.println("elecList.size():::" + elecList.size());
			System.out.println("elecList===" + elecList.toString());
		}
		if (listspaceId.size() > 0) {
			listspaceId.add(spaceId);
			System.out.println("listTagId.size==" + listspaceId.size());

			for (int i = 0; i < listspaceId.size(); i++) {
				String sql = "select b.tagId,c.energyConsumptionChildType from (select deviceId,parentSpaceId from equipment_space_parent where space_device_Type = '设备' and parentSpaceId='"
						+ listspaceId.get(i)
						+ "') a,device_propertie_tag b,device_propertie c  where a.deviceId = b.deviceId and b.devicePropertieID=c.devicePropertieID";

				tagIds = jdbcTemplate.queryForList(sql);
				System.out.println("tagIds.size():::" + tagIds.size());
				System.out.println("tagIds===" + tagIds.toString());

				elecList.addAll(tagIds);
				System.out.println("elecList.size():::" + elecList.size());
				System.out.println("elecList===" + elecList.toString());
			}
		}
		System.out.println("elecList.size():::" + elecList.size());

		for (int i = 0; i < elecList.size(); i++) {
			elecs.add(new EnergyType(elecList.get(i).get("tagId").toString(),
					elecList.get(i).get("energyConsumptionChildType").toString()));
		}

		for (int i = 0; i < elecs.size(); i++) {
			for (int j = elecs.size() - 1; j > i; j--) {
				if (elecs.get(i).getEnergyType().equals(elecs.get(j).getEnergyType())) {
					elecs.get(i).setId(elecs.get(i).getId() + "," + elecs.get(j).getId());
					elecs.remove(j);
				}
			}
		}

		return elecs;
	}

	@Override
	public List<EnergyType> getDevice(String spaceId, String energyType) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> elecs = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> elecList = new ArrayList<Map<String, Object>>();
		List<EnergyType> lists = new ArrayList<EnergyType>();
		List<String> listspaceId = new ArrayList<String>();

		String sql1 = "select spaceId from equipment_space_parent where space_device_Type = '空间' and parentSpaceId=?";

		listspaceId = jdbcTemplate.queryForList(sql1, new Object[] { spaceId }, String.class);

		System.out.println("listTagId.size==" + listspaceId.size());

		if (!energyType.equals("空调用电")) {
			String sql = "select b.tagId,d.deviceName from (select deviceId,parentSpaceId from equipment_space_parent where space_device_Type = '设备' and parentSpaceId='"
					+ spaceId
					+ "')a,device_propertie_tag b,device_propertie c ,device d where a.deviceId = b.deviceId and a.deviceId = d.deviceId and b.devicePropertieID=c.devicePropertieID and c.energyConsumptionChildType = '"
					+ energyType + "'";

			System.out.println(sql);

			elecs = jdbcTemplate.queryForList(sql);

			System.out.println("elecs.size():::" + elecs.size());
			System.out.println("elecs===" + elecs.toString());

		}

		if (listspaceId.size() > 0 && energyType.equals("空调用电")) {
			for (int i = 0; i < listspaceId.size(); i++) {
				String sql = "select b.tagId,d.deviceName from (select deviceId,parentSpaceId from equipment_space_parent where space_device_Type = '设备' and parentSpaceId='"
						+ listspaceId.get(i)
						+ "')a,device_propertie_tag b,device_propertie c ,device d where a.deviceId = b.deviceId and a.deviceId = d.deviceId and b.devicePropertieID=c.devicePropertieID and  c.energyConsumptionChildType = '"
						+ energyType + "'";

				System.out.println(sql);

				elecList = jdbcTemplate.queryForList(sql);
				System.out.println("elecList.size():::" + elecList.size());
				System.out.println("elecList===" + elecList.toString());

				elecs.addAll(elecList);
				System.out.println("elecs.size():::" + elecs.size());
				System.out.println("elecs===" + elecs.toString());
			}
		}

		for (int i = 0; i < elecs.size(); i++) {
			String s1 = elecs.get(i).get("tagId").toString();
			String s2 = elecs.get(i).get("deviceName").toString();
			System.out.println("s1=========" + s1);
			System.out.println("s2=========" + s2);

			lists.add(new EnergyType(s1, s2));
		}
		return lists;
	}

	@Override
	public List<HistoricalAmount> getHistoricalConsumption(String area, String build, String energyType, String tagId,
			String deviceName, String startTime, String endTime, int pageNumber, int pageSize) {
		// TODO Auto-generated method stub

		List<HisValue> list = MongoDBUtil.getHistoricalConsumption(tagId, startTime, endTime, pageNumber, pageSize);

		List<HistoricalAmount> historicalList = new ArrayList<HistoricalAmount>();
		for (int i = 0; i < list.size(); i++) {
			HistoricalAmount ha = new HistoricalAmount();
			ha.setBuildName(build);
			ha.setDeviceName(deviceName);
			ha.setEnergyType(energyType);
			ha.setTime(list.get(i).getNameKey());
			ha.setAmount(list.get(i).getNameValue());
			historicalList.add(ha);
		}
		return historicalList;
	}

	@Override
	public String getHistoricalConsumptions(HisQuery hisQuery) {
		// TODO Auto-generated method stub

		String build = hisQuery.getBuildingValue();
		String energyType = hisQuery.getTypeValue();
		int pageNumber = hisQuery.getPageNumber();
		int pageSize = hisQuery.getPageSize();
		String tagId = hisQuery.getDeviceId();
		String deviceName = hisQuery.getDeviceValue();
		String startTime = hisQuery.getStartTime();
		String endTime = hisQuery.getEndTime();

		System.out.println("build==" + build);
		System.out.println(energyType);
		System.out.println("pageNumber===" + pageNumber);
		System.out.println("pageSize===" + pageSize);
		System.out.println(tagId);
		System.out.println(deviceName);
		System.out.println(startTime);
		System.out.println(endTime);
		Map<String, String> map = new HashMap<String, String>();
		if (!hisQuery.getTypeValue().equals("空调用电")) {
			System.out.println("tagId==" + tagId);
			List<HisValue> list = MongoDBUtil.getHistoricalConsumption(tagId, startTime, endTime, pageNumber, pageSize);
			System.out.println("list.size====" + list.size());
			if (list != null) {
				List<HistoricalAmount> historicalList = new ArrayList<HistoricalAmount>();

				for (int i = 0; i < list.size(); i++) {
					HistoricalAmount ha = new HistoricalAmount();
					ha.setBuildName(build);
					ha.setDeviceName(deviceName);
					ha.setEnergyType(energyType);
					ha.setTime(list.get(i).getNameKey());
					ha.setAmount(list.get(i).getNameValue());

					historicalList.add(ha);
				}

				System.out.println("historicalList====" + historicalList.size());
				map.put("pageNumber", String.valueOf(pageNumber));
				map.put("rows", JSONArray.fromObject(historicalList).toString());
				map.put("total", String.valueOf(list.size()));

				System.out.println("map====" + map.toString());

				System.out.println(JSONObject.fromObject(map).toString());
				return JSONObject.fromObject(map).toString();
			}
		} else {
			System.out.println("tagId==" + tagId);
			String a[] = tagId.split(",");
			System.out.println(a[0]);
			System.out.println(a[1]);

			List<HistoricalAmount> historicalList = new ArrayList<>();

			List<HisValue> list = MongoDBUtil.getHistoricalConsumption(a[0], startTime, endTime, pageNumber, pageSize);
			List<HisValue> list1 = MongoDBUtil.getHistoricalConsumption(a[1], startTime, endTime, pageNumber, pageSize);
			System.out.println("list.size====" + list.size());
			System.out.println("list1.size====" + list1.size());

			if (list != null && list1 != null) {
				for (int i = 0; i < list.size(); i++) {
					HistoricalAmount ha = new HistoricalAmount();
					ha.setBuildName(build);
					ha.setDeviceName(deviceName);
					ha.setEnergyType(energyType);
					ha.setTime(list.get(i).getNameKey());
					ha.setAmount(list.get(i).getNameValue() + list1.get(i).getNameValue());
					historicalList.add(ha);
				}

				System.out.println("historicalList====" + historicalList.size());

				
				map.put("pageNumber", String.valueOf(pageNumber));
				map.put("rows", JSONArray.fromObject(historicalList).toString());
				map.put("total", String.valueOf(list.size()));

				System.out.println("map====" + map.toString());
				return JSONObject.fromObject(map).toString();
			}

		}
		return JSONObject.fromObject(map).toString();
	}

	@Override
	public RisingLevels getRisingLevels(HisRising hisRising) {
		// TODO Auto-generated method stub
		String area = hisRising.getArea();
		String build = hisRising.getBuilding();
		String energyType = hisRising.getEnergyType();
		String startTime = hisRising.getStartTime();
		String endTime = hisRising.getEndTime();

		List<String> listTagId = new ArrayList<String>();
		String sql = "select a.tagId from device_propertie_tag as a inner join device_propertie as c on a.devicePropertieID=c.devicePropertieID where c.energyConsumptionType=?";

		listTagId = jdbcTemplate.queryForList(sql, new Object[] { energyType }, String.class);
		System.out.println("listTagId===" + listTagId.toString());

		// String timeStart = MongoDBUtil.getTimeStart(new
		// SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		String lastOneMonth = MongoDBUtil.getLastOneMonth(startTime, 1);
		System.out.println("====================分割线1================================");

		String lastTwoMonth = MongoDBUtil.getLastOneMonth(startTime, 2);
		System.out.println("====================分割线2================================");

		String SlastOneMonthTotal = MongoDBUtil.getLastOneMonthTotalFormStatstics("statistics", lastOneMonth,
				listTagId);
		String SlastTwoMonthTotal = MongoDBUtil.getLastOneMonthTotalFormStatstics("statistics", lastTwoMonth,
				listTagId);

		Double DlastOneMonthTotal = Double.parseDouble(SlastOneMonthTotal);
		Double DlastTwoMonthTotal = Double.parseDouble(SlastTwoMonthTotal);
		System.out.println("DlastOneMonthTotal ======" + DlastOneMonthTotal);
		System.out.println("DlastTwoMonthTotal ======" + DlastTwoMonthTotal);
		Double risingLevelsOfAMonthAgo = ((DlastOneMonthTotal - DlastTwoMonthTotal) / DlastTwoMonthTotal);
		String SrisingLevelsOfAMonthAgo = String.valueOf(risingLevelsOfAMonthAgo);
		System.out.println("risingLevelsOfAMonthAgo ======" + risingLevelsOfAMonthAgo);
		System.out.println("SrisingLevelsOfAMonthAgo ======" + SrisingLevelsOfAMonthAgo);
		BigDecimal bd = new BigDecimal(SrisingLevelsOfAMonthAgo);// 建议使用String参数
		// BigDecimal bd_half_even = bd.setScale(2, RoundingMode.HALF_EVEN);
		BigDecimal bd_half_up = bd.setScale(2, RoundingMode.HALF_UP);

		System.out.println("bd_half_up========" + bd_half_up);

		System.out.println("===========同比==========================");
		// 同比

		String lastOneYear = MongoDBUtil.getLastOneYear(startTime, 1);
		String lastOneMonth2 = MongoDBUtil.getLastOneMonth(lastOneYear, 1);
		System.out.println("同比的lastOneMonth2=======" + lastOneMonth2);

		String SlastOneYearTotal = MongoDBUtil.getLastOneMonthTotalFormStatstics("statistics", lastOneMonth2,
				listTagId);
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
