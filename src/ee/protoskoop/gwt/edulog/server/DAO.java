package ee.protoskoop.gwt.edulog.server;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DefaultDateTimeFormatInfo;
import com.google.gwt.user.client.Window;

import ee.protoskoop.gwt.edulog.shared.User;

public class DAO {

	private static final Logger logger = Logger.getLogger(DAO.class);
	private DateTimeFormat dtf = new DateTimeFormat("yyyy-mm-dd", new DefaultDateTimeFormatInfo()) {};
	private static DAO instance;
	private ConnectionPool pool;


	public static DAO getInstance() {
		if (instance == null ) {
			instance = new DAO();
		}
		return instance;
	}

	public DAO() {

		// Load configuration
		Configuration.loadConfiguration("C:\\Users\\Leo\\eclipse-workspace\\EduLog\\settings.ini");
		PropertyConfigurator.configure(Configuration.LOG4J_PATH);
		Logger.getLogger("org.apache.fontbox").setLevel(Level.OFF);

		pool = new ConnectionPool();
		pool.start();
	}

	// purely for testing purposes
	public boolean connectionCheck() {
		return pool.isConnected();
	}

	// purely for testing purposes
	public boolean isAnyUserDataInDatabase () {

		boolean reply = false;

		try {
			PreparedStatement pstmt = pool.getConnection().prepareStatement(
					"SELECT email, password FROM el_user;", 
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE); 
			ResultSet rs = pstmt.executeQuery();

			rs.first();

			String testUserEmail = rs.getString("email");
			String testUserPassword = rs.getString("password");

			if(testUserEmail != "" && testUserPassword != "") {
				reply = true;
			}
		} catch (SQLException ex) {
			logger.error(ex.getMessage()); 
		}	
		return reply;
	}

