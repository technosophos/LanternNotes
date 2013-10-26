package com.technosophos.lantern.commands.journal;

import com.technosophos.lantern.types.JournalEnum;
import com.technosophos.rhizome.repository.DocumentRepository;
import com.technosophos.rhizome.document.Metadatum;
import com.technosophos.rhizome.document.RhizomeDocument;
//import com.technosophos.lantern.SinciputException;
//import com.technosophos.rhizome.document.DocumentID;
//import com.technosophos.rhizome.document.RhizomeParseException;
//import com.technosophos.rhizome.repository.DocumentNotFoundException;
//import com.technosophos.rhizome.repository.RepositoryAccessException;
//import com.technosophos.rhizome.repository.RhizomeInitializationException;

public class ModifyJournal extends AddJournal {
	/**
	 * Initialize a rhizome document. Here, it creates a new empty document
	 * with a generated DocID.
	 * Override this for modifications.
	 * @return Document with document ID (Document may have other properties set as well)
	 */
	protected RhizomeDocument initializeDocument() {
		
		try {
			DocumentRepository repo = this.repoman.getRepository(this.getCurrentRepository());
			RhizomeDocument doc = repo.getDocument(this.getFirstParam("doc", null).toString());
			Metadatum cOn = doc.getMetadatum(JournalEnum.CREATED_ON.getKey());
			Metadatum cBy = doc.getMetadatum(JournalEnum.CREATED_BY.getKey());
			doc.clearMetadata(); // Otherwise we get duplicates.
			//doc.getMetadatum(NotesEnum.TYPE.getKey());
			doc.addMetadatum(cOn);
			doc.addMetadatum(cBy);
			return doc;
		} catch (Exception e) {
			return null;
		}
		/*
		} catch (DocumentNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RhizomeInitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SinciputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RhizomeParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
}
