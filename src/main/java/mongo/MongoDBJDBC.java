package mongo;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoDBJDBC{
	private static MongoClient client = null;

	public static DB connectToMongo() throws Exception {
	    if (client != null) {
	        return client.getDB("Crawler");
	    }       
	    client = new MongoClient("localhost" , 27017);                
	    DB db=client.getDB("Crawler");
	   // db.getCollection("subdomain").drop();
	  //  db.getCollection("documents").drop();
	    
	    return db;
		}

	}