	// purely for testing purposes
	public boolean passwordsAreHash() {

		boolean reply = false;

		try {
			PreparedStatement pstmt = pool.getConnection().prepareStatement(
					"SELECT password FROM el_user;", 
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE); 
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				String storedPassword = rs.getString("password");
				if(storedPassword.length() != 24 && storedPassword.charAt(22) != '=' 
						&& storedPassword.charAt(23) != '=') { reply = false; } 
				else { reply = true; }			
			}

		} catch (SQLException ex) {
			logger.error(ex.getMessage()); 
		}	
		return reply;
	}

	public boolean doesUserExist(User user) {

		boolean reply = false;

		try {
			PreparedStatement pstmt = pool.getConnection().prepareStatement(
					"SELECT email FROM el_user;", 
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE); 
			ResultSet rs = pstmt.executeQuery();

			if(!rs.next()) {
				System.out.println("Empty resultset");
			}

			rs.first();
			while(rs.next()) {
				String email = rs.getString(1);
				if(user.getEmail().equals(email)) {
					reply = true;
					System.out.println("User exists!");
				} else {
					// System.out.println("No such user!");
				}
			}

		} catch (SQLException ex) {
			logger.error(ex.getMessage()); 
		} 

		return reply;
	}

	public String createNewUser(User user, String hashedPassword) {

		String result = "";
		String SQL = "INSERT INTO el_user(email, password) "
				+ "VALUES(?,?)";
		long id = 0;
		try {
			PreparedStatement pstmt = 
					pool.getConnection().prepareStatement(SQL,
							Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, user.getEmail());
			pstmt.setString(2, hashedPassword);
			int affectedRows = pstmt.executeUpdate();
			System.out.println("Created new user successfully!");

			// check the affected rows 
			if (affectedRows > 0) {
				// get the ID back
				try (ResultSet rs = pstmt.getGeneratedKeys()) {
					if (rs.next()) {
						result = "ok";
						id = rs.getLong(1);
					}
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
				} 
			}
		} catch (SQLException ex) {
			logger.error(ex.getMessage());
		}
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

			if(!rs.next()) {
				System.out.println("Empty resultset, perhaps no user at all...");
			}

			String passwordDatabase = rs.getString("password"); 

			if (hashedPassword.equals(passwordDatabase)) {
				result = "ok";
				System.out.println("Access granted!");
			} else {
				System.out.println("Access denied!");
			}
		} catch (SQLException ex) {
			logger.error(ex.getMessage());
		}

		return result;
	}

	// purely for testing purposes
	public boolean sessionsAreStoredLocally(String sessionTeacher, 
			ArrayList<String> sessionClass, ArrayList<String> sessionActivity) {

		boolean result = false;

		ArrayList<String> testClass = new ArrayList();
		ArrayList<String> testActivity = new ArrayList();

		if (sessionTeacher.length() > 0 && sessionClass.size() > 0 && sessionActivity.size() > 0 && 
				sessionClass.size() == sessionActivity.size()) {

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

	public boolean addSessionToDatabase(String sessionTeacher, 
			ArrayList<String> sessionClass, ArrayList<String> sessionActivity) {

		boolean result = false;

		if (sessionTeacher.length() > 0 && sessionClass.size() > 0 && sessionActivity.size() > 0 && 
				sessionClass.size() == sessionActivity.size()) {

			// post data to database
			logger.info("Initiating session data transfer to database");

			try {
				PreparedStatement pstmt = pool.getConnection().prepareStatement(
						"INSERT INTO el_session(teacher, study_group, activity) VALUES(?,?,?)", 
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_UPDATABLE);

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
			}

			// read same data from database
			logger.info("Initiation session data reading from database.");
			try {
				PreparedStatement pstmt = pool.getConnection().prepareStatement(
						"SELECT study_group, activity FROM el_session WHERE teacher = ?", 
						ResultSet.TYPE_SCROLL_INSENSITIVE,
						ResultSet.CONCUR_UPDATABLE); 
				pstmt.setString(1, sessionTeacher);
				ResultSet rs = pstmt.executeQuery();
				if (!rs.next()) {
					System.out.println("Empty resultset.");
				} else { logger.info("Session data reading from database succesful."); }

				// compare source and database session data
				ArrayList<String> testClassFromDatabase = new ArrayList<String>();
				ArrayList<String> testActivityFromDatabase = new ArrayList<String>();

				Array classFromDatabase = rs.getArray("study_group");
				Array activityFromDatabase = rs.getArray("activity");

				String[] str_classes = (String[])classFromDatabase.getArray();
				String[] str_activities = (String[])activityFromDatabase.getArray();

				for (int i = 0; i< str_classes.length; i++) {
					testClassFromDatabase.add(str_classes[i]);
					testActivityFromDatabase.add(str_activities[i]);
				}

				if(testClassFromDatabase.equals(sessionClass) && 
						testActivityFromDatabase.equals(sessionActivity)) {
					logger.info("Session data comparision succesful.");
					result = true;
				}

			} catch (SQLException ex2) {
				logger.error(ex2.getMessage());
			}

		} else { Window.alert("Something wrong with session data, writing to database cancelled.");}

		return result;
	}


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

		} catch (SQLException ex) {
			logger.error(ex.getMessage());
		}

		logger.info("Initiation subject data reading from database.");
		try {
			PreparedStatement pstmt = pool.getConnection().prepareStatement(
					"SELECT subject_list FROM el_subject WHERE teacher = ?", 
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE); 
			pstmt.setString(1, sessionTeacher);
			ResultSet rs = pstmt.executeQuery();
			if (!rs.next()) {
				System.out.println("Empty resultset.");
			} else { logger.info("Subject data reading from database succesful."); }

			// compare source and database session data
			ArrayList<String> testSubjectFromDatabase = new ArrayList();

			Array subjectFromDatabase = rs.getArray("subject_list");

			String[] str_subjects = (String[])subjectFromDatabase.getArray();

			for (int i = 0; i< str_subjects.length; i++) {
				testSubjectFromDatabase.add(str_subjects[i]);
			}

			if(testSubjectFromDatabase.equals(sessionSubject)) {
				logger.info("Session data comparision succesful.");
				result = true;
			}

		} catch (SQLException ex2) {
			logger.error(ex2.getMessage());
		}

		return result;
	}
	
}
