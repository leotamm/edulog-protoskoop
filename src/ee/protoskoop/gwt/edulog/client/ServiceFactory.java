package ee.protoskoop.gwt.edulog.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/** New Service class to unify database calls
 * 
 * @author Leo
 *
 */

public class ServiceFactory {
	
	/* unifies all database query url-s to '/database' format in web.xml servlets */
	
public static DatabaseServiceAsync getDBService() {
		
		DatabaseServiceAsync service = GWT.create(DatabaseService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		endpoint.setServiceEntryPoint(GWT.getHostPageBaseURL() + "database");
		return service;
	}

}
