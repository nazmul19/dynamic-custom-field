package com.demo.core.de.events;

import com.demo.core.domain.User;

public class UserSummary {
	
	private static final long serialVersionUID = -8113791999197573026L;

	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public static UserSummary from(User user) {
		UserSummary result = new UserSummary();
		result.setId(user.getId());
		return result;
	}
	
}
