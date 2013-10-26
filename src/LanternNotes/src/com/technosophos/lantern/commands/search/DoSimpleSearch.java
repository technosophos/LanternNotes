package com.technosophos.lantern.commands.search;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

import com.technosophos.lantern.SinciputException;
import com.technosophos.lantern.commands.LanternCommand;
import com.technosophos.rhizome.controller.ReRouteRequest;
import com.technosophos.rhizome.repository.RepositoryAccessException;
import com.technosophos.rhizome.repository.RepositorySearcher;
import com.technosophos.rhizome.repository.DocumentRepository;
import com.technosophos.rhizome.repository.RhizomeInitializationException;
import com.technosophos.lantern.util.Scrubby;
import com.technosophos.lantern.util.TemplateTools;

public class DoSimpleSearch extends LanternCommand {
	
	/**
	 * Parameter for search query: q
	 */
	public static final String SEARCH_QUERY_PARAM = "q";
	/**
	 * Parameter for search offset: o
	 */
	public static final String SEARCH_OFFSET_PARAM = "o";
	/**
	 * Directive for maximum number of items to return: maxSearchResults.
	 * This goes in commands.xml.
	 */
	public static final String SEARCH_PER_PAGE_MAX_DIR = "maxSearchResults";

	protected void execute() throws ReRouteRequest {
		// Fields to search:
		String[] names = {"title","author","description","subtitle","tag","type"};
		TemplateTools tt = new TemplateTools();
		
		// Get the search string
		String q = this.getFirstParam(SEARCH_QUERY_PARAM, "").toString();
		// If query is empty, do what?
		
		// Get the offset
		String o = this.getFirstParam(SEARCH_OFFSET_PARAM, "0").toString();
		int offset = Scrubby.asInt(o, 0);
		
		// Get the max results
		String[] maxArr = this.comConf.getDirective(SEARCH_PER_PAGE_MAX_DIR);
		int max = (maxArr == null || maxArr.length == 0) ? 25 : Scrubby.asInt(maxArr[0], 25);
		
		
		
		// Do the search
		try {
			String repoName = this.getCurrentRepository(); // Throws SinciputException
			RepositorySearcher search = this.repoman.getSearcher(repoName); // Throws a few exceptions
			DocumentRepository repo = this.repoman.getRepository(repoName); // Throws a few exceptions
			
			Map<String, String> args = new HashMap<String, String>();
			args.put("fields", tt.implode(",", Arrays.asList(names)));
			
			// Cast this to get some additional utility functions:
			LanternSearchResults sr = new LanternSearchResults(search.simpleSearch(
					q, names, args, repo, max, offset
			)); // Throws RepositoryAccessException
			
			this.results.add(this.createCommandResult(sr));
		} catch (RhizomeInitializationException e) {
			String ferr = "Search is offline right now.";
			String err = "Cannot initialize repository for reading or searching.";
			this.results.add(this.createErrorCommandResult(err, ferr, e));
		} catch (SinciputException e) {
			String ferr = "Search failed.";
			String err = "Cannot search";
			this.results.add(this.createErrorCommandResult(err, ferr, e));
		} catch (RepositoryAccessException e) {
			String ferr = "Access to the search engine is prohibited at the moment.";
			String err = "Repository cannot be accessed.";
			this.results.add(this.createErrorCommandResult(err, ferr, e));
		}
		
		
	}
	

}
