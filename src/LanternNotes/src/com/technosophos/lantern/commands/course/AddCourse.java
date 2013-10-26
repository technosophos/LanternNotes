package com.technosophos.lantern.commands.course;

import java.util.ArrayList;
import java.util.List;

import com.technosophos.lantern.SinciputException;
import com.technosophos.lantern.commands.AddDocument;
import com.technosophos.lantern.types.CourseEnum;
import com.technosophos.lantern.util.Scrubby;
import com.technosophos.rhizome.document.*;
//import com.technosophos.rhizome.RhizomeException;
//import com.technosophos.rhizome.controller.ReRouteRequest;
//import com.technosophos.lantern.commands.LanternCommand;
//import com.technosophos.rhizome.repository.RepositorySearcher;
//import com.technosophos.rhizome.repository.DocumentRepository;
//import com.technosophos.rhizome.repository.RhizomeInitializationException;
//import static com.technosophos.lantern.servlet.ServletConstants.*;

/**
 * Add a course to a given repository.
 * <p>A Course represents a class, course, or seminar.</p>
 * <p>This default AddCourse class assumes that the note is in HTML. For other body types, you
 * can simply override {@link prepareBody(RhizomeDocument)}.</p>
 * <p>What this object expects in parameters:</p>
 * <ul>
 * <li>title (REQUIRED): Title of the course</li>
 * <li>description: Description of the course</li>
 * <li>instructor: name of instructor(s)</li>
 * <li>instructor_email: Email of the instructor(s)</li>
 * <li>location: Course location</li>
 * <li>course_number: The organizational number assigned. This is free-form: Phil 479 or 
 * 89796-002 should both work.</li>
 * <li>course_times: Day/time info on when the course meets. Free form.</li>
 * <li>start_date: Date course starts</li>
 * <li>end_date: Date course ends</li>
 * <li>tag(s): Zero or more tags</li>
 * <li>body: A place to put a syllabus or so on.</li>
 * </ul> 
 * <p>Additionally, it expects to be able to fetch a username from the session.</p>
 * @author mbutcher
 *
 */
public class AddCourse extends AddDocument {
	
	public final static String COURSE_BODY = "body";

	public void populateDocument(RhizomeDocument doc) throws SinciputException {
		String uname = this.ses.getUserName();
		//String userid = this.ses.getUserUUID();
		
		String title = this.getFirstParam(CourseEnum.TITLE.getKey(), null).toString();
		if( title == null ) {
			String err = "No title specified";
			String ferr = "You need to give a title. Course titles are required.";
			results.add(this.createErrorCommandResult(err, ferr));
			return;
		}
		
		String description = this.getFirstParam(CourseEnum.DESCRIPTION.getKey(), "").toString();
		String location = this.getFirstParam(CourseEnum.LOCATION.getKey(), "").toString();
		String startDate = this.getFirstParam(CourseEnum.START_DATE.getKey(), "").toString();
		String endDate = this.getFirstParam(CourseEnum.END_DATE.getKey(), "").toString();
		String instructor = this.getFirstParam(CourseEnum.INSTRUCTOR.getKey(), "").toString();
		String instructorEmail = this.getFirstParam(CourseEnum.INSTRUCTOR_EMAIL.getKey(), "").toString();
		String courseNumber = this.getFirstParam(CourseEnum.COURSE_NUMBER.getKey(), "").toString();
		String courseTimes = this.getFirstParam(CourseEnum.COURSE_TIMES.getKey(), "").toString();
		String tags = this.getFirstParam(CourseEnum.TAG.getKey(), "").toString();
		List<String> ta = tags == null ? new ArrayList<String>() : this.prepareTags(tags);
		
		// - clean fields
		title = Scrubby.cleanText(title);
		description = Scrubby.cleanText(description);
		location = Scrubby.cleanText(location);
		startDate = Scrubby.cleanText(startDate);
		endDate = Scrubby.cleanText(endDate);
		instructor = Scrubby.cleanText(instructor);
		instructorEmail = Scrubby.cleanText(instructorEmail);
		courseNumber = Scrubby.cleanText(courseNumber);
		courseTimes = Scrubby.cleanText(courseTimes);
		
		// - put em in metadata objects:
		doc.addMetadatum(new Metadatum(CourseEnum.TITLE.getKey(), title));
		doc.addMetadatum(new Metadatum(CourseEnum.DESCRIPTION.getKey(), description));
		doc.addMetadatum(new Metadatum(CourseEnum.LOCATION.getKey(), location));
		doc.addMetadatum(new Metadatum(CourseEnum.START_DATE.getKey(), startDate));
		doc.addMetadatum(new Metadatum(CourseEnum.END_DATE.getKey(), endDate));
		doc.addMetadatum(new Metadatum(CourseEnum.INSTRUCTOR.getKey(), instructor));
		doc.addMetadatum(new Metadatum(CourseEnum.INSTRUCTOR_EMAIL.getKey(), instructorEmail));
		doc.addMetadatum(new Metadatum(CourseEnum.COURSE_NUMBER.getKey(), courseNumber));
		doc.addMetadatum(new Metadatum(CourseEnum.COURSE_TIMES.getKey(), courseTimes));
		doc.addMetadatum(new Metadatum(CourseEnum.TAG.getKey(), ta));
		
		// - set automatic fields
		String time = com.technosophos.rhizome.util.Timestamp.now();
		doc.addMetadatum(new Metadatum(CourseEnum.TYPE.getKey(), 
					CourseEnum.TYPE.getFieldDescription().getDefaultValue()));
		doc.addMetadatum(new Metadatum(CourseEnum.CREATED_ON.getKey(), time ));
		doc.addMetadatum(new Metadatum(CourseEnum.LAST_MODIFIED.getKey(), time));
		doc.addMetadatum(new Metadatum(CourseEnum.CREATED_BY.getKey(), uname ));
		doc.addMetadatum(new Metadatum(CourseEnum.MODIFIED_BY.getKey(), uname ));

		// Do the body:
		String body = this.getFirstParam(COURSE_BODY, "").toString();
		this.prepareBody(body, doc);
	}

}
