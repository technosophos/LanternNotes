package com.technosophos.rhizome;

import com.technosophos.rhizome.repository.*;
import com.technosophos.rhizome.repository.fs.*;
import com.technosophos.rhizome.controller.*;
import com.technosophos.rhizome.document.*;
import com.technosophos.rhizome.repository.lucene.*;
import java.util.*;
import java.io.File;

/**
 * This is a very simple sample application.
 */
public final class Runner {
	
	private static String repositoryPath = "/tmp/rhizome/repo";
	private static String indexPath = "/tmp/rhizome/index";
	//private static String filename = "testfile.xml";
	private static String configfile = "commands.xml";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		/*
		 * Make sure the dirs exist. Make them if they do not.
		 */
		File f_repo = new File(repositoryPath);
		File f_index = new File(indexPath);
		if(!f_repo.exists()) f_repo.mkdirs();
		if(!f_index.exists()) f_index.mkdirs();
		
		String default_repo_name = "REPO";
		
		
		// Create a new Rhizome Document
		ArrayList<String> md_vals = new ArrayList<String>();
		md_vals.add("Value 1");
		md_vals.add("Value 2");
		
		String filename = DocumentID.generateDocumentID();
		RhizomeDocument doc = new RhizomeDocument(filename);
		//doc.setBody("This is the test body.");
		//RhizomeData d = new RhizomeData("application/xml", "<?xml version=\"1.0\" ?> <a><b>This is text.</b></a>");
		RhizomeData d = new RhizomeData("application/xml", "<a><b>This is text.</b></a>");
		d.setXMLParseable(true);
		d.setIndexible(false);
		doc.setBody(d);
		doc.addRelation("FakeDocID");
		
		ArrayList<String> vals= new ArrayList<String>();
		vals.add("v1");
		vals.add("v2");
		vals.add("v3");
		
		doc.addMetadatum("Example Name", md_vals);
		doc.addMetadatum("ExampleName2", new ArrayList<String>());
		doc.addMetadatum("ExampleName3", vals);
		doc.addMetadatum("Example Name4", md_vals);
		doc.addMetadatum("Example Name5", md_vals);
		
		// Display doc properties:
		/*
		ArrayList<Metadatum> md2 = doc.getMetadata();
		Iterator<Metadatum> it = md2.iterator();
		while(it.hasNext()) {
			Metadatum datum = it.next();
			System.out.println(datum.toString());
		}
		*/
		//System.out.println(doc.toString());
		System.out.println("Document created. Now storing in repository.");
		
		RepositoryContext context = new RepositoryContext();
		context.addParam(FileSystemRepository.FILE_SYSTEM_PATH_NAME, Runner.repositoryPath);
		context.addParam(LuceneElements.LUCENE_INDEX_PATH_PARAM, Runner.indexPath);
		context.addParam("repository_name", default_repo_name);
		
