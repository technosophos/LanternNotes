package com.technosophos.lantern.commands.source;

import com.technosophos.lantern.types.SourceEnum;
import com.technosophos.rhizome.document.Metadatum;
import com.technosophos.rhizome.document.RhizomeDocument;
import com.technosophos.rhizome.repository.DocumentRepository;

public class ModifySource extends AddSource {
	/**
	 * Initialize a rhizome document. Here, it creates a new empty document
	 * with a generated DocID.
	 * Override this for modifications. The default behavior is to create a new document ID
	 * altogether. We want to grab an existing one and modify that document.
	 * @return Document with document ID (Document may have other properties set as well)
	 */
	protected RhizomeDocument initializeDocument() {
		
		try {
			DocumentRepository repo = this.repoman.getRepository(this.getCurrentRepository());
			RhizomeDocument doc = repo.getDocument(this.getFirstParam("doc", null).toString());
			Metadatum cOn = doc.getMetadatum(SourceEnum.CREATED_ON.getKey());
			Metadatum cBy = doc.getMetadatum(SourceEnum.CREATED_BY.getKey());
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
	 * Make sure nothing overwrites our relations.
	 */
	protected void manageRelations(RhizomeDocument doc, DocumentRepository r) {
		return;
	}
}
