package ee.protoskoop.gwt.edulog.shared;

import java.io.Serializable;
import java.util.ArrayList;


public class SessionObject implements Serializable {
	
	private static final long serialVersionUID = 4966094476517269040L;
	
	private String teacher;
	private String studyGroup;
	private Long sessionDateTime;
	private String subject;
	private String topic;
	private String goal;
	private ArrayList <String> activity;
	private ArrayList <Long> duration;
	private Long created;
	private Long planned;
	private Long finished;
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
	public Long getSessionDateTime() {
		return sessionDateTime;
	}
	public void setSessionDateTime(Long sessionDateTime) {
		this.sessionDateTime = sessionDateTime;
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
	public Long getCreated() {
		return created;
	}
	public void setCreated(Long created) {
		this.created = created;
	}
	public Long getPlanned() {
		return planned;
	}
	public void setPlanned(Long planned) {
		this.planned = planned;
	}
	public Long getFinished() {
		return finished;
	}
	public void setFinished(Long finished) {
		this.finished = finished;
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
