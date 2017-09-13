package com.ems.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ems.entity.HisValue;
import com.ems.entity.HistoricalAmount;

import net.sf.json.JSONArray;

public class Test3 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
/*		String startTime ="2017-09-11";
		String[] s = MongoDBUtil.changeFormatTime(startTime);
		int startYear = Integer.parseInt(s[0]);
		int startMonth = Integer.parseInt(s[1]);
		int startDay = Integer.parseInt(s[2]);

		System.out.println("startYear:" + startYear);
		System.out.println("startMonth:" + startMonth);
		System.out.println("startDay:" + startDay);
		String endTime ="2017-09-12";
		String[] e = MongoDBUtil.changeFormatTime(endTime);
		int endYear = Integer.parseInt(e[0]);
		int endMonth = Integer.parseInt(e[1]);
		int endDay = Integer.parseInt(e[2]);

		System.out.println("endYear:" + endYear);
		System.out.println("endMonth:" + endMonth);
		System.out.println("endDay:" + endDay);
		//s[0] + '-' + j + '-' + i
		String[] t1 = MongoDBUtil.changeFormatTime((new SimpleDateFormat("yyyy-MM-dd")).format(new Date(startYear - 1900, startMonth - 1, startDay)));
		String[] t2 = MongoDBUtil.changeFormatTime((new SimpleDateFormat("yyyy-MM-dd")).format(new Date(endYear - 1900, endMonth -1, endDay)));

		BasicDBObject timeObj = new BasicDBObject("day",new BasicDBObject("$gte",t1[0] + '-' + t1[1]+ '-' + t1[2] )).append("$lt",t2[0] + '-' + t2[1] + '-' + t2[2]);
		BasicDBObject tagIdObj = new BasicDBObject("tagId", "1");
		BasicDBObject andObj = new BasicDBObject("$and", Arrays.asList(timeObj, tagIdObj));
		System.out.println("anobj==" + andObj);
		
		Cursor cursor = MongoDBUtil.getMongoCollection("1").find(andObj);
		
		System.out.println(cursor.next());*/
	
		//MongoDBUtil.getElectricityNowTotalFormStatstics("statistics","3","2017-09-11 15:00:00");
		List<HisValue> list = MongoDBUtil.getHistoricalConsumption("22","2016-09-13","2019-09-13",1, 10);
	
		System.out.println("list==="+list.size());

		List<HistoricalAmount> historicalList = new ArrayList<HistoricalAmount>();
		
		for (int i = 0; i < list.size(); i++) {
			HistoricalAmount ha = new HistoricalAmount();
			ha.setBuildName("build");
			ha.setDeviceName("deviceName");
			ha.setEnergyType("energyType");
			ha.setTime(list.get(i).getNameKey()); 
			ha.setAmount(list.get(i).getNameValue());
			
			historicalList.add(ha);
		}
		
		System.out.println("historicalList==="+historicalList.size());
		Map<String, String> map = new HashMap<String, String>();  
		map.put("page", String.valueOf(1));
		map.put("rows", JSONArray.fromObject(historicalList).toString());  
		map.put("total", String.valueOf(historicalList.size()));
		
		System.out.println(map.toString());
		
	}

}
