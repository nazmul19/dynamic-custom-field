package com.demo.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table( name = "ACCOUNTS")
//@Getter @Setter @NoArgsConstructor
@Audited
public class Account extends BaseExtensionEntity{
	
	public static final String EXTN = "AccountExtension";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "NAME")
	private String name;
	
	@Column(name = "COUNTRY")
	private String country;

	@Override
	public String getEntityType() {
		return EXTN;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	public void update(Account newAccount) {
		setName(newAccount.getName());
		setCountry(newAccount.getCountry());
		setExtension(newAccount.getExtension());
	}
}
