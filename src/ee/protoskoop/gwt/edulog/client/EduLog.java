package ee.protoskoop.gwt.edulog.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import ee.protoskoop.gwt.edulog.shared.FeedbackObject;

/**
 * EduLog is a student engagement measuring web application
 * developed by Leo Tamm as an vali-it.ee Java development course project
 * during protoskoop.ee internship 
 */

public class EduLog extends Composite implements EntryPoint {

	private static SessionUiBinder uiBinder = GWT.create(SessionUiBinder.class);
	private final DatabaseServiceAsync databaseService = ServiceFactory.getDBService();

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

	String requiredStartCode;
	String activity;
	String providedStartCode;

	Long feedbackId;

	FeedbackObject feedbackObject;


	@UiHandler("buttonStartFeedbackSession")
	void OnClick1(ClickEvent eventButtonStartFeedbackSession) {

		// when correct start code provided, retrieve activity to be rated
		providedStartCode = startCodeTextBox.getText().toUpperCase();

		if (providedStartCode != "") {

			databaseService.getFeedbackDataFromDatabase(providedStartCode, new AsyncCallback<FeedbackObject>() {

				@Override
				public void onFailure(Throwable caught) { Window.alert("Getting feedback object from database failed"); }

				@Override
				public void onSuccess(FeedbackObject result) { 

					feedbackObject = result;

					feedbackId = feedbackObject.getId();
					activity = feedbackObject.getActivity();
					requiredStartCode = feedbackObject.getStartCode();

					if (providedStartCode.equalsIgnoreCase(requiredStartCode)) {

						rateActivityTextBox.setText(activity);

						buttonStartFeedbackSession.setEnabled(false);
						buttonSubmit.setEnabled(true);

					} else { Window.alert("Start code invalid"); }

				}

			});

		} else { Window.alert("Start code is required"); }

	}


	@UiHandler("buttonSubmit")
	void OnClick2(ClickEvent eventButtonSubmit) {

		String rating = ratingListBox.getSelectedValue();
		int ratingInt = Integer.parseInt(rating);

		// when feedback score selected, add this to database array
		databaseService.addMyFeedbackToDatabase(feedbackId, ratingInt, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) { Window.alert("Submitting your feedback failed"); }

			@Override
			public void onSuccess(Boolean result) { 

				buttonSubmit.setEnabled(false);

				rateActivityTextBox.setText("");
				startCodeTextBox.setText("Thank you!");
			}

		});

	}


	@Override
	public void onModuleLoad() {

		initWidget(uiBinder.createAndBindUi(this));
		RootPanel.get().add(this);

		buttonSubmit.setEnabled(false);

	}
}
