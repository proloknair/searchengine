package textprocess;
import java.util.*;
import java.io.*;
import java.util.regex.*;

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
				Matcher m = Pattern.compile("\\w+").matcher(line);
				while(m.find())
				{
					tokenslist.add(m.group());
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
	void print(List<String> tokens )
	{
		for(String str:tokens)
		{
			System.out.println(str);
		}
		
	}
	
	public static void main(String[] args) throws IOException {
		Tokenize tok = new Tokenize();
		List<String> tokens=tok.tokenizeFile("/Users/proloknair/Dropbox/IR-Proj/SearchEngine/Text.txt");
		tok.print(tokens);

	}

}
