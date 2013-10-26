package com.technosophos.lantern.commands.notes;

import java.util.ArrayList;
import java.util.List;

import com.technosophos.lantern.SinciputException;
import com.technosophos.lantern.commands.AddDocument;
import com.technosophos.lantern.types.JournalEnum;
import com.technosophos.lantern.types.NotesEnum;
import com.technosophos.lantern.util.Scrubby;
import com.technosophos.rhizome.RhizomeException;
//import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.document.*;
//import com.technosophos.rhizome.repository.DocumentNotFoundException;
//import com.technosophos.rhizome.repository.RepositoryAccessException;
//import com.technosophos.rhizome.repository.RepositorySearcher;
import com.technosophos.rhizome.repository.DocumentRepository;
//import com.technosophos.rhizome.repository.RhizomeInitializationException;
//import com.technosophos.lantern.types.CourseEnum;

//import static com.technosophos.lantern.servlet.ServletConstants.*;

/**
 * Add a note to a given repository.
 * <p>At the heart of Sinciput is the note. This command is used for taking user input
 * and creating a new note in a given repository.</p>
 * <p>This default addNote class assumes that the note is in HTML. For other body types, you
 * can simply override {@link prepareBody(RhizomeDocument)}.</p>
 * <p>What this object expects in parameters:</p>
 * <ul>
 * <li>title: Title of the note</li>
 * <li>subtitle: Subtitle of the note</li>
 * <li>tag(s): Zero or more tags</li>
 * <li>body: The text of the note</li>
 * </ul> 
 * <p>Additionally, it expects to be able to fetch a username from the session.</p>
 * @author mbutcher
 *
 */
public class AddNote extends AddDocument {
	
	public final static String NOTE_BODY = "body";
	public final static String NOTE_PARENT_DOCID = "parent";
	public final static String SINCIPUT_PARENT_RELATION = "parentOf";
	
	protected void populateDocument(RhizomeDocument doc) {
		String uname = this.ses.getUserName();
		
		// Fetch optional fields
		// - get all fields
		String title = this.getFirstParam(NotesEnum.TITLE.getKey(), "Untitled").toString();
		String subtitle = this.getFirstParam(NotesEnum.SUBTITLE.getKey(), "").toString();
		String tags = this.getFirstParam(JournalEnum.TAG.getKey(), null).toString();
		List<String> ta = tags == null ? new ArrayList<String>() : this.prepareTags(tags);
		
		// - clean fields
		title = Scrubby.cleanText(title);
		subtitle = Scrubby.cleanText(subtitle);
		
		// - put em in metadata objects:
		doc.addMetadatum(new Metadatum(NotesEnum.TITLE.getKey(), title));
		doc.addMetadatum(new Metadatum(NotesEnum.SUBTITLE.getKey(), subtitle));
		doc.addMetadatum(new Metadatum(NotesEnum.TAG.getKey(), ta));
		
		// - set automatic fields
		String time = com.technosophos.rhizome.util.Timestamp.now();
		doc.addMetadatum(new Metadatum(NotesEnum.TYPE.getKey(), 
				NotesEnum.TYPE.getFieldDescription().getDefaultValue()));
		doc.addMetadatum(new Metadatum(NotesEnum.CREATED_ON.getKey(), time ));
		doc.addMetadatum(new Metadatum(NotesEnum.LAST_MODIFIED.getKey(), time));
		doc.addMetadatum(new Metadatum(NotesEnum.CREATED_BY.getKey(), uname ));
		doc.addMetadatum(new Metadatum(NotesEnum.MODIFIED_BY.getKey(), uname ));

		// Do the body:
		String body = this.getFirstParam(NOTE_BODY, "").toString();
		
		try {
			this.prepareBody(body, doc);
		} catch (SinciputException se) {
			String err = String.format("HTML in \"%s\" was bad: %s", title, se.getMessage());
			String ferr = String.format("We encountered a problem when safety-checking %s.", title);
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}
		
		// - Check for relations:
		//this.addNoteToParent(doc, repoName);
	}

	
	protected void manageRelations( RhizomeDocument doc, DocumentRepository repo) throws RhizomeException {
		String parentID = this.getFirstParam(NOTE_PARENT_DOCID, "").toString();
		if( parentID.length() > 0 ) {
			//System.err.println("Adding note to parent.");
			RhizomeDocument parent = repo.getDocument(parentID);
			parent.addRelation(new Relation(SINCIPUT_PARENT_RELATION, doc.getDocID()));
			this.repoman.storeDocument(this.getCurrentRepository(), parent);
			
			//System.err.println("Done adding note to parent.");
		}
	}

}
