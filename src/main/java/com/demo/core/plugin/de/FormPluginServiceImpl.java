package com.demo.core.plugin.de;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.demo.core.common.DeException;
import com.demo.core.de.domain.DeObject;
import com.demo.core.de.events.RemoveFormContextOp;
import com.demo.core.de.services.FormContextProcessor;
import com.demo.core.domain.FormContextBean;
import com.demo.core.rest.FormContextBeanRepository;

import edu.common.dynamicextensions.domain.nui.Container;

@Component
public class FormPluginServiceImpl implements FormPluginService{
	
	@Autowired
	private FormContextBeanRepository formContextBeanRepository;
	
	private static Map<String, Function<Long, Boolean>> entityAccessCheckers = new HashMap<>();
	
	private Map<String, List<FormContextProcessor>> ctxtProcs = new HashMap<>();
	
	public Map<String, List<FormContextProcessor>> getCtxtProcs() {
		return ctxtProcs;
	}

	public void setCtxtProcs(Map<String, List<FormContextProcessor>> ctxtProcs) {
		this.ctxtProcs = ctxtProcs;
	}

	@Override
	@Transactional
	public List<FormContextDetail> getFormContexts(Long formId) {
		return formContextBeanRepository.getFormContexts(formId);
	}

	@Override
	@Transactional
	public List<FormContextDetail> addFormContexts(List<FormContextDetail> formCtxts) {
		try {
			// AccessCtrlMgr.getInstance().ensureFormUpdateRights();
			for (FormContextDetail formCtxtDetail : formCtxts) {
				Long formId = formCtxtDetail.getFormId();
				Long cpId = formCtxtDetail.getCollectionProtocol().getId();
				String entity = formCtxtDetail.getLevel();
				Long entityId = formCtxtDetail.getEntityId();

				if (entityId != null) {
					Function<Long, Boolean> accessChecker = entityAccessCheckers.get(entity);
					if (accessChecker != null && !accessChecker.apply(entityId)) {
						throw new DeException(String.format("Access Denied on Entity=%s, and EntityId=%s", entity, entityId));
					}
				} 
//				else if (cpId == -1L && !AuthUtil.isAdmin()) {
//					throw OpenSpecimenException.userError(RbacErrorCode.ACCESS_DENIED);
//				} else if (cpId != -1L) {
//					AccessCtrlMgr.getInstance().ensureUpdateCpRights(cpId);
//				}
				
				
				FormContextBean formCtxt = formContextBeanRepository.getFormContext(entityId == null, entity, entityId == null ? cpId : entityId, formId);
				if (formCtxt == null) {
					formCtxt = new FormContextBean();
					formCtxt.setContainerId(formId);
					formCtxt.setCpId(-1L);
					formCtxt.setEntityType(entity);
					formCtxt.setEntityId(entityId);
				}

				formCtxt.setMultiRecord(formCtxtDetail.isMultiRecord());
				formCtxt.setSortOrder(formCtxtDetail.getSortOrder());

				notifyContextSaved(formCtxt);
				formContextBeanRepository.save(formCtxt);
				formCtxtDetail.setFormCtxtId(formCtxt.getIdentifier());
			}
			
			return formCtxts;
		} catch (DeException deExc) {
			throw deExc;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void addFormContextProc(String entity, FormContextProcessor proc) {
		List<FormContextProcessor> procs = ctxtProcs.get(entity);
		if (procs == null) {
			procs = new ArrayList<>();
			ctxtProcs.put(entity, procs);
		}

		boolean exists = false;
		for (FormContextProcessor existing : procs) {
			if (existing == proc) {
				exists = true;
				break;
			}
		}

		if (!exists) {
			procs.add(proc);
		}
	}
	
	@Override
	@Transactional
	public Boolean removeFormContext(RemoveFormContextOp opDetail) {
		try {
//			AccessCtrlMgr.getInstance().ensureFormUpdateRights();

//			RemoveFormContextOp opDetail = req.getPayload();
			Long cpId = opDetail.getCpId();
			Long entityId = opDetail.getEntityId();
			FormContextBean formCtx = formContextBeanRepository.getFormContext(
				entityId == null,
				opDetail.getEntityType(),
				entityId == null ? opDetail.getCpId() : entityId,
				opDetail.getFormId());

			if (formCtx == null) {
				throw new DeException(String.format("No association between CP_ID=%s, and FormId=%s", cpId, opDetail.getFormId()));
			}
			
			if (formCtx.isSysForm()) {
				throw new DeException(String.format("System Form Delete not allowed FormId=%s", opDetail.getFormId()));
			}

//			if (entityId != null) {
//				Function<Long, Boolean> accessChecker = entityAccessCheckers.get(opDetail.getEntityType());
//				if (accessChecker != null && !accessChecker.apply(entityId)) {
//					return ResponseEvent.userError(RbacErrorCode.ACCESS_DENIED);
//				}
//			} else if (cpId == -1L && !AuthUtil.isAdmin()) {
//				return ResponseEvent.userError(RbacErrorCode.ACCESS_DENIED);
//			} else if (cpId != -1L) {
//				AccessCtrlMgr.getInstance().ensureUpdateCpRights(cpId);
//			}

			notifyContextRemoved(formCtx);
			switch (opDetail.getRemoveType()) {
				case SOFT_REMOVE: {
					formCtx.setDeletedOn(Calendar.getInstance().getTime());
					formContextBeanRepository.save(formCtx);
					break;
				}	
					
				case HARD_REMOVE:
					//formDao.delete(formCtx);
					formContextBeanRepository.delete(formCtx);
					break;
			}
			
			return true;
		} catch (DeException deEx) {
			throw deEx;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> getExtensionInfo(Long cpId, String entityType) {
		return getExtensionInfo(true, entityType, cpId);
	}

	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> getExtensionInfo(boolean cpBased, String entityType, Long entityId) {
		return DeObject.getFormInfo(cpBased, entityType, entityId);
	}
	
	private void notifyContextSaved(FormContextBean formCtxt) {
		notifyContextSaved(formCtxt.getEntityType(), formCtxt);
		notifyContextSaved("*", formCtxt);
	}

	private void notifyContextSaved(String entityType, FormContextBean formCtxt) {
		List<FormContextProcessor> procs = ctxtProcs.get(entityType);
		if (procs != null) {
			procs.forEach(proc -> proc.onSaveOrUpdate(formCtxt));
		}
	}

	private void notifyContextRemoved(FormContextBean formCtxt) {
		notifyContextRemoved(formCtxt.getEntityType(), formCtxt);
		notifyContextRemoved("*", formCtxt);
	}

	private void notifyContextRemoved(String entityType, FormContextBean formCtxt) {
		List<FormContextProcessor> procs = ctxtProcs.get(entityType);
		if (procs != null) {
			procs.forEach(proc -> proc.onRemove(formCtxt));
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Container getFormDefinition(Long formId) {
		Container form = getContainer(formId, null);
		return form;
	}
	
	private Container getContainer(Long formId, String formName) {
		Object key = null;
		Container form = null;
		if (formId != null) {
			form = Container.getContainer(formId);
			key = formId;
		} else if (StringUtils.isNotBlank(formName)) {
			form = Container.getContainer(formName);
			key = formName;
		}

		if (key == null) {
			throw new DeException("Name Required");
		} else if (form == null) {
			throw new DeException(String.format("Form not Found FormId = %s", formId));
		}

		return form;
	}
}