		RepositoryManager manager = new RepositoryManager();
		try {
			manager.init(context);
			if(!manager.hasRepository(default_repo_name)) {
				manager.createRepository(default_repo_name);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace(System.err);
		}
		try {
			//DocumentRepository repo = manager.getRepository();
			//repo.storeDocument(doc, true);
			manager.storeDocument(default_repo_name, doc);
			
			
			RepositorySearcher search = manager.getSearcher(default_repo_name);
			
			System.out.println("=== BEGIN TESTING SEARCHER.");
			System.out.println("=== Now getting metadata names.");
			String [] sss = search.getMetadataNames();
			for(String t: sss)
				System.out.format("Metadata Name: %s.\n", t);
			
			System.out.println("=== Now getting metadatum by doc ID.");
			Metadatum meta = search.getMetadatumByDocID("Example Name", filename);
			if(meta != null) {
				for(String t: meta.getValues())
					System.out.format("Metadata Value: %s.\n", t);
			}
			
			System.out.println("=== Now getting doc ID my MD name/val.");
			sss = search.getDocIDsByMetadataValue("ExampleName3", "v2");
			if(sss != null) {
			for(String t: sss)
				System.out.format("Document ID: %s.\n", t);
			}
			
			System.out.println("=== Now getting metadata by MD name.");
			java.util.Map<String, String[]> m = search.getMetadataByName("Example Name5");
			java.util.Set<String> keys = m.keySet();
			for(String key: keys) {
				StringBuffer sb = new StringBuffer();
				sb.append(key + ": ");
				String [] val_arr = m.get(key);
				for(String s: val_arr) sb.append(s + ",");
				System.out.println(sb.toString());
			}
			
			System.out.println("=== Now getting metadata by MD name.");
			DocumentList dl;
			dl = search.getMetadataByName("Example Name", new String[] {filename}, manager.getRepository(default_repo_name) );
			/*String[] key_arr = dc.getDocumentIDs();
			for(String key: key_arr) {
				StringBuffer sb = new StringBuffer();
				sb.append(key + ": ");
				List<Metadatum> val_arr = dc.getMetadata(key);
				for(Metadatum s: val_arr) sb.append(s.toString());
				System.out.println(sb.toString());
			}
			*/
			for(RhizomeDocument dd: dl) {
				if(dd instanceof ProxyRhizomeDocument)
					System.out.println("Document is a proxy.");
				System.out.println(dd.toString());
			}
			
			System.out.println("=== Now doing narrowing search.");
			HashMap<String, String> narrower = new HashMap<String, String>();
			narrower.put("Example Name", "Value 1");
			narrower.put("ExampleName3", "v3");
			narrower.put("Example Name5", "Value 2");
			sss = search.narrowingSearch(narrower);
			for(String t: sss)
				System.out.format("Doc Matching Narrower: %s.\n", t);
			
			System.out.println("=== Now doing narrowing search that should fail.");
			narrower.put("Example Name4", "Ol' Susanna");
			sss = search.narrowingSearch(narrower);
			if(sss.length == 0) System.out.println("Success! No bad matches.");
			for(String t: sss)
				System.out.format("FAILED: Doc Matching Narrower: %s.\n", t);
			
			
			/*
			String[] docList = repo.getAllDocumentIDs();
			for(int i = 0; i < docList.length; ++i) {
				System.out.format("Doc ID: %s\n", docList[i]);
			}
			*/
			System.out.println("=== END TESTING SEARCHER.");
			System.out.println("Document stored. Now retrieving.");
			RhizomeDocument newDoc = 
				manager.getDocument(default_repo_name, doc.getDocumentID());
			//System.out.println("Retrieved Data: " + newDoc.getData().toString());
			System.out.println(newDoc.toXML());
			System.out.println("Done retrieving document. Now removing.");
			manager.removeDocument(default_repo_name, newDoc.getDocumentID());
			System.out.format("There are now %d documents in the repository.\n", 
					manager.getRepository(default_repo_name).countDocumentIDs());
			
			
			
			System.out.println("=== BEGIN CONFIG PARSER TEST.");
			try {
				XMLRequestConfigurationReader c = new XMLRequestConfigurationReader(new HashMap<String, String>());
				Map<String, RequestConfiguration> items = 
					c.get(new File(Runner.repositoryPath, Runner.configfile));
				for(String key: items.keySet()) {
					RequestConfiguration rconf = items.get(key);
					Queue<CommandConfiguration> cmds = rconf.getQueue();
					System.out.println("Request: " + key);
					for(CommandConfiguration cconf: cmds) { 
						System.out.println("  Config: " + cconf.toString());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
			
			System.out.println("=== END CONFIG PARSER TEST.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("*** DELETE REPOSITORY ***");
			
			try {
				manager.removeRepository(default_repo_name);
				System.out.println("Repository deleted.");
			} catch(Exception e) { e.printStackTrace(System.err); }
			
		}
		System.exit(0);
		

	}

}
