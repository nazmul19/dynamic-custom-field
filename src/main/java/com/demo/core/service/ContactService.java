package com.demo.core.service;

import com.demo.core.events.ContactDetail;

public interface ContactService {
	public ContactDetail createContact(ContactDetail detail);
	public ContactDetail editContact(ContactDetail detail);
	public Boolean deleteContact(Long contactId);
	public ContactDetail readContact(Long contactId);
}
