package com.demo.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.core.domain.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

}
