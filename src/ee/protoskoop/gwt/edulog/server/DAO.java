package ee.protoskoop.gwt.edulog.server;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import ee.protoskoop.gwt.edulog.shared.FeedbackObject;

import ee.protoskoop.gwt.edulog.shared.SessionObject;
import ee.protoskoop.gwt.edulog.shared.User;

public class DAO {

	private static final Logger logger = Logger.getLogger(DAO.class);
	// private DateTimeFormat dtf = new DateTimeFormat("yyyy-mm-dd", new DefaultDateTimeFormatInfo()) {};
	private static DAO instance;
	private ConnectionPool pool;

	public static DAO getInstance() {

		if (instance == null) {
			instance = new DAO();
		}

		return instance;
	}


	public DAO() {

		// load configuration 1. local machine 2. live environment
		// Configuration.loadConfiguration("C:\\Users\\Leo\\eclipse-workspace2\\EduLog\\settings.ini");
		Configuration.loadConfiguration("/opt/tomcat/webapps/edulogconfig/settings.ini");
		
		System.out.println(Configuration.LOG4J_PATH);
		
		PropertyConfigurator.configure(Configuration.LOG4J_PATH);
		Logger.getLogger("org.apache.fontbox").setLevel(Level.OFF);

		pool = new ConnectionPool();
		//pool.start();
	}


	// purely for testing purposes
	public boolean connectionCheck() {
		return pool.isConnected();
	}


