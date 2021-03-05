package ee.protoskoop.gwt.edulog.shared;

import java.io.Serializable;
import java.util.ArrayList;

public class FeedbackObject implements Serializable {

	private static final long serialVersionUID = 8993366627969721108L;
	
	private Long id;
	private Long sessionId;
	private String teacher;
	private String startCode;
	private String activity;
	private ArrayList<Integer> feedback;
	private Long finishedTime;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getSessionId() {
		return sessionId;
	}
	public void setSessionId(Long sessionId) {
		this.sessionId = sessionId;
	}
	public String getTeacher() {
		return teacher;
	}
	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}
	public String getStartCode() {
		return startCode;
	}
	public void setStartCode(String startCode) {
		this.startCode = startCode;
	}
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	public ArrayList<Integer> getFeedback() {
		return feedback;
	}
	public void setFeedback(ArrayList<Integer> feedback) {
		this.feedback = feedback;
	}
	public Long getFinishedTime() {
		return finishedTime;
	}
	public void setFinishedTime(Long finishedTime) {
		this.finishedTime = finishedTime;
	}
	
}
