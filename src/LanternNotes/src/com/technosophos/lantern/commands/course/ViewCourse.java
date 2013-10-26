package com.technosophos.lantern.commands.course;

import com.technosophos.lantern.commands.ViewDocument;
import com.technosophos.lantern.types.CourseEnum;
import com.technosophos.rhizome.document.Metadatum;
import com.technosophos.rhizome.document.RhizomeDocument;

/**
 * View a course.
 * This command takes only one parameter: 
 * <ul><li>doc: The document ID of the item to retrieve and view.</li></ul>
 * @author mbutcher
 *
 */
public class ViewCourse extends ViewDocument {

	// Inherit Javadoc
	protected boolean verifyDocument(RhizomeDocument doc) {
		String type = CourseEnum.TYPE.getFieldDescription().getDefaultValue();
		Metadatum m = doc.getMetadatum(CourseEnum.TYPE.getKey());
		return ( m != null && m.hasValue(type));
	}

}
