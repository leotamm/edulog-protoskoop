package ee.protoskoop.gwt.edulog.server;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DefaultDateTimeFormatInfo;
import com.google.gwt.user.client.Window;

import ee.protoskoop.gwt.edulog.shared.SessionLargeData;
import ee.protoskoop.gwt.edulog.shared.SessionSmallData;
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

	/*	public boolean isSessionDataPresent(SessionLargeData testLargeSet) {

		String result = "";

		final String currentTeacher = testLargeSet.getTeacher();
		final HashMap<String, String> currentSmallSet = testLargeSet.getSessionSmallData();
		HashMap<String, String> testList = new HashMap<String, String>();

		for (int i = 0; i < currentSmallSet.size(); i++) {
			System.out.println(currentSmallSet.toString());
			// testList.put(currentSmallSet.get(course), currentTeacher)
		}
				return false;
	}
	 */

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

		ArrayList<String> testClass = new ArrayList();
		ArrayList<String> testActivity = new ArrayList();

		if (sessionTeacher.length() > 0 && sessionClass.size() > 0 && sessionActivity.size() > 0 && 
				sessionClass.size() == sessionActivity.size()) {

			for (int i = 0; i < sessionClass.size(); i++) {

				testClass.add(sessionClass.get(i));
				testActivity.add(sessionActivity.get(i));
			}

			if (testClass.equals(sessionClass) && testActivity.equals(sessionActivity)) {

				// post data to database
				logger.info("Initiating session data transfer to database");
				try {
					PreparedStatement pstmt = pool.getConnection().prepareStatement(
							"INSERT INTO el_session(teacher, class, activity) VALUES(?,?,?)");
					pstmt.setString(1, sessionTeacher);
					pstmt.setArray(2, (Array) testClass);
					pstmt.setArray(3, (Array) testActivity);
					pstmt.executeUpdate();
					logger.info("Session data transfer to database succesful.");

				} catch (SQLException ex) {
					logger.error(ex.getMessage());
				}

				// read same data from database
				logger.info("Initiation session data reading from database.");
				try {
					PreparedStatement pstmt = pool.getConnection().prepareStatement(
							"SELECT class, activity FROM el_session WHERE teacher = ?", 
							ResultSet.TYPE_SCROLL_INSENSITIVE,
							ResultSet.CONCUR_UPDATABLE); 
					pstmt.setString(1, sessionTeacher);
					ResultSet rs = pstmt.executeQuery();
					if (!rs.next()) {
						System.out.println("Empty resultset.");
					} else { logger.info("Session data reading from database succesful."); }

					// compare source and database session data
					rs.previous();
					
					ArrayList<String> testClassFromDatabase = new ArrayList();
					ArrayList<String> testActivityFromDatabase = new ArrayList();
					
					while (rs.next()) {
						String classFromDatabase = rs.getString("class");
						String activityFromDatabase = rs.getString("activity");
						testClassFromDatabase.add(classFromDatabase);
						testActivityFromDatabase.add(activityFromDatabase);
					}
					
					if(testClassFromDatabase.equals(testClass) && 
							testActivityFromDatabase.equals(testActivity)) {
						logger.info("Session data comparision succesful.");
						result = true;
					}

				} catch (SQLException ex2) {
					logger.error(ex2.getMessage());
				}
			}

		} else { Window.alert("Something wrong with session data, writing to database cancelled.");}

		return result;
	}


	
}
