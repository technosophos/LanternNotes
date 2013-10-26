package com.technosophos.lantern.commands.source;

import com.technosophos.lantern.commands.ViewDocument;
import com.technosophos.lantern.types.SourceEnum;
import com.technosophos.rhizome.document.Metadatum;
import com.technosophos.rhizome.document.RhizomeDocument;

public class ViewSource extends ViewDocument {

	// Inherit Javadoc
	protected boolean verifyDocument(RhizomeDocument doc) {
		String type = SourceEnum.TYPE.getFieldDescription().getDefaultValue();
		Metadatum m = doc.getMetadatum(SourceEnum.TYPE.getKey());
		
		//System.err.print("###Title:" + doc.getMetadatum("author").getFirstValue());
		return ( m != null && m.hasValue(type));
	}

}
