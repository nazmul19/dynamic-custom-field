package com.demo.core.plugin.de.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.core.plugin.de.domain.FormContextBean;

@Repository
public interface FormContextBeanRepository extends JpaRepository<FormContextBean, Long>, FormContextBeanRepositoryCustom{
	List<FormContextBean> findByEntityType(String entityType);
	FormContextBean findByFormIdAndCpIdAndEntityType(Long formdId, Long cpId, String entityType);
}
