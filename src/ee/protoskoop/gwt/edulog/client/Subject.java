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
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import ee.protoskoop.gwt.edulog.shared.User;

public class Subject extends Composite implements EntryPoint {

	private static SubjectUiBinder uiBinder = GWT.create(SubjectUiBinder.class);
	private final DatabaseServiceAsync databaseService = ServiceFactory.getDBService();

	interface SubjectUiBinder extends UiBinder<Widget, Subject> {
	}

	@UiField
	FlexTable subjectTable;
	@UiField 
	TextBox subjectTextBox;
	@UiField
	Button buttonAddSubject;
	@UiField
	Button buttonSaveSubject;
	@UiField
	Button buttonBackToMainPage;


	private int subjectAddingCounter = 0;
	private String selectedSubject = "";
	ArrayList<String> selectedSubjectList = new ArrayList<String>();


	@UiHandler("buttonAddSubject")
	void onClick(ClickEvent eventAddSubject) {
		
		selectedSubject = subjectTextBox.getText();

		if (subjectAddingCounter == 0 && selectedSubject != "") {
			subjectTable.clear();
			subjectTable.removeAllRows();
			
			selectedSubjectList.add(selectedSubject);
			subjectTable.insertRow(subjectAddingCounter);
			subjectTable.setHTML(subjectAddingCounter, 0, "<h6>" + selectedSubject + "</h6>");
			subjectAddingCounter ++;
			subjectTextBox.setText("");
			buttonSaveSubject.setEnabled(true);
		} else {

			if (selectedSubject != "") {

				boolean alreadyInList = false;
				for (String item : selectedSubjectList) {
					if (item.equals(selectedSubject)) {
						alreadyInList = true;
					}			
				}

				if (alreadyInList == false) {
					selectedSubjectList.add(selectedSubject);
					subjectTable.insertRow(subjectAddingCounter);
					subjectTable.setHTML(subjectAddingCounter, 0, "<h6>" + selectedSubject + "</h6>");
					subjectAddingCounter ++;
					subjectTextBox.setText("");
				} else { Window.alert("This subject is already chosen"); }
			} else { Window.alert("Please add a subject first"); }
		}	
	}

	@UiHandler("buttonSaveSubject")
	void onClick1(ClickEvent eventSaveStudyGroup) {

			String sessionTeacher = Cookies.getCookie("sessionUser");

			databaseService.addSubjectsToDatabase(sessionTeacher, selectedSubjectList, new AsyncCallback<Boolean>() {

				@Override
				public void onSuccess(Boolean result) {
					// TODO log with logger

					Window.alert("Your courses are saved");	
					
					buttonSaveSubject.setEnabled(false);
					
				}

				@Override
				public void onFailure(Throwable caught) {
					// TODO log with logger
					subjectTable.clear();
					subjectTable.removeAllRows();
					subjectTable.insertRow(0);
					subjectTable.setHTML(subjectAddingCounter, 0, "<h6>Info: Saving subjects failed</h6>");
				}
			});

			selectedSubjectList.clear();
			subjectAddingCounter = 0;
			selectedSubject = "";
			sessionTeacher= "";

	}

	@UiHandler("buttonBackToMainPage")
	void onClick2(ClickEvent eventBackToMainPage) {
		Window.Location.assign("Teacher.html");
	}

	private void setUpSubjectTable() {

		User user = new User();
		user.setEmail(Cookies.getCookie("sessionUser"));

		databaseService.getUserSubjects(user, new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) { /*Window.alert("Get user subjects failed!");*/ }
			@Override
			public void onSuccess(List<String> result) { 
				if (result.size() > 0) {
					subjectTable.clear();
					for (int i = 0; i < result.size(); i++) {
						subjectTable.insertRow(i);
						subjectTable.setHTML(i, 0, "<h6>" + result.get(i) + "</h6>");
					}
				} else {
					subjectTable.insertRow(0);
					subjectTable.setHTML(0, 1, "<p>Please select a subject below, and push the "
							+ "<kbd>Add subject</kbd> button. Repeat for another subject. "
							+ "Make sure to press <kbd>Save my subjects</kbd> button, "
							+ "once youre subjects' list is complete.</p>");
				}
			}});

	}


	@Override
	public void onModuleLoad() {

		initWidget(uiBinder.createAndBindUi(this));
		RootPanel.get().add(this);

		setUpSubjectTable();
		
		buttonSaveSubject.setEnabled(false);

	}

}
