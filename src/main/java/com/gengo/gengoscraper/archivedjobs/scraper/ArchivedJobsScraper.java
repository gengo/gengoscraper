package com.gengo.gengoscraper.archivedjobs.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.AuthenticationException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlListItem;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gengo.gengoscraper.archivedjobs.adapter.ArchivedJobAdapter;
import com.gengo.gengoscraper.archivedjobs.model.ArchivedJob;
import com.gengo.gengoscraper.archivedjobs.util.WebClientUtil;


/**
 * Scraper to get info about archived jobs from Gengo. The parameters
 * will be taken from file 'scraper.properties' if they are not
 * passed directly when running the program.
 * 
 * @author Juan García Heredero
 *
 */
@Component
@PropertySource("scraper.properties")
public class ArchivedJobsScraper {
	
	@Value("${gengo.user}")
	private String email;
	@Value("${gengo.password}")
	private String password;
	@Value("${gengo.max.pages}")
	private int maxPages;
	@Value("${gengo.results.file}")
	private String pathToFile;
	private final String GENGO_LOGIN_SUBSTRING = "/auth/form/login";
	private final String GENGO_DASHBOARD_PAGE = "http://gengo.com/t/dashboard";
	private final String GENGO_COMPLETED_JOBS_PAGE = "https://gengo.com/t/jobs/status/completed/";
	private final Logger LOG = LoggerFactory.getLogger(ArchivedJobsScraper.class);

	/**
	 * Retrieves a list of archived jobs.
	 * 
	 * @return List of archived jobs.
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ParseException
	 * @throws AuthenticationException 
	 */
	public List<ArchivedJob> listArchivedJobs()
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, ParseException, AuthenticationException {
		List<ArchivedJob> result = new ArrayList<ArchivedJob>();

		// Check that credentials are not empty
		if (StringUtils.isEmpty(email) || StringUtils.isEmpty(password)) {
			throw new AuthenticationException("User and password cannot be empty");
		}
		
		LOG.info("Retrieving up to " + maxPages + " pages of completed jobs for user " + email);

		WebClient webClient = WebClientUtil.openWebClient();
		login(webClient);
		HtmlPage page = getCompletedJobsPage(webClient);
		int numPages=0;
		do {
			result.addAll(retrieveCompletedJobs(page));
			page = getNextPage(page);
		} while (++numPages < maxPages && page != null);
		
		WebClientUtil.closeWebClient(webClient);

		LOG.info("Retrieval finished. Results stored in file " + pathToFile);
		return result;
	}

	/**
	 * Tries to login into Gengo. It does not really know if the
	 * login is successful or not; should be checked later.
	 * 
	 * @param webClient
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private void login(WebClient webClient) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		LOG.info("Logging into Gengo...");
		HtmlPage page = webClient.getPage(GENGO_DASHBOARD_PAGE);
		HtmlTextInput userInput = page.getFirstByXPath("//input[@name='login_email']");
		userInput.type(email);
		HtmlPasswordInput passwordInput = page.getFirstByXPath("//input[@type='password']");
		passwordInput.type(password);
		HtmlButton submitButton = page.getFirstByXPath("//button[text()='Sign in']");
		submitButton.click();
	}

	/**
	 * Retrieves the initial page of completed (archived) jobs. It can
	 * be retrieved only if the login was correct.
	 * 
	 * @param webClient
	 *            Web client.
	 * @return Page of completed (archived) jobs.
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws AuthenticationException If credentials are not valid
	 */
	private HtmlPage getCompletedJobsPage(WebClient webClient)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, AuthenticationException {
		
		HtmlPage page = webClient.getPage(GENGO_COMPLETED_JOBS_PAGE);
		if (page.getUrl().getPath().contains(GENGO_LOGIN_SUBSTRING)) {
			throw new AuthenticationException("Could not log into Gengo. Please check credentials");
		}
		
		webClient.waitForBackgroundJavaScript(10000);
		return page;
	}

	/**
	 * Retrieves the next page of completed (archived) jobs.
	 * 
	 * @param page
	 *            Current page of completed (archived) jobs.
	 * @return Next page of completed (archived) jobs, null if there isn't a next page.
	 * @throws FailingHttpStatusCodeException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private HtmlPage getNextPage(HtmlPage page)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage nextPage = null;
		HtmlAnchor nextPageLink = page.getFirstByXPath("//span[contains(@class, 'pagination_next')]/a");
		if (nextPageLink != null) {
			WebClient webClient = page.getWebClient();
			nextPage = webClient.getPage(nextPageLink.getHrefAttribute());
			webClient.waitForBackgroundJavaScript(10000);
		}
		return nextPage;
	}

	/**
	 * Creates a list of archived jobs from the info in a page.
	 * 
	 * @param page
	 *            Page with info about archived jobs.
	 * @return List of archived jobs.
	 * @throws ParseException
	 */
	private List<ArchivedJob> retrieveCompletedJobs(HtmlPage page) throws ParseException {
		List<ArchivedJob> archivedJobs = new ArrayList<ArchivedJob>();

		List<HtmlListItem> listItems = (List<HtmlListItem>) page
				.getByXPath("//ol[@id='job-list-completed']/li[contains(@class, 'order')]");
		for (HtmlListItem listItem : listItems) {
			ArchivedJob archivedJob = ArchivedJobAdapter.buildArchivedJob(listItem);
			archivedJobs.add(archivedJob);
		}

		LOG.info("Retrieved jobs from " + page.getUrl());		
		return archivedJobs;
	}

	public String getPathToFile() {
		return pathToFile;
	}
}
