package com.demo.core.service;

import com.demo.core.AccountDetail;

public interface AccountService {
	public AccountDetail createAccount(AccountDetail account);
	public AccountDetail editAccount(AccountDetail account);
	public Boolean deleteAccount(Long accountId);
	public AccountDetail readAccount(Long accountId);
}
