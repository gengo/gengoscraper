package com.gengo;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import javax.naming.AuthenticationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gengo.gengoscraper.archivedjobs.model.ArchivedJob;
import com.gengo.gengoscraper.archivedjobs.scraper.ArchivedJobsScraper;
import com.gengo.gengoscraper.archivedjobs.util.TextUtil;


/**
 * Application to retrieve info about archived jobs from Gengo.
 * 
 * @author Juan García Heredero
 *
 */
@SpringBootApplication
public class GengoScraperApplication {

	private static final Logger LOG = LoggerFactory.getLogger(GengoScraperApplication.class);
	
	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(GengoScraperApplication.class, args);
		ArchivedJobsScraper scraper = ctx.getBean(ArchivedJobsScraper.class);
		
		try {
			List<ArchivedJob> result = scraper.listArchivedJobs();
			TextUtil.generateCSV(result, scraper.getPathToFile());
		} catch (FailingHttpStatusCodeException | IOException | ParseException e) {
			e.printStackTrace();
		} catch (AuthenticationException e) {
			LOG.error(e.getMessage());
		}
	}
}
