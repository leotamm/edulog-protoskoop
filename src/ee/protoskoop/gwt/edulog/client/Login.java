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

	User user;

	@UiHandler("buttonLogin")
	void onClick(ClickEvent e) {

		String loginEmail = textLoginEmail.getValue();
		String loginPass = textLoginPass.getValue();

		// TODO: check if form fields were filled -> alert, reload page			COMPLETED
		// TODO: check credentials -> alert, reload page						COMPLETED
		// TODO: redirect to main page											IN PROGRESS			
		// TODO: write unit tests												NOT STARTED

		if(loginEmail !="" && loginPass !="") {
			user = new User();
			user.setEmail(loginEmail);
			user.setPassword(loginPass);

			// checks if correct password provided
			// returns true if all correct
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

	@Override
	public void onModuleLoad() {

		initWidget(uiBinder.createAndBindUi(this)); // uiBinder initiation
		RootPanel.get().add(this);

		textLoginEmail.getElement().setAttribute("type", "text");
		textLoginPass.getElement().setAttribute("type", "password");

	}

}
