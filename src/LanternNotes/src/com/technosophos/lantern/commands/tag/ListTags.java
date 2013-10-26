package com.technosophos.lantern.commands.tag;

import java.util.Map;

import com.technosophos.lantern.SinciputException;
import com.technosophos.lantern.commands.LanternCommand;
import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.repository.RepositoryAccessException;
import com.technosophos.rhizome.repository.RepositorySearcher;
import com.technosophos.rhizome.repository.RhizomeInitializationException;

import static com.technosophos.lantern.types.NotesEnum.TAG;;

/**
 * Get a list of all tags in the repository.
 * @author mbutcher
 *
 */
public class ListTags extends LanternCommand {

	// inherit docs
	protected void execute() throws ReRouteRequest {
		
		try {
			RepositorySearcher search = this.repoman.getSearcher(this.getCurrentRepository());
			Map<String, Integer> tags = search.getMetadataValues(TAG.getKey());
			this.results.add(this.createCommandResult(tags));
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
