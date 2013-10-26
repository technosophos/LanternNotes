package com.technosophos.lantern.commands;

import com.technosophos.lantern.SinciputException;
import com.technosophos.rhizome.RhizomeException;
import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.repository.RhizomeInitializationException;

/**
 * Ruthlessly delete a document from the repository.
 * @author mbutcher
 *
 */
public class DeleteDocument extends LanternCommand {

	/**
	 * Deletes a document.
	 */
	protected void execute() throws ReRouteRequest {
		// FIXME: This may leave orphans. Should check to see if this doc is a parent to 
		// others.
		if(this.hasParam("doc")) {
			String docID = this.getFirstParam("doc", "").toString();
			
			try {
				this.repoman.removeDocument(this.getCurrentRepository(), docID);
			} catch (RhizomeInitializationException e) {
				String err = String.format("Failure removing document %s: %s.", docID, e.toString());
				String ferr = "The selected document cannot be removed right now.";
				this.results.add(this.createErrorCommandResult(err, ferr, e));
			} catch (SinciputException e) {
				String err = String.format("Failure removing document %s: %s.", docID, e.toString());
				String ferr = "The selected document cannot be removed right now.";
				this.results.add(this.createErrorCommandResult(err, ferr, e));
			} catch (RhizomeException e) {
				String err = String.format("Failure removing document %s: %s.", docID, e.toString());
				String ferr = "The selected document cannot be removed right now.";
				this.results.add(this.createErrorCommandResult(err, ferr, e));
			}
		}
	}

}
