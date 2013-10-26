package com.technosophos.lantern.commands.notes;

import com.technosophos.rhizome.repository.DocumentRepository;
import com.technosophos.rhizome.document.RhizomeDocument;
import com.technosophos.rhizome.document.Metadatum;
import com.technosophos.lantern.types.NotesEnum;
//import com.technosophos.lantern.SinciputException;
//import com.technosophos.rhizome.document.DocumentID;
//import com.technosophos.rhizome.document.RhizomeParseException;
//import com.technosophos.rhizome.repository.DocumentNotFoundException;
//import com.technosophos.rhizome.repository.RepositoryAccessException;
//import com.technosophos.rhizome.repository.RhizomeInitializationException;

public class ModifyNote extends AddNote {
	/**
	 * Initialize a rhizome document. Here, it creates a new empty document
	 * with a generated DocID.
	 * Override this for modifications. The default behavior is to create a new document ID
	 * altogether. WE want to grab an existing one and modify that document.
	 * @return Document with document ID (Document may have other properties set as well)
	 */
	protected RhizomeDocument initializeDocument() {
		
		try {
			DocumentRepository repo = this.repoman.getRepository(this.getCurrentRepository());
			RhizomeDocument doc = repo.getDocument(this.getFirstParam("doc", null).toString());
			Metadatum cOn = doc.getMetadatum(NotesEnum.CREATED_ON.getKey());
			Metadatum cBy = doc.getMetadatum(NotesEnum.CREATED_BY.getKey());
			doc.clearMetadata(); // Otherwise we get duplicates.
			//doc.getMetadatum(NotesEnum.TYPE.getKey());
			doc.addMetadatum(cOn);
			doc.addMetadatum(cBy);
			return doc;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Do nothing.
	 * Override the parent, which inserts a relation. We just want to maintain the current
	 * relation, which (wonder of wonders) means we don't do anything.
	 */
	protected void manageRelations(RhizomeDocument doc, DocumentRepository r) {
		//System.out.println("Stubbornly refusing to do anything!");
		return;
	}
}
