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
import java.util.Date;
import java.util.List;


public class SessionTest {

	User testTeacher;
	ArrayList <String> testActivity;
	ArrayList <Long> testDuration;
	SessionObject testSession;
	
	Long lessonLong, createLong, planLong, finishLong, lessonLong2, createLong2, finishLong2, planLong2;

	Date lessonDate, createDate, planDate, finishDate;

	OffsetDateTime lessonDateTime, createDateTime, planDateTime, finishDateTime;
	
	//DateTimeFormat dtfToSeconds, dtfToDays;

	@BeforeMethod
	public void setUp() {
		
		testTeacher = new User();
		testActivity = new ArrayList<String>();
		testDuration = new ArrayList<Long>();
		testSession = new SessionObject();
		
		//dtfToSeconds = DateTimeFormat.getFormat("yyyyMMddHHmmss");
		//dtfToDays = DateTimeFormat.getFormat("yyyyMMdd");
		
		lessonDateTime = OffsetDateTime.now().plusDays(1).plusHours(1);
		createDateTime = OffsetDateTime.now();
		planDateTime = OffsetDateTime.now().plusHours(3);
		finishDateTime = OffsetDateTime.now().plusDays(2);
		
		lessonDate = new Date(lessonDateTime.toInstant().toEpochMilli());
		createDate = new Date(createDateTime.toInstant().toEpochMilli());
		planDate = new Date(planDateTime.toInstant().toEpochMilli());
		finishDate = new Date(finishDateTime.toInstant().toEpochMilli());
		
		//lessonLong = Long.parseLong(dtfToDays.format(lessonDate));
		//createLong = Long.parseLong(dtfToSeconds.format(createDate));
		//planLong = Long.parseLong(dtfToDays.format(planDate));
		//finishLong = Long.parseLong(dtfToDays.format(finishDate));
		
		lessonLong2 = lessonDateTime.toInstant().toEpochMilli();
		createLong2 = createDateTime.toInstant().toEpochMilli();
		planLong2 = planDateTime.toInstant().toEpochMilli();
		finishLong2 = finishDateTime.toInstant().toEpochMilli();

		testTeacher.setEmail("testTeacher@email.test");

		testActivity.add("testActivity reading");
		testActivity.add("testActivity discussion");
		testActivity.add("testActivity test");

		testDuration.add((long) (10*60*1000));
		testDuration.add((long) (25*60*1000));
		testDuration.add((long) (5*60*1000));

		testSession.setTeacher(testTeacher.getEmail());
		testSession.setStudyGroup("testStudyGroup");
		testSession.setSessionHappeningTime(lessonLong2);
		testSession.setSubject("testSubject");
		testSession.setTopic("testTopic");
		testSession.setGoal("Learn to write tests");
		testSession.setActivity(testActivity);
		testSession.setDuration(testDuration);
		testSession.setSessionCreatingTime(createLong2); // returns 00:00:00 at current date
		testSession.setSessionPlanningDate(planLong2);
		testSession.setSessionFinishingDate(finishLong2);
		testSession.setFeedback(false);
		testSession.setStartCode("RABBIT");

	}

	@Test
	public void sessionDataToDatabase() {

		boolean sessionDataToDatabase = DAO.getInstance().addSessionToDatabase(testSession);
		Assert.assertTrue(sessionDataToDatabase);

	}

	@Test void sessionDataFromDatabase() {

		List<SessionObject> sessionDataFromDatabase = DAO.getInstance().getSessionFromDatabase(testTeacher);
		Assert.assertTrue(sessionDataFromDatabase.size() > 0);

	}
	

	@AfterMethod
	public void tearDown() {

		testActivity.clear();
		testDuration.clear();
		testSession = null;
		testTeacher = null;

	}

}
