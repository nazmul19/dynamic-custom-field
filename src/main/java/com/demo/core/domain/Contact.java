package com.demo.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table( name = "ACCOUNTS")
@Getter @Setter @NoArgsConstructor
@Audited
public class Contact extends BaseEntity{
	
	public static final String EXTN = "ContactExtension";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "FIRST_NAME")
	private String firstName;
	
	@Column(name = "LAST_NAME")
	private String lastName;
	
	@ManyToOne
	@JoinColumn(name="ACCOUNT_ID")
	private Account account;
	
}
