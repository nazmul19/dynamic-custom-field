package com.demo.core.plugin.de.domain;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.envers.Audited;

import com.demo.core.config.SpringContext;
import com.demo.core.domain.BaseEntity;
import com.demo.core.plugin.de.repository.FormRecordEntryBeanRepository;

import edu.common.dynamicextensions.napi.FormDataManager;

@Audited
public abstract class BaseExtensionEntity extends BaseEntity {
	private DeObject extension;

	protected Integer extensionRev;

	public DeObject getExtensionIfPresent() {
		return extension;
	}

	public DeObject getExtension() {
		if (extension == null) {
			extension = createExtension();
		}
		
		return extension;
	}

	public void setExtension(DeObject extension) {
		this.extension = extension;
	}
	
	public void addOrUpdateExtension() {
		if (extension == null) {
			return;			
		}
		
		if (extension.saveOrUpdate()) {
			extensionRev = (extensionRev == null) ? 1 : extensionRev + 1;
		}
	}

	public Integer getExtensionRev() {
		return extensionRev;
	}

	public void setExtensionRev(Integer extensionRev) {
		this.extensionRev = extensionRev;
	}

	public boolean hasPhiFields() {
		return getExtension() != null && getExtension().hasPhiFields();
	}
	
	public void copyExtensionTo(BaseExtensionEntity entity) {
		if (getExtension() == null) {
			return;
		}
		
		getExtension().copyAttrsTo(entity.getExtension());
	}

	public DeObject createExtension() {
		DeObject extnObj = new DeObject() {	
			@Override
			public void setAttrValues(Map<String, Object> attrValues) {
				// TODO Auto-generated method stub				
			}
			
			@Override
			public Long getObjectId() {
				return BaseExtensionEntity.this.getId();
			}
			
			@Override
			public String getFormName() {
				return getFormNameByEntityType();
			}
			
			@Override
			public String getEntityType() {
				return BaseExtensionEntity.this.getEntityType();
			}
			
			@Override
			public Long getCpId() {
				return BaseExtensionEntity.this.getCpId();
			}

			@Override
			public boolean isCpBased() {
				return BaseExtensionEntity.this.isCpBased();
			}

			@Override
			public Long getEntityId() {
				return BaseExtensionEntity.this.getEntityId();
			}
		};
		extnObj.setFormDataMgr(SpringContext.getBean(FormDataManager.class));
		extnObj.setFrebRepo(SpringContext.getBean(FormRecordEntryBeanRepository.class));
		if (StringUtils.isBlank(extnObj.getFormName())) {
			return null;
		}
		
		if (getId() == null) {
			return extnObj;
		}
		
		Long recId = getRecordId(extnObj);
		extnObj.setId(recId);
		return extnObj;
	}
	
	public abstract String getEntityType();

	public Long getCpId() {
		return -1L;
	}

	public boolean isCpBased() {
		return true;
	}

	public Long getEntityId() {
		return null;
	}
	
	private Long getRecordId(DeObject extnObj) {
		List<Long> recIds = extnObj.getRecordIds();
		if (CollectionUtils.isEmpty(recIds)) {
			return null;
		}
		
		return recIds.iterator().next();		
	}
}
