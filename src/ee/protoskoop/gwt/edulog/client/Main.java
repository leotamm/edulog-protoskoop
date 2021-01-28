package ee.protoskoop.gwt.edulog.client;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author Leo
 *
 */
public class Main extends Composite implements EntryPoint {

	private static MainUiBinder uiBinder = GWT.create(MainUiBinder.class);
	private final DatabaseServiceAsync databaseService = ServiceFactory.getDBService();

	interface MainUiBinder extends UiBinder<Widget, Main> {
	}

	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	  *   xmlns:g="urn:import:**user's package**">
	 *  <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder>
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 */
//	public Main() {
//		initWidget(uiBinder.createAndBindUi(this));
//	}
	

	@Override
	public void onModuleLoad() {
		
		
	    // Create a stack panel containing three labels.
/*	    StackPanel panel = new StackPanel();
	    panel.add(new Label("Foo"), "foo");
	    panel.add(new Label("Bar"), "bar");
	    panel.add(new Label("Baz"), "baz");
*/

		initWidget(uiBinder.createAndBindUi(this));
		RootPanel.get().add(this);
		
	}

}
