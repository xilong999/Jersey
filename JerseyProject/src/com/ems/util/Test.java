package com.ems.util;

public class Test {

	public static String getName(String name){
		return name;
	}
	
	
	/**
	 * mongodb分页查询
	 * 
	 * （1）通过sike()与limit()方法------比较简单，但skip方法效率较低，
	 *     通过 db.myCollection.find().sort({"ID":1}).skip(10).limit(10)命令，
	 *     将其根据ID排序后，跳过10，查询10条，结果为10-19条的数据。
	 *     
	 * （2）获取前一页的最后一条记录，查询之后的指定条记录-----在数据量较多的情况下使用，但可能会占用较多的内存空间
	 * 	       在tmp中存储前面的10条记录，latest中存储前一页的最后一条记录（第9条），
	 *     在接下去的查询中，通过find后的参数，过滤去之前的记录，获取之后的10条记录。
	 *     
	 *     var temp = db.myCollection.find().sort({"ID":1}).limit(10)
	 *     var latest = null
	 *     while(temp.hasNext()){
	 *     		latest = temp.next();
	 *     }
	 *     
	 *     db.myCollection.find({"ID":{"$gt":latest.ID}}).sort({"ID":1}).limit(10)
	 * 		
	 * 		
	 * 
	 */
	
	/** 
     * 大数据量数据分页优化 
     * @param page -------------1、2、3......
     * @param pageSize ---------10
     * @param lastId 上一页的最大id --------page*pageSize-1
     * @return 
     */  
   /* public List<User> largePageList(int page, int pageSize, int lastId) {  
        DB myMongo = MongoManager.getDB("myMongo");  
        DBCollection userCollection = myMongo.getCollection("user");  
        
        DBCursor limit = null;  
        if (page == 1) {  
            limit = userCollection.find()  
                    .sort(new BasicDBObject("id", 1)).limit(pageSize);  
        } else {  
            limit = userCollection  
                    .find(new BasicDBObject("id", new BasicDBObject(  
                            QueryOperators.GT, lastId)))  
                    .sort(new BasicDBObject("id", 1)).limit(pageSize);  
        }  
  
        List<User> userList = new ArrayList<User>();  
        while (limit.hasNext()) {  
            User user = new User();  
            user.parse(limit.next());  
            userList.add(user);  
        }  
        return userList;  
    }  */
}
