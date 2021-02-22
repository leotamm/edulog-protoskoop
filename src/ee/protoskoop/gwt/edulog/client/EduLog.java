package ee.protoskoop.gwt.edulog.client;

import ee.protoskoop.gwt.edulog.client.Session.SessionUiBinder;
import ee.protoskoop.gwt.edulog.shared.FieldVerifier;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * EduLog is an student engagement measuring application
 * developed by Leo Tamm as an vali-it.ee Java development course project
 * during protoskoop.ee internship 
 */

public class EduLog extends Composite implements EntryPoint {
	
	private static SessionUiBinder uiBinder = GWT.create(SessionUiBinder.class);
	private final DatabaseServiceAsync databaseService = ServiceFactory.getDBService();
	
	interface SessionUiBinder extends UiBinder<Widget, EduLog> { }
	
	
	@UiField
	TextBox joinTheClassTextBox;
	@UiField
	Button buttonJoinTheClass;
	

	@Override
	public void onModuleLoad() {
		
		initWidget(uiBinder.createAndBindUi(this));
		RootPanel.get().add(this);
		
		joinTheClassTextBox.setText("Enter class code");
		
	}
}
