package ee.protoskoop.gwt.edulog.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.shared.DateTimeFormat;
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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import ee.protoskoop.gwt.edulog.shared.FeedbackObject;
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
	TextBox startCodeTextBox;
	@UiField
	Button buttonStartFeedback;
	@UiField
	TextBox feedbackProgressTextBox;
	@UiField
	Button buttonEndFeedback;
	@UiField
	Button buttonBack;

	
	ArrayList <SessionObject> allSessionsFromDatabase = new ArrayList<SessionObject>();
	ArrayList <String> selectedSessionActivities = new ArrayList<String>();
	ArrayList <Boolean> selectedSessionFeedback = new ArrayList<Boolean>();

	FeedbackObject feedbackObject;
	ArrayList <String> feedbackActivities = new ArrayList<String>();
	ArrayList <Long> feedbackNumbers = new ArrayList<Long>();

	DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd/MM");

	int selectedSessionIndex;

	static String sessionStartCode;
	static String sessionSubject;
	static String selectedActivity;
	
//	static ArrayList<Integer> collectedFeedback = new ArrayList<Integer>();

	
	@UiHandler("buttonSelectSession")
	void onClick1(ClickEvent eventButtonSelectSession) {

		feedbackObject = new FeedbackObject();

		listBoxSelectActivity.clear();
		startCodeTextBox.setText("");
		feedbackProgressTextBox.setText("");

		selectedSessionIndex = listBoxSelectSession.getSelectedIndex();

		int selectedSessionCounter = 0;

		for(SessionObject session : allSessionsFromDatabase) {

			if(selectedSessionCounter == selectedSessionIndex) {

				// read all activities, booleans and start code
				selectedSessionFeedback = session.isFeedback();
				selectedSessionActivities = session.getActivity();
				sessionStartCode = session.getStartCode();
				sessionSubject = session.getSubject();

				feedbackObject.setSessionId(session.getId());
				feedbackObject.setTeacher(Cookies.getCookie("sessionUser"));
				feedbackObject.setStartCode(sessionStartCode);

				int activityCounter = 0;

				for(String activity : selectedSessionActivities) {

					boolean feedbackRequestedForThisActivity = selectedSessionFeedback.get(activityCounter);

					if (feedbackRequestedForThisActivity) {

						listBoxSelectActivity.insertItem(activity, activityCounter);

						buttonSelectActivity.setEnabled(true);
					}
					
					activityCounter ++;
				}
			}
			
			selectedSessionCounter ++;
		}
	}


	@UiHandler("buttonSelectActivity")
	void onClick2(ClickEvent eventButtonSelectActivity) {

		selectedActivity = listBoxSelectActivity.getSelectedValue();
		feedbackActivities.add(selectedActivity);
		
		String selectedTopicAndActivity = sessionSubject + " - " + selectedActivity;
		
		feedbackObject.setActivity(selectedTopicAndActivity);

		startCodeTextBox.setText(sessionStartCode);

		buttonStartFeedback.setEnabled(true);
	}

	
	static Long feedbackId;

	@UiHandler("buttonStartFeedback")
	void onClick3(ClickEvent eventButtonStartFeedback) {
		
		//collectedFeedback = new ArrayList<Integer>();

		feedbackProgressTextBox.setText("Collection started...");

		buttonEndFeedback.setEnabled(true);

		// write session start time in el_session, besides valid start code, this will be an additional requirement for students to log on to session feedback session
		SessionObject sendStartTimeToDatabase = new SessionObject();

		Date sessionStartTime = new Date();
		Long sessionStartTimeInLong = sessionStartTime.getTime();
		sendStartTimeToDatabase.setSessionPlanningDate(sessionStartTimeInLong);

		Long feedbackSessionId = feedbackObject.getSessionId();
		sendStartTimeToDatabase.setId(feedbackSessionId);
		
		ArrayList<Integer> feedback = new ArrayList<Integer>();
		feedback.add(0);
		feedbackObject.setFeedback(feedback);

		databaseService.addStartTimeToSession(sendStartTimeToDatabase, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) { Window.alert("Sending session starting time to database failed"); }

			@Override
			public void onSuccess(Boolean result) { 

				databaseService.addFeedbackDataToDatabase(feedbackObject, new AsyncCallback<Long>() {

					@Override
					public void onFailure(Throwable caught) { Window.alert("Sending feedback to database failed"); }

					@Override
					public void onSuccess(Long id) { 

						feedbackId = id;
					}
				});
			}
		});
	}

	
	static Long feedbackSessionId;
	static int testScore;
	static int replies;

	@UiHandler("buttonEndFeedback")
	void onClick4(ClickEvent eventButtonEndFeedback) {

		// write session end time to el_session
		SessionObject sendEndTimeToDatabase = new SessionObject();

		Date sessionEndTime = new Date();
		Long sessionEndTimeInLong = sessionEndTime.getTime();
		sendEndTimeToDatabase.setSessionFinishingDate(sessionEndTimeInLong);

		feedbackSessionId = feedbackObject.getSessionId();
		sendEndTimeToDatabase.setId(feedbackSessionId);

		databaseService.addEndTimeToSession(sendEndTimeToDatabase, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) { Window.alert("Sending session ending time to database failed"); }

			@Override
			public void onSuccess(Boolean result) { 

				// send end time  to el_feedback
				FeedbackObject endtime = new FeedbackObject();
				
				Date feedbackFinishTime = new Date();
				Long feedbackFinishTimeInLong = feedbackFinishTime.getTime();
				endtime.setFinishedTime(feedbackFinishTimeInLong);

				databaseService.addEndtimeToFeedback(feedbackId, endtime, new AsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) { Window.alert("Adding end time and feedback to database failed"); }

					@Override
					public void onSuccess(Integer result) { 

						replies = result - 1;
						
						feedbackProgressTextBox.setText("Collection ended with " + String.valueOf(replies) + " replies");

						buttonSelectActivity.setEnabled(false);
						buttonStartFeedback.setEnabled(false);
						buttonEndFeedback.setEnabled(false);

					}
				});
			}
		});
	}
	
	
	@UiHandler("buttonBack")
	void onClick5(ClickEvent eventButtonBack) { Window.Location.assign("Teacher.html"); }


	public void setUpSessionAndActivitySelection() {

		User user = new User();
		user.setEmail(Cookies.getCookie("sessionUser"));

		databaseService.getSessionFromDatabase(user, new AsyncCallback<List<SessionObject>>() {

			@Override
			public void onFailure(Throwable caught) { Window.alert("Get user sessions fail"); }
			@Override
			public void onSuccess(List<SessionObject> sessionListFromDatabase) { 

				allSessionsFromDatabase = (ArrayList<SessionObject>) sessionListFromDatabase;

				if (sessionListFromDatabase.size() > 0) {

					// populate session listbox with user sessions
					int sessionCounter = 0;

					for(SessionObject session : sessionListFromDatabase) {

						Long plannedSessionLong = session.getSessionHappeningTime();

						Date plannedSessionDate = new Date (plannedSessionLong);
						String plannedSessionString = dateFormat.format(plannedSessionDate);

						String studyGroup = session.getStudyGroup();
						String subject = session.getSubject();

						listBoxSelectSession.insertItem(plannedSessionString + " - " + studyGroup + " - " + subject, sessionCounter);

						sessionCounter ++;
					}
					
				} else { Window.alert("Unable to read user session data"); }
			}
		});
	}
	

	@Override
	public void onModuleLoad() {

		initWidget(uiBinder.createAndBindUi(this));
		RootPanel.get().add(this);

		buttonSelectActivity.setEnabled(false);
		buttonStartFeedback.setEnabled(false);
		buttonEndFeedback.setEnabled(false);

		setUpSessionAndActivitySelection();

	}

}