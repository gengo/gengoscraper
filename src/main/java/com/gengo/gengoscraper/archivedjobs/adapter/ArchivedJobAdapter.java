package com.gengo.gengoscraper.archivedjobs.adapter;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlHeading3;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlListItem;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gengo.gengoscraper.archivedjobs.model.ArchivedJob;


/**
 * Adapter to create ArchivedJob objects
 * 
 * @author Juan García Heredero
 *
 */
public class ArchivedJobAdapter {
	
	//TODO: Check that Gengo dates are always retrieved in US format.
	private static final SimpleDateFormat SDF = new SimpleDateFormat("MMM dd yyyy", Locale.US);
	
	/**
	 * Creates an ArchivedJob from an HtmlListItem.
	 * 
	 * @param listItem HtmlListItem from which to create an ArchivedJob.
	 * @return ArchivedJob created from the HtmlListItem.
	 * @throws ParseException
	 */
	public static ArchivedJob buildArchivedJob(HtmlListItem listItem) throws ParseException {
		
		// Parse HTML using XPATH expressions to get the relevant info.
		String link = ((HtmlAnchor) listItem.getByXPath(".//a[contains(@href, '/jobs/details/')]").get(0)).getHrefAttribute();
		String collectionId = link.substring(link.lastIndexOf('/') + 1);
		String dateStr = ((HtmlSpan) listItem.getByXPath(".//span[contains(@class, 'date')]").get(0)).asText();
		Date date = SDF.parse(dateStr.trim());
		String difficulty = ((HtmlImage) listItem.getByXPath(".//img[1]").get(0)).getSrcAttribute();
		difficulty = difficulty.substring(difficulty.indexOf("icon_") + 5);
		difficulty = difficulty.substring(0, difficulty.lastIndexOf('.'));
		String documentType = ((HtmlImage) listItem.getByXPath(".//img[2]").get(0)).getSrcAttribute();
		documentType = documentType.substring(documentType.indexOf("icon_") + 5);
		documentType = documentType.substring(0, documentType.lastIndexOf('.'));
		String fromLanguage = ((DomText) listItem.getByXPath(".//div[contains(@class, 'basic-info')]/p/text()[1]").get(0)).asText().trim();
		String[] basicInfo = ((DomText) listItem.getByXPath(".//div[contains(@class, 'basic-info')]/p/text()[2]").get(0)).asText().split(",");
		String toLanguage = basicInfo[0].trim();
		int wordsCount = Integer.parseInt(basicInfo[1].substring(1, basicInfo[1].lastIndexOf(' ')));
		String[] basicInfo2 = basicInfo[2].split(" - ");
		int jobsCount = Integer.parseInt(basicInfo2[0].substring(1, basicInfo2[0].indexOf(" Job")));
		boolean preferred = listItem.getByXPath(".//div[contains(@class, 'reserved-box')]").size() > 0;
		String previewText = ((HtmlHeading3) listItem.getByXPath(".//div[contains(@class, 'basic-info')]/h3").get(0)).asText().trim();
		String totalPriceStr = ((HtmlSpan) listItem.getByXPath(".//span[contains(@class, 'price')]").get(0)).asText().trim();
		BigDecimal totalPrice = new BigDecimal(totalPriceStr.substring(1));
		String pricePerWordStr = ((DomText) listItem.getByXPath(".//div[contains(@class, 'meta-info-container')]/text()[1]").get(0)).asText().trim();
		pricePerWordStr = pricePerWordStr.substring(1, pricePerWordStr.indexOf('/'));
		BigDecimal pricePerWord = new BigDecimal(pricePerWordStr.substring(1));
		
		ArchivedJob archivedJob = new ArchivedJob();
		archivedJob.setCollectionId(collectionId);
		archivedJob.setDate(date);
		archivedJob.setDifficulty(difficulty);
		archivedJob.setDocumentType(documentType);
		archivedJob.setFromLanguage(fromLanguage);
		archivedJob.setLink(link);
		archivedJob.setPreferred(preferred);
		archivedJob.setPreviewText(previewText);
		archivedJob.setPricePerWord(pricePerWord);
		archivedJob.setToLanguage(toLanguage);
		archivedJob.setTotalPrice(totalPrice);
		archivedJob.setWordsCount(wordsCount);
		archivedJob.setJobsCount(jobsCount);
		return archivedJob;
	}
}
