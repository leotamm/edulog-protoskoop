package ee.protoskoop.gwt.edulog.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;


public class Teacher extends Composite implements EntryPoint{

	private static TeacherUiBinder uiBinder = GWT.create(TeacherUiBinder.class);
	// private final DatabaseServiceAsync databaseService = ServiceFactory.getDBService(); 

	interface TeacherUiBinder extends UiBinder<Widget, Teacher> { }


	@UiField
	Button buttonCourses;
	@UiField
	Button buttonSubjects;
	@UiField
	Button buttonSessions;
	@UiField
	Button buttonStartSession;
	@UiField
	Button buttonSessionResults;
	@UiField
	Button buttonLogout;
//	@UiField
//	Button buttonLoadWords;


	@UiHandler("buttonCourses")
	void onClick1(ClickEvent eventRedirectToCourses) {

		Window.Location.assign("Course.html");
	}

	@UiHandler("buttonSubjects")
	void onClick2(ClickEvent eventRedirectToSubjects) {

		Window.Location.assign("Subject.html");
	}

	@UiHandler("buttonSessions")
	void onClick3(ClickEvent eventRedirectToSessions) {

		Window.Location.assign("Session.html");
	}
	
	@UiHandler("buttonStartSession")
	void onClick4(ClickEvent eventStartSession) {

		Window.Location.assign("Main.html");
	}
	
	@UiHandler("buttonSessionResults")
	void onClick5(ClickEvent eventSessionResults) {

		Window.alert("Nothing here yet...");
	}

	@UiHandler("buttonLogout")
	void onClick6(ClickEvent eventLogout) {

		Cookies.removeCookie("sessionUser");
		Window.Location.assign("Login.html");
	}
	
	/**
	 * hidden component for quick el_word setup
	 * with a push of a button writes words from file to database
	 * 
	 * 1. above, uncomment buttonLoadWords
	 * 2. below, uncomment buttonLoadWords setEnabled
	
			@UiHandler("buttonLoadWords")
			void onClick5(ClickEvent eventLoadWords) {
		
				// call the method
				Window.alert("Starting load...");
				String junk = "junk";
				Integer x = 1;
		
				databaseService.loadWordToDatabase(x, junk, new AsyncCallback<Boolean>() {
		
					@Override
					public void onFailure(Throwable caught) { Window.alert("Load word to database failed!"); }
		
					@Override
					public void onSuccess(Boolean result) { Window.alert("Load word to database successful!"); }
		
				});
			}

		*/	
			
	@Override
	public void onModuleLoad() {

		initWidget(uiBinder.createAndBindUi(this));
		RootPanel.get().add(this);

		// buttonStartSession.setEnabled(false);
		
		/**
		buttonLoadWords.setEnabled(false);	
		String user = Cookies.getCookie("sessionUser");
		
		if (user.equals("leo.tamm@gmail.com")) {
			buttonLoadWords.setEnabled(true);
		}
		*/
	}

}

