package com.gengo.gengoscraper.archivedjobs.util;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * Utility class to deal with HtmlUnit's web client.
 * 
 * @author Juan García Heredero
 *
 */
public class WebClientUtil {
	
	/**
	 * Creates a new HtmlUnit's web client with the desired options.
	 * 
	 * @return New web client.
	 */
	public static WebClient openWebClient() {
		WebClient webClient = new WebClient(BrowserVersion.CHROME);
		webClient.getOptions().setJavaScriptEnabled(false);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setUseInsecureSSL(true);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getCookieManager().setCookiesEnabled(true);
		
		return webClient;
	}

	/**
	 * Closes a web client.
	 * 
	 * @param webClient The web client to close.
	 */
	public static void closeWebClient(WebClient webClient) {
		webClient.close();
	}
}
