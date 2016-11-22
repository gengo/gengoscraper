package com.gengo.gengoscraper.archivedjobs.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import com.gengo.gengoscraper.archivedjobs.model.ArchivedJob;


/**
 * Utility class to deal with texts.
 * 
 * @author jugarcia
 *
 */
public class TextUtil {

	private static final DateFormat DF = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
	private static final String SEPARATOR = "\t";
	private static final String WRAPPER = "\"";
	private static final String NEW_LINE = "\n";
	
	/**
	 * Generates a file with info about archived jobs. Each field will be wrapped by
	 * a WRAPPER character, will be separated by a SEPARATOR character and the lines will
	 * end with a NEW_LINE character. These characters are defined as constants in TextUtil.
	 * 
	 * @param archivedJobs List of archived jobs.
	 * @param path File in which to store the info about the archived jobs.
	 * @throws FileNotFoundException
	 */
	public static void generateCSV(List<ArchivedJob> archivedJobs, String path) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File(path));
        StringBuilder sb = new StringBuilder();
        
        //TODO: Could be interesting to use Java 8 streams. Lacks some flexibility, but it's a one-liner.
        //String totalText = archivedJobs.stream().map(ArchivedJob::toString).collect(Collectors.joining("\n"));
        
        String[] titles = {"Collection ID", "Difficulty", "Document type", "Original language", "Translated language",
            	 // "Preview text", // Gengo does not allow to store jobs text for more than 30 days. 
        			"Jobs", "Words", "Link", "Preferred", "Price per word", "Date", "Total price"
        		};
        addTitles(sb, titles);
        
        for (ArchivedJob archivedJob : archivedJobs) {
        	addRow(sb, archivedJob);
        }

        pw.write(sb.toString());
        pw.close();
	}

	/**
	 * Adds a row to the StringBuilder that represents an archived job.
	 * @param sb StringBuilder to which the info will be appended.
	 * @param archivedJob Archived job to append the info from.
	 */
	private static void addRow(StringBuilder sb, ArchivedJob archivedJob) {
		appendCell(sb, archivedJob.getCollectionId());
		appendCell(sb, archivedJob.getDifficulty());
		appendCell(sb, archivedJob.getDocumentType());
		appendCell(sb, archivedJob.getFromLanguage());
		appendCell(sb, archivedJob.getToLanguage());
		/*
		 * Preview text, although stored correctly, can be problematic when opening the file with MS Excel.
		 * Also, Gengo does not allow to store jobs on the local computer for more than 30 days, and this text is part of the job.
		 *
		 * appendCell(sb, cleanText(archivedJob.getPreviewText()));
		 */
		appendCell(sb, String.valueOf(archivedJob.getJobsCount()));
		appendCell(sb, String.valueOf(archivedJob.getWordsCount()));
		appendCell(sb, archivedJob.getLink());
		appendCell(sb, archivedJob.isPreferred()? "preferred" : "not preferred");
		appendCell(sb, formatBigDecimal(archivedJob.getPricePerWord()));
		appendCell(sb, DF.format(archivedJob.getDate()));
		appendCellAndNewLine(sb, formatBigDecimal(archivedJob.getTotalPrice()));
	}

	/**
	 * Adds a row to the StringBuilder with the desired titles.
	 * @param sb
	 */
	private static void addTitles(StringBuilder sb, String[] titles) {
		for (String title : titles) {
			appendCell(sb, title);
		}
		sb.append(NEW_LINE);
	}
	
	/**
	 * Appends a 'cell' to the StringBuilder
	 * @param sb The StringBuilder to which we want to append the cell.
	 * @param str Text to append.
	 */
	private static void appendCell(StringBuilder sb, String str) {
		sb.append(WRAPPER).append(str).append(WRAPPER).append(SEPARATOR);
	}
	
	/**
	 * Appends a 'cell' to the StringBuilder and adds a new line
	 * @param sb The StringBuilder to which we want to append the cell.
	 * @param str Text to append.
	 */
	private static void appendCellAndNewLine(StringBuilder sb, String str) {
		sb.append(WRAPPER).append(str).append(WRAPPER).append(NEW_LINE);
	}
	
	/**
	 * Cleans text so it can be shown properly.
	 * 
	 * @param text Text to clean.
	 * @return Clean text.
	 */
	public static String cleanText(String text) {
		return text.replaceAll("&amp;", "&");
	}

	/**
	 * Formats a BigDecimal to a String according to JVM Locale.
	 * 
	 * @param bigDecimal
	 * @return
	 */
	public static String formatBigDecimal(BigDecimal bigDecimal) {
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.getDefault());
		String result = String.valueOf(bigDecimal.doubleValue());
		result = result.replace('.', dfs.getDecimalSeparator());
		return result;
	}
}
