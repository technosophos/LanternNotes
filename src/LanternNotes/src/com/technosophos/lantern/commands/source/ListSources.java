package com.technosophos.lantern.commands.source;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.technosophos.lantern.commands.ListDocuments;
import com.technosophos.lantern.types.SourceEnum;
import com.technosophos.rhizome.document.DocumentList;
import com.technosophos.rhizome.document.RhizomeDocument;

/**
 * Fetch a list of all of the stored sources.
 * @author mbutcher
 *
 */
public class ListSources extends ListDocuments implements Comparator<RhizomeDocument> {

	String compareKey = null;
	
	protected Map<String, String> narrower() {
		Map<String, String> narrower = new HashMap<String, String>();
		narrower.put(SourceEnum.TYPE.getKey(), SourceEnum.TYPE.getFieldDescription().getDefaultValue());
		
		return narrower;
	}

	
	public String[] additionalMetadata() {
		return new String[] {
			SourceEnum.SORTABLE_TITLE.getKey(),
			SourceEnum.TITLE.getKey(),
			SourceEnum.AUTHOR.getKey(),
			SourceEnum.TAG.getKey(),
			//SourceEnum.PUBLISH_PLACE.getKey(),
			SourceEnum.PUBLISH_DATE.getKey(),
			SourceEnum.PUBLISHER.getKey(),
			SourceEnum.CREATED_ON.getKey(),
			SourceEnum.LAST_MODIFIED.getKey()
		};
	}
	
	/**
	 * Sort based on document metadata.
	 * @param list The list to sort.
	 * @param key The name of the metadata element to sort on. Assumes natural ordering of strings.
	 */
	protected void sortResults(DocumentList list, String key) {
		this.compareKey = key;
		Collections.sort(list, this);
		return;
	}
	
	protected void sortResults(DocumentList list) {
		// Set up Sortable Title as the default field to sort on.
		this.compareKey = SourceEnum.SORTABLE_TITLE.getKey();
		Collections.sort(list, this);
		return;
	}
	
	public int compare(RhizomeDocument r1, RhizomeDocument r2) {
		String t1 = r1.getMetadatum(this.compareKey).getFirstValue();
		String t2 = r2.getMetadatum(this.compareKey).getFirstValue();
		return t1.compareToIgnoreCase(t2);
	}
}
