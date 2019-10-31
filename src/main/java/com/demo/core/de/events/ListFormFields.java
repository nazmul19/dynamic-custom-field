package com.demo.core.de.events;


public class ListFormFields {
	private Long formId;
	
	private Long cpId;

	private Long cpGroupId;
	
	private boolean extendedFields;
	
	private boolean prefixParentFormCaption = false;

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public boolean isPrefixParentFormCaption() {
		return prefixParentFormCaption;
	}

	public void setPrefixParentFormCaption(boolean prefixParentFormCaption) {
		this.prefixParentFormCaption = prefixParentFormCaption;
	}

	public Long getCpId() {
		return cpId;
	}

	public void setCpId(Long cpId) {
		this.cpId = cpId;
	}

	public Long getCpGroupId() {
		return cpGroupId;
	}

	public void setCpGroupId(Long cpGroupId) {
		this.cpGroupId = cpGroupId;
	}

	public boolean isExtendedFields() {
		return extendedFields;
	}

	public void setExtendedFields(boolean extendedFields) {
		this.extendedFields = extendedFields;
	}
}
