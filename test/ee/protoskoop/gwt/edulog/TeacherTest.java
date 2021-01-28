package ee.protoskoop.gwt.edulog;

import org.testng.annotations.Test;

import ee.protoskoop.gwt.edulog.server.DAO;
import ee.protoskoop.gwt.edulog.shared.SessionLargeData;

import org.testng.annotations.BeforeMethod;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;

public class TeacherTest {
	
	//private Map<String, Map> expectedSessionData;
	//private Map<String, String> innerExpectedSessionData;

	final String sessionTeacher = "teacher_one";
	ArrayList<String> sessionClass = new ArrayList();
	ArrayList<String> sessionActivity = new ArrayList();
	
	
	@BeforeMethod
	public void beforeMethod() {

		/* final HashMap<String, HashMap<String, String>> expetedSessionData = new HashMap<String, HashMap<String, String>>();
		final HashMap<String, String> innerExpetectedSessionData = new HashMap<String, String>();
		innerExpetectedSessionData.put("1C", "outdoor_study_session");
		innerExpetectedSessionData.put("3B", "indoor_study_session");
		innerExpetectedSessionData.put("2A", "online_study_session");
		innerExpetectedSessionData.put("2B", "online_study_session");
		innerExpetectedSessionData.put("1A", "indoor_study_session");
		expetedSessionData.put("teacher1", innerExpetectedSessionData);
		 

		innerExpectedSessionData = Stream.of(new String[][] {
			{ "1C", "outdoor_study_session" },
			{ "3B", "indoor_study_session" },
			{ "2A", "online_study_session" },
			{ "2B", "online_study_session" },
			{ "1A", "indoor_study_session" },
		}).collect(Collectors.toMap(data -> data[0], data -> data[1]));

		expectedSessionData = new LinkedHashMap();	
		expectedSessionData = Stream.of(new Object[][] { 
			{ "teacher1", innerExpectedSessionData }, 
		}).collect(Collectors.toMap(data -> (String) data[0], data -> (Map) data[1]));
		*/

		sessionClass.add("1C");
		sessionClass.add("3B");
		sessionClass.add("2A");
		sessionClass.add("2B");
		sessionClass.add("1A");

		sessionActivity.add("outdoor_study_session");
		sessionActivity.add("indoor_study_session");
		sessionActivity.add("online_study_session");
		sessionActivity.add("online_study_session");
		sessionActivity.add("indoor_study_session");
	}

	@Test
	public void sessionsAreStoredLocally() {	// testing if session data is stored while teacher 
												// chooses activities for classes
		boolean sessionDataPresent = DAO.getInstance().sessionsAreStoredLocally(sessionTeacher, sessionClass, sessionActivity);			
		Assert.assertTrue(sessionDataPresent);
	}
	
	@Test
	public void sessionsAreStoredInDatabase() {	// testing saving the same dataset in database el_sessions
		
		boolean sessionDataStored = DAO.getInstance().addSessionToDatabase(sessionTeacher, sessionClass, sessionActivity);
		Assert.assertTrue(sessionDataStored);
	}
	
	
	@AfterMethod
	public void afterMethod() {

		sessionClass.clear();
		sessionActivity.clear();

	}

}
