package ee.protoskoop.gwt.edulog.client;

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
}
