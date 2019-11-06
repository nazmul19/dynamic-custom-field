package com.demo.core.events;

import com.demo.core.domain.Contact;
import com.demo.core.plugin.de.events.ExtensionDetail;

import lombok.Data;

@Data
public class ContactDetail {

	private Long id;
	private String firstName;
	private String lastName;
	private Long accountId;
	private ExtensionDetail extensionDetail;	
	
	public static ContactDetail from(Contact contact) {
		ContactDetail detail = new ContactDetail();
		detail.setAccountId(contact.getAccount().getId());
		detail.setId(contact.getId());
		detail.setFirstName(contact.getFirstName());
		detail.setLastName(contact.getLastName());
		detail.setExtensionDetail(ExtensionDetail.from(contact.getExtension()));
		return detail;
	}
}
