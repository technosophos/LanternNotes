package com.technosophos.lantern.commands.admin;

import com.technosophos.lantern.SinciputException;
import com.technosophos.lantern.commands.LanternCommand;
import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.repository.RepositoryAccessException;
import com.technosophos.rhizome.repository.RhizomeInitializationException;

/**
 * Rebuild the index for the user's current repository.
 * @author mbutcher
 *
 */
public class RebuildIndex extends LanternCommand {

	/**
	 * Rebuild an index.
	 */
	protected void execute() throws ReRouteRequest {
		// TODO Auto-generated method stub
		try {
			this.repoman.getIndexer(this.getCurrentRepository()).reindex(this.repoman);
		} catch (RepositoryAccessException e) {
			String err = "Failure accessing repository.";
			String ferr = "The repository is not available right now.";
			this.results.add(this.createErrorCommandResult(err, ferr, e));
		} catch (RhizomeInitializationException e) {
			String err = "Initialization of the repository failed: " + e.toString();
			String ferr = "The repository is not available right now.";
			this.results.add(this.createErrorCommandResult(err, ferr, e));
		} catch (SinciputException e) {
			String err = "Reindexing failed: " + e.toString();
			String ferr = "The repository cannot be recreated.";
			this.results.add(this.createErrorCommandResult(err, ferr, e));
		}
	}

}
