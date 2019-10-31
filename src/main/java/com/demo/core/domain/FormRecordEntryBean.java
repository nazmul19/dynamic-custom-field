package com.demo.core.domain;

import java.util.Collections;
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
import javax.persistence.Transient;

//import com.krishagni.catissueplus.core.common.util.AuthUtil;

import edu.common.dynamicextensions.domain.nui.Container;
import edu.common.dynamicextensions.domain.nui.UserContext;
import edu.common.dynamicextensions.napi.FormAuditManager;
import edu.common.dynamicextensions.napi.impl.FormAuditManagerImpl;

@Entity
@Table(name  = "FORM_RECORD_ENTRY")
public class FormRecordEntryBean {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IDENTIFIER", nullable = false, columnDefinition = "BIGINT UNSIGNED")
	private Long identifier;
	
	@Column(name = "FORM_CTXT_ID")
	private Long formCtxtId;
	
	@Column(name = "OBJECT_ID")
	private Long objectId;
	
	@Column(name = "RECORD_ID")
	private Long recordId;
	
	@Column(name = "UPDATED_BY")
	private Long updatedBy;
	
	@Column(name = "UPDATE_TIME")
	private Date updatedTime;
	
	@Column(name = "ACTIVITY_STATUS")
	private Status status;
	
	@Transient 
	private String activityStatusStr;
	
	
	@ManyToOne(fetch=FetchType.LAZY, optional = true)
	@JoinColumn(name="FORM_CTXT_ID", insertable = false, updatable = false)
	private FormContextBean formCtxt;

	public enum Status {
		ACTIVE, CLOSED
	}
	
	public Long getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Long identifier) {
		this.identifier = identifier;
	}

	public Long getFormCtxtId() {
		return formCtxtId;
	}

	public void setFormCtxtId(Long formCtxtId) {
		this.formCtxtId = formCtxtId;
	}

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	public Long getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Long updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public Status getActivityStatus() {
		return status;
	}

	public void setActivityStatus(Status status) {
	  this.status = status;
	}

	public String getActivityStatusStr() {
	  return status != null ? status.name() : null;
	}

	public void setActivityStatusStr(String status) {
	  this.status = status != null ? Status.valueOf(status) : Status.ACTIVE;
	}

	public String getEntityType() {
		return formCtxt != null ? formCtxt.getEntityType() : null;
	}

	public FormContextBean getFormCtxt() {
		return formCtxt;
	}

	public void setFormCtxt(FormContextBean formCtxt) {
		this.formCtxt = formCtxt;
	}

	public void delete() {
		setActivityStatus(Status.CLOSED);

		FormAuditManager auditMgr = new FormAuditManagerImpl();
		auditMgr.audit(getUserContext(), getDeForm(), Collections.emptyList(), "DELETE", getRecordId());
	}

	private UserContext getUserContext() {
		//User currentUser = AuthUtil.getCurrentUser();
		return new UserContext() {
			@Override
			public Long getUserId() {
				return 2L;
			}

			@Override
			public String getUserName() {
				return "MyUser";
			}

			@Override
			public String getIpAddress() {
				return null;
			}
		};
	}

	private Container getDeForm() {
		Container container = new Container();
		container.setId(formCtxt.getForm().getId());
		container.setName(formCtxt.getForm().getName());
		return container;
	}
}