package ee.protoskoop.gwt.edulog.server;

import ee.protoskoop.gwt.edulog.client.DatabaseService;
import ee.protoskoop.gwt.edulog.shared.FeedbackObject;
import ee.protoskoop.gwt.edulog.shared.FieldVerifier;
import ee.protoskoop.gwt.edulog.shared.SessionObject;
import ee.protoskoop.gwt.edulog.shared.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.server.Base64Utils;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.util.tools.shared.Md5Utils;

/**
 * The server-side implementation of the RPC service.
 */

@SuppressWarnings("serial")
public class DatabaseServiceImpl extends RemoteServiceServlet implements DatabaseService {

	/**
	 * Connect to the PostgreSQL database and return a Connection object
	 */

	public DatabaseServiceImpl () {		
		DAO.getInstance();
	}

	
	@Override
	public boolean doesUserExist(User user) {
		return DAO.getInstance().doesUserExist(user);	
	}
	

	@Override
	public String createNewUser(User user) {
		String hashedPassword = hashPassword(user);
		return DAO.getInstance().createNewUser(user, hashedPassword);
	}
	

	@Override
	public String checkUserCredentials(User user) {
		String hashedPassword = hashPassword(user);
		return DAO.getInstance().checkUserCredentials(user, hashedPassword);
	}
	

	@Override
	public boolean changePassword(User user) {
		return DAO.getInstance().changePassword(user);
	}
	

	public boolean forgotPassword(User userIn) {

		boolean result = false;
		boolean passwordChangedInDatabase = false;
		boolean passwordSentToEmail = false;

		// generate new random 8-digit password string
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
		StringBuilder password = new StringBuilder();

		while (password.length() < 8) {
			int randomIndex = (int) (Math.random() * SALTCHARS.length()) + 1 ;
			password.append(SALTCHARS.charAt(randomIndex));
		}

		String newStringPassword = password.toString();
		userIn.setPassword(newStringPassword);

		// call a method for sending user an email with new string password
		passwordSentToEmail = (SendEmailTLS.sendNewPassword(userIn));

		String newHashPassword = hashPassword(userIn);
		userIn.setPassword(newHashPassword);

		// call a method for updating hashed password in database -> changePassword(User user)
		passwordChangedInDatabase = changePassword(userIn);

		if (passwordSentToEmail & passwordChangedInDatabase) { result = true; }

		return result;
	}

	
	@Override
	public List<String> getUserClasses(User user) {
		return DAO.getInstance().getUserClasses(user);
	}
	

	@Override
	public boolean addStudyGroupsToDatabase(String sessionTeacher, ArrayList<String> selectedClassList) {
		return DAO.getInstance().addStudyGroupsToDatabase(sessionTeacher, selectedClassList);
	}

	
	@Override
	public List<String> getUserSubjects(User user) {
		return DAO.getInstance().getUserSubjects(user);
	}

	
	@Override
	public boolean addSubjectsToDatabase(String sessionTeacher, ArrayList<String> selectedSubjectList) {
		return DAO.getInstance().addSubjectsToDatabase(sessionTeacher, selectedSubjectList);
	}

	
	@Override
	public boolean addSessionToDatabase(SessionObject testSession) {
		return DAO.getInstance().addSessionToDatabase(testSession);
	}

	
	@Override
	public List<SessionObject> getSessionFromDatabase(User testTeacher) {
		return DAO.getInstance().getSessionFromDatabase(testTeacher);
	}

	
	public String hashPassword(User user) {

		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
		StringBuilder salt = new StringBuilder();

		// generate hash form user email + password + 10-digit salt using Md5
		while (salt.length() < 10) {

			int randomIndex = (int) Math.random() * SALTCHARS.length() + 1 ;
			salt.append(SALTCHARS.charAt(randomIndex));
		}

		String saltStr = salt.toString();
		String s = user.getEmail() + user.getPassword() + saltStr;
		String hashedPassword =  Base64Utils.toBase64(Md5Utils.getMd5Digest(s.getBytes()));

		return hashedPassword;
	}

	
	@Override
	public ArrayList<String> getExistingStartCodes() {
		return DAO.getInstance().getExistingStartCodes();
	}

	
	public String getRandomStartCode() {

		String startCode = "";
		
		ArrayList<String> storedStartcodes = new ArrayList<String>();

		// read existing start codes from el_session
		storedStartcodes = getExistingStartCodes();
		
		if (storedStartcodes.size() > 0) { GWT.log("Received used start code list"); }

		boolean codeAlreadyPresent = false;

		while (codeAlreadyPresent) {

			// read a word from el_word database by random integer key
			int randomIndex = (int) (Math.random() * 84094);
			startCode = DAO.getInstance().getRandomWordFromDatabase(randomIndex);
			
			GWT.log("Received random word " + startCode);

			// check if new code not in use		
			for (String code : storedStartcodes) {

				GWT.log("Checking random code with database codes");

				if (!startCode.equals(code)) { 

					codeAlreadyPresent = true; GWT.log("Confirmed code: " + String.valueOf(startCode)); 

				}
			}

		} storedStartcodes.clear(); 
		
		return startCode;
	}

