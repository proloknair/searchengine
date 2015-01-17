package textprocess;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Tokenize {
	List<String> tokenizeFile(String TextFile) throws IOException
	{
		List<String> tokenslist=new ArrayList<String>();
		BufferedReader br=null;
		try{
			
			br=new BufferedReader(new InputStreamReader(new FileInputStream(TextFile),"US-ASCII"));
			String line;
			
			while((line=br.readLine())!=null)
			{
				Matcher m = Pattern.compile("[a-zA-Z0-9]+").matcher(line);
				while(m.find())
				{
					tokenslist.add(m.group().toLowerCase());
				}
			}
			
			
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return tokenslist;
	}
	Map<String,Integer> computeWordFrequencies(List<String> tokenlist)
	{
		Map<String,Integer> freqcount=new HashMap<String,Integer>();
		for(String str:tokenlist)
		{
			if(freqcount.containsKey(str))
			{
				freqcount.put(str,freqcount.get(str)+1);
			}
			else
			{
				freqcount.put(str, 1);
			}
		}
		 
		 
			    
		return freqcount;
	}
	Map<String,Integer> computeTwoGramFrequencies(List<String> tokens)
	{
		Map<String,Integer> freqcount_2gram=new HashMap<String,Integer>();
		for(int i=0;i<tokens.size()-1;i++)
		{
			String str=tokens.get(i)+" "+tokens.get(i+1);
			if(freqcount_2gram.containsKey(str))
			{
				freqcount_2gram.put((str),freqcount_2gram.get(str)+1);
			}
			else
			{
				freqcount_2gram.put(str, 1);
			}
		}
		 
		 
			    
		return freqcount_2gram;
		
		
	}
	void print(List<String> tokenlist)
	{
		for(String str:tokenlist)
		{
			System.out.println(str);
		}
		
	}
	void print(Map<String,Integer> fcount)
	{
		List<Map.Entry<String,Integer>> list = new LinkedList<>( fcount.entrySet() );
	    Collections.sort( list, new Comparator<Map.Entry<String,Integer>>()
	    {
	        @Override
	        public int compare( Map.Entry<String,Integer> m1, Map.Entry<String,Integer> m2 )
	        {
	            return (m2.getValue()).compareTo( m1.getValue() );
	        }

		} );

	    
	    for (Map.Entry<String,Integer> entry : list)
	    {
	    	System.out.println( entry.getKey()+" : "+entry.getValue());
	    }
		
	}
	
	public static void main(String[] args) throws IOException {
		Tokenize tok = new Tokenize();
		List<String> tokens=tok.tokenizeFile("/Users/proloknair/Dropbox/IR-Proj/SearchEngine/Text.txt");
		Map<String,Integer> fcount=tok.computeTwoGramFrequencies(tokens);
		tok.print(fcount);

	}

}
