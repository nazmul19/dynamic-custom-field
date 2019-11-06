package com.demo.core.plugin.de;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.demo.core.plugin.de.domain.FormContextBean;
import com.demo.core.plugin.de.events.FormContextDetail;
import com.demo.core.plugin.de.events.FormSummary;
import com.demo.core.plugin.de.events.RemoveFormContextOp;
import com.demo.core.plugin.de.events.RemoveFormContextOp.RemoveType;
import com.demo.core.plugin.de.repository.FormContextBeanRepository;
import com.demo.core.plugin.de.services.FormPluginService;

import edu.common.dynamicextensions.domain.nui.Container;
import edu.common.dynamicextensions.nutility.ContainerJsonSerializer;
import edu.common.dynamicextensions.nutility.ContainerSerializer;

@RestController
@RequestMapping("app-forms")
public class DeFormPluginController {
	
	@Autowired
	private FormPluginService formSvc;
	
	@Autowired
	private FormContextBeanRepository formContextBeanRepository;
	
	@RequestMapping(method = RequestMethod.GET, value="query-form")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<FormContextBean> getAllQueryForms() {
		return formContextBeanRepository.findByEntityType("Query");
	}
	
	@RequestMapping(method = RequestMethod.GET, value="{id}/contexts")	
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<FormContextDetail> getFormContexts(@PathVariable("id") Long formId) {
		return formSvc.getFormContexts(formId);
	}
	
	@RequestMapping(method = RequestMethod.PUT, value="{id}/contexts")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<FormContextDetail> addFormContexts(
		@PathVariable("id")
		Long formId,
			
		@RequestBody
		List<FormContextDetail> formCtxts) {
		
		for (FormContextDetail formCtxt : formCtxts) {
			formCtxt.setFormId(formId);
		}
		return formSvc.addFormContexts(formCtxts);
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value="{id}/contexts")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Map<String, Object> removeFormContext(
		@PathVariable("id")
		Long formId,
			
		@RequestParam(value = "entityType", required = true)
		String entityType,
			
		@RequestParam(value = "cpId", required = true)
		Long cpId) {
		
		RemoveFormContextOp op = new RemoveFormContextOp();
		op.setCpId(cpId);
		op.setFormId(formId);
		op.setEntityType(entityType);
		op.setRemoveType(RemoveType.SOFT_REMOVE);
		
		Boolean result = formSvc.removeFormContext(op);
		return Collections.<String, Object>singletonMap("status",result);
    }
	
	@RequestMapping(method = RequestMethod.GET, value="{id}/definition")
	@ResponseStatus(HttpStatus.OK)
	public void getFormDefinition(
		@PathVariable("id")
		Long formId,
			
		@RequestParam(value = "maxPvs", required = false, defaultValue = "0")
		int maxPvListSize,
			
		HttpServletResponse httpResp)
	throws IOException {
		Container container = formSvc.getFormDefinition(formId);
		if(container != null) {
			httpResp.setCharacterEncoding("UTF-8");
			Writer writer = httpResp.getWriter();

			ContainerSerializer serializer = new ContainerJsonSerializer(container, writer);
			serializer.serialize(maxPvListSize);
			writer.flush();
		}
		
	}
}
