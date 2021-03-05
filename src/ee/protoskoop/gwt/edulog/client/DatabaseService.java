package ee.protoskoop.gwt.edulog.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import ee.protoskoop.gwt.edulog.shared.FeedbackObject;
import ee.protoskoop.gwt.edulog.shared.SessionObject;
import ee.protoskoop.gwt.edulog.shared.User;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("database") // "greet" asemel uus syntaks, peab kattuma web.xmliga!
public interface DatabaseService extends RemoteService {

	String greetServer(String name) throws IllegalArgumentException;

	boolean doesUserExist(User user);

	String createNewUser(User user);

	String checkUserCredentials(User user);
	
	public boolean changePassword(User user);

	List<String> getUserClasses(User user);

	boolean addStudyGroupsToDatabase(String sessionTeacher, ArrayList<String> selectedClassList);

	List<String> getUserSubjects(User user);

	boolean addSubjectsToDatabase(String sessionTeacher, ArrayList<String> selectedSubjectList);

	boolean addSessionToDatabase(SessionObject testSession);

	List<SessionObject> getSessionFromDatabase(User user);

	boolean forgotPassword(User user);
	
	String getRandomStartCode();

	ArrayList<String> getExistingStartCodes();

	boolean loadWordToDatabase(Integer integer, String word);
	
	String dateToString(Long dateInLong);

	boolean addStartTimeToSession(SessionObject session);
	
	boolean addEndTimeToSession(SessionObject session);
	
	Long addFeedbackDataToDatabase(FeedbackObject feedbackObject);
	
	FeedbackObject getFeedbackDataFromDatabase(String startCode);
	
	boolean addEndtimeAndFeedbackToDatabase(Long feedbackId, FeedbackObject feedbackObject);

}
