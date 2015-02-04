package crawler;


import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Arrays;

import org.bson.BSONObject;

import textprocess.ComputeFrequency;
import textprocess.Tokenize;
import mongo.MongoDBJDBC;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import com.mongodb.util.JSON;

public class Document {
	
	public static void addDocument(String url,String text) throws Exception
	{
		 DB db = MongoDBJDBC.connectToMongo();
		 DBCollection coll = db.getCollection("documents");
		 
		 
		 List<String> tokens=Tokenize.tokenizeString(text);
		 Map<String,Integer> freq=ComputeFrequency.computeWordFrequencies(tokens);
		  
		 
		 
		 StringBuilder dson=new StringBuilder("{'url':'"+url+"',");
		 dson.append(addfreq(freq)).append("}");
		 BasicDBObject query=(BasicDBObject) JSON.parse(dson.toString());
		 
		 
		 coll.insert(query);
	}
	private static String addfreq(Map<String,Integer> fcount)
	{
		
		
		Iterator<Entry<String, Integer>> it =fcount.entrySet().iterator();
		StringBuilder dson=new StringBuilder("'document':[");
		
	    while (it.hasNext()) {
	        Map.Entry<String,Integer> pairs = (Map.Entry<String,Integer>)it.next();
	        dson.append("{'word':'"+pairs.getKey()+"', 'count':"+pairs.getValue()+"},");
	        it.remove(); // avoids a ConcurrentModificationException
	        
	       // coll.update(q, o)
	    }
	    dson.deleteCharAt(dson.length()-1).append("]");
	    
	    
	    return dson.toString();
	    
	}
	public static void getAggregateData() throws Exception{
		DB db = MongoDBJDBC.connectToMongo();
		DBCollection coll = db.getCollection("documents");
		DBObject unwind=new BasicDBObject("$unwind","$document");
		DBObject groupFields = new BasicDBObject( "_id", "$document.word");

		
	    groupFields.put("count", new BasicDBObject( "$sum", "$document.count"));
	    DBObject group = new BasicDBObject("$group", groupFields );
	    
	    DBObject sortFields = new BasicDBObject("count", -1);
	    DBObject sort = new BasicDBObject("$sort", sortFields );

	   
	    List<DBObject> pipeline = Arrays.asList(unwind, group, sort);
	    AggregationOutput output = coll.aggregate(pipeline);
	    int counter=1;
	    for (DBObject result : output.results()) {
	    	if (counter>500)
	        	break;
	        System.out.println(counter+":"+result);
	        counter=counter+1;
	        
	        
	    }
			
	}
	public static void getMapReduceWord() throws Exception{
		DB db = MongoDBJDBC.connectToMongo();
		DBCollection coll = db.getCollection("documents");
		String map="function() {this.document.forEach(function(z){emit( z.word, z.count )});}";
		String reduce="function(key, values) {"+
					"  var total = 0;"+
					" values.forEach(function(z){total += z;});"+
					"  return total;}";
		MapReduceCommand cmd = new MapReduceCommand(coll, map, reduce, "wordcounts", MapReduceCommand.OutputType.REPLACE, null);
		coll.mapReduce(cmd);
		DBCollection wordcounts=db.getCollection("wordcounts");
		DBCursor cur=wordcounts.find().sort(new BasicDBObject("value",-1));
		int counter=1;
		try
		{
			while(cur.hasNext()) 
			{
		    	if (counter>500)
		        	break;
		        System.out.println(counter+":"+cur.next());
		        counter=counter+1;
		        
		        
		    }
		}
		finally{
			cur.close();
		}
	}
	
}
