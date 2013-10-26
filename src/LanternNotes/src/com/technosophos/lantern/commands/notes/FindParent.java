package com.technosophos.lantern.commands.notes;


import com.technosophos.lantern.commands.LanternCommand;
import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.repository.RepositorySearcher;
import com.technosophos.rhizome.repository.DocumentRepository;
import com.technosophos.rhizome.document.RhizomeDocument;
//import com.technosophos.rhizome.document.RhizomeParseException;
//import com.technosophos.rhizome.repository.RhizomeInitializationException;
//import com.technosophos.rhizome.repository.DocumentNotFoundException;
//import com.technosophos.rhizome.repository.RepositoryAccessException;
//import com.technosophos.lantern.SinciputException;

import static com.technosophos.lantern.commands.notes.AddNote.SINCIPUT_PARENT_RELATION;

/**
 * Find a document that is the parent of this note.
 * Given a document ID, search through the repository to see if a parent for this document 
 * can be found. First parent found will be returned (as a RhizomeDocument in the 
 * CommandResults).
 * @author mbutcher
 *
 */
public class FindParent extends LanternCommand {

	/**
	 * Get parent
	 */
	protected void execute() throws ReRouteRequest {
		
		String docID = this.getDocIDFromParams();
		if(docID.length() == 0)return; //Exit silently.
		
		try {
			String repoName = this.getCurrentRepository();
			RepositorySearcher search = this.repoman.getSearcher(repoName);
			DocumentRepository repo = this.repoman.getRepository(repoName);
			String[] parents = search.getReverseRelatedDocuments(docID, SINCIPUT_PARENT_RELATION);
			
			if(parents.length > 0) {
				RhizomeDocument doc = repo.getDocument(parents[0]);
				if(doc != null) this.results.add(this.createCommandResult(doc));
			} //else System.err.println("### No parents found.");
		/*} catch (DocumentNotFoundException e) {
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
		*/
		} catch(Exception e) {
			return; // Exit silently.
		}
	}

}
