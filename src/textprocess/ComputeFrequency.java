package textprocess;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ComputeFrequency {
	Map<String,Integer> computeWordFrequencies(List<String> tokenlist)
	{
		Map<String,Integer> freqcount=new LinkedHashMap<String,Integer>();
		for(String str:tokenlist)
		{
			if(freqcount.containsKey(str))
			{
				freqcount.put(str,freqcount.get(str)+1);//if the token is already present, increase its count
			}
			else
			{
				freqcount.put(str, 1);//new token, count assigned to 1
			}
		}
		 
		 
			    
		return freqcount;
	}
	Map<String,Integer> computeTwoGramFrequencies(List<String> tokens)
	{
		Map<String,Integer> freqcount_2gram=new LinkedHashMap<String,Integer>();
		for(int i=0;i<tokens.size()-1;i++)
		{
			String str=tokens.get(i)+" "+tokens.get(i+1);//concatenating two adjacent strings to get the 2 grams
			if(freqcount_2gram.containsKey(str))
			{
				freqcount_2gram.put((str),freqcount_2gram.get(str)+1);//2-grams already present,increase the count
			}
			else
			{
				freqcount_2gram.put(str, 1);//new 2-gram, count assigned to 1
			}
		}
		 
		 
			    
		return freqcount_2gram;
		
		
	}
	Map<String,Integer> computePalindromeFrequencies(List<String> tokens)
	{
		Map<String,Integer> freqcount_palindromes=new LinkedHashMap<String,Integer>();
		int noOfTokens=tokens.size();
		for(int i=0;i<noOfTokens;i++)
		{
			
		}
		
		return freqcount_palindromes;
	}
}
