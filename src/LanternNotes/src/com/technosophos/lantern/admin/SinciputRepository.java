package com.technosophos.lantern.admin;

import com.technosophos.rhizome.document.RhizomeDocument;
import com.technosophos.rhizome.repository.RepositoryManager;
import com.technosophos.rhizome.repository.RepositorySearcher;
import com.technosophos.rhizome.repository.DocumentRepository;
import com.technosophos.rhizome.repository.DocumentIndexer;

/**
 * A simple wrapper providing access to a repository.
 * @author mbutcher
 *
 */
public class SinciputRepository {
	
	RhizomeDocument repoDoc = null;
	RepositoryManager repoman = null;
	RepositorySearcher searcher = null;
	DocumentRepository repo = null;
	DocumentIndexer indexer = null;

	public SinciputRepository( RhizomeDocument doc, RepositoryManager repoman) {
		this.repoDoc = doc;
		this.repoman = repoman;
	}
	
	protected SinciputRepository( RepositoryManager repoman ) {
		this.repoman = repoman;
	}
	
	public RepositorySearcher getSearcher() {
		//if( this.searcher == null) this.searcher =
		return this.searcher;
	}
	
	public DocumentRepository getDocumentRepository() { return null; }
	
	public DocumentIndexer getIndexer() { return null; }
	
	public String getRepositoryName() {return null;}
	
	public boolean canWrite(String username) {return false;}
	
	public boolean canRead(String username) {return false;}
	
	
}
