package crawler;


import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Arrays;

import org.bson.BSONObject;

import textprocess.ComputeFrequency;
import textprocess.Tokenize;
import mongo.MongoDBJDBC;

import com.mongodb.AggregationOptions;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import com.mongodb.util.JSON;

public class Document {
	private static final String document="document";
	private static final String gram="gram";
	private static final String word="word";
	private static final String twogram="twogram";
	private static final String wordcoll="wordcounts";
	private static final String gramscoll="gramscount";
	
	public static void addDocument(String url,String subdomain,String text) throws Exception
	{
		 DB db = MongoDBJDBC.connectToMongo();
		 DBCollection coll = db.getCollection("documents");
		 
		 
		 List<String> tokens=Tokenize.tokenizeString(text);
		 Map<String,Integer> freq=ComputeFrequency.computeWordFrequencies(tokens);
		 Map<String,Integer> freq2gram=ComputeFrequency.computeTwoGramFrequencies(tokens);
		 int sizeToken=tokens.size();
		 if(sizeToken>=2)
		 { StringBuilder dson=new StringBuilder("{'url':'"+url+"',");
		 dson.append("'subdomain':'"+subdomain+"',");
		 dson.append("'count':'"+sizeToken+"',");
		 
		 dson.append(addfreq(freq,document,word)).append(",").append(addfreq(freq2gram,gram,twogram)).append("}");
		 BasicDBObject query=(BasicDBObject) JSON.parse(dson.toString());
		 
		 
		 coll.insert(query);
		 }
	}
	private static String addfreq(Map<String,Integer> fcount,String name,String nestedname)
	{
		
		Iterator<Entry<String, Integer>> it =fcount.entrySet().iterator();
		StringBuilder dson=new StringBuilder("'"+name+"':[");
		
	    while (it.hasNext()) {
	        Map.Entry<String,Integer> pairs = (Map.Entry<String,Integer>)it.next();
	        dson.append("{'"+nestedname+"':'"+pairs.getKey()+"', 'count':"+pairs.getValue()+"},");
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
	    DBObject limit=new BasicDBObject("$limit",500);
	    AggregationOptions agg = AggregationOptions.builder().allowDiskUse(true).build();

	   
	    List<DBObject> pipeline = Arrays.asList(unwind, group, sort,limit);
	    Cursor output = coll.aggregate(pipeline,agg);
	    int counter=1;
	    PrintWriter writer = new PrintWriter("CommonWords_I.txt", "UTF-8");
	    while (output.hasNext()) {
	    	if (counter>500)
	        	break;
	        writer.println(counter+":"+output.next().toString());
	        counter=counter+1;
	        
	        
	    }
	    writer.close();
	    
			
	}
	public static void getAggregate2gram() throws Exception{
		DB db = MongoDBJDBC.connectToMongo();
		DBCollection coll = db.getCollection("documents");
		DBObject unwind=new BasicDBObject("$unwind","$gram");
		DBObject groupFields = new BasicDBObject( "_id", "$gram.twogram");

		
	    groupFields.put("count", new BasicDBObject( "$sum", "$gram.count"));
	    DBObject group = new BasicDBObject("$group", groupFields );
	    
	    DBObject sortFields = new BasicDBObject("count", -1);
	    DBObject sort = new BasicDBObject("$sort", sortFields );
	    DBObject limit=new BasicDBObject("$limit",20);
	    AggregationOptions agg = AggregationOptions.builder().allowDiskUse(true).build();

	   
	    List<DBObject> pipeline = Arrays.asList(unwind, group, sort,limit);
	    Cursor output = coll.aggregate(pipeline,agg);
	    int counter=1;
	    PrintWriter writer = new PrintWriter("TwoGrams_I.txt", "UTF-8");
	    while (output.hasNext()) {
	    	
	    	if (counter>20)
	        	break;
	        writer.println(counter+":"+output.next().toString());
	        counter=counter+1;
	        
	        
	    }
	    
	    writer.close();
	}
	public static void getAggregateSubdomain() throws Exception{
		DB db = MongoDBJDBC.connectToMongo();
		DBCollection coll = db.getCollection("documents");
		
		DBObject groupFields = new BasicDBObject( "_id", "$subdomain");

		
	    groupFields.put("count", new BasicDBObject( "$sum", 1));
	    DBObject group = new BasicDBObject("$group", groupFields );
	    
	    DBObject sortFields = new BasicDBObject("count", -1);
	    DBObject sort = new BasicDBObject("$sort", sortFields );
	   // DBObject limit=new BasicDBObject("$limit",20);
	    AggregationOptions agg = AggregationOptions.builder().allowDiskUse(true).build();

	   
	    List<DBObject> pipeline = Arrays.asList(group, sort);
	    Cursor output = coll.aggregate(pipeline,agg);
	    int counter=1;
	    PrintWriter writer = new PrintWriter("SubDomain_I.txt", "UTF-8");
	    while (output.hasNext()) {
	    	
	    	
	        DBObject it=output.next();
	       
	        
	        
	        writer.println("http://" + it.get("_id").toString() + ".uci.edu, " + it.get("count").toString());
	        
	        
	    }
	    writer.close();
			
	}
	public static void getMapReduceWord() throws Exception
	{
		DB db = MongoDBJDBC.connectToMongo();
		DBCollection coll = db.getCollection("documents");
		String map="function() {this."+document+".forEach(function(z){emit( z."+word+", z.count )});}";
		String reduce="function(key, values) {"+
					"  var total = 0;"+
					" values.forEach(function(z){total += z;});"+
					"  return total;}";
		MapReduceCommand cmd = new MapReduceCommand(coll, map, reduce, wordcoll, MapReduceCommand.OutputType.REPLACE, null);
		coll.mapReduce(cmd);
		DBCollection wordcounts=db.getCollection(wordcoll);
		DBCursor cur=wordcounts.find().sort(new BasicDBObject("value",-1)).limit(500);
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
	public static void getMapReduce2gram() throws Exception
	{
		DB db = MongoDBJDBC.connectToMongo();
		DBCollection coll = db.getCollection("documents");
		String map="function() {this."+gram+".forEach(function(z){emit( z."+twogram+", z.count )});}";
		String reduce="function(key, values) {"+
					"  var total = 0;"+
					" values.forEach(function(z){total += z;});"+
					"  return total;}";
		MapReduceCommand cmd = new MapReduceCommand(coll, map, reduce, gramscoll, MapReduceCommand.OutputType.REPLACE, null);
		coll.mapReduce(cmd);
		DBCollection wordcounts=db.getCollection(gramscoll);
		DBCursor cur=wordcounts.find().sort(new BasicDBObject("value",-1)).limit(20);
		int counter=1;
		try
		{
			while(cur.hasNext()) 
			{
		    	if (counter>20)
		        	break;
		        System.out.println(counter+":"+cur.next());
		        counter=counter+1;     
		    }
		}
		finally{
			cur.close();
		}
	}
	public static void subDomains() throws Exception
	{
		DB db = MongoDBJDBC.connectToMongo();
		DBCollection coll = db.getCollection("documents");
		PrintWriter writer = new PrintWriter("SubDomain.txt", "UTF-8");
		
		List subd=coll.distinct("subdomain");
		
		for(Object s:subd)
		{
			writer.println(s.toString());
		}
		writer.close();
		
	}
	public static void uniqueUrls() throws Exception
	{
		DB db =MongoDBJDBC.connectToMongo();
		DBCollection coll=db.getCollection("documents");
		DBObject sortbyWordCount = new BasicDBObject("count",-1);
		System.out.println("Number of unique urls:"+coll.distinct("url").size());
		
		DBCursor iter = coll.find().sort(sortbyWordCount).limit(1);
		DBObject it=iter.next();
		System.out.println("Longest page:"+it.get("url").toString());
        System.out.println("count:"+it.get("count").toString() );
		
	}
	
}
