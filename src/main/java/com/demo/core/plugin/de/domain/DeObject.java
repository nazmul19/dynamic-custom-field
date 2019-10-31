package com.demo.core.plugin.de.domain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.demo.core.common.DeException;
import com.demo.core.config.SpringContext;
import com.demo.core.plugin.de.domain.FormRecordEntryBean.Status;
import com.demo.core.plugin.de.events.ExtensionDetail;
import com.demo.core.plugin.de.events.ExtensionDetail.AttrDetail;
import com.demo.core.plugin.de.repository.FormRecordEntryBeanRepository;
import com.demo.core.plugin.de.repository.ObjectIdRecordId;
import com.demo.core.plugin.de.repository.RecordId;
import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.common.dynamicextensions.domain.nui.CheckBox;
import edu.common.dynamicextensions.domain.nui.Container;
import edu.common.dynamicextensions.domain.nui.DatePicker;
import edu.common.dynamicextensions.domain.nui.SubFormControl;
import edu.common.dynamicextensions.domain.nui.UserContext;
import edu.common.dynamicextensions.napi.ControlValue;
import edu.common.dynamicextensions.napi.FileControlValue;
import edu.common.dynamicextensions.napi.FormData;
import edu.common.dynamicextensions.napi.FormDataManager;
@Configurable
@Component
public abstract class DeObject {
	private static FormInfoCache formInfoCache = new FormInfoCache();

	
	private FormDataManager formDataMgr;
	
//	@Autowired
//	protected DaoFactory daoFactory;
	
//	@Autowired
//	protected FormRepository formRepository;
	
	protected FormRecordEntryBeanRepository frebRepo;
	
	@JsonIgnore
	public FormDataManager getFormDataMgr() {
		return formDataMgr;
	}

	public void setFormDataMgr(FormDataManager formDataMgr) {
		this.formDataMgr = formDataMgr;
	}

	@JsonIgnore
	public FormRecordEntryBeanRepository getFrebRepo() {
		return frebRepo;
	}

	public void setFrebRepo(FormRecordEntryBeanRepository frebRepo) {
		this.frebRepo = frebRepo;
	}

	private Long id;
	
	private boolean recordLoaded = false;
	
	private boolean useUdn = false;
	
	private List<Attr> attrs = new ArrayList<>();

	public DeObject() { }
	
