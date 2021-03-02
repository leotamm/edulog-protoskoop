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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import ee.protoskoop.gwt.edulog.shared.SessionObject;
import ee.protoskoop.gwt.edulog.shared.User;

public class Session extends Composite implements EntryPoint {

	private static SessionUiBinder uiBinder = GWT.create(SessionUiBinder.class);
	private final DatabaseServiceAsync databaseService = ServiceFactory.getDBService();

	interface SessionUiBinder extends UiBinder<Widget, Session> {
	}

	@UiField
	FlexTable sessionTable;


	@UiField
	ListBox classListBox;
	@UiField
	ListBox dateListBox;
	@UiField
	ListBox subjectListBox;
	@UiField
	TextBox topicTextBox;
	@UiField
	TextBox goalTextBox;
	@UiField
	TextBox activityTextBox;
	@UiField
	ListBox durationListBox;
	@UiField
	ListBox feedbackListBox;

	@UiField
	FlexTable activityTable;

	@UiField
	Button buttonAddActivity;
	@UiField
	Button buttonSaveSession;
	@UiField
	Button buttonBackToMainPage;


	ArrayList<String> selectedActivityList = new ArrayList<String>();
	ArrayList<Long> selectedDurationList = new ArrayList<Long>();
	ArrayList<Boolean> selectedFeedbackList = new ArrayList<Boolean>();

	private int activityAddingCounter = 0;
	private int sessionCounter = 0;
	private String selectedActivity, course, subject, topic, goal;
	private Long selectedDuration, lessonTime, plannedDate, finishedDate;
	private boolean selectedFeedback;

	DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd/MM");

	String startCode = "";


	@UiHandler("buttonAddActivity")
	void onClick(ClickEvent eventAddactivity) {

		selectedActivity = activityTextBox.getText();
		selectedDuration = (long) 0;
		selectedFeedback = Boolean.valueOf(feedbackListBox.getSelectedValue());

		if (activityAddingCounter == 0 & selectedActivity != "") {
			activityTable.clear();
			activityTable.removeAllRows();

			databaseService.getRandomStartCode(new AsyncCallback<String>() {

				@Override
				public void onFailure(Throwable caught) { Window.alert("Create start code failed"); }

				@Override
				public void onSuccess(String result) { startCode = result; }

			});

			selectedDuration = Long.parseLong(durationListBox.getSelectedValue());

			selectedActivityList.add(selectedActivity);
			selectedDurationList.add(selectedDuration);
			selectedFeedbackList.add(selectedFeedback);
			activityTable.insertRow(activityAddingCounter);
			activityTable.setHTML(activityAddingCounter, 0, "<h6>" + selectedActivity + "</h6>");
			activityTable.setHTML(activityAddingCounter, 1, "<h6>(" + selectedDuration / 60000 + " minutes)</h6>");

			if(selectedFeedback) { activityTable.setHTML(activityAddingCounter, 2, "<h6>Get feedback</h6>"); }
			activityAddingCounter ++;
			activityTextBox.setText("");

			buttonSaveSession.setEnabled(true);

		} else {

			selectedDuration = Long.parseLong(durationListBox.getSelectedValue());

			if (selectedActivity != "" && selectedDuration > 0) {

				selectedActivityList.add(selectedActivity);
				selectedDurationList.add(selectedDuration);
				selectedFeedbackList.add(selectedFeedback);
				activityTable.insertRow(activityAddingCounter);
				activityTable.setHTML(activityAddingCounter, 0, "<h6>" + selectedActivity + "</h6>");
				activityTable.setHTML(activityAddingCounter, 1, "<h6>(" + selectedDuration / 60000 + " minutes)</h6>");

				if(selectedFeedback) { activityTable.setHTML(activityAddingCounter, 2, "<h6>Get feedback</h6>"); }
				activityAddingCounter ++;
				activityTextBox.setText("");

			} else { Window.alert("Please add a activity first"); }
		}	
	}


	@UiHandler("buttonSaveSession")
	void onClick1(ClickEvent eventSaveSession) {

		String sessionTeacher = Cookies.getCookie("sessionUser");

		Date time = new Date();
		Long creatingTime = time.getTime();

		String lessonTimeAsString = dateListBox.getSelectedValue();
		lessonTime = Long.valueOf(lessonTimeAsString);
		plannedDate = Long.parseLong("0"); 
		finishedDate = Long.parseLong("0");

		course = classListBox.getSelectedValue();
		subject = subjectListBox.getSelectedValue();
		topic = topicTextBox.getText();
		goal = goalTextBox.getText();

		if (course != "" && lessonTime > 0 && subject != "" && topic != "" && goal != "" &&
				selectedActivityList.size() > 0 && selectedDurationList.size() > 0) {

			SessionObject session = new SessionObject();

			session.setTeacher(sessionTeacher);
			session.setStudyGroup(course);
			session.setSessionHappeningTime(lessonTime);
			session.setSubject(subject);
			session.setTopic(topic);
			session.setGoal(goal);
			session.setActivity(selectedActivityList);
			session.setDuration(selectedDurationList);
			session.setSessionCreatingTime(creatingTime);
			session.setSessionPlanningDate(plannedDate);	// initiating with Long (0)
			session.setSessionFinishingDate(finishedDate);	// initiating with Long (0)
			session.setFeedback(selectedFeedbackList);
			session.setStartCode(startCode);				// generated by databaseService.getRandomStartCode()

			databaseService.addSessionToDatabase(session, new AsyncCallback<Boolean>() {

				@Override
				public void onSuccess(Boolean result) {

					sessionCounter = 0;
					setUpSessionFlexTable();
					buttonSaveSession.setEnabled(false);

					Window.alert("Your session is saved");

				}

				@Override
				public void onFailure(Throwable caught) { Window.alert("Session save failed"); }
				
			});

		} else { Window.alert("All fields must be filled"); }

	}

