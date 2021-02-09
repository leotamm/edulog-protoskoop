package ee.protoskoop.gwt.edulog.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.time.OffsetDateTime;

public class Session implements Serializable {
	
	private static final long serialVersionUID = 4966094476517269040L;
	
	private String teacher;
	private String studyGroup;
	private OffsetDateTime sessionDateTime;
	private String subject;
	private String topic;
	private String goal;
	private ArrayList <String> activity;
	private ArrayList <Long> duration;
	private OffsetDateTime created;
	private OffsetDateTime planned;
	private OffsetDateTime finished;
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
	public OffsetDateTime getSessionDateTime() {
		return sessionDateTime;
	}
	public void setSessionDateTime(OffsetDateTime sessionDateTime) {
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
	public OffsetDateTime getCreated() {
		return created;
	}
	public void setCreated(OffsetDateTime created) {
		this.created = created;
	}
	public OffsetDateTime getPlanned() {
		return planned;
	}
	public void setPlanned(OffsetDateTime planned) {
		this.planned = planned;
	}
	public OffsetDateTime getFinished() {
		return finished;
	}
	public void setFinished(OffsetDateTime finished) {
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
