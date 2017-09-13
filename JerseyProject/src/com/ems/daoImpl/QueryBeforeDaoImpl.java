package com.ems.daoImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ems.dao.QueryBeforeDao;
import com.ems.entity.AllElec;
import com.ems.entity.AllElecList;
import com.ems.util.MongoDBUtil;

@Repository("queryBeforeDao")
public class QueryBeforeDaoImpl implements QueryBeforeDao {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/*
	 * @Autowired private MongoTemplate mongoTemplate;
	 */

	@Override
	public List<AllElec> queryBeforeEnergyType(String id, String name) {
		// TODO Auto-generated method stub
		String sql;
		if(name.equals("电")){
			sql = "select b.tagId from (select deviceId,parentSpaceId from equipment_space_parent where space_device_Type = '设备' and parentSpaceId='"+id+"') a,device_propertie_tag b, device_propertie c where a.deviceId = b.deviceId and b.devicePropertieID=c.devicePropertieID and c.energyConsumptionChildType in ('"+"总用"+ name +"','空调用电')";
		}else{
			sql = "select b.tagId from (select deviceId,parentSpaceId from equipment_space_parent where space_device_Type = '设备' and parentSpaceId='"
					+ id
					+ "')a,device_propertie_tag b,device_propertie c  where a.deviceId = b.deviceId and b.devicePropertieID=c.devicePropertieID and c.energyConsumptionChildType = '"
					+ "总用" + name + "'";
		}
		System.out.println(sql);
		String tagId = (String) jdbcTemplate.queryForObject(sql, String.class);
		System.out.println("1111111111111111============" + tagId);

		String nowdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		System.out.println("date===" + nowdate);

		List<AllElec> value = new LinkedList<AllElec>();
		value = MongoDBUtil.getOneDocumentAllValue(tagId, MongoDBUtil.getTimeStart(nowdate), name);
		return value;
	}

	@Override
	public List<AllElecList> queryBeforeAllElecs(String id) {
		// TODO Auto-generated method stub

		List<Map<String, Object>> elecList = new ArrayList<Map<String, Object>>();
		List<AllElecList> lists = new ArrayList<AllElecList>();
		String sql = "select b.tagId,c.energyConsumptionChildType from (select deviceId,parentSpaceId from equipment_space_parent where space_device_Type = '设备' and parentSpaceId='"
				+ id
				+ "') a,device_propertie_tag b,device_propertie c  where a.deviceId = b.deviceId and b.devicePropertieID=c.devicePropertieID and c.energyConsumptionType = '电' ";
		System.out.println(sql);

		elecList = jdbcTemplate.queryForList(sql);

		System.out.println("elecList.size():::" + elecList.size());
		System.out.println("elecList===" + elecList.toString());

		for (int i = 0; i < elecList.size(); i++) {
			String s1 = elecList.get(i).get("tagId").toString();
			String s2 = elecList.get(i).get("energyConsumptionChildType").toString();
			System.out.println("s1=========" + s1);
			System.out.println("s2=========" + s2);

			AllElecList allelecList = queryBeforeOneElec(s1, s2);
			System.out.println("allelecList:::" + allelecList.toString());
			lists.add(allelecList);
		}
		System.out.println("lists==all " + lists.size());
		// 处理重复用电类型数据，将其累加
		for (int i = 0; i < lists.size(); i++) {
			for (int j = lists.size() - 1; j > i; j--) {
				if (lists.get(i).getAllElecs().get(0).getTypeOfElec()
						.equals(lists.get(j).getAllElecs().get(0).getTypeOfElec())) {
					System.out.println(lists.get(i).getAllElecs().get(0).getTypeOfElec()
							.equals(lists.get(j).getAllElecs().get(0).getTypeOfElec()));
					for (int k = 0; k < 20; k++) {
						lists.get(i).getAllElecs().get(k)
								.setValue((Double.parseDouble(lists.get(i).getAllElecs().get(k).getValue())
										+ Double.parseDouble(lists.get(j).getAllElecs().get(k).getValue())) + "");
					}
					lists.remove(j);
				}
			}
		}
		System.out.println("lists==after " + lists.size());
		return lists;
	}

	@Override
	public AllElecList queryBeforeOneElec(String id, String name) {
		// TODO Auto-generated method stub

		String nowdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		System.out.println("date===" + nowdate);

		AllElecList elecList = MongoDBUtil.getOneDocumentAllElecValue(id, MongoDBUtil.getTimeStart(nowdate), name);

		System.out.println("elecListOne===" + elecList.toString());
		return elecList;
	}

	@Override
	public List<AllElecList> queryBeforeAllElec(String id) {
		// TODO Auto-generated method stub
		List<Map<String, Object>> elecs = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> elecList = new ArrayList<Map<String, Object>>();
		List<AllElecList> lists = new ArrayList<AllElecList>();
		List<String> listspaceId = new ArrayList<String>();

		String sql1 = "select spaceId from equipment_space_parent where space_device_Type = '空间' and parentSpaceId=?";

		listspaceId = jdbcTemplate.queryForList(sql1, new Object[] { id }, String.class);

		System.out.println("listTagId.size==" + listspaceId.size());

		if (listspaceId.size() == 0) {
			String sql = "select b.tagId,c.energyConsumptionChildType from (select deviceId,parentSpaceId from equipment_space_parent where space_device_Type = '设备' and parentSpaceId='"
					+ id
					+ "') a,device_propertie_tag b,device_propertie c  where a.deviceId = b.deviceId and b.devicePropertieID=c.devicePropertieID and c.energyConsumptionType = '电' ";
			System.out.println(sql);

			elecs = jdbcTemplate.queryForList(sql);

			System.out.println("elecs.size():::" + elecs.size());
			System.out.println("elecs===" + elecs.toString());

		}

		if (listspaceId.size() > 0) {
			listspaceId.add(id);
			System.out.println("listTagId.size==" + listspaceId.size());

			for (int i = 0; i < listspaceId.size(); i++) {
				String sql = "select b.tagId,c.energyConsumptionChildType from (select deviceId,parentSpaceId from equipment_space_parent where space_device_Type = '设备' and parentSpaceId='"
						+ listspaceId.get(i)
						+ "') a,device_propertie_tag b,device_propertie c  where a.deviceId = b.deviceId and b.devicePropertieID=c.devicePropertieID and c.energyConsumptionType = '电' ";

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
			String s2 = elecs.get(i).get("energyConsumptionChildType").toString();
			System.out.println("s1=========" + s1);
			System.out.println("s2=========" + s2);

			AllElecList allelecList = queryBeforeOneElec(s1, s2);
			System.out.println("allelecList:::" + allelecList.toString());
			lists.add(allelecList);
		}
		System.out.println("lists==all " + lists.size());

		// 处理重复用电类型数据，将其累加
		for (int i = 0; i < lists.size(); i++) {
			for (int j = lists.size() - 1; j > i; j--) {
				if (lists.get(i).getAllElecs().get(0).getTypeOfElec()
						.equals(lists.get(j).getAllElecs().get(0).getTypeOfElec())) {
					System.out.println(lists.get(i).getAllElecs().get(0).getTypeOfElec()
							.equals(lists.get(j).getAllElecs().get(0).getTypeOfElec()));
					for (int k = 0; k < 20; k++) {
						lists.get(i).getAllElecs().get(k)
								.setValue((Double.parseDouble(lists.get(i).getAllElecs().get(k).getValue())
										+ Double.parseDouble(lists.get(j).getAllElecs().get(k).getValue())) + "");
					}
					lists.remove(j);
				}
			}
		}
		System.out.println("lists==after " + lists.size());

		return lists;
	}
}
