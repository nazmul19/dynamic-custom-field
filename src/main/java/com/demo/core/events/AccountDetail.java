package com.demo.core.events;

import com.demo.core.domain.Account;
import com.demo.core.plugin.de.events.ExtensionDetail;

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
