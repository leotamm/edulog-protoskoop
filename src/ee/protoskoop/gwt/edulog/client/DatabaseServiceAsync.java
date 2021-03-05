package ee.protoskoop.gwt.edulog.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import ee.protoskoop.gwt.edulog.shared.FeedbackObject;
import ee.protoskoop.gwt.edulog.shared.SessionObject;
import ee.protoskoop.gwt.edulog.shared.User;


/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface DatabaseServiceAsync {
	void greetServer(String input, AsyncCallback<String> callback) 
			throws IllegalArgumentException;

	void doesUserExist(User user, AsyncCallback<Boolean> asyncCallback)
			throws IllegalArgumentException;

	void createNewUser(User user, AsyncCallback<String> callback) 
			throws IllegalArgumentException;

	void checkUserCredentials(User user, AsyncCallback<String> callback) 
			throws IllegalArgumentException;
	
	void changePassword(User user, AsyncCallback<Boolean> asyncCallback)
			throws IllegalArgumentException;

	void getUserClasses(User user, AsyncCallback<List<String>> callback)
			throws IllegalArgumentException;

	void addStudyGroupsToDatabase(String sessionTeacher, ArrayList<String> selectedClassList, AsyncCallback<Boolean> callback)
			throws IllegalArgumentException;

	void getUserSubjects(User user, AsyncCallback<List<String>> callback)
			throws IllegalArgumentException;

	void addSubjectsToDatabase(String sessionTeacher, ArrayList<String> selectedSubjectList,
			AsyncCallback<Boolean> asyncCallback)
					throws IllegalArgumentException;

	void addSessionToDatabase(SessionObject testSession, AsyncCallback<Boolean> asyncCallback)
			throws IllegalArgumentException;

	void getSessionFromDatabase(User testTeacher, AsyncCallback<List<SessionObject>> asyncCallback)
			throws IllegalArgumentException;

	void forgotPassword(User user, AsyncCallback<Boolean> asyncCallback)
			throws IllegalArgumentException;
	
	void getExistingStartCodes(AsyncCallback <ArrayList<String>> asyncCallback)
			throws IllegalArgumentException;
	
	void getRandomStartCode(AsyncCallback<String> asyncCallback)
			throws IllegalArgumentException;
		
	void loadWordToDatabase(Integer integer, String word, AsyncCallback<Boolean> asyncCallback)
			throws IllegalArgumentException;
	
	void dateToString(Long dateInLong, AsyncCallback<String> asyncCallback)
			throws IllegalArgumentException;
	
	void addStartTimeToSession(SessionObject session, AsyncCallback<Boolean> asyncCallback)
			throws IllegalArgumentException;
	
	void addEndTimeToSession(SessionObject session, AsyncCallback<Boolean> asyncCallback)
			throws IllegalArgumentException;
	
	void addFeedbackDataToDatabase(FeedbackObject feedbackObject, AsyncCallback<Long> asyncCallback)
			throws IllegalArgumentException;
	
	void getFeedbackDataFromDatabase(String startCode, AsyncCallback<FeedbackObject> asyncCallback)
			throws IllegalArgumentException;
	
	void addEndtimeAndFeedbackToDatabase(Long feedbackId, FeedbackObject feedbackObject, AsyncCallback<Boolean> asyncCallback)
			throws IllegalArgumentException;
	
}
