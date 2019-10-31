package com.demo.core.de.domain;

import java.util.Date;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.demo.core.domain.FormContextBean;
import com.demo.core.domain.User;

import lombok.Data;

@Entity
@Table(name = "DYEXTN_CONTAINERS")
@Data
public class Form{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "IDENTIFIER", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    protected Long id; 
	
	@Column(name = "NAME")
	private String name;

	@Column(name = "CAPTION")
	private String caption;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CREATED_BY", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
	private User createdBy;

	@Column(name = "CREATE_TIME")
	private Date creationTime;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "LAST_MODIFIED_BY", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
	private User updatedBy;

	@Column(name = "LAST_MODIFY_TIME")
	private Date updateTime;

	@Lob
	@Column(name = "XML")
	private byte[] xml;
	
	@Column(name = "DELETED_ON")
	private Date deletedOn;

	@OneToMany(mappedBy="form")
    private Set<FormContextBean> associations;
}
