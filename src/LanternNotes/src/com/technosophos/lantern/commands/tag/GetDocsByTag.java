package com.technosophos.lantern.commands.tag;

import static com.technosophos.lantern.types.NotesEnum.*;

import com.technosophos.lantern.SinciputException;
import com.technosophos.lantern.commands.LanternCommand;
import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.repository.RepositoryAccessException;
import com.technosophos.rhizome.repository.RepositorySearcher;
import com.technosophos.rhizome.repository.DocumentRepository;
import com.technosophos.rhizome.repository.RhizomeInitializationException;
import com.technosophos.rhizome.document.DocumentList;

/**
 * Gets a list of documents by tag name.
 * @author mbutcher
 *
 */
public class GetDocsByTag extends LanternCommand {
	
	protected void execute() throws ReRouteRequest {
		
		String tag = this.getFirstParam(TAG.getKey(), "").toString();
		String[] retFields = {
			TITLE.getKey(),
			TYPE.getKey(),
			TAG.getKey(),
			LAST_MODIFIED.getKey(),
			CREATED_ON.getKey()
		};
		
		if(tag.length() == 0) {
			String err = String.format("No tag specified.");
			String ferr = String.format("You must specify a tag");
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}
		
		try {
			String repoName = this.getCurrentRepository();
			RepositorySearcher search = this.repoman.getSearcher(repoName);
			DocumentRepository repo = this.repoman.getRepository(repoName);
			String [] docIDs = search.getDocIDsByMetadataValue(TAG.getKey(), tag);
			DocumentList docs = search.getDocumentList(retFields, docIDs, repo);
			this.results.add(this.createCommandResult(docs));
		} catch (RhizomeInitializationException e) {
			String err = "Failed to initialize searcher: " + e.getMessage();
			String ferr = "Tags are temporarily unavailable. Try again later.";
			results.add(this.createErrorCommandResult(err, ferr, e));
			return;
		} catch (SinciputException e) {
			String err = "An error occured while preparing to search: " + e.getMessage();
			String ferr = "Tags are temporarily unavailable. Try again later.";
			results.add(this.createErrorCommandResult(err, ferr, e));
			return;
		} catch (RepositoryAccessException e) {
			String err = "Searching failed with an access exception: " + e.getMessage();
			String ferr = "Tags are temporarily unavailable. Try again later.";
			results.add(this.createErrorCommandResult(err, ferr, e));
			return;
		} 
	}

}
