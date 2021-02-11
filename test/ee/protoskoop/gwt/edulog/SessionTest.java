package ee.protoskoop.gwt.edulog;
import ee.protoskoop.gwt.edulog.server.DAO;
import ee.protoskoop.gwt.edulog.shared.SessionObject;
import ee.protoskoop.gwt.edulog.shared.User;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;

import java.time.OffsetDateTime;
import java.util.ArrayList;


public class SessionTest {

	User testTeacher;
	ArrayList <String> testActivity;
	ArrayList <Long> testDuration;
	SessionObject testSession;

	@BeforeMethod
	public void setUp() {
		
		testTeacher = new User();
		testActivity = new ArrayList<String>();
		testDuration = new ArrayList<Long>();
		testSession = new SessionObject();


		testTeacher.setEmail("testTeacher@email.test");

		testActivity.add("testActivity reading");
		testActivity.add("testActivity discussion");
		testActivity.add("testActivity test");

		testDuration.add((long) (10*60*1000));
		testDuration.add((long) (25*60*1000));
		testDuration.add((long) (5*60*1000));

		testSession.setTeacher(testTeacher.getEmail());
		testSession.setStudyGroup("testStudyGroup");
		//TODO testSession.setSessionDateTime(OffsetDateTime.now().plusDays(1).plusHours(1));
		testSession.setSubject("testSubject");
		testSession.setTopic("testTopic");
		testSession.setGoal("Learn to write tests");
		testSession.setActivity(testActivity);
		testSession.setDuration(testDuration);
		//TODO testSession.setCreated(OffsetDateTime.now()); // returns 00:00:00 at current date
		//TODO testSession.setPlanned(OffsetDateTime.now().plusHours(3));
		//TODO testSession.setFinished(OffsetDateTime.now().plusDays(2));
		testSession.setFeedback(false);
		testSession.setStartCode("RABBIT");

	}

	@Test
	public void sessionDataToDatabase() {

		boolean sessionDataToDatabase = DAO.getInstance().addSessionToDatabase(testSession);
		Assert.assertTrue(sessionDataToDatabase);

	}

	@Test void sessionDataFromDatabase() {

		boolean sessionDataFromDatabase = DAO.getInstance().getSessionFromDatabase(testTeacher);
		Assert.assertTrue(sessionDataFromDatabase);

	}

	@AfterMethod
	public void tearDown() {

		testActivity.clear();
		testDuration.clear();
		testSession = null;
		testTeacher = null;

	}

}
