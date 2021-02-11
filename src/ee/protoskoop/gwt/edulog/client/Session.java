package ee.protoskoop.gwt.edulog.client;

import java.util.ArrayList;
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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import ee.protoskoop.gwt.edulog.shared.SessionObject;
import ee.protoskoop.gwt.edulog.shared.User;

public class Session extends Composite implements EntryPoint{

	private static SessionUiBinder uiBinder = GWT.create(SessionUiBinder.class);
	private final DatabaseServiceAsync databaseService = ServiceFactory.getDBService();

	interface SessionUiBinder extends UiBinder<Widget, Session> {
	}


	@UiField
	ListBox classListBox;
	@UiField
	TextBox lessonDateTextBox;
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
	FlexTable activityTable;

	@UiField
	Button buttonAddActivity;
	@UiField
	Button buttonSaveSession;
	@UiField
	Button buttonBackToMainPage;


	private int activityAddingCounter = 0;
	private String selectedActivity, selectedDuration = "";

	ArrayList<String> selectedActivityList = new ArrayList<String>();
	ArrayList<String> selectedDurationList = new ArrayList<String>();
	
	private String course, lessonDate, subject, topic, goal;
	

	@UiHandler("buttonAddActivity")
	void onClick(ClickEvent eventAddactivity) {

		if (activityAddingCounter == 0) {
			activityTable.clear();
			activityTable.removeAllRows();
			selectedActivity = activityTextBox.getText();
			selectedDuration = durationListBox.getSelectedValue();
			selectedActivityList.add(selectedActivity);
			selectedActivityList.add(selectedDuration);
			activityTable.insertRow(activityAddingCounter);
			activityTable.setHTML(activityAddingCounter, 0, "<h6>" + selectedActivity + "</h6>");
			activityTable.setHTML(activityAddingCounter, 1, "<h6>" + selectedDuration + "</h6>");
			activityAddingCounter ++;
			activityTextBox.setText("");
		} else {
			selectedActivity = activityTextBox.getText();
			selectedDuration = durationListBox.getSelectedValue();

			if (selectedActivity != "" && selectedDuration != "") {

				selectedActivityList.add(selectedActivity);
				selectedDurationList.add(selectedDuration);
				activityTable.insertRow(activityAddingCounter);
				activityTable.setHTML(activityAddingCounter, 0, "<h6>" + selectedActivity + "</h6>");
				activityTable.setHTML(activityAddingCounter, 1, "<h6>" + selectedDuration + "</h6>");
				activityAddingCounter ++;
				activityTextBox.setText("");

			} else { Window.alert("Please add a activity first"); }
		}	
	}


	@UiHandler("buttonSaveSession")
	void onClick1(ClickEvent eventSaveSession) {
		
		String sessionTeacher = Cookies.getCookie("sessionUser");
		Long creatingTime = (long) 1000000;
		Long lessonTime = (long) 2333111;
		
		
		course = classListBox.getSelectedItemText();
		lessonDate = lessonDateTextBox.getText();
		subject = subjectListBox.getSelectedItemText();
		topic = topicTextBox.getSelectedText();
		goal = goalTextBox.getSelectedText();
		
		if (course != "" && lessonDate != "" && subject != "" && topic != "" && goal != "" && 
				selectedActivityList.size() > 0 && selectedDurationList.size() > 0) {
			
			SessionObject session = new SessionObject();
			
			session.setTeacher(sessionTeacher);
			session.setStudyGroup(course);
			session.setSessionDateTime(lessonTime); // TODO receives String but expects OffsetDateTime, hard-coded
			session.setSubject(subject);
			session.setTopic(topic);
			session.setGoal(goal);
			session.setActivity(selectedActivityList);
			//session.setDuration(selectedDurationList); // TODO receives String but expects long
			session.setCreated(creatingTime);
			//session.setPlanned(OffsetDateTime.now().plusHours(3));	won't initiate here
			//session.setFinished(OffsetDateTime.now().plusDays(2));	won't initiate here
			//session.setFeedback(false);								won't initiate here
			//session.setStartCode("RABBIT");							won't initiate here
			
			databaseService.addSessionToDatabase(session, new AsyncCallback<Boolean>() {

				@Override
				public void onSuccess(Boolean result) {
					// TODO log with logger
					 Window.alert("Session succesfully saved");
					
					lessonDateTextBox.setText("");
					topicTextBox.setText("");
					goalTextBox.setText("");
					activityTextBox.setText("");
					activityTable.clear();

				}

				@Override
				public void onFailure(Throwable caught) {
					// TODO log with logger
					 Window.alert("Session save failed");
				}
			});
			
		} else { Window.alert("All fields must be filled"); }

	}

	@UiHandler("buttonBackToMainPage")
	void onClick2(ClickEvent eventBackToMainPage) {
		Window.Location.assign("Teacher.html");
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

		setUpSubjectListBox();

		setUpClassListBox();

		activityTable.insertRow(0);
		activityTable.setHTML(0, 0, "<h6>No activities yet...</h6>");

	}

}
