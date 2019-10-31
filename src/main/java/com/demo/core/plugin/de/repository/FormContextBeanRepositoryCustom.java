package com.demo.core.plugin.de.repository;

import java.util.List;

import com.demo.core.plugin.de.domain.FormContextBean;
import com.demo.core.plugin.de.events.FormContextDetail;

public interface FormContextBeanRepositoryCustom {
	FormContextBean getFormContext(boolean cpBased, String entityType, Long entityId, Long formId);
	List<FormContextDetail> getFormContexts(Long formId);
}
