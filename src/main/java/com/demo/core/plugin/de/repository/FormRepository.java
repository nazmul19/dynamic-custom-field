package com.demo.core.plugin.de.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.demo.core.plugin.de.domain.Form;

@Repository
public interface FormRepository extends PagingAndSortingRepository<Form, Long>{
	
}
