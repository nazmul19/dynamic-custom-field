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

import com.demo.core.AccountDetail;
import com.demo.core.domain.Account;
import com.demo.core.plugin.de.FormPluginService;
import com.demo.core.service.AccountService;

@RestController
@RequestMapping("accounts")
public class AccountsController {
	
	@Autowired
	private FormPluginService formSvc;
	
	@Autowired
	private AccountService accountService;
	
	@RequestMapping(method = RequestMethod.GET, value="{id}")	
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public AccountDetail readAccount(@PathVariable("id") Long accountId) {
		return accountService.readAccount(accountId);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public AccountDetail createAccount(@RequestBody AccountDetail accountDetail) {
		return accountService.createAccount(accountDetail);
	}
	
	@RequestMapping(method = RequestMethod.PUT, value="{id}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public AccountDetail updateAccount(
		@PathVariable("id")
		Long accountId,
			
		@RequestBody
		AccountDetail accountDetail) {
		
		return accountService.editAccount(accountDetail);
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value="{id}")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Boolean removeAccount(
		@PathVariable("id")
		Long accountId) {
		
		return accountService.deleteAccount(accountId);
		
    }
	
	@RequestMapping(method = RequestMethod.GET, value="/extension-form")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Map<String, Object> getForm() {
		return formSvc.getExtensionInfo(-1L, Account.EXTN);
	}
}
