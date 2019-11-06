package com.demo.core.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.demo.core.domain.Contact;
import com.demo.core.events.ContactDetail;
import com.demo.core.plugin.de.services.FormPluginService;
import com.demo.core.service.ContactService;

@RestController
@RequestMapping("contacts")
public class ContactsController {
	
	@Autowired
	private FormPluginService formSvc;
	
	@Autowired
	private ContactService contactService;
	
	@RequestMapping(method = RequestMethod.GET, value="{id}")	
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ContactDetail readContact(@PathVariable("id") Long contactId) {
		return contactService.readContact(contactId);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ContactDetail createContact(@RequestBody ContactDetail contactDetail) {
		return contactService.createContact(contactDetail);
	}
	
	@RequestMapping(method = RequestMethod.PUT, value="{id}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public ContactDetail updateContact(
		@PathVariable("id")
		Long accountId,
			
		@RequestBody
		ContactDetail ContactDetail) {
		
		return contactService.editContact(ContactDetail);
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value="{id}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Boolean removeContact(
		@PathVariable("id")
		Long contactId) {
		
		return contactService.deleteContact(contactId);
		
    }
	
	@RequestMapping(method = RequestMethod.GET, value="/extension-form")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Map<String, Object> getForm() {
		return formSvc.getExtensionInfo(-1L, Contact.EXTN);
	}
}
