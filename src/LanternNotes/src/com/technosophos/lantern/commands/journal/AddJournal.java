package com.technosophos.lantern.commands.journal;


import java.util.ArrayList;
import java.util.List;
import com.technosophos.lantern.SinciputException;
import com.technosophos.lantern.commands.AddDocument;
import com.technosophos.lantern.types.JournalEnum;
import com.technosophos.lantern.util.Scrubby;
import com.technosophos.rhizome.document.Metadatum;
import com.technosophos.rhizome.document.RhizomeDocument;

/**
 * Add a journal to a given repository.
 * 
 * <p>What this object expects in parameters:</p>
 * <ul>
 * <li>title: Title of the journal</li>
 * <li>description: Description of the journal</li>
 * <li>tag(s): Zero or more tags</li>
 * </ul> 
 * <p>Additionally, it expects to be able to fetch a username from the session.</p>
 * @author mbutcher
 *
 */
public class AddJournal extends AddDocument {

	public final static String JOURNAL_BODY = "body";
	
	/**
	 * Given a document, populate the metadata and body of the doc.
	 * @param doc Document with a doc ID
	 * @throws SinciputException Only when a formatting issue is encountered with the body.
	 */
	protected void populateDocument(RhizomeDocument doc) throws SinciputException {
		String uname = this.ses.getUserName();
		
		// Fetch optional fields
		// - get all fields
		String title = this.getFirstParam(JournalEnum.TITLE.getKey(), "Untitled").toString();
		String description = this.getFirstParam(JournalEnum.DESCRIPTION.getKey(), "").toString();
		String tags = this.getFirstParam(JournalEnum.TAG.getKey(), null).toString();
		List<String> ta = tags == null ? new ArrayList<String>() : this.prepareTags(tags);
		
		// - clean fields
		title = Scrubby.cleanText(title);
		description = Scrubby.cleanText(description);
		
		// - put em in metadata objects:
		doc.addMetadatum(new Metadatum(JournalEnum.TITLE.getKey(), title));
		doc.addMetadatum(new Metadatum(JournalEnum.DESCRIPTION.getKey(), description));
		doc.addMetadatum(new Metadatum(JournalEnum.TAG.getKey(), ta));
		
		// - set automatic fields
		String time = com.technosophos.rhizome.util.Timestamp.now();
		doc.addMetadatum(new Metadatum(JournalEnum.TYPE.getKey(), 
				JournalEnum.TYPE.getFieldDescription().getDefaultValue()));
		doc.addMetadatum(new Metadatum(JournalEnum.CREATED_ON.getKey(), time ));
		doc.addMetadatum(new Metadatum(JournalEnum.LAST_MODIFIED.getKey(), time));
		doc.addMetadatum(new Metadatum(JournalEnum.CREATED_BY.getKey(), uname ));
		doc.addMetadatum(new Metadatum(JournalEnum.MODIFIED_BY.getKey(), uname ));

		// Do the body:
		
		String body = this.getFirstParam(JOURNAL_BODY, "").toString();
		this.prepareBody(body, doc);
		
	}
}
