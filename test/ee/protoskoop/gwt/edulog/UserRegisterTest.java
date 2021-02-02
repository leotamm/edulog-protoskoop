package ee.protoskoop.gwt.edulog;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ee.protoskoop.gwt.edulog.server.DAO;
import ee.protoskoop.gwt.edulog.shared.User;

public class UserRegisterTest {


	@BeforeMethod
	public void setUp() {
		final User testUser = new User();
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
	
/*	@Test void newUserIsStoredInDatabase (User testUser) {

		final User expectedUser = new User(); 
		expectedUser.setEmail("test@user");
		expectedUser.setPassword("testPass");

		String hashedPassword = DatabaseService.hashPassword(User testUser);
		User actualUser = DAO.getInstance().createNewUser(testUser, hashedPassword);

				Assert.assertEquals(testUser, expectedUser);
	}
*/

	@AfterMethod	public void tearDown() {

		User testUser = null;
	}

}
