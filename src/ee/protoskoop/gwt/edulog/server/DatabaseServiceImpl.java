package ee.protoskoop.gwt.edulog.server;

import ee.protoskoop.gwt.edulog.client.DatabaseService;
import ee.protoskoop.gwt.edulog.client.GreetingService;
import ee.protoskoop.gwt.edulog.shared.FieldVerifier;
import ee.protoskoop.gwt.edulog.shared.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.google.gwt.user.client.Window;
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
	private final String url = "jdbc:postgresql://localhost/eduLogDatabase";
	private final String user = "postgres";
	private final String password = "docker";

	
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

	/*	public Connection connect() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, user, password);
			System.out.println("Connected to the PostgreSQL server successfully.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}
	 */

	public String hashPassword(User user) {

		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
		StringBuilder salt = new StringBuilder();

		while (salt.length() < 10) { // length of the random string.
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
