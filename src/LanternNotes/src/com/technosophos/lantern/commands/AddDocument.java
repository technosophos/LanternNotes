package com.technosophos.lantern.commands;

import static com.technosophos.lantern.servlet.ServletConstants.SETTINGS_REPO;

import com.technosophos.lantern.SinciputException;
import com.technosophos.lantern.util.Scrubby;
import com.technosophos.rhizome.RhizomeException;
import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.document.DocumentID;
import com.technosophos.rhizome.document.RhizomeDocument;
import com.technosophos.rhizome.repository.DocumentRepository;
import com.technosophos.rhizome.repository.RepositorySearcher;
import com.technosophos.rhizome.repository.RhizomeInitializationException;

/**
 * Provides some default functionality for commands that add documents.
 * This can also provide the majority of the functionality needed to modify or edit documents.
 * @author mbutcher
 *
 */
public abstract class AddDocument extends LanternCommand {

	//protected abstract void execute() throws ReRouteRequest;
	protected void execute() throws ReRouteRequest {
		// Get user from session.
		String uname = this.ses.getUserName();
		String userid = this.ses.getUserUUID();
		String repoName;
		RepositorySearcher s_search;
		DocumentRepository s_repo;
		
		if(uname == null || userid == null) {
			String err = "No user object!";
			String ferr = "We can not verify the user ID at this time. Are you logged in?";
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}


		try {
			s_search = this.repoman.getSearcher(SETTINGS_REPO);
			s_repo = this.repoman.getRepository(SETTINGS_REPO);
		} catch (RhizomeInitializationException e1) {
			String err = "Failed to initialize searcher and repo: " + e1.getMessage();
			String ferr = "Our system cannot initialize your repository. Try again later.";
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}
		
		// Get repository
		try {
			repoName = this.getCurrentRepository(s_search);
		} catch (SinciputException e) {
			String err = "Repository not found: " + e.getMessage();
			String ferr = "Our system cannot find your repository. Try again later.";
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}
		if(repoName == null)  return; // This should be cleaner... fix LanternCommand
		
		// Check write access to repo
		if( ! this.userCanWriteRepo(s_repo)) {
			String err = "No write permissions to repository " + repoName;
			String ferr = "You are not allowed to write notes in this repository.";
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}
		
		// Create document
		RhizomeDocument doc = this.initializeDocument();//new RhizomeDocument(DocumentID.generateDocumentID());
		if(doc == null) {
			String err = "Failed to initialize document.";
			String ferr = "We cannot create a new document.";
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}
		
		// Populate Metadata, Relations, Extensions:
		try {
			this.populateDocument(doc);
		} catch (SinciputException se) {
			String title = doc.getMetadatum("title").getFirstValue();
			String err = String.format("HTML in \"%s\" was bad: %s", title, se.getMessage());
			String ferr = String.format("We encountered a problem when safety-checking %s.", title);
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}
		
		// - Store in repo
		try {
			this.repoman.storeDocument(repoName, doc);
		} catch (RhizomeException e) {
			// XXX: Do we need to rollback anything?
			String title = doc.getMetadatum("title").getFirstValue();
			String err = String.format("Could not store \"%s\" in %s.", title, repoName);
			String ferr = String.format("We could not store \"%s\" in your repository.", title);
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}
		
		DocumentRepository repo;
		try {
			repo = this.repoman.getRepository(repoName);
			this.manageRelations(doc, repo);
			
		} catch (RhizomeInitializationException e1) {
			String err = "Could not get the repository: " + e1.getMessage();
			String ferr = "We could not related this Note to its parent, but we did save the note.";
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		} catch (RhizomeException e) {
			String err = "Manage relations reports an error: " + e.getMessage();
			String ferr = "We could not related this Note to its parent, but we did save the note.";
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}
		
		// Might as well pass the doc on
		this.results.add(this.createCommandResult(doc));
	}

	/**
	 * Initialize a rhizome document. Here, it creates a new empty document
	 * with a generated DocID.
	 * Override this for modifications.
	 * @return Document with document ID (Document may have other properties set as well)
	 */
	protected RhizomeDocument initializeDocument() {
		return new RhizomeDocument(DocumentID.generateDocumentID());
	}
	
	/**
	 * Insert the body into the document.
	 * This method assumes that the body is HTML (or, rather RHTML). It is safe to 
	 * override this method to perform other processing (including setting the text type)
	 * on the text, and then storing it in the document.
	 * @param body The body text, as extracted from params. No cleaning has been done, yet.
	 * @param doc The document that will hold the note.
	 */
	protected void prepareBody( String body, RhizomeDocument doc ) throws SinciputException {
		// 1. Do cleaning
		body = Scrubby.cleanSafeHTML(body);
		
		// 2. Set the type and store the document
		doc.setBody("text/html", body);
	}
	
	/**
	 * Handle relationships.
	 * This method may modify relationships between the document and other resources
	 * in the repository.
	 * <p>The default behavior is to do nothing.</p>
	 * @param doc Document, with fields populated already.
	 * @param repos DocumentRepository, initialized to the repository in which the document lives.
	 * @throws SinciputException If there was an error writing the relationship data.
	 */
	protected void manageRelations(RhizomeDocument doc, DocumentRepository repos) throws RhizomeException {
		return;
	}
	
	/**
	 * This method is responsible for taking a body and adding Metadata, Relations, and Extensions.
	 * 
	 * IF a document has a body (i.e. data), this, too should be set here. Implementors may want to 
	 * use {@link prepareBody(String, RhizomeDocument)} to prepare the body for insertion.
	 * @param doc Document WITH DOCUMENT ID SET.
	 * @throws SinciputException If data cannot be added to the document, or if the data is corrupt in some way.
	 */
	protected abstract void populateDocument(RhizomeDocument doc) throws SinciputException;
}
