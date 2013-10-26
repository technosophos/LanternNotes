package com.technosophos.lantern.admin;

import java.util.ArrayList;

import com.technosophos.rhizome.document.Extension;
import com.technosophos.rhizome.document.Metadatum;
import com.technosophos.rhizome.document.Relation;
import com.technosophos.rhizome.document.RhizomeData;
import com.technosophos.rhizome.document.RhizomeDocument;

public class User extends RhizomeDocument {

	public User(String docID) {
		super(docID);
		// TODO Auto-generated constructor stub
	}

	public User(String docID, String data) {
		super(docID, data);
		// TODO Auto-generated constructor stub
	}

	public User(String docID, ArrayList<Metadatum> metadata,
			ArrayList<Relation> relations, RhizomeData body,
			ArrayList<Extension> extensions) {
		super(docID, metadata, relations, body, extensions);
		// TODO Auto-generated constructor stub
	}
	
	/*
	public static User createNewUser() {
		return new User(null)''
	}
	*/

}
