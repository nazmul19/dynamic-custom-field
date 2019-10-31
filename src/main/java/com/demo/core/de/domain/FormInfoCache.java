package com.demo.core.de.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import com.demo.core.config.SpringContext;
import com.demo.core.de.services.FormContextProcessor;
//import com.demo.core.de.services.FormService;
import com.demo.core.domain.FormContextBean;
import com.demo.core.rest.FormContextBeanRepository;
import com.demo.core.rest.FormNameContextId;
import com.demo.core.rest.FormRecordEntryBeanRepo;

import edu.common.dynamicextensions.domain.nui.Container;
import edu.common.dynamicextensions.napi.FormEventsListener;
import edu.common.dynamicextensions.napi.FormEventsNotifier;

//
// Used by DeObject. Any further use of this object needs to be carefully
// reviewed, as lot of internal details of this object are based on how
// DeObject works.
//

@Configurable
@Component
class FormInfoCache implements FormContextProcessor, FormEventsListener {
	
//	@Autowired
//	private FormService formService;
	
	private FormContextBeanRepository formContextBeanRepository;
	
	private FormRecordEntryBeanRepo formRecordEntryBeanRepo;

	//
	// Key is CP ID. For non CP specific, key is -1L
	//
	private final Map<Long, ContextInfo> contextInfoMap = new HashMap<>();

	//
	// Key is form name
	//
	private final Map<String, Container> formsCache = new HashMap<>();

	public FormInfoCache() {
		this.formContextBeanRepository = SpringContext.getBean(FormContextBeanRepository.class);
		this.formRecordEntryBeanRepo = SpringContext.getBean(FormRecordEntryBeanRepo.class);
	}

	@PostConstruct
	public void registerListeners() {
		FormEventsNotifier.getInstance().addListener(this);
		//getFormService().addFormContextProc("*", this);
	}

	public String getFormName(Long cpId, String entityType) {
		return getFormName(true, entityType, cpId);
	}

	public String getFormName(boolean cpBased, String entityType, Long entityId) {
		Long cpId = cpBased ? entityId : -1L;
		String entityTypeKey = cpBased ? entityType : entityType + "-" + entityId;

		ContextInfo ctxtInfo = getCpContextInfo(cpId);
		if (!ctxtInfo.hasFormName(entityTypeKey)) {
			synchronized (ctxtInfo) {
				String formName = null;
				Long formCtxtId = null;

				//Pair<String, Long> formInfo = getDaoFactory().getFormDao().getFormNameContext(cpId, entityType, cpBased ? null : entityId);
				FormNameContextId formNameCtxtId = formRecordEntryBeanRepo.findByCpIdAndEntityTypeAndEntityId(cpId, entityType, cpBased ? null : entityId);
				
				if (formNameCtxtId != null) {
					formName = formNameCtxtId.getFormName();
					formCtxtId = formNameCtxtId.getCtxtId();
				}

				ctxtInfo.addFormName(entityTypeKey, formName);
				ctxtInfo.addFormContext(entityTypeKey, formName, formCtxtId);
			}
		}

		return ctxtInfo.getFormName(entityTypeKey);
	}

	public Long getFormContext(Long cpId, String entityType, String formName) {
		return getFormContext(true, entityType, cpId, formName);
	}

	public Long getFormContext(boolean cpBased, String entityType, Long entityId, String formName) {
		Long cpId = cpBased ? entityId : -1L;
		String entityTypeKey = cpBased ? entityType : entityType + "-" + entityId;

		ContextInfo ctxtInfo = getCpContextInfo(cpId);
		if (!ctxtInfo.hasFormContext(entityType, formName)) {
			Container form = getForm(formName);
			synchronized (ctxtInfo) {
				//FormContextBean formCtx = getDaoFactory().getFormDao().getFormContext(cpBased, entityType, entityId, form.getId());
				FormContextBean formCtx = formContextBeanRepository.getFormContext(cpBased, entityType, entityId, form.getId());
				if (formCtx != null) {
					ctxtInfo.addFormContext(entityTypeKey, formName, formCtx.getIdentifier());
				}
			}
		}

		return ctxtInfo.getFormContext(entityTypeKey, formName);
	}

	public Container getForm(String formName) {
		Container form = formsCache.get(formName);
		if (form == null) {
			synchronized (formsCache) {
				form = Container.getContainer(formName);
				formsCache.put(formName, form);
			}
		}

		return form;
	}

	public Map<String, Object> getFormInfo(Long cpId, String entity) {
		return getFormInfo(true, entity, cpId);
	}

