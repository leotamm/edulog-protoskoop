package ee.protoskoop.gwt.edulog.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * EduLog is an student engagement measuring web application
 * developed by Leo Tamm as an vali-it.ee Java development course project
 * during protoskoop.ee internship 
 */

public class EduLog extends Composite implements EntryPoint {
	
	private static SessionUiBinder uiBinder = GWT.create(SessionUiBinder.class);
	//private final DatabaseServiceAsync databaseService = ServiceFactory.getDBService();
	
	interface SessionUiBinder extends UiBinder<Widget, EduLog> { }
	
	
	@UiField
	TextBox startCodeTextBox;
	@UiField
	Button buttonStartFeedbackSession;
	@UiField
	TextBox rateActivityTextBox;
	@UiField
	ListBox ratingListBox;
	@UiField
	Button buttonSubmit;
	
	
	@UiHandler("buttonStartFeedbackSession")
	void OnClick1(ClickEvent eventButtonStartFeedbackSession) {
		
		String providedStartCode = startCodeTextBox.getText();
		
		if (providedStartCode != "") {
			
			// continue
			
		} else { Window.alert("Start code is required"); }
		
		//Window.alert(providedStartCode);
		
	}
	

	@Override
	public void onModuleLoad() {
		
		initWidget(uiBinder.createAndBindUi(this));
		RootPanel.get().add(this);
		
		buttonSubmit.setEnabled(false);

	}
}
