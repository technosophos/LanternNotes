package com.technosophos.lantern.commands.source;

import com.technosophos.lantern.SinciputException;
import com.technosophos.lantern.commands.AddDocument;
import com.technosophos.rhizome.document.Metadatum;
import com.technosophos.rhizome.document.RhizomeDocument;
import com.technosophos.lantern.types.CourseEnum;
import com.technosophos.lantern.types.SourceEnum;
import com.technosophos.lantern.util.Scrubby;
import java.util.ArrayList;
import java.util.List;

public class AddSource extends AddDocument {
	
	/**
	 * Given a title, format it for sorting.
	 * This simply moves non-sorts to the end. For example "The Wind in the Willows"
	 * becomes "Wind in the Willows, The".
	 * @param title The standard title
	 * @return A sortable title, with non-sortables appended at the end.
	 */
	public String formatSortableTitle(String title) {
		String lowtitle = title.toLowerCase();
		String stitle = title;
		if(lowtitle.startsWith("the ")) 
			stitle = title.substring(4) + ", The";
		else if(lowtitle.startsWith("a "))
			stitle = title.substring(2) + ", A";
		return stitle;
	}

	// Inherit javadocs
	protected void populateDocument(RhizomeDocument doc)
			throws SinciputException {
		
		// TITLES
		String title = this.getFirstParam(CourseEnum.TITLE.getKey(), null).toString();
		if( title == null ) {
			String err = "No title specified";
			String ferr = "You need to give a title. A source must have a title.";
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}
		doc.addMetadatum(new Metadatum(SourceEnum.TITLE.getKey(), Scrubby.cleanText(title)));
		String stitle = this.getFirstParam(SourceEnum.SORTABLE_TITLE.getKey(), "").toString();
		if(stitle.length() == 0)
			doc.addMetadatum(new Metadatum(SourceEnum.SORTABLE_TITLE.getKey(), 
					Scrubby.cleanText(this.formatSortableTitle(title))));
		
		// CREATORS
		String[] authors = this.getFirstParam(SourceEnum.AUTHOR.getKey(), "").toString().split(";");
		String[] editors = this.getFirstParam(SourceEnum.EDITOR.getKey(), "").toString().split(";");
		String[] translators = this.getFirstParam(SourceEnum.TRANSLATOR.getKey(), "").toString().split(";");
		String[] illustrators = this.getFirstParam(SourceEnum.ILLUSTRATOR.getKey(), "").toString().split(";");
		this.scrubArray(authors, editors, translators, illustrators);
		doc.addMetadatum(new Metadatum(SourceEnum.ILLUSTRATOR.getKey(), illustrators));
		doc.addMetadatum(new Metadatum(SourceEnum.AUTHOR.getKey(), authors));
		doc.addMetadatum(new Metadatum(SourceEnum.EDITOR.getKey(), editors));
		doc.addMetadatum(new Metadatum(SourceEnum.TRANSLATOR.getKey(), translators));
		
		// IDENTIFIERS AND CLASSIFICATIONS
		String isbn_a = Scrubby.cleanText(this.getFirstParam("isbn", "").toString());
		String lccn_a = Scrubby.cleanText(this.getFirstParam("lccn","").toString());
		
		ArrayList<String> identifiers = new ArrayList<String>();
		
		//Check ISBN
		if(isbn_a.length() > 0) {
			// Remove hyphens
			if(isbn_a.indexOf('-') > -1) isbn_a.replaceAll("-","");
			if(!Scrubby.checkISBN(isbn_a)) isbn_a = isbn_a + " (Probably invalid)";
			
			
			String isbn = "isbn:" + isbn_a;
			identifiers.add(isbn);
		}
		
		// Check LCCN
		if(lccn_a.length() > 0) {
			// Remove hyphens and pad to appropriate length.
			if(lccn_a.indexOf('-') > -1) {
				if(lccn_a.length() == 10) {
					// This is improbable... new standard doesn't have dashes.
					lccn_a.replace('-', '0');
				} else {
					// LCCN is in old format.
					// Pad it to get it up to eight chars
					int pad = 9 - lccn_a.length();
					String[] ss = lccn_a.split("-");
					StringBuilder sb = new StringBuilder();
					sb.append(ss[0]);
					for(int i = 0; i < pad; ++i)
						sb.append('0');
					lccn_a = sb.append(ss[1]).toString();
				}
			}
			if(!Scrubby.checkDigits(lccn_a)) lccn_a = lccn_a + " (Invalid)";
			String lccn = "lccn:" + lccn_a;
			identifiers.add(lccn);
		}
		// Add identifiers
		if(identifiers.size() != 0)
			doc.addMetadatum(SourceEnum.IDENTIFIER.getKey(), identifiers);
		
		// LCC is a classification
		String lcc = "lcc:" + Scrubby.cleanText(this.getFirstParam("lcc","").toString());
		if(lcc.length() > 4) 
			doc.addMetadatum(new Metadatum(SourceEnum.CLASSIFICATION.getKey(), lcc));
		
		// Location URL
		this.getScrubAdd(SourceEnum.LOCATION_URL.getKey(), "", doc);
		
		// PUBLISHING INFORMATION
		this.getScrubAdd(SourceEnum.PUBLISH_DATE.getKey(), "", doc);
		this.getScrubAdd(SourceEnum.PUBLISHER.getKey(), "", doc);
		this.getScrubAdd(SourceEnum.PUBLISH_PLACE.getKey(), "", doc);
		
		// RESOURCE INFORMATION
		this.getScrubAdd(SourceEnum.GENRE.getKey(), "", doc);
		this.getScrubAdd(SourceEnum.LANGUAGE.getKey(), "", doc);
		this.getScrubAdd(SourceEnum.TARGET_AUDIENCE.getKey(), "", doc);
		this.getScrubAdd(SourceEnum.SOURCE_TYPE.getKey(), "", doc);
		
		// TEXT FIELDS
		this.getScrubAdd(SourceEnum.ABSTRACT.getKey(), "", doc);
		this.getScrubAdd(SourceEnum.TABLE_OF_CONTENTS.getKey(), "", doc);
		this.getScrubAdd(SourceEnum.NOTE.getKey(), "", doc);
		
		// SUBJECTS
		String[] subjs = this.getFirstParam(SourceEnum.SUBJECT.getKey(), "").toString().split(";");
		for(int i = 0; i < subjs.length; ++i)
			subjs[i] = Scrubby.cleanText(subjs[i]);
		doc.addMetadatum(new Metadatum(SourceEnum.SUBJECT.getKey(), subjs));
		
		// TAGS
		String tags = this.getFirstParam(SourceEnum.TAG.getKey(), "").toString();
		List<String> ta = tags == null ? new ArrayList<String>() : this.prepareTags(tags);
		doc.addMetadatum(new Metadatum(SourceEnum.TAG.getKey(), ta));
		
		// Automatic fields
		String time = com.technosophos.rhizome.util.Timestamp.now();
		String uname = this.ses.getUserName();
		doc.addMetadatum(new Metadatum(SourceEnum.TYPE.getKey(), 
				SourceEnum.TYPE.getFieldDescription().getDefaultValue()));
		doc.addMetadatum(new Metadatum(SourceEnum.CREATED_ON.getKey(), time ));
		doc.addMetadatum(new Metadatum(SourceEnum.LAST_MODIFIED.getKey(), time));
		doc.addMetadatum(new Metadatum(SourceEnum.CREATED_BY.getKey(), uname ));
		doc.addMetadatum(new Metadatum(SourceEnum.MODIFIED_BY.getKey(), uname ));
	}

	/**
	 * Simplify the workflow by handling getting, scrubbing, and storing.
	 * Getting: uses getFirstParam, and uses defaultValue for the default
	 * Scrubbing: Uses {@link Scrubby.cleanText}.
	 * Storing: doc.addMetadatum(new Metadatum(key, value))
	 * @param key
	 * @param defaultValue
	 * @param doc
	 */
	protected void getScrubAdd(String key, String defaultValue, RhizomeDocument doc) {
		String v = this.getFirstParam(key, defaultValue).toString();
		v = Scrubby.cleanText(v);
		doc.addMetadatum(new Metadatum(key, v));
	}
	
	/**
	 * Perform a {@link Scrubby.cleanText(String)} on one or more String arrays.
	 * Laziness is a virtue.
	 * @param arr One or more String arrays to clean.
	 */
	protected void scrubArray(String[] ... arr) {
		for(String[] vals: arr) {
			for(int i = 0; i < vals.length; ++i)
				vals[i] = Scrubby.cleanText(vals[i]);
		}
	}
}


