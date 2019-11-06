package com.demo.core.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.core.domain.Account;
import com.demo.core.domain.Contact;
import com.demo.core.events.ContactDetail;
import com.demo.core.plugin.de.domain.DeObject;
import com.demo.core.repository.AccountRepository;
import com.demo.core.repository.ContactRepository;

@Service
public class ContactServiceImpl implements ContactService{

	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	
	@Override
	@Transactional
	public ContactDetail createContact(ContactDetail detail) {
		Contact contact = create0(detail);
		contactRepository.saveAndFlush(contact);
		contact.addOrUpdateExtension();
		return ContactDetail.from(contact);
	}

	@Override
	@Transactional
	public ContactDetail editContact(ContactDetail detail) {
		if(detail.getId() == null) throw new RuntimeException("Please pass Contact Record Id.");
		Contact newContact = create0(detail);
		Optional<Contact> contactFetch = contactRepository.findById(detail.getId());
		if(! contactFetch.isPresent()) throw new RuntimeException("Contact Record not found in system.");
		Contact existingContact = contactFetch.get();
		existingContact.update(newContact);
		contactRepository.save(existingContact);
		existingContact.addOrUpdateExtension();
		return ContactDetail.from(existingContact);
	}

	@Override
	@Transactional
	public Boolean deleteContact(Long contactId) {
		Optional<Contact> contactFetch = contactRepository.findById(contactId);
		if(!contactFetch.isPresent()) throw new RuntimeException("Contact Record not found in Database.");
		Contact con = contactFetch.get();
		con.getExtension().delete();
		contactRepository.delete(con);
		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public ContactDetail readContact(Long contactId) {
		Optional<Contact> contactFetch = contactRepository.findById(contactId);
		if(!contactFetch.isPresent()) throw new RuntimeException("Contact Record not found in Database.");
		return ContactDetail.from(contactFetch.get());
	}
	
	private Contact create0(ContactDetail detail) {
		Contact contact = new Contact();
		contact.setId(detail.getId());
		contact.setFirstName(detail.getFirstName());
		contact.setLastName(detail.getLastName());
		Optional<Account> accountFetch = accountRepository.findById(detail.getAccountId());
		if(accountFetch.isEmpty()) throw new RuntimeException("Contact Account Not Found in Database");
		contact.setAccount(accountFetch.get());
		DeObject extension = DeObject.createExtension(detail.getExtensionDetail(), contact);
		contact.setExtension(extension);
		return contact;
	}

}
