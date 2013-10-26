package com.technosophos.lantern.commands.notes;

import com.technosophos.lantern.commands.LanternCommand;
import com.technosophos.rhizome.controller.ReRouteRequest;

import static com.technosophos.lantern.commands.notes.AddNote.*;

/**
 * This class looks for a parent ID, and re-sets that as the main document ID.
 * What? The idea of this command is to make it simple to start with a child, and 
 * redirect to a parent.
 * 
 * @author mbutcher
 *
 */
public class ToParent extends LanternCommand {
	
	protected void execute() throws ReRouteRequest {
		
		
		String parentID = this.getFirstParam(NOTE_PARENT_DOCID, "").toString();
		if(parentID != null && parentID.length() > 0)
			this.params.put("doc", parentID);
	}

}
