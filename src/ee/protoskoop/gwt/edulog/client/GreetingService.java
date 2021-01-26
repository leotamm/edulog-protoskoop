package ee.protoskoop.gwt.edulog.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet") // "greet" asemel uus syntaks, peab kattuma web.xmliga!
public interface GreetingService extends RemoteService {
String greetServer(String name) throws IllegalArgumentException;

}
