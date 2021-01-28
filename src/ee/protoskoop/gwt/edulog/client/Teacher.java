package ee.protoskoop.gwt.edulog.client;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.util.Cookie;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import ee.protoskoop.gwt.edulog.shared.Session;

public class Teacher extends Composite implements EntryPoint{

	private static TeacherUiBinder uiBinder = GWT.create(TeacherUiBinder.class);
	private final DatabaseServiceAsync databaseService = ServiceFactory.getDBService(); 

	interface TeacherUiBinder extends UiBinder<Widget, Teacher> {
	}

	@UiField
	ListBox listboxClass;
	@UiField
	ListBox listboxActivity;
	@UiField
	Button buttonAddSession;
	
	@UiField
	TextBox selectedSession;
	
	@UiField
	FlexTable sessionTable;
	
	@UiField
	FlexTable tableClassInSessionList;
	@UiField
	FlexTable tableActivityInSessionList;
	@UiField
	Button buttonEditSessionList;
	@UiField
	Button buttonDeleteSessionList;
	
	@UiField
	Button buttonSaveSessionChanges;

	
	private int sessionCounter = 0;
	private List <Session> Sessions = new ArrayList <Session> ();
	private Session session = new Session();
	
	
	@UiHandler("buttonAddSession")
	void onClick(ClickEvent e) {
		
		// reading user input and outputting it in TextBox
		String selectedClass = listboxClass.getSelectedValue();
		String selectedActivity = listboxActivity.getSelectedValue();
		String reply = selectedActivity + " for class " + selectedClass;
		selectedSession.setValue(reply, isVisible());
		
		// handling the session list with FlexCell

		sessionTable.insertRow(sessionCounter);
		
		sessionTable.setText(sessionCounter, 0, String.valueOf(sessionCounter + 1) + ".");
		sessionTable.setText(sessionCounter, 1, selectedClass);
		sessionTable.setText(sessionCounter, 2, selectedActivity);
		
		sessionCounter = sessionCounter + 1;
		
	    
		// handling the session log by CellTable won't work UiHandler does not support CelTable
		// used a list of Session items for storing 
		session.setClassUnit(selectedClass);
		session.setActivity(selectedActivity);
		Sessions.set(sessionCounter, session);		
		
/*		buttonAddSession.setEnabled(true);	
		buttonAddSession.setVisible(true);
*/			
	}

	@Override
	public void onModuleLoad() {
		// TODO Auto-generated method stub
		initWidget(uiBinder.createAndBindUi(this));
		RootPanel.get().add(this);
		
		GWT.log(Cookies.getCookie("sessionUser")); // cookie works!
		
		
		//FlexTable initiation
		FlexTable sessionTable = new FlexTable();

//		sessionTable.getFlexCellFormatter().setColSpan(1, 0, 2);
	    RootPanel.get().add(sessionTable);
	    
	    tableClassInSessionList.setText(0, 0, "Some random class");
	    tableActivityInSessionList.setText(0, 0, "Some random activity with a very long description");
	    
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
