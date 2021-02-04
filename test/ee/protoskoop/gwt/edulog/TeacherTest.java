package ee.protoskoop.gwt.edulog;
import ee.protoskoop.gwt.edulog.server.DAO;
import ee.protoskoop.gwt.edulog.shared.User;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;

import java.util.ArrayList;
import java.util.List;

public class TeacherTest {

	//private Map<String, Map> expectedSessionData;
	//private Map<String, String> innerExpectedSessionData;

	final String sessionTeacher = "teacher_three";
	ArrayList<String> sessionClass = new ArrayList<String>();
	ArrayList<String> sessionActivity = new ArrayList<String>();
	ArrayList<String> sessionSubject = new ArrayList<String>();



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

		sessionSubject.add("Nature");
		sessionSubject.add("Math");
		sessionSubject.add("Arts");
		sessionSubject.add("PE");
		sessionSubject.add("Music");

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

	@Test void subjectsAreStoredInDatabase() {		// testing if subjects' data is stored in database el_	

		boolean subjectDataStored = DAO.getInstance().addSubjectsToDatabase(sessionTeacher, sessionSubject);
		Assert.assertTrue(subjectDataStored);		
	}

	@Test void studyGroupTableIsPopulated () {

		User userTeacher = new User();
		userTeacher.setEmail("every_teacher");

		List<String> listHasValues = DAO.getInstance().getUserClasses(userTeacher);
		Assert.assertTrue(listHasValues.size() > 0);
	}

	@Test void studyGroupsAreStoredInDatabase () {

		boolean studyGroupsAreStored = DAO.getInstance().addStudyGroupsToDatabase(sessionTeacher, sessionClass);
		Assert.assertTrue(studyGroupsAreStored);

	}

	@Test void subjectTableIsPopulated () {

		User user = new User();
		user.setEmail("every_teacher");

		List<String> listHasValues = DAO.getInstance().getUserSubjects(user);
		Assert.assertTrue(listHasValues.size() > 0);
	}


	@AfterMethod
	public void afterMethod() {

		sessionClass.clear();
		sessionActivity.clear();
		sessionSubject.clear();

	}

}
