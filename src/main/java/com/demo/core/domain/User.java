package com.demo.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "USERS")
@Getter @Setter @NoArgsConstructor
@Audited
public class User extends BaseEntity {

	public static final String EXTN = "UserExtension";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Column(name = "USER_NAME")
	private String username;
	
	@Column(name = "PASSWORD")
	private String password;
    
}
