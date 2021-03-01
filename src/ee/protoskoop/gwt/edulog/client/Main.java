package ee.protoskoop.gwt.edulog.client;
import java.util.Date;
import java.util.List;

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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import ee.protoskoop.gwt.edulog.shared.SessionObject;
import ee.protoskoop.gwt.edulog.shared.User;


public class Main extends Composite implements EntryPoint {

	private static MainUiBinder uiBinder = GWT.create(MainUiBinder.class);
	private final DatabaseServiceAsync databaseService = ServiceFactory.getDBService();

	interface MainUiBinder extends UiBinder<Widget, Main> {
	}
	
	@UiField
	ListBox listBoxSelectSession;
	@UiField
	Button buttonSelectSession;
	@UiField
	ListBox listBoxSelectActivity;
	@UiField
	Button buttonSelectActivity;
	@UiField
	Button buttonStartFeedback;
	@UiField
	Button buttonBack;
	
	
	@UiHandler("buttonBack")
	void onClick1(ClickEvent eventButtonBack) {
		
		Window.Location.assign("Teacher.html");
	}
	
	
	public void setUpSessionAndActivitySelection() {
		
		User user = new User();
		user.setEmail(Cookies.getCookie("sessionUser"));
		
		databaseService.getSessionFromDatabase(user, new AsyncCallback<List<SessionObject>>() {

			@Override
			public void onFailure(Throwable caught) { Window.alert("Get user sessions fail"); }
			@Override
			public void onSuccess(List<SessionObject> sessionListFromDatabase) { 

				if (sessionListFromDatabase.size() > 0) {
					
					// compile session listbox with user sessions
					// use sessionCounter to indicate the index of selected session -> .getSelectedIndex()
					// when looking up feedback activities
					int sessionCounter = 0;
					
					for(SessionObject session : sessionListFromDatabase) {
						
						Long createdLong = session.getSessionCreatingTime();
						Date createdDate = new Date(createdLong);
						String createdString = String.valueOf(createdDate);
						String displayCreatedString = createdString.substring(4, 11);
						
						String dateAsString = String.valueOf(session.getSessionPlanningDate());
						String studyGroup = session.getStudyGroup();
						String subject = session.getSubject();
						
						listBoxSelectSession.insertItem(displayCreatedString + " - " + studyGroup + " - " + subject, sessionCounter);

						sessionCounter ++;
					}

				} else { Window.alert("Unable to read user session data"); }
				
				
/*				
				// read feedback true positions in database parallel with session activities
				ArrayList <Integer> trueBooleanPositions = new ArrayList<Integer>();
				
				for (SessionObject session : sessionListFromDatabase) {
					
					SessionObject checkThisSession = new SessionObject();
					ArrayList <Boolean> feedbackBooleans = checkThisSession.isFeedback();
					
					int feedbackBooleanCounter = 0;
					for(Boolean feedbackBoolean : feedbackBooleans) {
						
						if(feedbackBoolean) {
							trueBooleanPositions.add(feedbackBooleanCounter);
						} else { Window.alert("No feedback activities in selected session"); }					
					}
				}
*/				
				// compile activity listbox with activities
				
				
			}
		});
	}
	
	@Override
	public void onModuleLoad() {

		initWidget(uiBinder.createAndBindUi(this));
		RootPanel.get().add(this);
		
		buttonSelectActivity.setEnabled(false);
		buttonStartFeedback.setEnabled(false);
		
		setUpSessionAndActivitySelection();
				
	}

}
