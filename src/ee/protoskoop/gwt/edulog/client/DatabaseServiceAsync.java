package ee.protoskoop.gwt.edulog.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import ee.protoskoop.gwt.edulog.shared.User;


/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface DatabaseServiceAsync {
	void greetServer(String input, AsyncCallback<String> callback) 
			throws IllegalArgumentException;

	void doesUserExist(User user, AsyncCallback<Boolean> callback)
			throws IllegalArgumentException;

	void createNewUser(User user, AsyncCallback<String> callback) 
			throws IllegalArgumentException;

	void checkUserCredentials(User user, AsyncCallback<String> callback) 
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
}
