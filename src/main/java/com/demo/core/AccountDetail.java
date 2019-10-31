package com.demo.core;

import com.demo.core.de.events.ExtensionDetail;
import com.demo.core.domain.Account;

import lombok.Data;
@Data
public class AccountDetail {
	private Long id;
	private String name;
	private String country;
	private ExtensionDetail extensionDetail;	
	
	public static AccountDetail from(Account account) {
		AccountDetail detail = new AccountDetail();
		detail.setCountry(account.getCountry());
		detail.setName(account.getName());
		detail.setId(account.getId());
		detail.setExtensionDetail(ExtensionDetail.from(account.getExtension()));
		return detail;
	}
}
