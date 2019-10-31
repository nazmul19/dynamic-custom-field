package com.demo.core.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.demo.core.AccountDetail;
import com.demo.core.de.domain.DeObject;
import com.demo.core.domain.Account;
import com.demo.core.repository.AccountRepository;

@Service
public class AccountServiceImpl implements AccountService{

	@Autowired
	private AccountRepository accountRepository;
	
	@Override
	@Transactional
	public AccountDetail createAccount(AccountDetail accountDetail) {
		Account account = createAccount0(accountDetail);
		accountRepository.saveAndFlush(account);
		account.addOrUpdateExtension();
		return AccountDetail.from(account);
	}

	@Override
	@Transactional
	public AccountDetail editAccount(AccountDetail accountDetail) {
		if(accountDetail.getId() == null) throw new RuntimeException("Please pass Record Id.");
		Account newAccount = createAccount0(accountDetail);
		Optional<Account> accountFetch = accountRepository.findById(accountDetail.getId());
		if(! accountFetch.isPresent()) throw new RuntimeException("Record not found in system.");
		Account existingAccount = accountFetch.get();
		existingAccount.update(newAccount);
		accountRepository.save(existingAccount);
		newAccount.addOrUpdateExtension();
		return AccountDetail.from(newAccount);
	}

	@Override
	@Transactional
	public Boolean deleteAccount(Long accountId) {
		Optional<Account> account = accountRepository.findById(accountId);
		if(!account.isPresent()) throw new RuntimeException("Record not found in Database.");
		Account acc = account.get();
		acc.getExtension().delete();
		accountRepository.delete(acc);
		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public AccountDetail readAccount(Long accountId) {
		Optional<Account> accountFetch = accountRepository.findById(accountId);
		if(!accountFetch.isPresent()) throw new RuntimeException("Record not found in Database.");
		return AccountDetail.from(accountFetch.get());
	}
	
	private Account createAccount0(AccountDetail detail) {
		Account account = new Account();
		account.setId(detail.getId());
		account.setName(detail.getName());
		account.setCountry(detail.getCountry());
		DeObject extension = DeObject.createExtension(detail.getExtensionDetail(), account);
		account.setExtension(extension);
		return account;
	}
}
