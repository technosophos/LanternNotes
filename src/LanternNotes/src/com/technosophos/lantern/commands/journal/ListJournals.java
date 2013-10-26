package com.technosophos.lantern.commands.journal;

import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

import com.technosophos.lantern.commands.ListDocuments;
import com.technosophos.lantern.types.JournalEnum;
import com.technosophos.rhizome.document.DocumentList;

public class ListJournals extends ListDocuments {

	// Inherit javadoc
	protected Map<String, String> narrower() {
		Map<String, String> narrower = new HashMap<String, String>();
		narrower.put(JournalEnum.TYPE.getKey(), JournalEnum.TYPE.getFieldDescription().getDefaultValue());
		
		return narrower;
	}
	/**
	 * Retrieve title, tag, and created_on.
	 */
	public String[] additionalMetadata() {
		return new String[] {
			JournalEnum.TITLE.getKey(),
			JournalEnum.CREATED_ON.getKey(),
			JournalEnum.TAG.getKey(),
		};
	}
	
	/**
	 * List from newest (modification time) to oldest.
	 */
	public void sortResults(DocumentList list) {
		// Most recent modification FIRST.
		Collections.reverse(list);
	}
}