	// purely for testing purposes
	public boolean isAnyUserDataInDatabase() {

		boolean reply = false;

		try {

			PreparedStatement pstmt = pool.getConnection().prepareStatement(
					"SELECT email, password FROM el_user;",
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

			ResultSet rs = pstmt.executeQuery();

			rs.first();

			String testUserEmail = rs.getString("email");
			String testUserPassword = rs.getString("password");

			if (testUserEmail != "" && testUserPassword != "") {
				reply = true;
				rs.close();
			}

		} catch (SQLException ex) { logger.error(ex.getMessage()); }

		return reply;
	}


	// purely for testing purposes
	public boolean passwordsAreHash() {

		boolean reply = false;

		try {

			PreparedStatement pstmt = pool.getConnection().prepareStatement(
					"SELECT password FROM el_user;",
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				String storedPassword = rs.getString("password");

				if (storedPassword.length() != 24 && storedPassword.charAt(22) != '='
						&& storedPassword.charAt(23) != '=') { 
					reply = false; 
					rs.close();

				} else { reply = true; }
			}

		} catch (SQLException ex) { logger.error(ex.getMessage()); }

		return reply;
	}


	public boolean changePassword(User user) {

		boolean reply = false;

		try {

			PreparedStatement updatePassword = pool.getConnection().prepareStatement(
					"UPDATE el_user SET password = ? WHERE email = ?", 
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			updatePassword.setString(1, user.getPassword());
			updatePassword.setString(2, user.getEmail());
			updatePassword.executeUpdate();

			reply = true;

		} catch (SQLException ex) { logger.error(ex.getMessage()); }

		return reply;
	}


	public boolean doesUserExist(User user) {

		boolean reply = false;

		try {

			PreparedStatement pstmt = pool.getConnection().prepareStatement(
					"SELECT email FROM el_user;",
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

			ResultSet rs = pstmt.executeQuery();

			if (!rs.next()) { System.out.println("Empty resultset"); }

			rs.beforeFirst();

			while (rs.next()) {

				String email = rs.getString(1);

				if (user.getEmail().equals(email)) {
					reply = true;
					rs.close();
				} 
			}

		} catch (SQLException ex) { logger.error(ex.getMessage()); }

		return reply;
	}


	public String createNewUser(User user, String hashedPassword) {

		String result = "";
		//long id = 0;

		try {

			PreparedStatement pstmt = pool.getConnection().prepareStatement(
					"INSERT INTO el_user(email, password) " + "VALUES(?,?)", 
					Statement.RETURN_GENERATED_KEYS);

			pstmt.setString(1, user.getEmail());
			pstmt.setString(2, hashedPassword);
			int affectedRows = pstmt.executeUpdate();

			if (affectedRows > 0) {

				try (ResultSet rs = pstmt.getGeneratedKeys()) {
					if (rs.next()) {
						result = "ok";
						//id = rs.getLong(1);
					}

				} catch (SQLException ex) { logger.error(ex); }
			}

		} catch (SQLException ex2) { logger.error(ex2.getMessage()); }

		return result;
	}


	public String checkUserCredentials(User user, String hashedPassword) {

		String result = "";

		try {

			PreparedStatement pstmt = pool.getConnection().prepareStatement(
					"SELECT password FROM el_user WHERE email = ?", 
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			pstmt.setString(1, user.getEmail());

			ResultSet rs = pstmt.executeQuery();

			if (!rs.next()) { System.out.println("Empty resultset, perhaps no user at all..."); }

			String passwordDatabase = rs.getString("password");

			if (hashedPassword.equals(passwordDatabase)) {
				result = "ok";
				System.out.println("Access granted!");

			} else { System.out.println("Access denied!"); }

		} catch (SQLException ex) { logger.error(ex.getMessage()); }

		return result;
	}


	// purely for testing purposes
	public boolean sessionsAreStoredLocally(String sessionTeacher, ArrayList<String> sessionClass, ArrayList<String> sessionActivity) {

		boolean result = false;

		ArrayList<String> testClass = new ArrayList<String>();
		ArrayList<String> testActivity = new ArrayList<String>();

		if (sessionTeacher.length() > 0 && sessionClass.size() > 0 && sessionActivity.size() > 0
				&& sessionClass.size() == sessionActivity.size()) {

			for (int i = 0; i < sessionClass.size(); i++) {
				testClass.add(sessionClass.get(i));
				testActivity.add(sessionActivity.get(i));
			}

			if (testClass.equals(sessionClass) && testActivity.equals(sessionActivity)) { 
				result = true;
			}
		}

		return result;
	}
	/*
	
	// deprecated method - use addSessionToDatabase instead
	// test is now supposed to fail
	public ThrowingRunnable addSessionsToDatabase(String sessionTeacher, ArrayList<String> sessionClass, ArrayList<String> sessionActivity) {

		ThrowingRunnable result = null;

		if (sessionTeacher.length() > 0 && sessionClass.size() > 0 && sessionActivity.size() > 0
				&& sessionClass.size() == sessionActivity.size()) {

			// post data to database
			logger.info("Initiating session data transfer to database");

			try {
				PreparedStatement pstmt = pool.getConnection().prepareStatement(
						"INSERT INTO el_session(teacher, study_group, activity) VALUES(?,?,?)",
						ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

				pstmt.setString(1, sessionTeacher);

				final String[] classDataToSql = sessionClass.toArray(new String[sessionClass.size()]);
				final java.sql.Array classSqlArray = pool.getConnection().createArrayOf("text", classDataToSql);
				pstmt.setArray(2, classSqlArray);

				final String[] activityDataToSql = sessionActivity.toArray(new String[sessionActivity.size()]);
				final java.sql.Array activitySqlArray = pool.getConnection().createArrayOf("text", activityDataToSql);
				pstmt.setArray(3, activitySqlArray);

				pstmt.executeUpdate();
				logger.info("Session data transfer to database succesful.");

			} catch (SQLException ex) {
				logger.error(ex.getMessage());
				result = (ThrowingRunnable) ex;
			}

			// read same data from database
			logger.info("Initiation session data reading from database.");
			try {
				PreparedStatement pstmt = pool.getConnection().prepareStatement(
						"SELECT study_group, activity FROM el_session WHERE teacher = ?",
						ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				pstmt.setString(1, sessionTeacher);
				ResultSet rs = pstmt.executeQuery();
				if (!rs.next()) {
					System.out.println("Empty resultset.");
				} else {
					logger.info("Session data reading from database succesful.");
				}

				// compare source and database session data
				ArrayList<String> testClassFromDatabase = new ArrayList<String>();
				ArrayList<String> testActivityFromDatabase = new ArrayList<String>();

				Array classFromDatabase = rs.getArray("study_group");
				Array activityFromDatabase = rs.getArray("activity");

				String[] str_classes = (String[]) classFromDatabase.getArray();
				String[] str_activities = (String[]) activityFromDatabase.getArray();

				for (int i = 0; i < str_classes.length; i++) {
					testClassFromDatabase.add(str_classes[i]);
					testActivityFromDatabase.add(str_activities[i]);
				}

				if (testClassFromDatabase.equals(sessionClass) && testActivityFromDatabase.equals(sessionActivity)) {
					logger.info("Session data comparision succesful.");
					//result = true;
				}

			} catch (SQLException ex2) {
				logger.error(ex2.getMessage());
				result = (ThrowingRunnable) ex2;
			}

		} else {
			Window.alert("Something wrong with session data, writing to database cancelled.");
		}

		return result;
	}
	 */
	public boolean addSubjectsToDatabase(String sessionTeacher, ArrayList<String> sessionSubject) {

		boolean result = false;

		logger.info("Initiating subject data transfer to database");

		try {

			PreparedStatement pstmt = pool.getConnection().prepareStatement(
					"INSERT INTO el_subject(teacher, subject_list) VALUES(?,?)", 
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			pstmt.setString(1, sessionTeacher);

			final String[] subjectDataToSql = sessionSubject.toArray(new String[sessionSubject.size()]);
			final java.sql.Array subjectSqlArray = pool.getConnection().createArrayOf("text", subjectDataToSql);
			pstmt.setArray(2, subjectSqlArray);

			pstmt.executeUpdate();
			logger.info("Subject data transfer to database succesful.");

		} catch (SQLException ex) { logger.error(ex.getMessage()); }

		logger.info("Initiation subject data reading from database.");


		try {

			PreparedStatement pstmt = pool.getConnection().prepareStatement(
					"SELECT subject_list FROM el_subject WHERE teacher = ?", 
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			pstmt.setString(1, sessionTeacher);

			ResultSet rs = pstmt.executeQuery();

			if (!rs.next()) { System.out.println("Empty resultset."); } 
			else { logger.info("Subject data reading from database succesful."); }

			// compare source and database session data
			ArrayList<String> testSubjectFromDatabase = new ArrayList<String>();

			Array subjectFromDatabase = rs.getArray("subject_list");

			String[] str_subjects = (String[]) subjectFromDatabase.getArray();

			for (int i = 0; i < str_subjects.length; i++) {
				testSubjectFromDatabase.add(str_subjects[i]);
			}

			if (testSubjectFromDatabase.equals(sessionSubject)) {
				logger.info("Session data comparision succesful.");
				result = true;
			}

		} catch (SQLException ex2) { logger.error(ex2.getMessage()); }

		return result;
	}


	public List<String> getUserClasses(User user) {

		List<String> groupsArrayFromDatabase = new ArrayList<String>();
		String email = user.getEmail();
		logger.debug("Initiating user class check");

		try {

			PreparedStatement pstmt = pool.getConnection().prepareStatement(
					"SELECT group_list FROM el_study_group WHERE teacher = ?", 
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			pstmt.setString(1, email);

			ResultSet rs = pstmt.executeQuery();

			if (!rs.next()) { logger.debug("User class check received empty resultset"); } 

			else {
				logger.debug("User class check resultset received");

				rs.last();
				Array groupsFromDatabase = rs.getArray("group_list");
				rs.close();

				String[] str_groups = (String[]) groupsFromDatabase.getArray();

				for (int i = 0; i < str_groups.length; i++) {
					groupsArrayFromDatabase.add(str_groups[i]);
				}

				logger.debug("User classes written in return ArrayList");

			}

		} catch (SQLException ex) { logger.error(ex.getMessage()); }

		return groupsArrayFromDatabase;
	}


	public boolean addStudyGroupsToDatabase(String sessionTeacher, ArrayList<String> sessionClass) {

		boolean result = false;
		logger.debug("Initiating teacher classset data transfer to database");

		try {

			PreparedStatement pstmt = pool.getConnection().prepareStatement(
					"INSERT INTO el_study_group(teacher, group_list) VALUES(?,?)", 
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			pstmt.setString(1, sessionTeacher);

			final String[] classDataToSql = sessionClass.toArray(new String[sessionClass.size()]);
			final java.sql.Array classSqlArray = pool.getConnection().createArrayOf("text", classDataToSql);
			pstmt.setArray(2, classSqlArray);

			pstmt.executeUpdate();
			pstmt.close();
			logger.debug("Teacher classset data transfer to database succesful.");

		} catch (SQLException ex) { logger.debug(ex.getMessage()); }

		logger.debug("Teacher classset data transfer to database failed.");

		try {

			PreparedStatement pstmt = pool.getConnection().prepareStatement(
					"SELECT group_list FROM el_study_group WHERE teacher = ?", ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			pstmt.setString(1, sessionTeacher);

			ResultSet rs = pstmt.executeQuery();

			if (!rs.next()) { System.out.println("Empty resultset."); } 
			else { logger.debug("Teacher classset data reading from database succesful."); }

			// compare source and database session data
			ArrayList<String> testClasssetFromDatabase = new ArrayList<String>();

			Array classsetFromDatabase = rs.getArray("group_list");
			rs.close();

			String[] str_classset = (String[]) classsetFromDatabase.getArray();

			for (int i = 0; i < str_classset.length; i++) {
				testClasssetFromDatabase.add(str_classset[i]);
			}

			if (testClasssetFromDatabase.equals(sessionClass)) {
				logger.info("Teacher classet data comparision succesful.");
				result = true;
			}

		} catch (SQLException ex2) { logger.error(ex2.getMessage()); }

		return result;
	}


	public List<String> getUserSubjects(User user) {

		List<String> subjectsArrayFromDatabase = new ArrayList<String>();
		String email = user.getEmail();
		logger.debug("Initiating user subject check");

		try {

			PreparedStatement pstmt = pool.getConnection().prepareStatement(
					"SELECT subject_list FROM el_subject WHERE teacher = ?", 
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					// ResultSet.FETCH_REVERSE,
					ResultSet.CONCUR_UPDATABLE);
			pstmt.setString(1, email);
			ResultSet rs = pstmt.executeQuery();
			if (!rs.next()) {
				logger.debug("User subject check received empty resultset");
			} else {
				logger.debug("User subject check resultset received");

				rs.last();
				Array subjectsFromDatabase = rs.getArray("subject_list");
				rs.close();

				String[] str_subjects = (String[]) subjectsFromDatabase.getArray();

				for (int i = 0; i < str_subjects.length; i++) {
					subjectsArrayFromDatabase.add(str_subjects[i]);
				}
				logger.debug("User subjects written in return ArrayList");
			}
		} catch (SQLException ex) { logger.error(ex.getMessage()); }

		return subjectsArrayFromDatabase;
	}


	public boolean addSessionToDatabase(SessionObject testSession) {

		boolean result = false;

		logger.debug("Session data transfer to database initiated.");

		try {

			PreparedStatement pstmt = pool.getConnection().prepareStatement(
					"INSERT INTO el_session(teacher, study_group, planned_time, subject, topic, goal, activity, "
							+ "duration, created_time, started_time, finished_time, feedback, start_code) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)", 
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_UPDATABLE);

			pstmt.setString(1, testSession.getTeacher());
			pstmt.setString(2, testSession.getStudyGroup());
			pstmt.setObject(3, testSession.getSessionHappeningTime());
			pstmt.setString(4, testSession.getSubject());
			pstmt.setString(5, testSession.getTopic());
			pstmt.setString(6, testSession.getGoal());

			final String[] activityDataToSql = testSession.getActivity().toArray(new String[testSession.getActivity().size()]);
			final java.sql.Array activitySqlArray = pool.getConnection().createArrayOf("text", activityDataToSql);
			pstmt.setArray(7, activitySqlArray);

			final Object[] durationDataToSql = testSession.getDuration().toArray(new Object[testSession.getDuration().size()]);
			final java.sql.Array durationSqlArray = pool.getConnection().createArrayOf("bigint", durationDataToSql);
			pstmt.setArray(8, durationSqlArray);

			pstmt.setObject(9, testSession.getSessionCreatingTime());
			pstmt.setObject(10, testSession.getSessionPlanningDate());
			pstmt.setObject(11, testSession.getSessionFinishingDate());

			final Object[] feedbackDataToSql = testSession.isFeedback().toArray(new Object[testSession.isFeedback().size()]);
			final java.sql.Array feedbackSqlArray = pool.getConnection().createArrayOf("boolean", feedbackDataToSql);
			pstmt.setArray(12, feedbackSqlArray);

			pstmt.setString(13, testSession.getStartCode());

			pstmt.executeUpdate();
			pstmt.close();

			result = true;
			logger.debug("Session data transfer to database successful.");

		} catch (SQLException ex) { logger.debug(ex.getMessage()); }

		return result;
	}


	public List<SessionObject> getSessionFromDatabase(User testTeacher) {

		List <SessionObject> returnSessionList = new ArrayList<SessionObject>();
		String teacherEmail = testTeacher.getEmail();

		logger.debug("Session data reading from database initiated.");

		try {

			PreparedStatement pstmt = pool.getConnection().prepareStatement(
					"SELECT * FROM el_session WHERE teacher = ?", 
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			
			pstmt.setObject(1, teacherEmail);
			ResultSet rs = pstmt.executeQuery();

			if (!rs.next()) {
				
				logger.debug("Empty resultset. Session data reading from database failed.");

			} else {

				int resultListCounter = 0;

				rs.beforeFirst();

				while(rs.next()) {
					ArrayList <String> temporaryActivityList = new ArrayList<String>();
					ArrayList <Long> temporaryDurationList = new ArrayList<Long>();
					ArrayList <Boolean> temporaryFeedbackList = new ArrayList<Boolean>();

					SessionObject returnSession = new SessionObject();

					Long sessionIdFromDatabase = rs.getLong("id");
					returnSession.setId(sessionIdFromDatabase);

					String sessionTeacherFromDatabase = rs.getString("teacher");
					returnSession.setTeacher(sessionTeacherFromDatabase);

					String studyGroupFromDatabase = rs.getString("study_group");
					returnSession.setStudyGroup(studyGroupFromDatabase);

					Long dateFromDataBase = rs.getLong("planned_time");
					returnSession.setSessionHappeningTime(dateFromDataBase);

					String subjectFromDatabase = rs.getString("subject");
					returnSession.setSubject(subjectFromDatabase);

					String topicFromDatabase = rs.getString("topic");
					returnSession.setTopic(topicFromDatabase);

					String goalFromDatabase = rs.getString("goal");
					returnSession.setGoal(goalFromDatabase);

					Array actvitiesFromDatabase = rs.getArray("activity");
					String[] str_activities = (String[]) actvitiesFromDatabase.getArray();
					for (int i = 0; i < str_activities.length; i++) {
						temporaryActivityList.add(str_activities[i]);
					}
					returnSession.setActivity(temporaryActivityList);

					Array durationFromDatabase = rs.getArray("duration");
					Long[] long_durations = (Long[]) durationFromDatabase.getArray();
					for (int i = 0; i < long_durations.length; i++) {
						temporaryDurationList.add(long_durations[i]);
					}
					returnSession.setDuration(temporaryDurationList);

					Long createdFromDatabase = rs.getLong("created_time");
					returnSession.setSessionCreatingTime(createdFromDatabase);

					Long plannedFromDatabase = rs.getLong("started_time");
					returnSession.setSessionPlanningDate(plannedFromDatabase);

					Long finishedFromDatabase = rs.getLong("finished_time");
					returnSession.setSessionFinishingDate(finishedFromDatabase);

					Array feedbackFromDatabase = rs.getArray("feedback");
					Boolean[] boolean_feedback = (Boolean[]) feedbackFromDatabase.getArray();
					for (int i = 0; i < boolean_feedback.length; i++) {
						temporaryFeedbackList.add(boolean_feedback[i]);
					}
					returnSession.setFeedback(temporaryFeedbackList);

					String startcodeFromDatabase = rs.getString("start_code");
					returnSession.setStartCode(startcodeFromDatabase);	

					returnSessionList.add(returnSession);

					resultListCounter ++;

				}

				logger.debug("Session data reading from database successful. Counted " + resultListCounter  + " list item(s)");
				rs.close();
			}

		} catch (SQLException ex2) { logger.error(ex2.getMessage()); }

		return returnSessionList;
	}


	public ArrayList<String> getExistingStartCodes() {

		logger.debug("Reading existing start codes started");

		ArrayList<String> storedStartcodes = new ArrayList<String>();

		try {

			PreparedStatement pstmt = pool.getConnection().prepareStatement(
					"SELECT start_code FROM el_session", 
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = pstmt.executeQuery();

			if (!rs.next()) {
				System.out.println("Empty resultset.");
				logger.debug("Reading exisiting start codes failed");

			} else {
				rs.beforeFirst();
				while(rs.next()) { 
					storedStartcodes.add(rs.getString("start_code")); 

				}

				logger.debug("Reading exisiting start codes success. Returning list of " + String.valueOf(storedStartcodes.size()) + " used codes");
			}

		} catch (SQLException ex) { logger.error(ex.getMessage()); }

		return storedStartcodes;
	}


	public boolean loadWordToDatabase(Integer counter, String startCode) {
		
		boolean result = false;

		try {

			PreparedStatement pstmt = pool.getConnection().prepareStatement(
					"INSERT INTO el_word (id, word) values (?,?)",
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

			pstmt.setInt(1, counter);
			pstmt.setString(2, startCode);
			pstmt.executeUpdate();

			result = true;

		} catch (SQLException ex) { logger.error(ex.getMessage()); }

		return result;
	}

	public String getRandomWordFromDatabase(Integer integer) {

		logger.debug("Reading random start code started");

		String startCode = "";

		try {

			PreparedStatement pstmt = pool.getConnection().prepareStatement(
					"SELECT word FROM el_word WHERE id = ?", 
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			pstmt.setLong(1, integer);
			ResultSet rs = pstmt.executeQuery();

			if (!rs.next()) {
				System.out.println("Empty resultset.");
				logger.debug("Reading random start code failed");

			} else {
				//rs.beforeFirst();
				startCode = rs.getString(1);		
			}

		} catch (SQLException ex) { logger.error(ex.getMessage()); }

		return startCode;
	}


	public boolean addStartTimeToSession(SessionObject session) {

		logger.debug("Adding start time to session started");

		boolean result = false;

		try {

			PreparedStatement pstmt = pool.getConnection().prepareStatement(
					"UPDATE el_session SET started_time = ? where id = ?",
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

			pstmt.setLong(1, session.getSessionPlanningDate());
			pstmt.setLong(2, session.getId());
			pstmt.executeUpdate();

			result = true;

			logger.debug("Adding start time to session succeeded");

		} catch (SQLException ex) { logger.error(ex.getMessage()); logger.debug("Adding start time to session failed"); }

		return result;
	}


	public boolean addEndTimeToSession(SessionObject session) {

		logger.debug("Adding end time to session started");

		boolean result = false;

		try {

			PreparedStatement pstmt = pool.getConnection().prepareStatement(
					"UPDATE el_session SET finished_time = ? where id = ?",
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

			pstmt.setLong(1, session.getSessionFinishingDate());
			pstmt.setLong(2, session.getId());
			pstmt.executeUpdate();

			result = true;

			logger.debug("Adding end time to session succeeded");

		} catch (SQLException ex) { logger.error(ex.getMessage()); logger.debug("Adding end time to session failed"); }

		return result;
	}


	public Long addFeedbackDataToDatabase(FeedbackObject feedbackObject) {

		logger.debug("Adding feedback data to database started");

		Long id = (long) 0;

		try {

			PreparedStatement pstmt = pool.getConnection().prepareStatement(
					"INSERT INTO el_feedback (session_id, teacher, start_code, activity, feedback) values (?,?,?,?,?) RETURNING id",
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

			pstmt.setLong(1, feedbackObject.getSessionId());
			pstmt.setString(2, feedbackObject.getTeacher());
			pstmt.setString(3, feedbackObject.getStartCode());
			pstmt.setString(4, feedbackObject.getActivity());
			// we are setting the first feedback score value as 0, that should later not be considered in analysis

			final Integer[] testScore = feedbackObject.getFeedback().toArray(new Integer[feedbackObject.getFeedback().size()]);
			final java.sql.Array testScoreArray = pool.getConnection().createArrayOf("integer", testScore);
			pstmt.setArray(5, testScoreArray);

			//pstmt.setInt(5, 0);

			ResultSet rs = pstmt.executeQuery();

			rs.next();
			id = rs.getLong(1);

			logger.debug("Adding feedback data to database success");

		} catch (SQLException ex) { logger.error(ex.getMessage()); logger.debug("Adding feedback data to database fail"); }

		return id;
	}


	public FeedbackObject getFeedbackDataFromDatabase(String startCode) {

		logger.debug("Getting feedback object from database started");

		FeedbackObject feedbackObject = new FeedbackObject();
		Long id;
		String activityBack;
		String startCodeBack;

		try {

			PreparedStatement pstmt = pool.getConnection().prepareStatement(
					"SELECT id, activity, start_code FROM el_feedback WHERE start_code = ?", 
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);

			pstmt.setString(1, startCode);
			ResultSet rs = pstmt.executeQuery();

			if (!rs.next()) {
				logger.debug("Empty resultset. Getting feedback object from database success");

			} else {
				rs.last();
				id = rs.getLong(1);
				activityBack = rs.getString(2);
				startCodeBack = rs.getString(3);
				feedbackObject.setId(id);
				feedbackObject.setActivity(activityBack);
				feedbackObject.setStartCode(startCodeBack);

				logger.debug("Getting feedback object from database success");
			}

		} catch (SQLException ex) { logger.error(ex.getMessage()); logger.debug("Getting feedback object from database failed"); }

		return feedbackObject;
	}


	public boolean addMyFeedbackToDatabase(Long feedbackId, Integer feedbackScore) {

		//TODO read current list IN database, update this by new value and write it again

		logger.debug("Adding single feedback score to database  - reading current results - started");

		boolean result = false;

		ArrayList<Integer> storedFeedbackScores = new ArrayList<Integer>();

		try {

			PreparedStatement pstmt = pool.getConnection().prepareStatement(
					"SELECT feedback FROM el_feedback where id = ?", 
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			pstmt.setLong(1, feedbackId);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {

				Array feedbackScoresFromDatabase = rs.getArray("feedback");

				Integer[] int_durations = (Integer[]) feedbackScoresFromDatabase.getArray();
				for (int i = 0; i < int_durations.length; i++) {
					storedFeedbackScores.add(int_durations[i]);
				}

			}

			logger.debug("Adding single feedback score to database  - reading current results - finished");

		} catch (SQLException ex) { logger.error(ex.getMessage()); logger.debug("Adding single feedback score to database  - reading current results - failed"); }

		storedFeedbackScores.add(feedbackScore);

		try {

			logger.debug("Adding single feedback score to database - updating with current result - started");

			PreparedStatement pstmt = pool.getConnection().prepareStatement(
					"UPDATE el_feedback SET feedback = ? WHERE id = ?",
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

			final Object[] feedbackDataToSql = storedFeedbackScores.toArray(new Object[storedFeedbackScores.size()]);
			final java.sql.Array feedbackSqlArray = pool.getConnection().createArrayOf("integer", feedbackDataToSql);

			pstmt.setArray(1, feedbackSqlArray);
			pstmt.setLong(2, feedbackId);

			pstmt.executeUpdate();
			pstmt.close();

			result = true;

			logger.debug("Adding single feedback score to database - updating with current result - finished");

		} catch (SQLException ex) { logger.error(ex.getMessage()); logger.debug("Adding single feedback score to database - updating with current result - failed"); }

		return result;
	}


	public Integer addEndtimeToFeedback(Long feedbackId, FeedbackObject feedbackObject) {

		logger.debug("Adding end time to database and getting feedback count - started");

		Integer feedbackCount = 0;

		try {

			PreparedStatement pstmt = pool.getConnection().prepareStatement(
					"UPDATE el_feedback SET finished_time = ? where id = ? RETURNING feedback",
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

			pstmt.setLong(1, feedbackObject.getFinishedTime());
			pstmt.setLong(2, feedbackId);

			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {

				Array feedbackScoresFromDatabase = rs.getArray("feedback");
				Integer[] int_scores = (Integer[]) feedbackScoresFromDatabase.getArray();
				feedbackCount = int_scores.length;

				logger.debug("Adding end time to database and getting feedback count - success");

			}

		} catch (SQLException ex) { logger.error(ex.getMessage()); logger.debug("Adding end time to database and getting feedback count - fail"); }

		return feedbackCount;
	}

}


