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

public class Course<HoverEvent> extends Composite implements EntryPoint {

	private static CourseUiBinder uiBinder = GWT.create(CourseUiBinder.class);
	private final DatabaseServiceAsync databaseService = ServiceFactory.getDBService();

	//private static final Logger logger = Logger.getLogger(Course.class);

	interface CourseUiBinder extends UiBinder<Widget, Course> {
	}

	@UiField
	FlexTable studyGroupTable;
	@UiField
	TextBox studyGroupTextBox;
	@UiField
	Button buttonAddStudyGroup;
	@UiField
	Button buttonSaveStudyGroup;
	@UiField
	Button buttonBackToMainPage;


	private int classAddingCounter = 0;
	private String selectedClass1 = "";
	ArrayList<String> selectedClassList = new ArrayList<String>();


	@UiHandler("buttonAddStudyGroup")
	void onClick(ClickEvent eventAddClass) {
		
		selectedClass1 = studyGroupTextBox.getText();

		if (classAddingCounter == 0 && selectedClass1 != "") {
			studyGroupTable.clear();
			studyGroupTable.removeAllRows();
			
			selectedClassList.add(selectedClass1);
			studyGroupTable.insertRow(classAddingCounter);
			studyGroupTable.setHTML(classAddingCounter, 0, "<h6>" + selectedClass1 + "</h6>");
			classAddingCounter ++;
			studyGroupTextBox.setText("");
			buttonSaveStudyGroup.setEnabled(true);
		} else {

			if (selectedClass1 != "") {

				boolean alreadyInList = false;
				for (String item : selectedClassList) {
					if (item.equals(selectedClass1)) {
						alreadyInList = true;
					}			
				}

				if (alreadyInList == false) {
					selectedClassList.add(selectedClass1);
					studyGroupTable.insertRow(classAddingCounter);
					studyGroupTable.setHTML(classAddingCounter, 0, "<h6>" + selectedClass1 + "</h6>");
					classAddingCounter ++;
					studyGroupTextBox.setText("");
				} else { Window.alert("This class is already chosen"); }
			} else { Window.alert("Please add a class first"); }
		}				
	}

	@UiHandler("buttonSaveStudyGroup")
	void onClick1(ClickEvent eventSaveStudyGroup) {

		if (!selectedClassList.isEmpty()) {

			String sessionTeacher = Cookies.getCookie("sessionUser");

			databaseService.addStudyGroupsToDatabase(sessionTeacher, selectedClassList, new AsyncCallback<Boolean>() {

				@Override
				public void onSuccess(Boolean result) {
					// TODO log with logger

					Window.alert("Your classes are saved");	
					buttonSaveStudyGroup.setEnabled(false);
				}

				@Override
				public void onFailure(Throwable caught) {
					// TODO log with logger
					studyGroupTable.clear();
					studyGroupTable.removeAllRows();
					studyGroupTable.insertRow(0);
					studyGroupTable.setHTML(classAddingCounter, 0, "<h6>Info: Saving classes failed</h6>");
				}
			});

			selectedClassList.clear();
			classAddingCounter = 0;
			selectedClass1 = "";
			sessionTeacher= "";

		} else { Window.alert("Please add class(es) first"); }

	}

	@UiHandler("buttonBackToMainPage")
	void onClick2(ClickEvent eventBackToMainPage) {
		Window.Location.assign("Teacher.html");
	}

	public void setUpStudyGroupTable() {

		User user = new User();
		user.setEmail(Cookies.getCookie("sessionUser"));

		databaseService.getUserClasses(user, new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) { /*Window.alert("Get user classes failed!");*/ }
			@Override
			public void onSuccess(List<String> result) { 
				if (result.size() > 0) {
					studyGroupTable.clear();
					for (int i = 0; i < result.size(); i++) {
						studyGroupTable.insertRow(i);
						studyGroupTable.setHTML(i, 0, "<h6>" + result.get(i) + "</h6>");
					}
				} else {
					studyGroupTable.insertRow(0);
					studyGroupTable.setHTML(0, 1, "<p>Please select a class below, and push the "
							+ "<kbd>Add class</kbd> button. Repeat for another class. "
							+ "Make sure to press <kbd>Save my classes</kbd> button, "
							+ "once your class list is complete.</p>");
				}
			}});
	}

	@Override
	public void onModuleLoad() {

		initWidget(uiBinder.createAndBindUi(this));
		RootPanel.get().add(this);

		setUpStudyGroupTable();
		
		buttonSaveStudyGroup.setEnabled(false);

	}

}
