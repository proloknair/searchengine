package crawler;

import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.Header;

import textprocess.Subdomain;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class BasicCrawler extends WebCrawler {
	private final static Pattern BINARY_FILES_EXTENSIONS =
	        Pattern.compile(".*\\.(bmp|gif|jpe?g|png|tiff?|pdf|ico|xaml|pict|rif|pptx?|ps" +
	        "|mid|mp2|mp3|mp4|wav|wma|au|aiff|flac|ogg|3gp|aac|amr|au|vox" +
	        "|avi|mov|mpe?g|ra?m|m4v|smil|wm?v|swf|aaf|asf|flv|mkv" +
	        "|zip|rar|gz|7z|aac|ace|alz|apk|arc|arj|dmg|jar|lzip|lha)" +
	        "(\\?.*)?$"); // For url Query parts ( URL?q=... )

	  /**
	   * You should implement this function to specify whether the given url
	   * should be crawled or not (based on your crawling logic).
	   */
	  @Override
	  public boolean shouldVisit(Page page, WebURL url) {
	    String href = url.getURL().toLowerCase();

	    return !BINARY_FILES_EXTENSIONS.matcher(href).matches() && href.startsWith("http://www.ics.uci.edu/");
	  }

	  /**
	   * This function is called when a page is fetched and ready to be processed
	   * by your program.
	   */
	  @Override
	  public void visit(Page page) {
	    int docid = page.getWebURL().getDocid();
	    String url = page.getWebURL().getURL();
	    String domain = page.getWebURL().getDomain();
	    String path = page.getWebURL().getPath();
	    String subDomain = page.getWebURL().getSubDomain();
	    String parentUrl = page.getWebURL().getParentUrl();
	    String anchor = page.getWebURL().getAnchor();
	    
	    try {
			Subdomain.addSubDomain(subDomain);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    

	    logger.debug("Docid: {}", docid);
	    logger.info("URL: ", url);
	    logger.debug("Domain: '{}'", domain);
	    logger.debug("Sub-domain: '{}'", subDomain);
	    logger.debug("Path: '{}'", path);
	    logger.debug("Parent page: {}", parentUrl);
	    logger.debug("Anchor text: {}", anchor);

	    if (page.getParseData() instanceof HtmlParseData) {
	      HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
	      String text = htmlParseData.getText();
	      String html = htmlParseData.getHtml();
	      Set<WebURL> links = htmlParseData.getOutgoingUrls();
	      
	      try {
			Document.addDocument(url, text);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	      logger.debug("Text length: {}", text.length());
	      logger.debug("Html length: {}", html.length());
	      logger.debug("Number of outgoing links: {}", links.size());
	    }

	    Header[] responseHeaders = page.getFetchResponseHeaders();
	    if (responseHeaders != null) {
	      logger.debug("Response headers:");
	      for (Header header : responseHeaders) {
	        logger.debug("\t{}: {}", header.getName(), header.getValue());
	      }
	    }

	    logger.debug("=============");
	  }
	  
}
