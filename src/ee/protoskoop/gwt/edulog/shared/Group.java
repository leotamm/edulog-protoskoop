package ee.protoskoop.gwt.edulog.shared;

import java.io.Serializable;

public class Group implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4659047462094217348L;
	
	private long id;
	private String name;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