	@UiHandler("buttonBackToMainPage")
	void onClick2(ClickEvent eventBackToMainPage) {
		Window.Location.assign("Teacher.html");
	}


	private void setUpSessionFlexTable() {

		User user = new User();
		user.setEmail(Cookies.getCookie("sessionUser"));

		databaseService.getSessionFromDatabase(user, new AsyncCallback<List<SessionObject>>() {

			@Override
			public void onFailure(Throwable caught) { /*Window.alert("Get user sessions failed!");*/ }

			@Override
			public void onSuccess(List<SessionObject> sessionListFromDatabase) { 

				if (sessionListFromDatabase.size() > 0) {
					sessionTable.clear();
					sessionTable.removeAllRows();

					for(SessionObject session : sessionListFromDatabase) {

						Long showSessionDate = session.getSessionHappeningTime();

						Date convertSessionDate = new Date (showSessionDate);
						String thisSessionString = dateFormat.format(convertSessionDate);

						String studyGroup = session.getStudyGroup();
						String subject = session.getSubject();
						String topic = session.getTopic();

						sessionTable.insertRow(sessionCounter);
						sessionTable.setHTML(sessionCounter, 0, "<h6>" + thisSessionString + " - </h6>");
						sessionTable.setHTML(sessionCounter, 1, "<h6>" + studyGroup + " - </h6>");
						sessionTable.setHTML(sessionCounter, 2, "<h6>" + subject + " - </h6>");
						sessionTable.setHTML(sessionCounter, 3, "<h6>" + topic + "</h6>");

						sessionCounter ++;
					}

				} else { 
					sessionTable.insertRow(0);
					sessionTable.setHTML(0, 0, "<p>Please select your session course, date, "
							+ "subject, topic and goal below. Then prepare activities list by "
							+ "listing an activity and pressing <kbd>Add subject</kbd> button. "
							+ "Once you session is complete, save it by pressing "
							+ "<kbd>Save my subjects</kbd> button</p>");

				}
			}
		});

	}


	private void setUpDateListBox() {

		// input is a Date in long format
		// we will calculate time in long and
		// display in dateListBox as String

		Date setupTime = new Date();
		Long timeInLong = setupTime.getTime();

		int daysToDisplay = 10;
		Long oneDay = (long) (24*60*60*1000);

		for (int i = 0; i < daysToDisplay; i++) {

			Date convertDate = new Date (timeInLong);
			String thisDateItem = dateFormat.format(convertDate);

			String thisDateValue = String.valueOf(timeInLong);

			dateListBox.addItem(thisDateItem, thisDateValue );

			timeInLong = timeInLong + oneDay;

		}

	}


	private void setUpClassListBox() {

		User user = new User();
		user.setEmail(Cookies.getCookie("sessionUser"));

		databaseService.getUserClasses(user, new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) { /*Window.alert("Get user classes failed!");*/ }

			@Override
			public void onSuccess(List<String> result) { 

				if (result.size() > 0) {

					for (int i = 0; i < result.size(); i++) {
						classListBox.addItem(result.get(i));
					}

				} else { classListBox.addItem("Failed to load your classes"); }
			}
		});
	}


	private void setUpSubjectListBox() {

		User user = new User();
		user.setEmail(Cookies.getCookie("sessionUser"));

		databaseService.getUserSubjects(user, new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) { /*Window.alert("Get user subjects failed!");*/ }

			@Override
			public void onSuccess(List<String> result) { 

				if (result.size() > 0) {

					for (int i = 0; i < result.size(); i++) {
						subjectListBox.addItem(result.get(i));
					}

				} else { subjectListBox.addItem("Failed to load your subjects"); }

			}});
	}


	@Override
	public void onModuleLoad() {

		initWidget(uiBinder.createAndBindUi(this));
		RootPanel.get().add(this);

		setUpSessionFlexTable();
		setUpClassListBox();
		setUpSubjectListBox();
		setUpDateListBox();



		buttonSaveSession.setEnabled(false);

		activityTable.insertRow(0);
		activityTable.setHTML(0, 0, "<p>No session activities</p>");

	}

}
