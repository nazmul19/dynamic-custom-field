package com.demo.core.rest;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import com.demo.core.de.domain.Form;

@Repository
@RepositoryRestResource
public interface FormRepository extends PagingAndSortingRepository<Form, Long>{
	
}
