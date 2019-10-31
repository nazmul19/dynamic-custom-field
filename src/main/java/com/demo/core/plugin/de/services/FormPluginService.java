package com.demo.core.plugin.de.services;

import java.util.List;
import java.util.Map;

import com.demo.core.plugin.de.events.FormContextDetail;
import com.demo.core.plugin.de.events.RemoveFormContextOp;

import edu.common.dynamicextensions.domain.nui.Container;

public interface FormPluginService {
	public List<FormContextDetail> getFormContexts(Long formId);
	public List<FormContextDetail> addFormContexts(List<FormContextDetail> formCtxts);
	public Boolean removeFormContext(RemoveFormContextOp op);
	
	public Map<String, Object> getExtensionInfo(Long cpId, String entityType);
	public Map<String, Object> getExtensionInfo(boolean cpBased, String entityType, Long entityId);
	
	public void addFormContextProc(String entity, FormContextProcessor proc);
	public Container getFormDefinition(Long formId);
}
