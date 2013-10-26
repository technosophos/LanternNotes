package com.technosophos.lantern.commands.notes;

import java.util.List;
import java.util.ArrayList;

import com.technosophos.lantern.SinciputException;
import com.technosophos.lantern.commands.LanternCommand;
import com.technosophos.lantern.types.NotesEnum;
import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.controller.CommandResult;
import com.technosophos.rhizome.document.RhizomeDocument;
import com.technosophos.rhizome.document.Relation;
import com.technosophos.rhizome.document.DocumentList;
import com.technosophos.rhizome.repository.RhizomeInitializationException;
import com.technosophos.rhizome.repository.RepositorySearcher;
import com.technosophos.rhizome.repository.DocumentRepository;
import com.technosophos.rhizome.repository.RepositoryAccessException;

public class GetRelatedNotes extends LanternCommand {

	
	protected void execute() throws ReRouteRequest {
		int r = this.results.size();
		if( r == 0) return;
		
		// Get the last CR
		CommandResult res = this.results.get(r-1);
		
		Object o = res.getResult();
		if(!(o instanceof RhizomeDocument )) return;
		
		RhizomeDocument doc = (RhizomeDocument)o;
		List<Relation> relations = doc.getRelations();
		
		List<String> docIDs = this.getRelatedDocIDs(relations);
		
		if(docIDs.size() > 0 ) {
			String[] names = {
				NotesEnum.TITLE.getKey(),
				NotesEnum.TAG.getKey(),
				NotesEnum.CREATED_ON.getKey()
			};
			String[] idArray = docIDs.toArray(new String[docIDs.size()-1]);
			try {
				String repoID = this.getCurrentRepository();
				DocumentRepository repo = this.repoman.getRepository(repoID);
				RepositorySearcher search = this.repoman.getSearcher(repoID);
				DocumentList docs = search.getDocumentList(names, idArray, repo);
				
				// Return:
				if(docs.size() > 0)
					this.results.add(this.createCommandResult(docs));
				
			// All exceptions should result in just a return. The UI can deal with a
			// missing CR.
			} catch (RhizomeInitializationException e) {
				e.printStackTrace();
				return;
			} catch (RepositoryAccessException e) {
				return;
			} catch (SinciputException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
		
	}
	
	protected List<String> getRelatedDocIDs(List<Relation> relations) {
		ArrayList<String> docIDs = new ArrayList<String>();
		
		for(Relation rel: relations) {
			if(AddNote.SINCIPUT_PARENT_RELATION.equals(rel.getRelationType()))
				docIDs.add(rel.getDocID());
		}
		
		return docIDs;
	}

}
