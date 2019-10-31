package com.demo.core.rest;

import java.util.List;

import com.demo.core.domain.FormContextBean;
import com.demo.core.plugin.de.FormContextDetail;

public interface FormContextBeanRepositoryCustom {
	FormContextBean getFormContext(boolean cpBased, String entityType, Long entityId, Long formId);
	List<FormContextDetail> getFormContexts(Long formId);
}
