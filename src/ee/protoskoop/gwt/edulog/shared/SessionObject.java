package ee.protoskoop.gwt.edulog.shared;

import java.io.Serializable;
import java.util.ArrayList;

public class SessionObject implements Serializable {
	
	private static final long serialVersionUID = 4966094476517269040L;
	
	/* All time items are defined and handled as <OffsetDateTime> objects initiated as Long <Date> */
	
	private String teacher;
	private String studyGroup;
	private Long sessionHappeningDate;
	private String subject;
	private String topic;
	private String goal;
	private ArrayList <String> activity;
	private ArrayList <Long> duration;
	private Long sessionCreatingTime;
	private Long sessionPlanningDate;
	private Long sessionFinishingDate;
	private boolean feedback;
	private String startCode;
	
	public String getTeacher() {
		return teacher;
	}
	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}
	public String getStudyGroup() {
		return studyGroup;
	}
	public void setStudyGroup(String studyGroup) {
		this.studyGroup = studyGroup;
	}
	public Long getSessionHappeningTime() {
		return sessionHappeningDate;
	}
	public void setSessionHappeningTime(Long sessionHappeningTime) {
		this.sessionHappeningDate = sessionHappeningTime;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getGoal() {
		return goal;
	}
	public void setGoal(String goal) {
		this.goal = goal;
	}
	public ArrayList <String> getActivity() {
		return activity;
	}
	public void setActivity(ArrayList <String> activity) {
		this.activity = activity;
	}
	public ArrayList <Long> getDuration() {
		return duration;
	}
	public void setDuration(ArrayList <Long> duration) {
		this.duration = duration;
	}
	public Long getSessionCreatingTime() {
		return sessionCreatingTime;
	}
	public void setSessionCreatingTime(Long sessionCreatingTime) {
		this.sessionCreatingTime = sessionCreatingTime;
	}
	public Long getSessionPlanningDate() {
		return sessionPlanningDate;
	}
	public void setSessionPlanningDate(Long sessionPlanningDate) {
		this.sessionPlanningDate = sessionPlanningDate;
	}
	public Long getSessionFinishingDate() {
		return sessionFinishingDate;
	}
	public void setSessionFinishingDate(Long sessionFinishingDate) {
		this.sessionFinishingDate = sessionFinishingDate;
	}
	public boolean isFeedback() {
		return feedback;
	}
	public void setFeedback(boolean feedback) {
		this.feedback = feedback;
	}
	public String getStartCode() {
		return startCode;
	}
	public void setStartCode(String startCode) {
		this.startCode = startCode;
	}
	
}