	// el_word database setup method, to be run before allowing user registration
	
	@Override
	public boolean loadWordToDatabase(Integer integer, String word) {

		Boolean result = false;
		
		System.out.println("Starting loading words");

		try {
			
			Integer x = 0;
			
			System.out.println("Starting loading words - file found");
			
			Scanner scanner = new Scanner(new File("/home/ubuntu/english_words.txt"));
			
			if (!scanner.hasNextLine()) { System.out.println("Scanner cannot find next line"); }
			
			System.out.println("Starting loading words - scanner started");

			while (scanner.hasNextLine()) {
				
				x ++;
				String wordFromFile = scanner.nextLine().toUpperCase();
				
				result = DAO.getInstance().loadWordToDatabase(x, wordFromFile);
				
			}

			scanner.close();
			
			System.out.println("Completed loading words. Result: " + String.valueOf(x) + " words in database");

		} catch (FileNotFoundException e) { e.printStackTrace(); System.out.println("File not found");}

		return result;
	}
	
	
	@Override
	public boolean addStartTimeToSession(SessionObject session) {
		return DAO.getInstance().addStartTimeToSession(session);
	}

	
	@Override
	public boolean addEndTimeToSession(SessionObject session) {
		return DAO.getInstance().addEndTimeToSession(session);
	}
	
	
	@Override
	public Long addFeedbackDataToDatabase(FeedbackObject feedbackObject) {
		return DAO.getInstance().addFeedbackDataToDatabase(feedbackObject);
	}
	
	
	@Override
	public FeedbackObject getFeedbackDataFromDatabase(String startCode) {
		return DAO.getInstance().getFeedbackDataFromDatabase(startCode);
	}
	
	@Override
	public boolean addMyFeedbackToDatabase(Long feedbackId, Integer feedbackScore) {
		return DAO.getInstance().addMyFeedbackToDatabase(feedbackId, feedbackScore);
	}
	
		
	@Override
	public Integer addEndtimeToFeedback(Long feedbackId, FeedbackObject feedbackObject) {
		return DAO.getInstance().addEndtimeToFeedback(feedbackId, feedbackObject);
	}
	

	public String greetServer(String input) throws IllegalArgumentException {
		// Verify that the input is valid. 
		if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back to
			// the client.
			throw new IllegalArgumentException("Name must be at least 4 characters long");
		}

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		// Escape data from the client to avoid cross-site script vulnerabilities.
		input = escapeHtml(input);
		userAgent = escapeHtml(userAgent);

		return "Hello, " + input + "!<br><br>I am running " + serverInfo + ".<br><br>It looks like you are using:<br>"
		+ userAgent;
	}

	
	// UI excepts only date as time object
	// we will convert it to Calendar, extract day month and year and the return formatted String
	public String dateToString(Long dateInLong) {
		
		String dateAsString = "";
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(dateInLong);
		
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH) + 1;
		int year = calendar.get(Calendar.YEAR);

		dateAsString = day + "/" + month + "/" + year;
		
		return dateAsString;
	}

	
	 private String escapeHtml(String html) {
		 if (html == null) {
			 return null;
		 }
		 return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	 }

}
