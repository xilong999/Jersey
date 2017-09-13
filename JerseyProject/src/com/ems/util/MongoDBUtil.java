package com.ems.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import com.ems.entity.AllElec;
import com.ems.entity.AllElecList;
import com.ems.entity.HisValue;
import com.ems.entity.LastOneDay;
import com.ems.entity.LastOneDayList;
import com.ems.entity.NowData;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongoDBUtil {
	private static String mongo_ip = "";
	private static int mongo_port;
	private static String mongo_database = "";
	// private static String mongo_collection = "";
	private static MongoClient mongoClient = null;
	private static DB db = null;
	private static Map<String, DBCollection> mongoCollectionMap = new ConcurrentHashMap<String, DBCollection>();

	static {
		Properties properties = new Properties();
		try {
			InputStream in = DataToMongodb.class.getClassLoader().getResourceAsStream("mongodb.properties");
			properties.load(in);
			mongo_ip = properties.getProperty("mongo_ip");
			mongo_port = Integer.parseInt(properties.getProperty("mongo_port"));
			mongo_database = properties.getProperty("mongo_database");
			// mongo_collection = properties.getProperty("mongo_collection");
			Enumeration enumeration = properties.propertyNames();
			while (enumeration.hasMoreElements()) {
				String key = (String) enumeration.nextElement();
				System.out.println("key: " + key + "value: " + properties.getProperty(key));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("读取mongodb.properties出错！");
		}
	}

	/**
	 * 获取mongoClient
	 * 
	 * @return
	 */
	public static MongoClient getMongoClient() {
		if (mongoClient == null) {
			mongoClient = new MongoClient(mongo_ip, mongo_port);
		}
		db = mongoClient.getDB(mongo_database);
		return mongoClient;
	}

	/**
	 * 获取collection
	 * 
	 * @param collectionName
	 * @return
	 */
	public static DBCollection getMongoCollection(String collectionName) {
		MongoDBUtil.getMongoClient();
		DBCollection collection = null;
		if (mongoCollectionMap.containsKey(collectionName)) {
			collection = mongoCollectionMap.get(collectionName);
		} else {
			collection = MongoDBUtil.db.getCollection(collectionName);
		}

		if (null != collection) {
			mongoCollectionMap.put(collectionName, collection);
		}

		return collection;
	}

	/**
	 * 插入数据
	 * 
	 * @param collectionName
	 * @param newDocument
	 */
	public static void insertDocument(String collectionName, DBObject newDocument) {
		MongoDBUtil.getMongoCollection(collectionName).save(newDocument);
	}

	/**
	 * 往内嵌属性中增加一条信息，参数传入collectionName，原始的那条document，类型为为DBObject，
	 * 要插入的BasicDBObject类型信息。
	 * 
	 * @param collectionName
	 * @param devicePropertieUpdateTime
	 * @param devicePropertieValue
	 * @param tagId
	 * @param deviceId
	 * @param devicePropertieName
	 * @param devicePropertieID
	 * @param energyConsumptionType
	 * @param energyConsumptionChildType
	 */
	public static void insertChildDocument(String collectionName, String devicePropertieUpdateTime,
			String devicePropertieValue, String tagId, String deviceId, String devicePropertieName,
			String devicePropertieID, String energyConsumptionType, String energyConsumptionChildType) {
		String s = devicePropertieUpdateTime.substring(0, 13);
		System.out.println(s);
		String all = s.concat(":00:00");
		System.out.println(all);

		// 插入timeStart的时候，需要修改时间， 时间更改为：2017-08-10 14:00:00
		BasicDBObject queryObject = new BasicDBObject("timeStart", all);
		DBObject obj = MongoDBUtil.getMongoCollection(collectionName).findOne(queryObject);
		System.out.println(obj);

		/*
		 * 修改插入时间，时间原本格式为：2017-08-10 14:59:56 插入到内嵌应该更改为：14:59:00
		 */
		String time = devicePropertieUpdateTime.substring(11, 16);
		String embededTime = time + ":00";
		System.out.println(embededTime);

		if (obj != null) {
			System.out.println("恭喜你，mongoDB中找到这条记录，插入内嵌数据");
			BasicDBObject u = new BasicDBObject().append("$set",
					new BasicDBObject().append("devicePropertiesValue." + embededTime, devicePropertieValue));
			MongoDBUtil.getMongoCollection(collectionName).update(obj, u);
		} else {
			System.out.println("mongoDB里面没有这条document，得重新插入一条");

			DBObject user = new BasicDBObject();

			user.put("tagId", tagId);
			user.put("deviceId ", deviceId);
			user.put("devicePropertieName", devicePropertieName);
			user.put("devicePropertieID", devicePropertieID);
			user.put("energyConsumptionType", energyConsumptionType);
			user.put("energyConsumptionChildType", energyConsumptionChildType);
			user.put("timeStart", all);

			DBObject user2 = new BasicDBObject();
			user2.put(embededTime, devicePropertieValue);
			user.put("devicePropertiesValue", user2);

			// collection.save(user);
			MongoDBUtil.insertDocument(collectionName, user);

		}
	}

	/**
	 * 秒级数据 往内嵌属性中增加一条信息，参数传入collectionName，原始的那条document，类型为为DBObject，
	 * 要插入的BasicDBObject类型信息。
	 * 
	 * @param collectionName
	 * @param devicePropertieUpdateTime
	 * @param devicePropertieValue
	 * @param tagId
	 * @param deviceId
	 * @param devicePropertieName
	 * @param devicePropertieID
	 * @param energyConsumptionType
	 * @param energyConsumptionChildType
	 */
	public static void insertSecondChildDocument(String collectionName, String devicePropertieUpdateTime,
			String devicePropertieValue, String tagId, String deviceId, String devicePropertieName,
			String devicePropertieID, String energyConsumptionType, String energyConsumptionChildType) {
		String s = devicePropertieUpdateTime.substring(0, 13);
		System.out.println(s);
		String all = s.concat(":00:00");
		System.out.println(all);

		// 插入timeStart的时候，需要修改时间， 时间更改为：2017-08-10 14:00:00
		BasicDBObject queryObject = new BasicDBObject("timeStart", all);
		DBObject obj = MongoDBUtil.getMongoCollection(collectionName).findOne(queryObject);
		System.out.println(obj);

		/*
		 * 修改插入时间，时间原本格式为：2017-08-10 14:59:56 插入到内嵌应该更改为：14:59:00
		 */
		String time = devicePropertieUpdateTime.substring(11, 19);
		// String embededTime = time+":00";
		// System.out.println(embededTime);

		if (obj != null) {
			System.out.println("恭喜你，mongoDB中找到这条记录，插入内嵌数据");
			BasicDBObject u = new BasicDBObject().append("$set",
					new BasicDBObject().append("devicePropertiesValue." + time, devicePropertieValue));
			MongoDBUtil.getMongoCollection(collectionName).update(obj, u);
		} else {
			System.out.println("mongoDB里面没有这条document，得重新插入一条");

			DBObject user = new BasicDBObject();

			user.put("tagId", tagId);
			user.put("deviceId ", deviceId);
			user.put("devicePropertieName", devicePropertieName);
			user.put("devicePropertieID", devicePropertieID);
			user.put("energyConsumptionType", energyConsumptionType);
			user.put("energyConsumptionChildType", energyConsumptionChildType);
			user.put("timeStart", all);

			DBObject user2 = new BasicDBObject();
			user2.put(time, devicePropertieValue);
			user.put("devicePropertiesValue", user2);

			// collection.save(user);
			MongoDBUtil.insertDocument(collectionName, user);

		}
	}

	/**
	 * 返回单个document的所有value值总和 totalValue
	 * 
	 * @param collectionName
	 * @param timeStart
	 * @return
	 */
	public static double getOneDocumentTotalValue(String collectionName, String timeStart) {
		// String timeStart1 = "2017-08-10 13:00:00";

		BasicDBObject queryObject = new BasicDBObject("timeStart", timeStart);
		DBObject obj = MongoDBUtil.getMongoCollection(collectionName).findOne(queryObject);
		System.out.println(obj);

		System.out.println(obj.get("devicePropertiesValue"));
		BasicDBObject data = (BasicDBObject) obj.get("devicePropertiesValue");
		// System.out.println(data.get("2017-08-10 13:02:00"));

		// System.out.println(timeStart1.substring(0, 15));
		String front1 = timeStart.substring(11, 15); // 13:0
		String front2 = timeStart.substring(11, 14); // 13:
		System.out.println("front1=" + front1);
		System.out.println("front2=" + front2);

		// System.out.println(data.get("13:02:00"));
		System.out.println("============================================================");
		double value1 = 0;
		double value2 = 0;
		for (int i = 0; i <= 9; i++) {
			// System.out.println(data.get(front1 + i +":00"));
			if (data.get(front1 + i + ":00") != null) {
				// System.out.println(data.get("2017-08-10 13:"+i+":00"));
				String values = (String) data.get(front1 + i + ":00");
				System.out.println(values);

				double result = Double.parseDouble(values);
				value1 += result;
			}
		}
		System.out.println("****************************************");
		for (int i = 10; i <= 60; i++) {
			// System.out.println(data.get("2017-08-10 13:"+i+":00"));
			if (data.get(front2 + i + ":00") != null) {
				// System.out.println(data.get("2017-08-10 13:"+i+":00"));
				String values = (String) data.get(front2 + i + ":00");
				System.out.println(values);

				double result = Double.parseDouble(values);
				value2 += result;
			}
		}

		System.out.println("-----------------------------------------");
		System.out.println(value1);
		System.out.println(value2);
		System.out.println("-----------------------------------------");
		double totalValue = value1 + value2;
		System.out.println("这个document的总的数值为：" + totalValue);
		return totalValue;
	}

	/**
	 * 返回单个document的所有value值的平均值 avg
	 * 
	 * @param collectionName
	 * @param timeStart
	 * @return
	 */
	public static double getOneDocumentAvgValue(String collectionName, String timeStart) {
		// String timeStart1 = "2017-08-10 13:00:00";

		BasicDBObject queryObject = new BasicDBObject("timeStart", timeStart);
		DBObject obj = MongoDBUtil.getMongoCollection(collectionName).findOne(queryObject);
		System.out.println(obj);

		System.out.println(obj.get("devicePropertiesValue"));
		BasicDBObject data = (BasicDBObject) obj.get("devicePropertiesValue");
		// System.out.println(data.get("2017-08-10 13:02:00"));

		// System.out.println(timeStart1.substring(0, 15));
		String front1 = timeStart.substring(11, 15); // 13:0
		String front2 = timeStart.substring(11, 14); // 13:
		System.out.println("front1=" + front1);
		System.out.println("front2=" + front2);

		// System.out.println(data.get("13:02:00"));
		System.out.println("============================================================");
		double value1 = 0;
		double value2 = 0;
		int flag = 0;
		for (int i = 0; i <= 9; i++) {
			// System.out.println(data.get(front1 + i +":00"));
			if (data.get(front1 + i + ":00") != null) {
				flag++;
				// System.out.println(data.get("2017-08-10 13:"+i+":00"));
				String values = (String) data.get(front1 + i + ":00");
				System.out.println(values);

				double result = Double.parseDouble(values);
				value1 += result;
			}
		}
		System.out.println("****************************************");
		for (int i = 10; i <= 60; i++) {
			// System.out.println(data.get("2017-08-10 13:"+i+":00"));
			if (data.get(front2 + i + ":00") != null) {
				flag++;
				// System.out.println(data.get("2017-08-10 13:"+i+":00"));
				String values = (String) data.get(front2 + i + ":00");
				System.out.println(values);

				double result = Double.parseDouble(values);
				value2 += result;
			}
		}

		System.out.println("-----------------------------------------");
		System.out.println(value1);
		System.out.println(value2);
		System.out.println("-----------------------------------------");
		double totalValue = value1 + value2;
		System.out.println("这个document的总的数值为：" + totalValue);
		System.out.println("flag = " + flag);
		double avgValue = totalValue / flag;
		System.out.println("avgtValue = " + avgValue);
		return avgValue;

	}

	/**
	 * 返回单个document的所有value值的最大值 max
	 * 
	 * @param collectionName
	 * @param timeStart
	 * @return
	 */
	public static double getOneDocumentMaxValue(String collectionName, String timeStart) {
		// String timeStart1 = "2017-08-10 13:00:00";

		BasicDBObject queryObject = new BasicDBObject("timeStart", timeStart);
		DBObject obj = MongoDBUtil.getMongoCollection(collectionName).findOne(queryObject);
		System.out.println(obj);

		System.out.println(obj.get("devicePropertiesValue"));
		BasicDBObject data = (BasicDBObject) obj.get("devicePropertiesValue");
		// System.out.println(data.get("2017-08-10 13:02:00"));

		// System.out.println(timeStart1.substring(0, 15));
		String front1 = timeStart.substring(11, 15); // 13:0
		String front2 = timeStart.substring(11, 14); // 13:
		System.out.println("front1=" + front1);
		System.out.println("front2=" + front2);

		// System.out.println(data.get("13:02:00"));
		System.out.println("============================================================");
		int flag = 0;
		double max1 = 0, max2 = 0;

		for (int i = 0; i <= 9; i++) {
			// System.out.println(data.get(front1 + i +":00"));
			if (data.get(front1 + i + ":00") != null) {
				flag++;
				// System.out.println(data.get("2017-08-10 13:"+i+":00"));
				String values = (String) data.get(front1 + i + ":00");
				System.out.println(values);

				double result = Double.parseDouble(values);

				if (flag == 1) {
					max1 = result;
				}

				if (result > max1) {
					max1 = result;
				}

			}
		}
		System.out.println("****************************************");
		int flag2 = 0;
		for (int i = 10; i <= 60; i++) {
			// System.out.println(data.get("2017-08-10 13:"+i+":00"));
			if (data.get(front2 + i + ":00") != null) {
				flag++;
				flag2++;
				// System.out.println(data.get("2017-08-10 13:"+i+":00"));
				String values = (String) data.get(front2 + i + ":00");
				System.out.println(values);

				double result = Double.parseDouble(values);

				if (flag2 == 1) {
					max2 = result;
				}
				if (result > max2) {
					max2 = result;
				}
			}
		}

		System.out.println("flag = " + flag);

		System.out.println("max1 = " + max1);
		System.out.println("max2 = " + max2);
		double max = max1 > max2 ? max1 : max2;
		System.out.println("max = " + max);
		return max;
	}

	/**
	 * 返回单个document的所有value值的最小值 min
	 * 
	 * @param collectionName
	 * @param timeStart
	 * @return
	 */
	public static double getOneDocumentMinValue(String collectionName, String timeStart) {
		// String timeStart1 = "2017-08-10 13:00:00";

		BasicDBObject queryObject = new BasicDBObject("timeStart", timeStart);
		DBObject obj = MongoDBUtil.getMongoCollection(collectionName).findOne(queryObject);
		System.out.println(obj);

		System.out.println(obj.get("devicePropertiesValue"));
		BasicDBObject data = (BasicDBObject) obj.get("devicePropertiesValue");
		// System.out.println(data.get("2017-08-10 13:02:00"));

		// System.out.println(timeStart1.substring(0, 15));
		String front1 = timeStart.substring(11, 15); // 13:0
		String front2 = timeStart.substring(11, 14); // 13:
		System.out.println("front1=" + front1);
		System.out.println("front2=" + front2);

		// System.out.println(data.get("13:02:00"));
		System.out.println("============================================================");
		int flag = 0;
		double min1 = 0, min2 = 0;
		for (int i = 0; i <= 9; i++) {
			// System.out.println(data.get(front1 + i +":00"));
			if (data.get(front1 + i + ":00") != null) {
				flag++;
				// System.out.println(data.get("2017-08-10 13:"+i+":00"));
				String values = (String) data.get(front1 + i + ":00");
				System.out.println(values);

				double result = Double.parseDouble(values);

				if (flag == 1) {
					min1 = result;
				}

				if (result < min1) {
					min1 = result;
				}

			} else {
				min1 = -999999;
			}
		}
		System.out.println("****************************************");
		int flag2 = 0;
		for (int i = 10; i <= 60; i++) {
			// System.out.println(data.get("2017-08-10 13:"+i+":00"));
			if (data.get(front2 + i + ":00") != null) {
				flag++;
				flag2++;
				// System.out.println(data.get("2017-08-10 13:"+i+":00"));
				String values = (String) data.get(front2 + i + ":00");
				System.out.println(values);

				double result = Double.parseDouble(values);

				if (flag2 == 1) {
					min2 = result;
				}
				if (result < min2) {
					min2 = result;
				}
			}
		}
		double min;
		System.out.println("-----------------------------------------");

		System.out.println("flag = " + flag);

		System.out.println("min1 = " + min1);
		System.out.println("min2 = " + min2);

		if (min1 == -999999) {
			min = min2;
		} else {
			min = min1 > min2 ? min2 : min1;
		}

		System.out.println("min = " + min);
		return min;
	}

	/**
	 * 返回整个collection的所有value值的总和 collectionTotalValue
	 * 
	 * @param collectionName
	 * @param timeStart
	 * @return
	 */
	public static double getCollectionTotalValue(String collectionName, String tagId) {
		Pattern queryPattern = Pattern.compile(tagId, Pattern.CASE_INSENSITIVE);
		BasicDBObject queryObject = new BasicDBObject("tagId", queryPattern);
		Cursor cursor = MongoDBUtil.getMongoCollection(collectionName).find(queryObject);

		double collectionTotalValue = 0;
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			// System.out.println(obj.toString());
			System.out.println(obj.get("timeStart"));
			String timeStart = (String) obj.get("timeStart");

			BasicDBObject data = (BasicDBObject) obj.get("devicePropertiesValue");
			System.out.println(data);
			// System.out.println(data.get("13:02:00"));

			double result = MongoDBUtil.getOneDocumentTotalValue(collectionName, timeStart);
			collectionTotalValue += result;

			System.out.println("collectionTotalValue = " + collectionTotalValue);

		}
		return collectionTotalValue;
	}

	/**
	 * 返回整个collection的所有value值的总平均值 collectionTotalAvgValue
	 * 
	 * @param collectionName
	 * @param timeStart
	 * @return
	 */
	public static double getCollectionTotalAvgValue(String collectionName, String tagId) {
		Pattern queryPattern = Pattern.compile(tagId, Pattern.CASE_INSENSITIVE);
		BasicDBObject queryObject = new BasicDBObject("tagId", queryPattern);
		Cursor cursor = MongoDBUtil.getMongoCollection(collectionName).find(queryObject);

		double collectionTotalValue = 0;
		int flag = 0;
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			// System.out.println(obj.toString());
			System.out.println(obj.get("timeStart"));
			String timeStart = (String) obj.get("timeStart");

			BasicDBObject data = (BasicDBObject) obj.get("devicePropertiesValue");
			System.out.println(data);
			// System.out.println(data.get("13:02:00"));

			double result = MongoDBUtil.getOneDocumentTotalValue(collectionName, timeStart);
			flag++;
			collectionTotalValue += result;

			System.out.println("collectionTotalValue = " + collectionTotalValue);

		}
		System.out.println("flag = " + flag);
		double collectionTotalAvgValue = collectionTotalValue / flag;
		return collectionTotalAvgValue;
	}

	/**
	 * 插入统计的数据
	 * 
	 * @param collectionName
	 * @param devicePropertieUpdateTime
	 * @param tagId
	 * @param deviceId
	 * @param devicePropertieName
	 * @param devicePropertieID
	 * @param energyConsumptionType
	 * @param energyConsumptionChildType
	 * @param totalValue
	 * @param avgValue
	 * @param maxValue
	 * @param minValue
	 */
	public static void insertStatsticsDocument(String collectionName, String devicePropertieUpdateTime, String tagId,
			String statisticsCollectionName) {

		int year = Integer.parseInt(devicePropertieUpdateTime.substring(0, 4));
		System.out.println(year);

		int month = Integer.parseInt(devicePropertieUpdateTime.substring(5, 7));
		System.out.println(month);

		int date = Integer.parseInt(devicePropertieUpdateTime.substring(8, 10));
		System.out.println(date);

		int hourOfDay = Integer.parseInt(devicePropertieUpdateTime.substring(11, 13));
		System.out.println(hourOfDay);

		int minute = Integer.parseInt(devicePropertieUpdateTime.substring(14, 16));
		System.out.println(minute);

		Calendar c = Calendar.getInstance();
		c.set(year, month - 1, date, hourOfDay, minute);
		System.out.println("当前时间假设为：");
		String nowTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(c.getTime());
		System.out.println(nowTime);
		System.out.println("***********************************************************");

		c.add(Calendar.HOUR_OF_DAY, -1);
		System.out.println("上一个小时的时间为：");
		String lastOneHour = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(c.getTime());
		System.out.println(lastOneHour);

		String s = lastOneHour.substring(0, 13);
		System.out.println(s);
		String timeStart = s.concat(":00:00");
		System.out.println(timeStart);

		/*
		 * 拿到了上一小时的时间后，在统计表（statistics）里面查找，有没有对应的数据，如果有，就表明已经统计过了，如果没有，
		 * 统计后插入到statistics
		 */

		BasicDBObject timeStartObject1 = new BasicDBObject("timeStart", timeStart);
		BasicDBObject tagIdObj1 = new BasicDBObject("tagId", tagId);

		BasicDBObject andObj1 = new BasicDBObject("$and", Arrays.asList(timeStartObject1, tagIdObj1));
		Cursor cursor1 = MongoDBUtil.getMongoCollection(statisticsCollectionName).find(andObj1);

		if (cursor1.hasNext()) {
			DBObject statisticsObj = cursor1.next();
			System.out.println(statisticsObj.toString());
			System.out.println("已经将上一小时的数据统计了。！！！！！！！！！！！！！！！！！！！！！！！！！！！！！");
		} else {
			BasicDBObject timeStartObject = new BasicDBObject("timeStart", timeStart);
			BasicDBObject tagIdObj = new BasicDBObject("tagId", tagId);

			BasicDBObject andObj = new BasicDBObject("$and", Arrays.asList(timeStartObject, tagIdObj));

			Cursor cursor = MongoDBUtil.getMongoCollection(collectionName).find(andObj);
			while (cursor.hasNext()) {
				DBObject obj = cursor.next();
				System.out.println(obj.toString());

				System.out.println("查找到对应tagId的上一小时的数据了");

				Double totalValue = MongoDBUtil.getOneDocumentTotalValue(tagId, timeStart);
				Double avgValue = MongoDBUtil.getOneDocumentAvgValue(tagId, timeStart);
				Double maxValue = MongoDBUtil.getOneDocumentMaxValue(tagId, timeStart);
				Double minValue = MongoDBUtil.getOneDocumentMinValue(tagId, timeStart);

				System.out.println("单document统计的数据为：" + totalValue);
				System.out.println("单document平均值：" + avgValue);
				System.out.println("单document最大值：" + maxValue);
				System.out.println("单document最小值：" + minValue);

				DBObject statisticsData = new BasicDBObject();
				statisticsData.put("tagId", tagId);

				statisticsData.put("timeStart", timeStart);
				statisticsData.put("totalValue", totalValue);
				statisticsData.put("avgValue", avgValue);
				statisticsData.put("maxValue", maxValue);
				statisticsData.put("minValue", minValue);
				MongoDBUtil.getMongoCollection(statisticsCollectionName).save(statisticsData);

			}
		}
	}

	public static List<AllElec> getOneDocumentAllValue(String collectionName, String timeStart, String name) {
		List<AllElec> value3 = new LinkedList<AllElec>();

		String nowdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		System.out.println("date===" + nowdate);

		BasicDBObject queryObject = new BasicDBObject("timeStart", timeStart);
		DBObject obj = MongoDBUtil.getMongoCollection(collectionName).findOne(queryObject);
		System.out.println("obj===" + obj);
		if (obj != null) {
			System.out.println(obj.get("devicePropertiesValue"));
			BasicDBObject data = (BasicDBObject) obj.get("devicePropertiesValue");
			// System.out.println(data.get("2017-08-10 13:02:00"));

			// System.out.println(timeStart1.substring(0, 15));
			String front1 = timeStart.substring(11, 15); // 13:0
			String front2 = timeStart.substring(11, 14); // 13:
			System.out.println("front1=" + front1);
			System.out.println("front2=" + front2);

			// System.out.println(data.get("13:02:00"));
			System.out.println("============================================================");
			int flag = 0;
			List<AllElec> value = new LinkedList<AllElec>();
			for (int i = 0; i <= 9; i++) {
				// System.out.println(data.get(front1 + i +":00"));
				if (data.get(front1 + i + ":00") != null) {
					flag++;
					// System.out.println(data.get("2017-08-10 13:"+i+":00"));
					String values = (String) data.get(front1 + i + ":00");
					System.out.println(values);

					double result = Double.parseDouble(values);
					value.add(new AllElec(name, result + ""));
				}
			}
			System.out.println("****************************************");
			for (int i = 10; i <= 60; i++) {
				// System.out.println(data.get("2017-08-10 13:"+i+":00"));
				if (data.get(front2 + i + ":00") != null) {
					flag++;
					// System.out.println(data.get("2017-08-10 13:"+i+":00"));
					String values = (String) data.get(front2 + i + ":00");
					System.out.println(values);

					double result = Double.parseDouble(values);
					value.add(new AllElec(name, result + ""));
				}
			}

			System.out.println("flag = " + flag);
			System.out.println("valueList.size = " + value.size());
			List<AllElec> value2 = new LinkedList<AllElec>();
			int num = 0;
			while (value.size() + value2.size() < 20) {
				int total = value.size() + value2.size();
				System.out.println("value.size()+ value2.size()===" + total);
				String startTime = MongoDBUtil.getLastOneHour(MongoDBUtil.getLastDate(nowdate, num));
				BasicDBObject queryObject2 = new BasicDBObject("timeStart", startTime);
				DBObject obj2 = MongoDBUtil.getMongoCollection(collectionName).findOne(queryObject2);
				System.out.println("obj2===" + obj2);
				if (obj2 != null) {
					System.out.println(obj2.get("devicePropertiesValue"));
					BasicDBObject data2 = (BasicDBObject) obj2.get("devicePropertiesValue");
					// System.out.println(data.get("2017-08-10 13:02:00"));

					// System.out.println(timeStart1.substring(0, 15));
					String front3 = startTime.substring(11, 15); // 13:0
					String front4 = startTime.substring(11, 14); // 13:
					System.out.println("front3=" + front3);
					System.out.println("front4=" + front4);

					// System.out.println(data.get("13:02:00"));
					System.out.println("============================================================");

					for (int i = 60; i >= 10; i--) {
						// System.out.println(data.get("2017-08-10
						// 13:"+i+":00"));
						if (data2.get(front4 + i + ":00") != null) {
							// System.out.println(data.get("2017-08-10
							// 13:"+i+":00"));
							String values = (String) data2.get(front4 + i + ":00");
							System.out.println(values);

							double result = Double.parseDouble(values);
							System.out.println("result===" + result);

							value2.add(new AllElec(name, result + ""));
							System.out.println("valueSize222====" + value2.size());
						}
					}
					for (int i = 9; i >= 0; i--) {
						// System.out.println(data.get(front1 + i +":00"));
						if (data2.get(front3 + i + ":00") != null) {
							// System.out.println(data.get("2017-08-10
							// 13:"+i+":00"));
							String values = (String) data2.get(front3 + i + ":00");
							System.out.println(values);

							double result = Double.parseDouble(values);
							System.out.println("result===" + result);
							value2.add(new AllElec(name, result + ""));
							System.out.println("valueSize111====" + value2.size());
						}
					}
					num++;
					System.out.println("****************************************");
				}
			}

			Collections.reverse(value2);

			for (int i = 0; i < value.size(); i++) {
				value2.add(value.get(i));
			}

			Collections.reverse(value2);
			for (int i = 0; i < 20; i++) {
				value3.add(value2.get(i));
			}
			Collections.reverse(value3);
			return value3;
		}
		return value3;
	}

	public static AllElecList getOneDocumentAllElecValue(String collectionName, String timeStart, String name) {
		AllElecList list = null;
		String nowdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		System.out.println("date===" + nowdate);

		BasicDBObject queryObject = new BasicDBObject("timeStart", timeStart);
		DBObject obj = MongoDBUtil.getMongoCollection(collectionName).findOne(queryObject);
		System.out.println("obj===" + obj);
		if (obj != null) {
			System.out.println(obj.get("devicePropertiesValue"));
			BasicDBObject data = (BasicDBObject) obj.get("devicePropertiesValue");
			// System.out.println(data.get("2017-08-10 13:02:00"));

			// System.out.println(timeStart1.substring(0, 15));
			String front1 = timeStart.substring(11, 15); // 13:0
			String front2 = timeStart.substring(11, 14); // 13:
			System.out.println("front1=" + front1);
			System.out.println("front2=" + front2);

			// System.out.println(data.get("13:02:00"));
			System.out.println("============================================================");
			int flag = 0;
			List<AllElec> value = new LinkedList<AllElec>();
			for (int i = 0; i <= 9; i++) {
				// System.out.println(data.get(front1 + i +":00"));
				if (data.get(front1 + i + ":00") != null) {
					flag++;
					// System.out.println(data.get("2017-08-10 13:"+i+":00"));
					String values = (String) data.get(front1 + i + ":00");
					System.out.println(values);

					double result = Double.parseDouble(values);
					value.add(new AllElec(name, result + ""));
				}
			}
			System.out.println("****************************************");
			for (int i = 10; i <= 60; i++) {
				// System.out.println(data.get("2017-08-10 13:"+i+":00"));
				if (data.get(front2 + i + ":00") != null) {
					flag++;
					// System.out.println(data.get("2017-08-10 13:"+i+":00"));
					String values = (String) data.get(front2 + i + ":00");
					System.out.println(values);

					double result = Double.parseDouble(values);
					value.add(new AllElec(name, result + ""));
				}
			}

			System.out.println("flag = " + flag);
			System.out.println("valueList.size = " + value.size());
			List<AllElec> value2 = new LinkedList<AllElec>();
			int num = 0;
			while (value.size() + value2.size() < 20) {
				int total = value.size() + value2.size();
				System.out.println("value.size()+ value2.size()===" + total);
				String startTime = MongoDBUtil.getLastOneHour(MongoDBUtil.getLastDate(nowdate, num));
				BasicDBObject queryObject2 = new BasicDBObject("timeStart", startTime);
				DBObject obj2 = MongoDBUtil.getMongoCollection(collectionName).findOne(queryObject2);
				System.out.println("obj2===" + obj2);

				if (obj2 != null) {
					System.out.println(obj2.get("devicePropertiesValue"));
					BasicDBObject data2 = (BasicDBObject) obj2.get("devicePropertiesValue");
					// System.out.println(data.get("2017-08-10 13:02:00"));

					// System.out.println(timeStart1.substring(0, 15));
					String front3 = startTime.substring(11, 15); // 13:0
					String front4 = startTime.substring(11, 14); // 13:
					System.out.println("front3=" + front3);
					System.out.println("front4=" + front4);

					// System.out.println(data.get("13:02:00"));
					System.out.println("============================================================");

					for (int i = 60; i >= 10; i--) {
						// System.out.println(data.get("2017-08-10
						// 13:"+i+":00"));
						if (data2.get(front4 + i + ":00") != null) {
							// System.out.println(data.get("2017-08-10
							// 13:"+i+":00"));
							String values = (String) data2.get(front4 + i + ":00");
							System.out.println(values);

							double result = Double.parseDouble(values);
							System.out.println("result===" + result);

							value2.add(new AllElec(name, result + ""));
							System.out.println("valueSize222====" + value2.size());
						}
					}
					for (int i = 9; i >= 0; i--) {
						// System.out.println(data.get(front1 + i +":00"));
						if (data2.get(front3 + i + ":00") != null) {
							// System.out.println(data.get("2017-08-10
							// 13:"+i+":00"));
							String values = (String) data2.get(front3 + i + ":00");
							System.out.println(values);

							double result = Double.parseDouble(values);
							System.out.println("result===" + result);
							value2.add(new AllElec(name, result + ""));
							System.out.println("valueSize111====" + value2.size());
						}
					}
					num++;
					System.out.println("****************************************");
				}
			}
			Collections.reverse(value2);

			for (int i = 0; i < value.size(); i++) {
				value2.add(value.get(i));
			}

			Collections.reverse(value2);
			List<AllElec> value3 = new LinkedList<AllElec>();
			for (int i = 0; i < 20; i++) {
				value3.add(value2.get(i));
			}
			Collections.reverse(value3);
			list = new AllElecList(value3);
			return list;
		}
		return list;
	}

	/**
	 * 拿到上一小时的timeStart。
	 * 
	 * @param nowdate
	 * @return
	 */
	public static String getLastOneHour(String nowdate) {

		String startTime;
		int year = Integer.parseInt(nowdate.substring(0, 4));
		System.out.println(year);

		int month = Integer.parseInt(nowdate.substring(5, 7));
		System.out.println(month);

		int date = Integer.parseInt(nowdate.substring(8, 10));
		System.out.println(date);

		int hourOfDay = Integer.parseInt(nowdate.substring(11, 13));
		System.out.println(hourOfDay);

		int minute = Integer.parseInt(nowdate.substring(14, 16));
		System.out.println(minute);

		Calendar c = Calendar.getInstance();
		c.set(year, month - 1, date, hourOfDay, minute);
		System.out.println("当前时间假设为：");
		String nowTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(c.getTime());
		System.out.println(nowTime);
		System.out.println("***********************************************************");

		c.add(Calendar.HOUR_OF_DAY, -1);
		System.out.println("上一个小时的时间为：");
		String lastOneHour = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(c.getTime());
		System.out.println(lastOneHour);

		String s = lastOneHour.substring(0, 13);
		System.out.println(s);
		startTime = s.concat(":00:00");
		System.out.println(startTime);

		return startTime;

	}

	/**
	 * 根据当前时间拿到timeStart。
	 * 
	 * @param nowDate
	 * @return
	 */
	public static String getTimeStart(String nowDate) {
		String s = nowDate.substring(0, 13);
		System.out.println(s);
		String timeStart = s.concat(":00:00");
		System.out.println(timeStart);
		return timeStart;
	}

	public static String getLastDate(String nowdate, int num) {
		int year = Integer.parseInt(nowdate.substring(0, 4));
		System.out.println(year);

		int month = Integer.parseInt(nowdate.substring(5, 7));
		System.out.println(month);

		int date = Integer.parseInt(nowdate.substring(8, 10));
		System.out.println(date);

		int hourOfDay = Integer.parseInt(nowdate.substring(11, 13));
		System.out.println(hourOfDay);

		int minute = Integer.parseInt(nowdate.substring(14, 16));
		System.out.println(minute);

		Calendar c = Calendar.getInstance();
		c.set(year, month - 1, date, hourOfDay - num, minute);
		System.out.println("当前时间假设为：");
		String nowTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(c.getTime());
		System.out.println(nowTime);
		System.out.println("***********************************************************");

		c.add(Calendar.HOUR_OF_DAY, -1);
		return nowTime;
	}

	/**
	 * 返回当前电的totalValue
	 *
	 * @param collectionName
	 * @param tagId
	 */
	public static Double getElectricityNowTotalFormStatstics(String collectionName, String tagId, String testTime) {
		/*
		 * 1. 获取当前系统时间 2. 取当前时间的整点 3. 将传进来的tagId对应的totalValue返回
		 */
		Double totalValue = 0.0;

		// 获取当前系统时间
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowTime = sdf.format(d);
		System.out.println("nowTime: " + nowTime);

		String s = nowTime.substring(0, 13);
		System.out.println(s);
		String timeStart = s.concat(":00:00");
		System.out.println(timeStart);
		// BasicDBObject ageObj = new BasicDBObject("day",new
		// BasicDBObject("$gt",23));
		BasicDBObject timeStartObject = new BasicDBObject("timeStart", testTime);
		BasicDBObject tagIdObj = new BasicDBObject("tagId", tagId);

		BasicDBObject andObj = new BasicDBObject("$and", Arrays.asList(timeStartObject, tagIdObj));

		Cursor cursor = MongoDBUtil.getMongoCollection(collectionName).find(andObj);
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			System.out.println(obj.toString());
			totalValue = (Double) obj.get("totalValue");
			System.out.println("当前tagId的totalValue为：" + totalValue);

		}
		return totalValue;
	}

	/**
	 * 返回前23个小时的totalValue
	 *
	 * @param collectionName
	 * @param nowTime
	 */
	public static LastOneDayList getElectricityLastOneDayTotalFormStatstics(String collectionName, String nowTime,
			List<String> listTagId) {

		Map<String, String> map = new LinkedHashMap();

		List<LastOneDay> listLastOneDay = new LinkedList<>();

		/*
		 * 拿到电的所有tagId,存放到list里面。
		 */
		System.out.println("getMysql_electricity===进来了！");
		/*
		 * listTagId = C3P0Util.getElectricity(); System.out.println(listTagId);
		 */

		// 获取上一小时,拿到的是整点
		for (int i = 1; i <= 23; i++) {
			String timeStart = MongoDBUtil.getLastOneHour(nowTime, i);
			Double totalValue23 = 0.0;
			String result = "";
			for (int j = 0; j <= listTagId.size() - 1; j++) {

				BasicDBObject timeStartObject = new BasicDBObject("timeStart", timeStart);
				BasicDBObject tagIdObj = new BasicDBObject("tagId", listTagId.get(j));

				BasicDBObject andObj = new BasicDBObject("$and", Arrays.asList(timeStartObject, tagIdObj));

				Cursor cursor = MongoDBUtil.getMongoCollection(collectionName).find(andObj);
				while (cursor.hasNext()) {
					DBObject obj = cursor.next();
					System.out.println(obj.toString());
					Double totalValue = (Double) obj.get("totalValue");
					System.out.println("当前tagId的totalValue为：" + totalValue);
					totalValue23 += totalValue;
					System.out.println(totalValue23);
				}
				// 小数点后取2位
				DecimalFormat df = new DecimalFormat("#.00");
				result = df.format(totalValue23);
				if (result.equals(".00")) {
					result = "0";
				}
				// map.put(timeStart, result);
				// System.out.println("totalValue23 ==================== " +
				// totalValue23);

			}
			listLastOneDay.add(new LastOneDay(timeStart, result));
		}
		Collections.reverse(listLastOneDay);

		LastOneDayList lastOneDayList = new LastOneDayList(listLastOneDay);
		return lastOneDayList;
	}

	/**
	 * 获取上一小时的时间
	 *
	 * @param nowTime
	 * @param i
	 * @return
	 */
	public static String getLastOneHour(String nowTime, int i) {
		int year = Integer.parseInt(nowTime.substring(0, 4));
		System.out.println(year);

		int month = Integer.parseInt(nowTime.substring(5, 7));
		System.out.println(month);

		int date = Integer.parseInt(nowTime.substring(8, 10));
		System.out.println(date);

		int hourOfDay = Integer.parseInt(nowTime.substring(11, 13));
		System.out.println(hourOfDay);

		int minute = Integer.parseInt(nowTime.substring(14, 16));
		System.out.println(minute);

		Calendar c = Calendar.getInstance();
		c.set(year, month - 1, date, hourOfDay, minute);
		System.out.println("当前时间假设为：");
		String nowTime2 = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(c.getTime());
		System.out.println(nowTime2);
		System.out.println("***********************************************************");

		c.add(Calendar.HOUR_OF_DAY, -i);
		System.out.println("上一个小时的时间为：");
		String lastOneHour = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(c.getTime());
		System.out.println(lastOneHour);

		String s = lastOneHour.substring(0, 13);
		System.out.println(s);
		String timeStart = s.concat(":00:00");
		System.out.println(timeStart);
		return timeStart;
	}

	/**
	 * 返回当前电的totalValue
	 *
	 * @param collectionName
	 * @param timeStart
	 * @return
	 */
	public static NowData getElectricityNowTotalFormStatstics(String collectionName, String timeStart,
			List<String> listTagId) {
		/*
		 * 1. 获取当前系统时间 2. 取当前时间的整点 3. 将传进来的tagId对应的totalValue返回
		 */

		/*
		 * 拿到电的所有tagId,存放到list里面。
		 */

		System.out.println("getMysql_electricity===进来了！");
		/*
		 * listTagId = C3P0Util.getElectricity(); System.out.println(listTagId);
		 */

		Double totalValue1 = 0.0;
		String result = "";
		for (int j = 0; j <= listTagId.size() - 1; j++) {

			BasicDBObject timeStartObject = new BasicDBObject("timeStart", timeStart);
			BasicDBObject tagIdObj = new BasicDBObject("tagId", listTagId.get(j));

			BasicDBObject andObj = new BasicDBObject("$and", Arrays.asList(timeStartObject, tagIdObj));

			Cursor cursor = MongoDBUtil.getMongoCollection(collectionName).find(andObj);
			while (cursor.hasNext()) {
				DBObject objAll = cursor.next();
				Double totalValue = (Double) objAll.get("totalValue");
				System.out.println("当前tagId的totalValue为：" + totalValue);
				totalValue1 += totalValue;
				System.out.println(totalValue1);
			}
			// 小数点后取2位
			DecimalFormat df = new DecimalFormat("#.00");
			result = df.format(totalValue1);
			if (result.equals(".00")) {
				result = "0";
			}
		}

		return new NowData(result);
	}

	/**
	 * 跟换时间格式----去零
	 * 
	 * @param nowTime
	 * @return
	 */
	public static String[] changeFormatTime(String nowTime) {
		String changedTime = nowTime;
		String s1 = nowTime.substring(5, 6);
		System.out.println(s1);

		String s2 = nowTime.substring(8, 9);
		System.out.println(s2);

		int year = Integer.parseInt(nowTime.substring(0, 4));
		System.out.println(year);
		String yearTime = String.valueOf(year);

		int month = Integer.parseInt(nowTime.substring(5, 7));
		System.out.println(month);
		String monthTime = String.valueOf(month);

		int date = Integer.parseInt(nowTime.substring(8, 10));
		System.out.println(date);
		String dateTime = String.valueOf(date);

		String[] s = { yearTime, monthTime, dateTime };

		/*
		 * if(s1.equals("0") || s2.equals("0")) { if(s1.equals("0")) {
		 * changedTime =
		 * yearTime.concat("-").concat(monthTime).concat("-").concat(dateTime);
		 * System.out.println("changedTime========"+changedTime); }
		 * if(s2.equals("0")) { changedTime =
		 * yearTime.concat("-").concat(monthTime).concat("-").concat(dateTime);
		 * System.out.println("changedTime========"+changedTime); } }
		 */
		return s;
	}

	/**
	 * 返回指定时间段的历史数据
	 *
	 * @param collectionName
	 * @param tagId
	 */
	public static int getHistoricalTotal(String collectionName, String startTime, String endTime, int page,
			int pageSize) {
		HisValue n = null;
		List<HisValue> list = new LinkedList<HisValue>();

		String[] s = MongoDBUtil.changeFormatTime(startTime);
		int startYear = Integer.parseInt(s[0]);
		int startMonth = Integer.parseInt(s[1]);
		int startDay = Integer.parseInt(s[2]);

		System.out.println("startYear:" + startYear);
		System.out.println("startMonth:" + startMonth);
		System.out.println("startDay:" + startDay);

		String[] e = MongoDBUtil.changeFormatTime(endTime);
		int endYear = Integer.parseInt(e[0]);
		int endMonth = Integer.parseInt(e[1]);
		int endDay = Integer.parseInt(e[2]);

		System.out.println("endYear:" + endYear);
		System.out.println("endMonth:" + endMonth);
		System.out.println("endDay:" + endDay);

		System.out.println("page====" + page);
		System.out.println("pageSize===" + pageSize);

		for (int j = 1; j <= 12; j++) {
			for (int i = 1; i <= 31; i++) {

				if (j >= startMonth && j <= endMonth) {
					if (i >= startDay && i <= endDay) {
						System.out.println("i============" + i);

						BasicDBObject timeObj = new BasicDBObject("day", s[0] + '-' + j + '-' + i);
						BasicDBObject tagIdObj = new BasicDBObject("tagId", collectionName);
						BasicDBObject andObj = new BasicDBObject("$and", Arrays.asList(timeObj, tagIdObj));
						System.out.println("anobj==" + andObj);

						DBCursor cursor = MongoDBUtil.getMongoCollection(collectionName).find(andObj);
						System.out.println("cursor==" + cursor.hasNext());

						while (cursor.hasNext()) {
							DBObject obj = cursor.next();

							String timeStart = (String) obj.get("timeStart");
							System.out.println(obj.get("devicePropertiesValue"));
							BasicDBObject data = (BasicDBObject) obj.get("devicePropertiesValue");

							String front1 = timeStart.substring(11, 15); // 13:0
							String front2 = timeStart.substring(11, 14); // 13:
							System.out.println("front1=" + front1);
							System.out.println("front2=" + front2);
							System.out.println("============================================================");

							for (int k = 0; k <= 9; k++) {
								if (data.get(front1 + k + ":00") != null) {
									System.out.println(front1 + k + ":00");
									String values = (String) data.get(front1 + k + ":00");
									System.out.println(values);
									double result = Double.parseDouble(values);
									n = new HisValue(obj.get("day") + " " + front1 + k + ":00", values);
									list.add(n);
								}
							}
							System.out.println("****************************************");
							for (int m = 10; m <= 60; m++) {
								if (data.get(front2 + m + ":00") != null) {
									System.out.println(front2 + m + ":00");
									String values = (String) data.get(front2 + m + ":00");
									System.out.println(values);
									double result = Double.parseDouble(values);
									n = new HisValue(obj.get("day") + " " + front2 + m + ":00", values);
									list.add(n);
								}
							}

						}
					}
				}

			}
		}
		System.out.println("list.size" + list.size());
		System.out.println("list===" + list.toString());
		return list.size();
	}

	public static List<HisValue> getHistoricalConsumption(String collectionName, String startTime, String endTime,
			int page, int pageSize) {
		HisValue n = null;
		List<HisValue> list = new LinkedList<HisValue>();

		startTime = MongoDBUtil.getSTime(startTime, page - 1);

		String[] s = MongoDBUtil.changeFormatTime(startTime);
		int startYear = Integer.parseInt(s[0]);
		int startMonth = Integer.parseInt(s[1]);
		int startDay = Integer.parseInt(s[2]);

		System.out.println("startYear:" + startYear);
		System.out.println("startMonth:" + startMonth);
		System.out.println("startDay:" + startDay);

		String[] e = MongoDBUtil.changeFormatTime(endTime);
		int endYear = Integer.parseInt(e[0]);
		int endMonth = Integer.parseInt(e[1]);
		int endDay = Integer.parseInt(e[2]);

		System.out.println("endYear:" + endYear);
		System.out.println("endMonth:" + endMonth);
		System.out.println("endDay:" + endDay);

		System.out.println("page====" + page);
		System.out.println("pageSize===" + pageSize);

		int betweenYear = endYear - startYear;
		System.out.println("betweenYear=="+betweenYear);
		if (MongoDBUtil.compareTime(startTime, endTime)) {

			for (int z = 0; z < betweenYear+1; z++) {
				for (int j = 1; j <= 12; j++) {
					for (int i = 1; i <= 31; i++) {
						if (j == startMonth) {
							if (i == startDay) {
								System.out.println("i============" + i);
								BasicDBObject timeObj = new BasicDBObject("day", String.valueOf(startYear+z)+ '-' + j + '-' + i);
								BasicDBObject tagIdObj = new BasicDBObject("tagId", collectionName);
								BasicDBObject andObj = new BasicDBObject("$and", Arrays.asList(timeObj, tagIdObj));
								System.out.println("anobj==" + andObj);

								int num = MongoDBUtil.getMongoCollection(collectionName).find(andObj).count();
								System.out.println("num===" + num);
								DBCursor cursor = MongoDBUtil.getMongoCollection(collectionName).find(andObj);
								System.out.println("cursor==" + cursor.hasNext());

								while (cursor.hasNext()) {
									DBObject obj = cursor.next();

									String timeStart = (String) obj.get("timeStart");
									System.out.println(obj.get("devicePropertiesValue"));
									BasicDBObject data = (BasicDBObject) obj.get("devicePropertiesValue");

									String front1 = timeStart.substring(11, 15); // 13:0
									String front2 = timeStart.substring(11, 14); // 13:
									System.out.println("front1=" + front1);
									System.out.println("front2=" + front2);
									System.out.println("============================================================");

									for (int k = 0; k <= 9; k++) {
										if (data.get(front1 + k + ":00") != null) {
											System.out.println(front1 + k + ":00");
											String values = (String) data.get(front1 + k + ":00");
											System.out.println(values);
											double result = Double.parseDouble(values);
											n = new HisValue(obj.get("day") + " " + front1 + k + ":00", values);
											list.add(n);
										}
									}
									System.out.println("****************************************");
									for (int m = 10; m <= 60; m++) {
										if (data.get(front2 + m + ":00") != null) {
											System.out.println(front2 + m + ":00");
											String values = (String) data.get(front2 + m + ":00");
											System.out.println(values);
											double result = Double.parseDouble(values);
											n = new HisValue(obj.get("day") + " " + front2 + m + ":00", values);
											list.add(n);
										}
									}

								}
							}
						}

					}
				}

			}
			System.out.println("list.size" + list.size());
			System.out.println("list===" + list.toString());
			return list;
		} else {
			System.out.println("傻逼了，超过时间了，点毛毛啊点！");
			return null;
		}
	}

	public static String getSTime(String startDate, int page) {
		int year = Integer.parseInt(startDate.substring(0, 4));
		System.out.println(year);

		int month = Integer.parseInt(startDate.substring(5, 7));
		System.out.println(month);

		int date = Integer.parseInt(startDate.substring(8, 10));
		System.out.println(date);

		Calendar c = Calendar.getInstance();
		c.set(year, month - 1, date);
		System.out.println("当前时间假设为：");
		c.add(Calendar.DATE, page);
		String STime = (new SimpleDateFormat("yyyy-MM-dd")).format(c.getTime());
		System.out.println(STime);
		System.out.println("***********************************************************");

		return STime;
	}

	public static String getETime(String startDate) {
		int year = Integer.parseInt(startDate.substring(0, 4));
		System.out.println(year);

		int month = Integer.parseInt(startDate.substring(5, 7));
		System.out.println(month);

		int date = Integer.parseInt(startDate.substring(8, 10));
		System.out.println(date);

		int hourOfDay = Integer.parseInt(startDate.substring(11, 13));
		System.out.println(hourOfDay);

		int minute = Integer.parseInt(startDate.substring(14, 16));
		System.out.println(minute);

		Calendar c = Calendar.getInstance();
		c.set(year, month - 1, date, hourOfDay, minute);
		System.out.println("当前时间假设为：");
		c.add(Calendar.DATE, 1);
		String STime = (new SimpleDateFormat("yyyy-MM-dd")).format(c.getTime());
		System.out.println(STime);
		System.out.println("***********************************************************");

		return STime;
	}

	/**
	 * 比较时间大小
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static Boolean compareTime(String startTime, String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		long millionSeconds1;
		long millionSeconds2;
		long result = -99999;
		try {
			millionSeconds1 = sdf.parse(startTime).getTime();
			System.out.println("毫秒数= ：" + millionSeconds1);
			millionSeconds2 = sdf.parse(endTime).getTime();
			System.out.println("毫秒数= ：" + millionSeconds2);
			result = millionSeconds2 - millionSeconds1;
			System.out.println(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 毫秒值
		if (result >= 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取上一年，i控制具体几年。
	 *
	 * @param nowTime
	 * @param i
	 * @return
	 */
	public static String getLastOneYear(String nowTime, int i) {
		int year = Integer.parseInt(nowTime.substring(0, 4));
		System.out.println(year);

		int month = Integer.parseInt(nowTime.substring(5, 7));
		System.out.println(month);

		int date = Integer.parseInt(nowTime.substring(8, 10));
		System.out.println(date);

		int hourOfDay = Integer.parseInt(nowTime.substring(11, 13));
		System.out.println(hourOfDay);

		int minute = Integer.parseInt(nowTime.substring(14, 16));
		System.out.println(minute);

		Calendar c = Calendar.getInstance();
		c.set(year, month - 1, date, hourOfDay, minute);
		System.out.println("当前时间假设为：");
		String nowTime2 = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(c.getTime());
		System.out.println(nowTime2);
		System.out.println("***********************************************************");

		c.add(Calendar.YEAR, -i);
		System.out.println("上一个年的时间为：");
		String lastOneHour = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(c.getTime());
		System.out.println(lastOneHour);

		String s = lastOneHour.substring(0, 13);
		System.out.println(s);
		String timeStart = s.concat(":00:00");
		System.out.println(timeStart);

		int year2 = Integer.parseInt(timeStart.substring(0, 4));
		System.out.println(year2);
		String newYear = String.valueOf(year2);
		System.out.println(newYear);
		return timeStart;
	}

	/**
	 * 获取上一个月的时间，i控制上几个月，比如i赋值2时，就是前2个月的时间。
	 *
	 * @param nowTime
	 * @param i
	 * @return
	 */
	public static String getLastOneMonth(String nowTime, int i) {
		int year = Integer.parseInt(nowTime.substring(0, 4));
		System.out.println(year);

		int month = Integer.parseInt(nowTime.substring(5, 7));
		System.out.println(month);

		int date = Integer.parseInt(nowTime.substring(8, 10));
		System.out.println(date);

		int hourOfDay = Integer.parseInt(nowTime.substring(11, 13));
		System.out.println(hourOfDay);

		int minute = Integer.parseInt(nowTime.substring(14, 16));
		System.out.println(minute);

		Calendar c = Calendar.getInstance();
		c.set(year, month - 1, date, hourOfDay, minute);
		System.out.println("当前时间假设为：");
		String nowTime2 = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(c.getTime());
		System.out.println(nowTime2);
		System.out.println("***********************************************************");

		c.add(Calendar.MONTH, -i);
		System.out.println("上一个个月的时间为：");
		String lastOneHour = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(c.getTime());
		System.out.println(lastOneHour);

		String s = lastOneHour.substring(0, 13);
		System.out.println(s);
		String timeStart = s.concat(":00:00");
		System.out.println(timeStart);

		int year2 = Integer.parseInt(timeStart.substring(0, 4));
		System.out.println(year2);
		String newYear = String.valueOf(year2);
		System.out.println(newYear);

		int month2 = Integer.parseInt(timeStart.substring(5, 7));
		System.out.println(month2);
		String newMonth = String.valueOf(year2).concat("-").concat(String.valueOf(month2));
		System.out.println(newMonth);

		return newMonth;
	}

	/**
	 * 获取上一月的数据总和
	 *
	 * @param collectionName
	 * @param lastOneMonth
	 */
	public static String getLastOneMonthTotalFormStatstics(String collectionName, String lastOneMonth,
			List<String> listTagId) {
		Map<String, String> map = new LinkedHashMap();

		// List<String> listTagId = new ArrayList<String>();

		List<LastOneDay> listLastOneDay = new LinkedList<>();

		System.out.println("getMysql_electricity===进来了！");
		// listTagId = C3P0Util.getElectricity();
		// System.out.println(listTagId);
		Double totalValue23 = 0.0;
		String result = "";
		for (int j = 0; j <= listTagId.size() - 1; j++) {
			BasicDBObject timeStartObject = new BasicDBObject("month", lastOneMonth);
			BasicDBObject tagIdObj = new BasicDBObject("tagId", listTagId.get(j));
			BasicDBObject andObj = new BasicDBObject("$and", Arrays.asList(timeStartObject, tagIdObj));
			Cursor cursor = MongoDBUtil.getMongoCollection(collectionName).find(andObj);
			while (cursor.hasNext()) {
				DBObject obj = cursor.next();
				System.out.println(obj.toString());
				Double totalValue = (Double) obj.get("totalValue");
				System.out.println("当前tagId的totalValue为：" + totalValue);
				totalValue23 += totalValue;
				System.out.println(totalValue23);
			}
			// 小数点后取2位
			DecimalFormat df = new DecimalFormat("#.00");
			result = df.format(totalValue23);
			if (result.equals(".00")) {
				result = "0";
			}
			System.out.println("result======" + result);
			// map.put(timeStart, result);
			// System.out.println("totalValue23 ==================== " +
			// totalValue23);
		}
		return result;
	}

	/**
	 * 计算两个日期之间相差的天数
	 * 
	 * @param smdate
	 *            较小的时间
	 * @param bdate
	 *            较大的时间
	 * @return 相差天数
	 * @throws ParseException
	 */
	public static int daysBetween(Date smdate, Date bdate) throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		smdate = sdf.parse(sdf.format(smdate));
		bdate = sdf.parse(sdf.format(bdate));
		Calendar cal = Calendar.getInstance();
		cal.setTime(smdate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(bdate);
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * 字符串的日期格式的计算
	 */
	public static int daysBetween(String smdate, String bdate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse(smdate));
		long time1 = cal.getTimeInMillis();
		cal.setTime(sdf.parse(bdate));
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return Integer.parseInt(String.valueOf(between_days));
	}
}
