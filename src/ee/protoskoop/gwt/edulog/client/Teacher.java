package ee.protoskoop.gwt.edulog.client;

import java.util.ArrayList;

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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Teacher extends Composite implements EntryPoint{

	private static TeacherUiBinder uiBinder = GWT.create(TeacherUiBinder.class);
	private final DatabaseServiceAsync databaseService = ServiceFactory.getDBService(); 

	interface TeacherUiBinder extends UiBinder<Widget, Teacher> {
	}
	
	@UiField
	FlexTable studyGroupTable;
	@UiField
	TextBox studyGroupTextBox;
	@UiField
	Button buttonAddStudyGroup;
	@UiField
	Button saveStudyGroup;

	@UiField
	ListBox listboxClass;
	@UiField
	ListBox listboxActivity;
	@UiField
	Button buttonAddSession;

	@UiField
	FlexTable sessionTable;

	/*	@UiField
	FlexTable tableClassInSessionList;
	@UiField
	FlexTable tableActivityInSessionList;
	@UiField
	Button buttonEditSessionList;
	@UiField
	Button buttonDeleteSessionList;
	 */
	@UiField
	Button buttonSaveSessionChanges;

	@UiField
	Button buttonLoadSessions;


	private int sessionCounter = 0;
	private String selectedClass = "";
	private String selectedActivity = "";
	//	private List <Session> Sessions = new ArrayList <Session> ();
	//	private Session session = new Session();
	ArrayList<String> sessionClass = new ArrayList<String>();
	ArrayList<String> sessionActivity = new ArrayList<String>();


	@UiHandler("buttonAddSession")
	void onClick1(ClickEvent eventAddSession) {

		// reading user input and outputting it in TextBox
		selectedClass = listboxClass.getSelectedValue();
		selectedActivity = listboxActivity.getSelectedValue();
		
		if (sessionCounter == 0) {
			sessionTable.clear();
			sessionTable.removeAllRows();
		}
		
		// handling the session list with FlexCell
		sessionTable.insertRow(sessionCounter);

		//		sessionTable.setText(sessionCounter, 0, String.valueOf(sessionCounter + 1) + ".");
		sessionTable.setHTML(sessionCounter, 1, "<h6>" + selectedClass + "</h6>");
		//		sessionTable.setText(sessionCounter, 1, selectedClass);
		sessionTable.setHTML(sessionCounter, 2, "<h6>" + selectedActivity + "</h6>");
		//		sessionTable.setText(sessionCounter, 2, selectedActivity);

		sessionClass.add(sessionCounter, selectedClass);
		sessionActivity.add(sessionCounter, selectedActivity);

		sessionCounter = sessionCounter + 1;

		/*		handling the session log by CellTable won't work UiHandler does not support CellTable
		used a list of Session items for storing 
		session.setClassUnit(selectedClass);
		session.setActivity(selectedActivity);
		Sessions.set(sessionCounter, session);		

		buttonAddSession.setEnabled(true);	
		buttonAddSession.setVisible(true);
		 */			
	}

	@UiHandler("buttonSaveSessionChanges")
	void onClick2(ClickEvent eventSaveSessions) {

		if (sessionCounter > 0 && selectedClass != "" && selectedActivity != "") {

			String teacher = Cookies.getCookie("sessionUser");

			GWT.log("Attempting to save session of teacher " + teacher + " saving class " 
					+ sessionClass.get(0) + " activity " + sessionActivity.get(0));

			sessionCounter = 0;
			sessionClass.clear();
			sessionActivity.clear();

			sessionTable.clear();
			sessionTable.removeAllRows();
			Window.alert("Your session is saved");
			sessionTable.insertRow(0);
			sessionTable.setHTML(0, 1, "<h6>No session data yet...</h6>");

		} else { Window.alert("Please add some session data first"); }
	}

	@UiHandler("buttonLoadSessions")
	void onClick3(ClickEvent eventloadSessions) {
		
		Window.alert("Nothing here yet");

	}


	@Override
	public void onModuleLoad() {
		
		initWidget(uiBinder.createAndBindUi(this));
		RootPanel.get().add(this);
		
		sessionTable.insertRow(0);
		sessionTable.setHTML(0, 1, "<h6>No session data yet...</h6>");
		
		studyGroupTable.insertRow(0);
		studyGroupTable.setHTML(0, 1, "<h6>No classes/courses yet...</h6>");

		//FlexTable initiation
		//FlexTable sessionTable = new FlexTable();

		//		RootPanel.get().add(sessionTable);


		//		sessionTable.getFlexCellFormatter().setColSpan(1, 0, 2);

		//		tableClassInSessionList.setText(0, 0, "Some random class");
		//		tableActivityInSessionList.setText(0, 0, "Some random activity with a very long description");

		//CellTable initiation
		/*	    CellTable<Session> sessionLog = new CellTable<Session>();
	    sessionLog.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

	    TextColumn<Session> classColumn = new TextColumn<Session>() {
	        @Override
	        public String getValue(Session object) {
	            return object.getClassUnit();
	        }
	    };
	    sessionLog.addColumn(classColumn, "Class");

	    TextColumn<Session> activityColumn = new TextColumn<Session>() {
	    	@Override
	    	public String getValue(Session object) {
	    		return object.getActivity();
	    	}	
	    } ;
	    sessionLog.addColumn(activityColumn, "Activity");

	    final SingleSelectionModel<Session> selectionModel = new SingleSelectionModel<Session>();
	    sessionLog.setSelectionModel(selectionModel);
	    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
	    	public void onSelectionChange(SelectionChangeEvent event) {
	    		Session selected = selectionModel.getSelectedObject();
	    		if (selected != null) {
	    			Window.alert("You selected: " + selected.getActivity() + " for " + selected.getClassUnit());
	    			}
	    	}
	    });

	    sessionLog.setRowCount(Sessions.size(), true);
	    sessionLog.setRowData(10, Sessions);

	    RootPanel.get().add(sessionLog);
		 */	    

	}

}