	public DeObject(boolean useUdn) {
		this.useUdn = useUdn;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public boolean isRecordLoaded() {
		return recordLoaded;
	}

	public void setRecordLoaded(boolean recordLoaded) {
		this.recordLoaded = recordLoaded;
	}

	public boolean isUseUdn() {
		return useUdn;
	}

	public void setUseUdn(boolean useUdn) {
		this.useUdn = useUdn;
	}

	public List<Attr> getAttrs() {
		loadRecordIfNotLoaded();
		return attrs;
	}

	public void setAttrs(List<Attr> attrs) {
		this.attrs = attrs;
	}
	
	public Long getFormId() {
		Container form = getForm();
		return form != null ? form.getId() : null;
	}

	public String getFormCaption() {
		Container form = getForm();
		return form != null ? form.getCaption() : null;
	}

	public boolean saveOrUpdate() {
		try {
			Container form = getForm();
			UserContext userCtx = getUserCtx();
			FormData formData = prepareFormData(form);

			int revision = formData.getRevision();
			boolean isInsert = (this.id == null);
			this.id = formDataMgr.saveOrUpdateFormData(userCtx, formData);
			if (isInsert && this.id != null) {
				saveRecordEntry();			
			}

			attrs.clear();
			attrs.addAll(getAttrs(formData));
			return revision != formData.getRevision();
		} catch(IllegalArgumentException ex) {
			throw new DeException(ex);
		} catch (DataAccessException dae) {
			throw new DeException(dae);
		} catch (Exception e) {
			throw new DeException(e);
		}
	}
	
	public void saveRecordEntry() {
		FormRecordEntryBean re = prepareRecordEntry(getUserCtx(), getFormContext(), getId());
		//daoFactory.getFormDao().saveOrUpdateRecordEntry(re);
		frebRepo.save(re);
	}
	
	public void delete() {
		if (getId() == null) {
			return;
		}

		//FormRecordEntryBean re = daoFactory.getFormDao().getRecordEntry(getFormContext(), getObjectId(), getId());
		FormRecordEntryBean re = frebRepo.findByFormCtxtIdAndObjectIdAndRecordId(getFormContext(), getObjectId(), getId());
		if (re == null) {
			return;
		}
		
		re.delete();
		//daoFactory.getFormDao().saveOrUpdateRecordEntry(re);
		frebRepo.save(re);
	}
	
	/** Hackish method */
	public List<Long> getRecordIds() {
		Long formCtx = getFormContext();
		if (formCtx == null) {
			return null;
		}
		
//		List<FormRecordSummary> records = daoFactory.getFormDao().getFormRecords(formCtx, getObjectId());
//		List<Long> recIds = new ArrayList<Long>();
//		for (FormRecordSummary rec : records) {
//			recIds.add(rec.getRecordId());
//		}
		List<Long> recIds = new ArrayList<Long>();
		List<RecordId> records = frebRepo.findByFormCtxtIdAndObjectId(formCtx, getObjectId());
		for (RecordId rec : records) {
			recIds.add(rec.getRecordId());
		}
		
		return recIds;
	}
	
	public boolean hasPhiFields() {
		return getForm().hasPhiFields();
	}
	
	public void anonymize() {
		if (!hasPhiFields()) {
			return;
		}

		Long recordId = getId();
		if (recordId == null) {
			return;
		}

		formDataMgr.anonymize(getUserCtx(), getForm(), recordId);
	}

	public void copyAttrsTo(DeObject other) {
		for (Attr attr : getAttrs()) {
			other.getAttrs().add(attr.copy());
		}
	}

	protected void loadRecordIfNotLoaded() {
		Long recordId = getId();
		if (recordLoaded || recordId == null) {
			return;
		}

		FormData formData = formDataMgr.getFormData(getForm(), recordId);
		loadRecord(formData);
	}

	protected void loadRecord(FormData formData) {
		recordLoaded = true;
		attrs.clear();

		if (formData == null) {
			return;
		}

		attrs.addAll(getAttrs(formData));

		Map<String, Object> attrValues = new HashMap<>();
		for (ControlValue cv : formData.getOrderedFieldValues()) {
			attrValues.put(cv.getControl().getUserDefinedName(), cv.getValue());
		}

		setAttrValues(attrValues);
	}

	protected String getFormNameByEntityType() {
		Long entityId = isCpBased() ? getCpId() : getEntityId();
		if (entityId == null || entityId <= 0L) {
			entityId = -1L;
		}

		String formName = formInfoCache.getFormName(isCpBased(), getEntityType(), entityId);
		if (StringUtils.isBlank(formName) && entityId != -1L) {
			formName = formInfoCache.getFormName(isCpBased(), getEntityType(), -1L);
		}

		return formName;
	}
	
	public abstract Long getObjectId();
	
	public abstract String getEntityType();
	
	public abstract String getFormName();
	
	public abstract Long getCpId();

	public abstract boolean isCpBased();

	public abstract Long getEntityId();

	public abstract void setAttrValues(Map<String, Object> attrValues);

	public Map<String, Object> getAttrValues() {
		loadRecordIfNotLoaded();
		return getAttrValues(attrs);
	}

	public Map<String, String> getLabelValueMap() {
		//String notSpecified = MessageUtil.getInstance().getMessage("common_not_specified");
		String notSpecified = "NOT SPECIFIFIED";
		return getAttrs().stream().collect(
			Collectors.toMap(
				attr -> attr.getCaption(),
				attr -> attr.getDisplayValue(notSpecified),
				(v1, v2) -> {throw new IllegalStateException("Duplicate key");},
				LinkedHashMap::new)
		);
	}

	//
	// TODO: Used only by the LE plugin
	//
	public static Long saveFormData(
			final String formName, 
			final String entityType, 
			final Long cpId,
			final Long objectId, 
			final Map<String, Object> values) {
		
		DeObject object = new DeObject() {
			@Override
			public void setAttrValues(Map<String, Object> attrValues) {				
			}	
			
			@Override
			public Long getObjectId() {
				return objectId;
			}
			
			@Override
			public String getFormName() {
				if (StringUtils.isBlank(formName)) {
					return getFormNameByEntityType();
				}
				return formName;
			}
			
			@Override
			public String getEntityType() {
				return entityType;
			}
			
			@Override
			public Long getCpId() {
				return cpId;
			}

			@Override
			public boolean isCpBased() {
				return true;
			}

			@Override
			public Long getEntityId() {
				return null;
			}

			@Override
			public Map<String, Object> getAttrValues() {
				return values;
			}
		};
		object.setFormDataMgr(SpringContext.getBean(FormDataManager.class));
		object.setFrebRepo(SpringContext.getBean(FormRecordEntryBeanRepository.class));
		object.saveOrUpdate();
		return object.getId();
	}
	
	public static DeObject createExtension(ExtensionDetail detail, BaseExtensionEntity entity) {
		if (detail == null) {
			return null;
		}
		
		DeObject extension = entity.createExtension();
		if (extension == null) {
			return null;
		}
		
		Map<String, Attr> existingAttrs = new HashMap<>();
		for (Attr attr : extension.getAttrs()) {
			existingAttrs.put(attr.getName(), attr);
		}

		Map<String, Object> valueMap = new HashMap<>();
		for (AttrDetail attrDetail : detail.getAttrs()) {
			valueMap.put(attrDetail.getName(), attrDetail.getValue());
		}

		FormData formData = FormData.getFormData(extension.getForm(), valueMap);
		List<Attr> attrs = extension.getAttrs(formData);
		for (Attr attr : attrs) {
			existingAttrs.remove(attr.getName());
		}
		
		attrs.addAll(existingAttrs.values());
		extension.setAttrs(attrs);
		return extension;
	}

	public static List<DeObject> createExtensions(boolean cpBased, String entityType, Long entityId, Collection<? extends BaseExtensionEntity> objects) {
		if (CollectionUtils.isEmpty(objects)) {
			return Collections.emptyList();
		}

		DeObject fakeDeObj = fakeObject();
		Map<Long, BaseExtensionEntity> objectsMap = objects.stream().collect(Collectors.toMap(BaseExtensionEntity::getId, obj -> obj));

		List<DeObject> result = new ArrayList<>();
		String formName = null;

		Map<String, Object> formInfo = DeObject.getFormInfo(cpBased, entityType, entityId);
		if (formInfo != null) {
			Long formCtxtId = (Long) formInfo.get("formCtxtId");
			formName = (String) formInfo.get("formName");

			//Map<Long, List<Long>> objRecordIds = fakeDeObj.daoFactory.getFormDao().getRecordIds(formCtxtId, objectsMap.keySet());
			List<ObjectIdRecordId> rows = fakeDeObj.frebRepo.findByObjectIdInAndFormCtxtId(objectsMap.keySet(), formCtxtId);
			Map<Long, List<Long>> objRecordIds = new HashMap<>();
			for (ObjectIdRecordId row : rows) {
				List<Long> recordIds = objRecordIds.computeIfAbsent((Long) row.getObjectId(), (u) -> new ArrayList<>());
				recordIds.add((Long) row.getRecordId());
			}
			
			Map<Long, Long> recObjIdMap = objRecordIds.entrySet().stream()
				.collect(Collectors.toMap(re -> re.getValue().get(0), Map.Entry::getKey));

			List<FormData> formDataList = null;
			if (recObjIdMap.isEmpty()) {
				formDataList = Collections.emptyList();
			} else {
				formDataList = fakeDeObj.formDataMgr.getFormData(getForm(formName), new ArrayList<>(recObjIdMap.keySet()));
			}

			for (FormData formData : formDataList) {
				Long objectId = recObjIdMap.get(formData.getRecordId());
				BaseExtensionEntity object = objectsMap.remove(objectId);

				DeObject extn = newDeObject(formName, object);
				extn.setId(formData.getRecordId());
				extn.loadRecord(formData);
				object.setExtension(extn);

				result.add(extn);
			}
		}

		for (BaseExtensionEntity obj : objectsMap.values()) {
			DeObject extn = newDeObject(formName, obj);
			extn.setId(null);
			extn.setRecordLoaded(true);
			obj.setExtension(extn);
		}

		return result;
	}

	public static Map<String, Object> getFormInfo(Long cpId, String entity) {
		return getFormInfo(true, entity, cpId);
	}

	public static Map<String, Object> getFormInfo(boolean cpBased, String entity, Long entityId) {
		return formInfoCache.getFormInfo(cpBased, entity, entityId);
	}

	public static Container getForm(String formName) {
		return formInfoCache.getForm(formName);
	}

	public static Long getFormContextId(boolean cpBased, String entity, Long entityId, String formName) {
		return DeObject.fakeObject().getFormContext(cpBased, entity, entityId, formName);
	}

	public static Long saveRecord(Long formCtxtId, Long objectId, Long recordId) {
		FormRecordEntryBean re = new FormRecordEntryBean();
		re.setFormCtxtId(formCtxtId);
		re.setObjectId(objectId);
		re.setRecordId(recordId);
		//re.setUpdatedBy(AuthUtil.getCurrentUser().getId());
		re.setUpdatedBy(2L);
		re.setUpdatedTime(Calendar.getInstance().getTime());
		re.setActivityStatus(Status.ACTIVE);
		//DeObject.fakeObject().daoFactory.getFormDao().saveOrUpdateRecordEntry(re);
		DeObject.fakeObject().frebRepo.save(re);
		return re.getIdentifier();
	}

	private static DeObject fakeObject() {
		return newDeObject(null, null);
	}

	private static DeObject newDeObject(String formName, BaseExtensionEntity object) {
		
		DeObject deObject = new DeObject() {
			@Override
			public Long getObjectId() {
				return object != null ? object.getId() : null;
			}

			@Override
			public String getEntityType() {
				return object != null ? object.getEntityType() : null;
			}

			@Override
			public String getFormName() {
				return formName;
			}

			@Override
			public Long getCpId() {
				return object != null ? object.getCpId() : -1L;
			}

			@Override
			public boolean isCpBased() {
				return object != null && object.isCpBased();
			}

			@Override
			public Long getEntityId() {
				return object != null ? object.getEntityId() : null;
			}

			@Override
			public void setAttrValues(Map<String, Object> attrValues) { }
		};
		deObject.setFormDataMgr(SpringContext.getBean(FormDataManager.class));
		deObject.setFrebRepo(SpringContext.getBean(FormRecordEntryBeanRepository.class));
		return deObject;
	}

	private UserContext getUserCtx() {
		//final User user = AuthUtil.getCurrentUser();
		return new UserContext() {
			
			@Override
			public String getUserName() {				
				return "MyUsername";
			}
			
			@Override
			public Long getUserId() {
				//return user.getId();
				return 2L;
			}
			
			@Override
			public String getIpAddress() {
				return null;
			}
		};
	}
	
	private FormData prepareFormData(Container container) {
		FormData formData = FormData.getFormData(container, getAttrValues(), useUdn, null);
		formData.setRecordId(this.id);

		formData.validate();
		return formData;		
	}
	
	
	private FormRecordEntryBean prepareRecordEntry(UserContext userCtx, Long formCtxId, Long recordId) {
		FormRecordEntryBean re = new FormRecordEntryBean();
		re.setFormCtxtId(formCtxId);
		re.setObjectId(getObjectId());
		re.setRecordId(recordId);
		re.setUpdatedBy(userCtx.getUserId());
		re.setUpdatedTime(Calendar.getInstance().getTime());
		re.setActivityStatus(Status.ACTIVE);
		return re;
	}	
		
	private Container getForm() {
		return formInfoCache.getForm(getFormName());
	}

	private Long getFormContext() {
		String formName = getFormName();
		if (StringUtils.isBlank(formName)) {
			return null;
		}

		Long entityId = isCpBased() ? getCpId() : getEntityId();
		return getFormContext(isCpBased(), getEntityType(), entityId, formName);
	}

	private Long getFormContext(boolean cpBased, String entityType, Long entityId, String formName) {
		Long formCtxt = formInfoCache.getFormContext(cpBased, entityType, entityId, formName);
		if (formCtxt == null && entityId != -1L) {
			formCtxt = formInfoCache.getFormContext(cpBased, entityType, -1L, formName);
		}

		return formCtxt;
	}

	private List<Attr> getAttrs(FormData formData) {
		List<Attr> attrs = new ArrayList<>();
		for (ControlValue cv : formData.getOrderedFieldValues()) {
			if (cv == null) {
				continue;
			}
                
			if (cv.getControl() instanceof SubFormControl) {
				SubFormControl sfCtrl = (SubFormControl)cv.getControl();
				if (sfCtrl.isOneToOne()) {
					cv.setValue(getAttrs((FormData)cv.getValue()));
				} else {
					List<List<Attr>> values = new ArrayList<>();
					for (Object fd : (List)cv.getValue()) {
						values.add(getAttrs((FormData)fd));
					}
					cv.setValue(values);
				}
			}
			attrs.add(Attr.from(cv));
		}

		return attrs;
	}

	private Map<String, Object> getAttrValues(List<Attr> attrs) {
		Map<String, Object> result = new LinkedHashMap<>();

		Object value = null;
		for (Attr attr : attrs) {
			if (attr.isSubForm()) {
				if (attr.isOneToOne()) {
					value = getAttrValues((List<Attr>)attr.getValue());
				} else {
					value = ((List<List<Attr>>)attr.getValue()).stream()
						.map(sfAttrs -> getAttrValues(sfAttrs))
						.collect(Collectors.toList());
				}
			} else {
				value = attr.getValue();
			}

			result.put(isUseUdn() ? attr.getUdn() : attr.getName(), value);
		}

		return result;
	}

	public static class Attr {
		private ControlValue ctrlValue;

		private String name;

		private String udn;

		private String caption;

		private Object value;

		private String type;

		public ControlValue getCtrlValue() {
			return ctrlValue;
		}

		public void setCtrlValue(ControlValue ctrlValue) {
			this.ctrlValue = ctrlValue;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUdn() {
			return udn;
		}

		public void setUdn(String udn) {
			this.udn = udn;
		}

		public String getCaption() {
			return caption;
		}

		public void setCaption(String caption) {
			this.caption = caption;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getDisplayValue() {
			String displayValue = ctrlValue.getControl().toDisplayValue(ctrlValue.getValue());

			//
			// Hack: DatePicker should have returned display value in required format;
			// but the UI and backend formats differ
			//
			if (StringUtils.isNotBlank(displayValue) && ctrlValue.getControl() instanceof DatePicker) {
				DatePicker dateCtrl = (DatePicker)ctrlValue.getControl();
				Date date = dateCtrl.fromString(displayValue);
				displayValue = dateCtrl.isDateTimeFmt() ? getDateTimeString(date) : getDateString(date);
			} else if (ctrlValue.getControl() instanceof CheckBox) {
				Boolean truth = StringUtils.equals(displayValue, "1") || StringUtils.equalsIgnoreCase(displayValue, "true");
				//displayValue = StringUtils.capitalize(MessageUtil.getInstance().getBooleanMsg(truth));
				displayValue = truth.toString();
				
			}

			return displayValue;
		}
		
		public static String getDateString(Date date) {
			return getDateString(date, false);
		}
		
		public static String getDateTimeString(Date date) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
//			TimeZone timeZone = AuthUtil.getUserTimeZone();
//			if (timeZone != null) {
//				sdf.setTimeZone(timeZone);
//			}

			return sdf.format(date);
		}
		
		public static String getDateString(Date date, boolean dateOnly) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if (dateOnly) {
				return sdf.format(date);
			}

//			TimeZone timeZone = AuthUtil.getUserTimeZone();
//			if (timeZone != null) {
//				sdf.setTimeZone(timeZone);
//			}

			return sdf.format(date);
		}
		
		public String getDisplayValue(String defValue) {
			String result = getDisplayValue();
			if (StringUtils.isBlank(result)) {
				return  defValue;
			}

			return result;
		}

		public boolean isPhi() {
			return ctrlValue.getControl().isPhi();
		}
		
		public boolean isSubForm() {
			return "subForm".equals(type);
		}

		public boolean isOneToOne() {
			if (ctrlValue != null && ctrlValue.getControl() instanceof SubFormControl) {
				return ((SubFormControl)ctrlValue.getControl()).isOneToOne();
			}

			return false;
		}

		public Attr copy() {
			Attr copy = new Attr();
			BeanUtils.copyProperties(this, copy);
			return copy;
		}

		public static Attr from(ControlValue cv) {
			Attr attr = new Attr();
			attr.setCtrlValue(cv);
			attr.setName(cv.getControl().getName());
			attr.setUdn(cv.getControl().getUserDefinedName());
			attr.setCaption(cv.getControl().getCaption());
			attr.setType(cv.getControl().getCtrlType());

			Object value = cv.getValue();
			if (value != null && value.getClass().isArray()) {
				value = Arrays.asList((String[])value);
			} else if (value instanceof FileControlValue) {
				value = ((FileControlValue) value).toValueMap();
			}

			attr.setValue(value);
			return attr;
		}
	}
}