package com.demo.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.core.domain.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long>{

}