	public Map<String, Object> getFormInfo(boolean cpBased, String entityType, Long entityId) {
		String formName = getFormName(cpBased, entityType, entityId);
		if (StringUtils.isBlank(formName) && entityId != -1L) {
			entityId = -1L;
			formName = getFormName(cpBased, entityType, entityId);
		}

		if (StringUtils.isBlank(formName)) {
			return null;
		}

		Map<String, Object> formInfo = new HashMap<>();
		formInfo.put("formId", getForm(formName).getId());
		formInfo.put("formCtxtId", getFormContext(cpBased, entityType, entityId, formName));
		formInfo.put("formName", formName);
		return formInfo;
	}

	@Override
	public void onSaveOrUpdate(FormContextBean formCtxt) {
		removeCpFormContext(formCtxt);
	}

	@Override
	public void onRemove(FormContextBean formCtxt) {
		removeCpFormContext(formCtxt);
	}

	@Override
	public void onCreate(Container container) {
		formsCache.remove(container.getName());
	}

	@Override
	public void preUpdate(Container form) {
	}

	@Override
	public void onUpdate(Container container) {
		formsCache.remove(container.getName());
	}

	@Override
	public synchronized void onDelete(Container container) {
		formsCache.remove(container.getName());

		for (ContextInfo ctxtInfo : contextInfoMap.values()) {
			ctxtInfo.removeForm(container.getName());
		}
	}

	public void removeCpFormContext(FormContextBean formCtxt) {
		ContextInfo contextInfo = getCpContextInfo(formCtxt.getCpId());
		if (contextInfo == null) {
			return;
		}

		synchronized (contextInfo) {
			String entityTypeKey = formCtxt.getEntityType();
			if (formCtxt.getEntityId() != null) {
				entityTypeKey += "-" + formCtxt.getEntityId();
			}

			contextInfo.removeFormName(entityTypeKey);
			contextInfo.removeFormContext(formCtxt.getIdentifier());
		}
	}

//	private FormService getFormService() {
//		return formService;
//	}
//
//	private DaoFactory getDaoFactory() {
//		return daoFactory;
//	}

	private ContextInfo getCpContextInfo(Long cpId) {
		if (!contextInfoMap.containsKey(cpId)) {
			synchronized (contextInfoMap) {
				if (!contextInfoMap.containsKey(cpId)) {
					contextInfoMap.put(cpId, new ContextInfo());
				}
			}
		}

		return contextInfoMap.get(cpId);
	}

	private static class ContextInfo {
		//
		// Key is entity. e.g. ParticipantExtension, SpecimenExtension etc
		// Value is name of form representing the extension
		//
		private Map<String, String> entityForms = new HashMap<>();

		//
		// Key is entity#form_name. e.g. ParticipantExtension#ParticipantCustomFields
		// Value is form context ID
		//
		private Map<String, Long> formContexts = new HashMap<>();

		public Map<String, String> getEntityForms() {
			return entityForms;
		}

		public void setEntityForms(Map<String, String> entityForms) {
			this.entityForms = entityForms;
		}

		public Map<String, Long> getFormContexts() {
			return formContexts;
		}

		public void setFormContexts(Map<String, Long> formContexts) {
			this.formContexts = formContexts;
		}

		public boolean hasFormName(String entityType) {
			return entityForms.containsKey(entityType);
		}

		public String getFormName(String entityType) {
			return entityForms.get(entityType);
		}

		public void addFormName(String entityType, String formName) {
			entityForms.put(entityType, formName);
		}

		public void removeFormName(String entityType) {
			entityForms.remove(entityType);
		}

		public void removeForm(String formName) {
			List<String> entities = new ArrayList<>();
			Iterator<Map.Entry<String, String>> iter = entityForms.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, String> entityFormName = iter.next();
				if (entityFormName.getValue() != null && entityFormName.getValue().equals(formName)) {
					entities.add(entityFormName.getKey() + "#" + formName);
					iter.remove();
				}
			}

			for (String entity : entities) {
				formContexts.remove(entity);
			}
		}

		public boolean hasFormContext(String entityType, String formName) {
			return formContexts.containsKey(getFormCtxtKey(entityType, formName));
		}

		public Long getFormContext(String entityType, String formName) {
			return formContexts.get(getFormCtxtKey(entityType, formName));
		}

		public void addFormContext(String entityType, String formName, Long ctxtId) {
			formContexts.put(getFormCtxtKey(entityType, formName), ctxtId);
		}

		private String getFormCtxtKey(String entityType, String formName) {
			return entityType + "#" + formName;
		}

		private void removeFormContext(Long formCtxtId) {
			Iterator<Map.Entry<String, Long>> iter = formContexts.entrySet().iterator();
			while (iter.hasNext()) {
				Long cachedId = iter.next().getValue();
				if (cachedId == null || cachedId.equals(formCtxtId)) {
					iter.remove();
				}
			}
		}
	}
}

