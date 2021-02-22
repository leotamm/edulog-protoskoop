package ee.protoskoop.gwt.edulog.server;

import ee.protoskoop.gwt.edulog.client.DatabaseService;

import ee.protoskoop.gwt.edulog.shared.FieldVerifier;
import ee.protoskoop.gwt.edulog.shared.SessionObject;
import ee.protoskoop.gwt.edulog.shared.User;

import java.util.ArrayList;
import java.util.List;

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
	
	// this connection is deprecated with the use of settings.ini 
	// private final String url = "jdbc:postgresql://localhost/eduLogDatabase";
	// private final String user = "postgres";
	//private final String password = "docker";

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
			// TODO fix random which always returns second char or 'B'
			int randomIndex = (int) Math.random() * SALTCHARS.length() + 1 ;
			salt.append(SALTCHARS.charAt(randomIndex));
		}

		String saltStr = salt.toString();
		String s = user.getEmail() + user.getPassword() + saltStr;
		String hashedPassword =  Base64Utils.toBase64(Md5Utils.getMd5Digest(s.getBytes()));

		return hashedPassword;
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

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */;
	 private String escapeHtml(String html) {
		 if (html == null) {
			 return null;
		 }
		 return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	 }

}
