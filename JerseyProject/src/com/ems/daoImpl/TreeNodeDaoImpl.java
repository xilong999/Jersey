package com.ems.daoImpl;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.ems.dao.TreeNodeDao;
import com.ems.entity.AllElec;
import com.ems.entity.BasicDevice;
import com.ems.entity.Device;
import com.ems.entity.DeviceStatistics;
import com.ems.entity.NameOfElec;
import com.ems.entity.TreeNode;
import com.ems.entity.User;
import com.mongodb.BasicDBObject;

@Repository("treeNodeDao")
public class TreeNodeDaoImpl implements TreeNodeDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Autowired
	private RedisTemplate<Serializable, Object> redisTemplate;

	@Resource
	public void setRedisTemplate(RedisTemplate<Serializable, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/**
	 * 获取空间关系的树形结构 +++ 水电气
	 */
	@Override
	public List<TreeNode> queryNodeAll() {
		// TODO Auto-generated method stub
		String sql = "select spaceId,parentSpaceId,spaceName from equipment_space_parent where space_device_Type = '空间' ";
		String sql1 = "select parentSpaceId from equipment_space_parent where space_device_Type = '空间' group by parentSpaceId";

		List<TreeNode> treeNodes = null;
		List<String> parentIds = null;
		System.out.println("返回list");
		try {
			treeNodes = jdbcTemplate.query(sql, new RowMapper<TreeNode>() {
				public TreeNode mapRow(java.sql.ResultSet rs, int rowNum) throws SQLException {
					TreeNode tn = new TreeNode();
					tn.setId(rs.getString("spaceId"));
					tn.setpId(rs.getString("parentSpaceId"));
					tn.setName(rs.getString("spaceName"));
					return tn;
				}
			});
			System.out.println("treeNodes1====" + treeNodes.size());

			parentIds = jdbcTemplate.query(sql1, new RowMapper<String>() {
				public String mapRow(java.sql.ResultSet rs, int rowNum) throws SQLException {
					return rs.getString("parentSpaceId");
				}
			});

			System.out.println("parentIds====" + parentIds);
			String str;
			List<String> diff = new ArrayList<String>();
			for (int i = 0; i < treeNodes.size(); i++) {
				str = treeNodes.get(i).getId();
				if (!parentIds.contains(str)) {
					diff.add(str);
				}
			}
			List<String> energyTypeList = new ArrayList<String>();
			System.out.println("diff====" + diff.size());
			for (String s : diff) {
				String sql2 = "select c.energyConsumptionType from (select deviceId,parentSpaceId from equipment_space_parent where space_device_Type = '设备' and parentSpaceId=?) a,device_propertie_tag b,device_propertie c where a.deviceId = b.deviceId and b.devicePropertieID=c.devicePropertieID group by c.energyConsumptionType";
				energyTypeList = jdbcTemplate.queryForList(sql2, new Object[] { s }, String.class);
				System.out.println("s===="+s);
				System.out.println("energyTypeList.size==="+energyTypeList.size());
				System.out.println("energyTypeList==="+energyTypeList.toString());
				if(energyTypeList.size()==1){
					treeNodes.add(new TreeNode("dian", s, energyTypeList.get(0)));
				}
				if(energyTypeList.size()==2){
					treeNodes.add(new TreeNode("shui", s, energyTypeList.get(0)));
					treeNodes.add(new TreeNode("dian", s, energyTypeList.get(1)));
				}
				if(energyTypeList.size()==3){
					treeNodes.add(new TreeNode("qi", s, energyTypeList.get(0)));
					treeNodes.add(new TreeNode("shui", s, energyTypeList.get(1)));
					treeNodes.add(new TreeNode("dian", s, energyTypeList.get(2)));
				}
			}
			System.out.println("treeNodes2====" + treeNodes.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return treeNodes;
	}

	/**
	 * 区域或建筑的各种用电树形结构
	 */
	@Override
	public List<TreeNode> queryNodeAllElec() {
		// TODO Auto-generated method stub
		String sql = "select spaceId,parentSpaceId,spaceName from equipment_space_parent where space_device_Type = '空间' ";
		String sql1 = "select parentSpaceId from equipment_space_parent where space_device_Type = '空间' group by parentSpaceId";

		List<TreeNode> treeNodes = null;
		List<String> parentIds = null;
		System.out.println("返回list");
		try {
			treeNodes = jdbcTemplate.query(sql, new RowMapper<TreeNode>() {
				public TreeNode mapRow(java.sql.ResultSet rs, int rowNum) throws SQLException {
					TreeNode tn = new TreeNode();
					tn.setId(rs.getString("spaceId"));
					tn.setpId(rs.getString("parentSpaceId"));
					tn.setName(rs.getString("spaceName"));
					return tn;
				}
			});
			System.out.println("treeNodes1====" + treeNodes.size());

			parentIds = jdbcTemplate.query(sql1, new RowMapper<String>() {
				public String mapRow(java.sql.ResultSet rs, int rowNum) throws SQLException {
					return rs.getString("parentSpaceId");
				}
			});

			System.out.println("parentIds====" + parentIds);
			String str;
			List<String> diff = new ArrayList<String>();
			for (int i = 0; i < treeNodes.size(); i++) {
				str = treeNodes.get(i).getId();
				if (!parentIds.contains(str)) {
					diff.add(str);
				}
			}
			System.out.println("diff====" + diff.size());
			for (String s : diff) {
				treeNodes.add(new TreeNode("zong", s, "总用电"));
				treeNodes.add(new TreeNode("zhaoming", s, "照明用电"));
				treeNodes.add(new TreeNode("dongli", s, "动力用电"));
				treeNodes.add(new TreeNode("kongtiao", s, "空调用电"));
				treeNodes.add(new TreeNode("teshu", s, "特殊用电"));
			}
			System.out.println("treeNodes2====" + treeNodes.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return treeNodes;
	}

	/**
	 * 简单空间结构树形结构
	 */
	@Override
	public List<TreeNode> queryNode() {
		// TODO Auto-generated method stub
		String sql = "select spaceId,parentSpaceId,spaceName from equipment_space_parent where space_device_Type = '空间' ";

		List<TreeNode> treeNodes = null;
		System.out.println("返回list");
		try {
			treeNodes = jdbcTemplate.query(sql, new RowMapper<TreeNode>() {
				public TreeNode mapRow(java.sql.ResultSet rs, int rowNum) throws SQLException {
					TreeNode tn = new TreeNode();
					tn.setId(rs.getString("spaceId"));
					tn.setpId(rs.getString("parentSpaceId"));
					tn.setName(rs.getString("spaceName"));
					return tn;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return treeNodes;
	}

	/**
	 * 根据空间id，电的类型，返回各类电的实时数据
	 */
	@Override
	public String queryEveryTypeElec(String id, String name) {
		// TODO Auto-generated method stub
		String sql = "select b.tagId from (select deviceId,parentSpaceId from equipment_space_parent where space_device_Type = '设备' and parentSpaceId='"
				+ id
				+ "')a,device_propertie_tag b,device_propertie c  where a.deviceId = b.deviceId and b.devicePropertieID=c.devicePropertieID and c.energyConsumptionType ='电' and c.energyConsumptionChildType = '"
				+ name + "'";
		System.out.println(sql);
		final String tagId = (String) jdbcTemplate.queryForObject(sql, String.class);
		System.out.println("1111111111111111============" + tagId);

		// 2.然后根据tagId从redis中获取实时数据value

		if (tagId != null) {
			return redisTemplate.execute(new RedisCallback<String>() {
				String devicePropertieValue;

				@Override
				public String doInRedis(RedisConnection connection) throws DataAccessException {
					byte[] key = redisTemplate.getStringSerializer().serialize("nowData:" + tagId);
					if (connection.exists(key)) {
						List<byte[]> value = connection.hMGet(key,
								redisTemplate.getStringSerializer().serialize("deviceId"),
								redisTemplate.getStringSerializer().serialize("devicePropertieName"),
								redisTemplate.getStringSerializer().serialize("devicePropertieID"),
								redisTemplate.getStringSerializer().serialize("energyConsumptionType"),
								redisTemplate.getStringSerializer().serialize("energyConsumptionChildType"),
								redisTemplate.getStringSerializer().serialize("devicePropertieValue"),
								redisTemplate.getStringSerializer().serialize("devicePropertieUpdateTime"));

						devicePropertieValue = redisTemplate.getStringSerializer().deserialize(value.get(5));
					}
					return "{\"tagId\":\"" + tagId + "\",\"devicePropertieValue\":\"" + devicePropertieValue + "\"}";
				}
			});
		} else {
			return null;
		}
	}

	/**
	 * 根据空间id，能源类型，返回各类能耗的实时数据
	 */
	@Override
	public String queryAllEnergyType(String id, String name) {
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
		final String tagId = (String) jdbcTemplate.queryForObject(sql, String.class);
		System.out.println("1111111111111111============" + tagId);

		if (tagId != null) {
			return redisTemplate.execute(new RedisCallback<String>() {
				String devicePropertieValue;

				@Override
				public String doInRedis(RedisConnection connection) throws DataAccessException {
					byte[] key = redisTemplate.getStringSerializer().serialize("nowData:" + tagId);
					if (connection.exists(key)) {
						List<byte[]> value = connection.hMGet(key,
								redisTemplate.getStringSerializer().serialize("deviceId"),
								redisTemplate.getStringSerializer().serialize("devicePropertieName"),
								redisTemplate.getStringSerializer().serialize("devicePropertieID"),
								redisTemplate.getStringSerializer().serialize("energyConsumptionType"),
								redisTemplate.getStringSerializer().serialize("energyConsumptionChildType"),
								redisTemplate.getStringSerializer().serialize("devicePropertieValue"),
								redisTemplate.getStringSerializer().serialize("devicePropertieUpdateTime"));

						devicePropertieValue = redisTemplate.getStringSerializer().deserialize(value.get(5));
					}
					return "{\"tagId\":\"" + tagId + "\",\"devicePropertieValue\":\"" + devicePropertieValue + "\"}";
				}
			});
		} else {
			return null;
		}
	}

	/**
	 * 根据区域id,查询该区域下所有类型电的实时数据
	 */
	@Override
	public List<AllElec> queryAllElec(String id) {
		List<String> elecs = new ArrayList<String>();
		List<Map<String, Object>> elecList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> tagIds = new ArrayList<Map<String, Object>>();

		AllElec ae;
		
		List<String> listspaceId = new ArrayList<String>();
		//List<String> tagIds = new ArrayList<String>();

		String sql1 = "select spaceId from equipment_space_parent where space_device_Type = '空间' and parentSpaceId=?";

		listspaceId = jdbcTemplate.queryForList(sql1, new Object[] { id }, String.class);

		System.out.println("listspaceId.size==" + listspaceId.size());
		
		if (listspaceId.size() == 0) {
			String sql = "select b.tagId from (select deviceId,parentSpaceId from equipment_space_parent where space_device_Type = '设备' and parentSpaceId='"+id+"') a,device_propertie_tag b,device_propertie c  where a.deviceId = b.deviceId and b.devicePropertieID=c.devicePropertieID and c.energyConsumptionType = '电' ";
			System.out.println(sql);

			elecList = jdbcTemplate.queryForList(sql);

			System.out.println("elecList.size():::" + elecList.size());
			System.out.println("elecList===" + elecList.toString());

		}
		if (listspaceId.size() > 0) {
		
			listspaceId.add(id);
			System.out.println("listTagId.size==" + listspaceId.size());

			for (int i = 0; i < listspaceId.size(); i++) {
				String sql = "select b.tagId,c.energyConsumptionChildType from (select deviceId,parentSpaceId from equipment_space_parent where space_device_Type = '设备' and parentSpaceId='"+listspaceId.get(i)+"') a,device_propertie_tag b,device_propertie c  where a.deviceId = b.deviceId and b.devicePropertieID=c.devicePropertieID and c.energyConsumptionType = '电' ";

				tagIds = jdbcTemplate.queryForList(sql);
				System.out.println("tagIds.size():::" + tagIds.size());
				System.out.println("tagIds===" + tagIds.toString());

				elecList.addAll(tagIds);
				System.out.println("elecList.size():::" + elecList.size());
				System.out.println("elecList===" + elecList.toString());
			}
		}

		System.out.println("elecList.size():::" + elecList.size());

		List<AllElec> elecLists = new ArrayList<AllElec>();

		for (int i = 0; i < elecList.size(); i++) {
			elecs.add(elecList.get(i).get("tagId").toString());
		}
		
		
		for (final Iterator iter = elecs.iterator(); iter.hasNext();) {
			ae = redisTemplate.execute(new RedisCallback<AllElec>() {
				@Override
				public AllElec doInRedis(RedisConnection connection) throws DataAccessException {
					byte[] key = redisTemplate.getStringSerializer().serialize("nowData:" + iter.next());
					AllElec allElec = new AllElec();
					if (connection.exists(key)) {
						List<byte[]> value = connection.hMGet(key,
								redisTemplate.getStringSerializer().serialize("deviceId"),
								redisTemplate.getStringSerializer().serialize("devicePropertieName"),
								redisTemplate.getStringSerializer().serialize("devicePropertieID"),
								redisTemplate.getStringSerializer().serialize("energyConsumptionType"),
								redisTemplate.getStringSerializer().serialize("energyConsumptionChildType"),
								redisTemplate.getStringSerializer().serialize("devicePropertieValue"),
								redisTemplate.getStringSerializer().serialize("devicePropertieUpdateTime"));

						String energyConsumptionChildType = redisTemplate.getStringSerializer()
								.deserialize(value.get(4));
						allElec.setTypeOfElec(energyConsumptionChildType);

						String devicePropertieValue = redisTemplate.getStringSerializer().deserialize(value.get(5));
						allElec.setValue(devicePropertieValue);

						System.out.println(allElec.toString());

					}
					return allElec;
				}
			});
			elecLists.add(ae);
		}
		System.out.println("elecLists.size()1==="+elecLists.size());
		
		for(int i=0;i<elecLists.size();i++){
			System.out.println("elecLists.get(i)=="+elecLists.toString());
			
            for(int j=elecLists.size()-1;j>i;j--){
                if(elecLists.get(i).getTypeOfElec().equals(elecLists.get(j).getTypeOfElec())){
                	elecLists.get(i).setValue((Double.parseDouble(elecLists.get(i).getValue())+Double.parseDouble(elecLists.get(j).getValue()))+"");
                	elecLists.remove(j);
                }
            }
        }
		System.out.println("elecLists.size()2==="+elecLists.size());
		return elecLists;
	}

	/**
	 * 根据空间id查询该空间下的所有类型电的名称
	 */
	@Override
	public List<NameOfElec> queryNameOfAllElec(String id) {
		// TODO Auto-generated method stub

		List<String> listspaceId = new ArrayList<String>();
		String sql1 = "select spaceId from equipment_space_parent where space_device_Type = '空间' and parentSpaceId=?";

		listspaceId = jdbcTemplate.queryForList(sql1, new Object[] { id }, String.class);

		System.out.println("listTagId.size==" + listspaceId.size());
		List<NameOfElec> names = new ArrayList<NameOfElec>();
		List<NameOfElec> name = new ArrayList<NameOfElec>();

		if (listspaceId.size() == 0) {
			String sql = "select c.energyConsumptionChildType from (select deviceId,parentSpaceId from equipment_space_parent where space_device_Type = '设备' and parentSpaceId='"
					+ id
					+ "') a,device_propertie_tag b,device_propertie c  where a.deviceId = b.deviceId and b.devicePropertieID=c.devicePropertieID and c.energyConsumptionType = '电' group by c.energyConsumptionChildType";
			System.out.println(sql);

			try {
				names = jdbcTemplate.query(sql, new RowMapper<NameOfElec>() {
					public NameOfElec mapRow(java.sql.ResultSet rs, int rowNum) throws SQLException {
						NameOfElec noe = new NameOfElec();
						noe.setNameKey("energyConsumptionChildType");
						noe.setNameValue(rs.getString("energyConsumptionChildType"));
						return noe;
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (listspaceId.size() > 0) {
			listspaceId.add(id);
			System.out.println("listTagId.size==" + listspaceId.size());
			String sql;
			for (int i = 0; i < listspaceId.size(); i++) {
				sql = "select b.tagId,c.energyConsumptionChildType from (select deviceId,parentSpaceId from equipment_space_parent where space_device_Type = '设备' and parentSpaceId='"
						+ listspaceId.get(i)
						+ "') a,device_propertie_tag b,device_propertie c  where a.deviceId = b.deviceId and b.devicePropertieID=c.devicePropertieID and c.energyConsumptionType = '电' group by c.energyConsumptionChildType";

				System.out.println(sql);
				try {
					name = jdbcTemplate.query(sql, new RowMapper<NameOfElec>() {
						public NameOfElec mapRow(java.sql.ResultSet rs, int rowNum) throws SQLException {
							NameOfElec noe = new NameOfElec();
							noe.setNameKey("energyConsumptionChildType");
							noe.setNameValue(rs.getString("energyConsumptionChildType"));
							return noe;
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
				names.addAll(name);
			}
			for(int i=0;i<names.size();i++){
	            for(int j=names.size()-1;j>i;j--){
	                if(names.get(i).getNameValue().equals(names.get(j).getNameValue())){
	                	names.remove(j);
	                }
	            }
	        }
		}
		return names;
	}
}
