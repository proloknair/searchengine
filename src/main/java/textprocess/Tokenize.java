package textprocess;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenize {
	public static List<String> tokenizeFile(String TextFile) throws IOException
	{
		List<String> tokenslist=new ArrayList<String>();
		BufferedReader br=null;
		try{
			
			br=new BufferedReader(new InputStreamReader(new FileInputStream(TextFile),"US-ASCII")); //reading from file
			String line;
			Matcher m;
			
			while((line=br.readLine())!=null)
			{
				m = Pattern.compile("[a-zA-Z0-9]+").matcher(line); //using regex to find all the tokens
				while(m.find())
				{
					tokenslist.add(m.group().toLowerCase()); //adding everything to tokenlist
				}
			}
			
			
		}catch(IOException e)
		{
			e.printStackTrace();
			if(e instanceof FileNotFoundException)
			{
				System.out.println("File Not Found");
			}
			
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

	
	
	public static void main(String[] args) throws IOException {
		long startTime = System.nanoTime();
		List<String> tokens=Tokenize.tokenizeFile("/Users/proloknair/Dropbox/IR-Proj/SearchEngine/Text.txt");
	    
		PrintToConsole.print(ComputeFrequency.computePalindromeFrequencies(tokens));
		
		long endTime = System.nanoTime();
		System.out.println("Took "+(endTime - startTime) + " ns"); 

	}

}
