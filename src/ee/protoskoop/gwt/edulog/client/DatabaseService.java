package ee.protoskoop.gwt.edulog.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

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
	
	List<String> getUserClasses(User user);

}
