package textprocess;


import java.io.IOException;

import mongo.MongoDBJDBC;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;



public class Subdomain {
	public Subdomain() throws IOException
	{
		
	}

	public static void addSubDomain(String subdomain) throws Exception
	{
		     DB db = MongoDBJDBC.connectToMongo();
			 DBCollection coll = db.getCollection("subdomain");
			 BasicDBObject query = new BasicDBObject();
			 query.put("address",subdomain);
			 coll.insert(query);
	         
		
	}
	public static void aggregatestore() throws Exception
	{
		DB db = MongoDBJDBC.connectToMongo();
		DBCollection coll = db.getCollection("subdomain");
		    DBObject groupFields = new BasicDBObject( "_id", "$address");

		
		    groupFields.put("count", new BasicDBObject( "$sum", 1));
		    DBObject group = new BasicDBObject("$group", groupFields );
		    
		    DBObject sortFields = new BasicDBObject("count", -1);
		    DBObject sort = new BasicDBObject("$sort", sortFields );

		    @SuppressWarnings("deprecation")
		    AggregationOutput output = coll.aggregate(group, sort);

		    for (DBObject result : output.results()) {
		        System.out.println(result);
		    }
	}
	
	
	
	
}
