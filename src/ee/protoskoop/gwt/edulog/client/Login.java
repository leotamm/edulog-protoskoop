package ee.protoskoop.gwt.edulog.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import ee.protoskoop.gwt.edulog.shared.User;

public class Login extends Composite implements EntryPoint { // siin oli varem HasText (auto-generated)

	private static LoginUiBinder uiBinder = GWT.create(LoginUiBinder.class); 
	private final DatabaseServiceAsync databaseService = ServiceFactory.getDBService();

	interface LoginUiBinder extends UiBinder<Widget, Login> {
	}

	public Login() {
		//		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	TextBox textLoginEmail;
	@UiField
	TextBox textLoginPass;
	@UiField
	Button buttonLogin;
	@UiField
	Button buttonForgotPassword;
	@UiField
	Button buttonNewUser;


	User user;
	User user1;

	@UiHandler("buttonLogin")
	void onClick(ClickEvent e) {

		String loginEmail = textLoginEmail.getValue();
		String loginPass = textLoginPass.getValue();
		
		// check if name and password provided
		if (loginEmail !="" && loginPass !="") {
			user = new User();
			user.setEmail(loginEmail);
			user.setPassword(loginPass);

			// check if valid password 
			databaseService.checkUserCredentials(user, new AsyncCallback<String>() {

				@Override
				public void onFailure(Throwable caught) { /*Window.alert("Database query failed!");*/ }

				@Override
				public void onSuccess(String result) { /* Window.alert("Database query successful");*/ 

					if (result.equals("ok")) {
						Cookies.setCookie("sessionUser", user.getEmail());
						Window.Location.assign("Teacher.html");

					} else {
						Window.alert("Login failed");
						textLoginEmail.setValue(null, isVisible());
						textLoginPass.setValue(null, isVisible());
					}

				}}); 


		} else {
			Window.alert("Both fields must be filled!");
			textLoginEmail.setValue(null, isVisible());
			textLoginPass.setValue(null, isVisible());
		}

	}

	@UiHandler("buttonForgotPassword")
	void onClick1(ClickEvent e) {

		String loginEmail1 = textLoginEmail.getValue();
		final User user1 = new User();
		user1.setEmail(loginEmail1);

		// check if email provided
		if (loginEmail1 != "") {

			// continue if user exists
			databaseService.doesUserExist(user1, new AsyncCallback <Boolean> () {

				@Override
				public void onFailure(Throwable caught) { Window.alert("Checking user failed"); }
				@Override
				public void onSuccess(Boolean userExists) { 

					if (userExists) {
						
						// generate random password, update this in database and send this to user email
						databaseService.forgotPassword(user1, new AsyncCallback <Boolean> () {

							@Override
							public void onFailure(Throwable caught) { Window.alert("Creating new password failed"); }
							@Override
							public void onSuccess(Boolean passwordChanged) { 

								if (passwordChanged) { Window.alert("New password was sent to your email"); }

							}
						});

					} else { Window.alert("Unknown user"); }

				}
			});

		} else { Window.alert("Please enter your email!"); }
	}
	
	@UiHandler("buttonNewUser")
	void onClick2(ClickEvent e) {
		
		// redirect to new user register page
		Window.Location.assign("Register.html");
	}


	@Override
	public void onModuleLoad() {

		initWidget(uiBinder.createAndBindUi(this)); // uiBinder initiation
		RootPanel.get().add(this);

		textLoginEmail.getElement().setAttribute("type", "text");
		textLoginPass.getElement().setAttribute("type", "password");

	}

}
