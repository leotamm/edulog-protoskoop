package ee.protoskoop.gwt.edulog.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import ee.protoskoop.gwt.edulog.shared.User;

public class Register extends Composite implements EntryPoint { // siin oli varem HasText (auto-generated)

	private static RegisterUiBinder uiBinder = GWT.create(RegisterUiBinder.class);
	//	private final DatabaseServiceAsync databaseService = GWT.create(DatabaseService.class);    default service not used
	private final DatabaseServiceAsync databaseService = ServiceFactory.getDBService(); 	// custom service in use

	interface RegisterUiBinder extends UiBinder<Widget, Register> {}

	@UiField
	TextBox textEmail;
	@UiField
	PasswordTextBox textPass1;
	@UiField
	PasswordTextBox textPass2;
	@UiField
	Button buttonSubmit;

	Button specialButton = new Button();



	@UiHandler("buttonSubmit")
	void onClick(ClickEvent e) {

		// TODO: get data from register form 									COMPLETED
		String email = textEmail.getValue();
		String pass1 = textPass1.getValue();
		String pass2 = textPass2.getValue();

		// TODO: check if form fields were filled -> alert, reload page			COMPLETED
		// TODO: check if both passwords match -> alert, reload page			COMPLETED
		// TODO: save user data in 'user' database with jdbc sql query			COMPLETED
		// TODO: check if user already exists -> window alert: deny				COMPLETED
		// TODO: write unit tests for writing and reading user data in db		NOT STARTED

		if(email != "" && pass1 != "" && pass2 != "") {

			if(pass1 == pass2) {

				User user = new User();
				user.setEmail(email);
				user.setPassword(pass2);

				// new method returns true if user exists in database
				databaseService.doesUserExist(user, new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) { /*Window.alert("User check failed!");*/ }

					@Override
					public void onSuccess(Boolean result) { /*Window.alert("User check successful!");*/ 
						Window.Location.assign("Login.html");
					}

				}); 

				databaseService.createNewUser(user, new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) { Window.alert("Create user failed!"); }

					@Override
					public void onSuccess(String result) { Window.alert("Create user successful!"); }
				});

			} else {
				Window.alert("Passwords don't match!");
				textPass1.setValue(null, isVisible());
				textPass2.setValue(null, isVisible());
			}

		} else {
			Window.alert("All fields must be filled!");
			textEmail.setValue(null, isVisible());
			textPass1.setValue(null, isVisible());
			textPass2.setValue(null, isVisible());
		}

	}


	@Override
	public void onModuleLoad() {
		// TODO Auto-generated method stub
		initWidget(uiBinder.createAndBindUi(this));
		RootPanel.get().add(this);
		//		RootPanel.get("myDdiv").add(specialButton); // set a Bootstrap component into a specific HTML tag

		/*		got this sorted with Password TextBox widget	
		textName.getElement().setAttribute("type", "text");
		textPass1.getElement().setAttribute("type", "password");
		textPass2.getElement().setAttribute("type", "password");
		 */		
	}

}
