package textprocess;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ComputeFrequency {
	private static int longestpalindrome=17826; //length of the longest existing palindrome phrase
	private static Set<String> stopWords=null;
	private static final String StopFile="StopWords.txt";
	private static void InitializeStopWords() throws IOException
	{
		
		if (stopWords==null)
		{
			stopWords=new HashSet<String>();
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(StopFile),"US-ASCII"));
			String line;
			while((line=br.readLine())!=null)
			{
				if(!stopWords.contains(line))
				{
					stopWords.add(line);
				}
			}
			br.close();
		}
	}
	public static Map<String,Integer> computeWordFrequencies(List<String> tokenlist) throws IOException
	
	{
		InitializeStopWords();
		Map<String,Integer> freqcount=new LinkedHashMap<String,Integer>();
		for(String str:tokenlist)
		{
			if(!stopWords.contains(str))
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
		}
		 
		if(freqcount.isEmpty())
			System.out.println("The file does not contain any valid tokens"); 
			    
		return freqcount;
	}
	
	public static Map<String,Integer> computeTwoGramFrequencies(List<String> tokens) throws IOException
	{
		InitializeStopWords();
		Map<String,Integer> freqcount_2gram=new LinkedHashMap<String,Integer>();
		for(int i=0;i<tokens.size()-1;i++)
		{
			String token1=tokens.get(i);
			String token2=tokens.get(i+1);
			if(!(stopWords.contains(token1)||stopWords.contains(token2)))
			{
				String str=token1+" "+token2;//concatenating two adjacent strings to get the 2 grams
				
				if(freqcount_2gram.containsKey(str))
				{
					freqcount_2gram.put((str),freqcount_2gram.get(str)+1);//2-grams already present,increase the count
				}
				else
				{
					freqcount_2gram.put(str, 1);//new 2-gram, count assigned to 1
				}
			}
		}
		 
		if(freqcount_2gram.isEmpty())
			System.out.println("The file does not contain any valid 2-grams");
			    
		return freqcount_2gram;
		
		
	}
	public static Map<String,Integer> computePalindromeFrequencies(List<String> tokens)
	{
		Map<String,Integer> freqcount_palindromes=new LinkedHashMap<String,Integer>();
		int noOfTokens=tokens.size();
	
		for(int i=0;i<noOfTokens;i++)
		{
			StringBuilder strPal=new StringBuilder("");//String takes n square to concatenate,stringbuilder does in linear time
			
			for(int j=i;j<noOfTokens;j++)
			{
				if((j-i)>longestpalindrome)
					break;
				strPal.append(" ").append(tokens.get(j));
				
				if(palindromecheck(strPal))
				{
					String strPal_temp=strPal.toString().trim();//converting the stringbuilder back to string(only the palindromes are converted)
					if(strPal_temp.length()!=1)// single letter words are eliminated
					{
						if(freqcount_palindromes.containsKey(strPal_temp))
						{
							freqcount_palindromes.put((strPal_temp),freqcount_palindromes.get(strPal_temp)+1);//Palindrome already present,increase the count
						}
						else
						{
							freqcount_palindromes.put(strPal_temp, 1);//new palindrome, count assigned to 1
						}
					}
				}
			}
			
		}
		if(freqcount_palindromes.isEmpty())
			System.out.println("The file does not contain any valid palindromes");
		return freqcount_palindromes;
	}
	private static boolean palindromecheck(StringBuilder str)//check whether the string is palindrome or not.
	{
		int length=str.length();
	
		
		int i=0,j=length-1;
		while(i<j)
		{
			if(str.charAt(i)==' ')
				i++;
			if(str.charAt(j)==' ')
				j--;
			if(str.charAt(i)!=str.charAt(j))
			{
				return false;
			}
			i++;
			j--;
				
		}
		return true;
	}
}
