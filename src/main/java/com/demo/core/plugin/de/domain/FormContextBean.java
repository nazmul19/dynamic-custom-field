package com.demo.core.plugin.de.domain;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
@Entity
@Table(name = "APP_FORM_CONTEXT")
public class FormContextBean {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IDENTIFIER", nullable = false, columnDefinition = "BIGINT UNSIGNED")
	private Long identifier;
	
	@Column(name = "CONTAINER_ID")
	private Long containerId;
	
	@Column(name = "ENTITY_TYPE")
	private String entityType;
	
	@Column(name = "CP_ID")
	private Long cpId;

	@Column(name = "ENTITY_ID")
	private Long entityId;
	
	@Column(name = "SORT_ORDER")
	private Integer sortOrder;
	
	@Column(name = "IS_MULTIRECORD")
	private boolean multiRecord;
	
	@Column(name = "IS_SYSFORM")
	private boolean sysForm;
	
	@Column(name = "DELETED_ON")
	private Date deletedOn;

	@ManyToOne(fetch=FetchType.LAZY, optional = true)
	@JoinColumn(name="CONTAINER_ID", insertable = false, updatable = false)
	private Form form;

	public Long getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Long identifier) {
		this.identifier = identifier;
	}

	public Long getContainerId() {
		return containerId;
	}

	public void setContainerId(Long containerId) {
		this.containerId = containerId;
	}

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public Long getCpId() {
		return cpId;
	}

	public void setCpId(Long cpId) {
		this.cpId = cpId;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public boolean isMultiRecord() {
		return multiRecord;
	}

	public void setMultiRecord(boolean multiRecord) {
		this.multiRecord = multiRecord;
	}

	public boolean isSysForm() {
		return sysForm;
	}

	public void setSysForm(boolean sysForm) {
		this.sysForm = sysForm;
	}

	public Date getDeletedOn() {
		return deletedOn;
	}

	public void setDeletedOn(Date deletedOn) {
		this.deletedOn = deletedOn;
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}
	
	
}
