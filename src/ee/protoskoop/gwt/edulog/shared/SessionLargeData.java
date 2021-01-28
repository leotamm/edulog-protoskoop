package ee.protoskoop.gwt.edulog.shared;

import java.util.HashMap;

public class SessionLargeData {

	private HashMap<String, String> sessionSmallData;
	private String teacher;
	private String course;
	private String activity;

	public HashMap<String, String> getSessionSmallData() {
		sessionSmallData.get(course);
		sessionSmallData.get(activity);
		return sessionSmallData;
	}
	public void setSessionSmallData(HashMap<String, String> sessionSmallData) {
		this.sessionSmallData.put(course, activity);
		this.sessionSmallData = sessionSmallData;
	}
	public String getTeacher() {
		return teacher;
	}
	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

}
