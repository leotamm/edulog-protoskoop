package ee.protoskoop.gwt.edulog;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ee.protoskoop.gwt.edulog.client.DatabaseService;
import ee.protoskoop.gwt.edulog.server.DAO;
import ee.protoskoop.gwt.edulog.shared.User;

public class UserRegisterTest {


	@BeforeMethod
	public void setUp() {
		User testUser = new User();
		testUser.setEmail("test@user");
		testUser.setPassword("testPass");
	}

	@Test void connectionCheck () {				// testing postgresql database connection
		boolean areWeConnected = DAO.getInstance().connectionCheck();
		Assert.assertTrue(areWeConnected);
	}

	@Test void userDatabaseHasUserData () {		// testing for the presence of user email and password 
		boolean userDataPresent = DAO.getInstance().isAnyUserDataInDatabase();
		Assert.assertTrue(userDataPresent);
	}
	
	@Test void allPasswordsAreSavedAsHash () {	// testing if all passwords are stored as hash
		boolean passwordsAreHash = DAO.getInstance().passwordsAreHash();
		Assert.assertTrue(passwordsAreHash);
	}
	
	@AfterMethod	public void tearDown() {

		User testUser = null;
	}

}
