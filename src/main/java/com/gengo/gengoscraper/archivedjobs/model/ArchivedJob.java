package com.gengo.gengoscraper.archivedjobs.model;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Represents an archived job in Gengo
 * 
 * @author Juan García Heredero
 *
 */
@Data
@Accessors(chain=true)
@NoArgsConstructor
public class ArchivedJob {

	private String difficulty;
	private String documentType;
	private String previewText;
	private String fromLanguage;
	private String toLanguage;
	private int wordsCount;
	private int jobsCount;
	private String collectionId;
	private boolean preferred;
	private String link;
	private BigDecimal pricePerWord;
	private BigDecimal totalPrice;
	private Date date;
	
	// This toString() is prepared to show the object as a comma-separated String.
	// This way it would be easier to use it with Java 8 streams (see TextUtil.generateCSV)
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SIMPLE_STYLE);
	}
}
