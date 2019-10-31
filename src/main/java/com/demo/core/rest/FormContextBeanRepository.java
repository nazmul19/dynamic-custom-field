package com.demo.core.rest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.core.domain.FormContextBean;

@Repository
public interface FormContextBeanRepository extends JpaRepository<FormContextBean, Long>, FormContextBeanRepositoryCustom{

}
