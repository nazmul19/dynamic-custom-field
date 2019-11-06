package com.demo.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import com.demo.core.plugin.de.domain.BaseExtensionEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table( name = "CONTACTS")
@Getter @Setter @NoArgsConstructor
@Audited
public class Contact extends BaseExtensionEntity{
	
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

	@Override
	public String getEntityType() {
		return EXTN;
	}
	
	public void update(Contact newContact) {
		setId(newContact.getId());
		setFirstName(newContact.getFirstName());
		setLastName(newContact.getLastName());
		setAccount(newContact.getAccount());
		setExtension(newContact.getExtension());
	}
}
