package com.technosophos.lantern.commands.source;

import com.technosophos.lantern.types.SourceEnum;

import com.technosophos.lantern.SinciputException;
import com.technosophos.lantern.commands.LanternCommand;
import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.document.DocumentList;
import com.technosophos.rhizome.repository.DocumentRepository;
import com.technosophos.rhizome.repository.RepositoryAccessException;
import com.technosophos.rhizome.repository.RepositorySearcher;
import com.technosophos.rhizome.repository.RhizomeInitializationException;

/**
 * Given a subject, get a list of sources that have that subject.
 * Looks for the param <code>subject</code>.
 * @author mbutcher
 *
 */
public class GetDocsBySubject extends LanternCommand {

	
	protected void execute() throws ReRouteRequest {
		String subject = this.getFirstParam(SourceEnum.SUBJECT.getKey(), "").toString();
		String[] retFields = {
			SourceEnum.TITLE.getKey(),
			SourceEnum.AUTHOR.getKey(),
			SourceEnum.TYPE.getKey(),
			SourceEnum.SUBJECT.getKey(),
			SourceEnum.LAST_MODIFIED.getKey(),
			SourceEnum.CREATED_ON.getKey()
		};
		
		if(subject.length() == 0) {
			String err = String.format("No tag specified.");
			String ferr = String.format("You must specify a tag");
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}
		
		try {
			String repoName = this.getCurrentRepository();
			RepositorySearcher search = this.repoman.getSearcher(repoName);
			DocumentRepository repo = this.repoman.getRepository(repoName);
			String [] docIDs = search.getDocIDsByMetadataValue(SourceEnum.SUBJECT.getKey(), subject);
			DocumentList docs = search.getDocumentList(retFields, docIDs, repo);
			this.results.add(this.createCommandResult(docs));
		} catch (RhizomeInitializationException e) {
			String err = "Failed to initialize searcher: " + e.getMessage();
			String ferr = "Subject headings are temporarily unavailable. Try again later.";
			results.add(this.createErrorCommandResult(err, ferr, e));
			return;
		} catch (SinciputException e) {
			String err = "An error occured while preparing to search: " + e.getMessage();
			String ferr = "Subject headings are temporarily unavailable. Try again later.";
			results.add(this.createErrorCommandResult(err, ferr, e));
			return;
		} catch (RepositoryAccessException e) {
			String err = "Searching failed with an access exception: " + e.getMessage();
			String ferr = "Subject headings are temporarily unavailable. Try again later.";
			results.add(this.createErrorCommandResult(err, ferr, e));
			return;
		} 

	}

}
