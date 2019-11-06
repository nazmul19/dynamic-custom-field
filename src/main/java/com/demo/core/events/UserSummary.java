package com.demo.core.events;

import com.demo.core.domain.User;

import lombok.Data;

@Data
public class UserSummary {
	private static final long serialVersionUID = -8113791999197573026L;
	private Long id;
	private String userName;
	
	public static UserSummary from(User user) {
		UserSummary result = new UserSummary();
		result.setId(user.getId());
		result.setUserName(user.getUsername());

		return result;
	}
}
