package crawler;


import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import textprocess.ComputeFrequency;
import textprocess.Tokenize;
import mongo.MongoDBJDBC;

import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class Document {
	
	public static void addDocument(String url,String text) throws Exception
	{
		 DB db = MongoDBJDBC.connectToMongo();
		 DBCollection coll = db.getCollection("documents");
		 BasicDBObject query = new BasicDBObject();
		 
		 List<String> tokens=Tokenize.tokenizeString(text);
		 Map<String,Integer> freq=ComputeFrequency.computeWordFrequencies(tokens);
		 
		 query.put("url",url);
		 query.put("document",tokens);
		 query.put("count", tokens.size());
		 
		 coll.insert(query);
	}
	private static void addfreq(DBCollection coll,Map<String,Integer> fcount)
	{
		DBObject tokencount=new BasicDBObject();
		DBObject updt=new BasicDBObject();
		Iterator<Entry<String, Integer>> it =fcount.entrySet().iterator();
		
	    while (it.hasNext()) {
	        Map.Entry<String,Integer> pairs = (Map.Entry<String,Integer>)it.next();
	        tokencount.put("name",pairs.getKey());
	        tokencount.put("count",pairs.getValue());
	        it.remove(); // avoids a ConcurrentModificationException
	        
	       // coll.update(q, o)
	    }
	    
	}

}
