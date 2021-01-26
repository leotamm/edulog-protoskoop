package ee.protoskoop.gwt.edulog.shared;

import java.io.Serializable;

public class Session implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4966094476517269040L;
	private String classUnit;
	private String activity;
	
	public String getClassUnit() {
		return classUnit;
	}
	public void setClassUnit(String classUnit) {
		this.classUnit = classUnit;
	}
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}

}
