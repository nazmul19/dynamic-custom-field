/**
 * 
 */
package com.demo.core.plugin.de.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Nazmul Hassan
 *
 */
@Entity
@Table(name = "IMPORT_FORMS_LOG")
@Getter @Setter @NoArgsConstructor
public class ImportFormLog {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDENTIFIER", nullable = false, columnDefinition = "BIGINT UNSIGNED")
	private Long id;
	
	@Column(name = "FILENAME")
	private String filename;
	
	@Column(name = "FORM_ID")
	private Long formId;
	
	@Column(name = "MD5_DIGEST")
	private String digest;
	
	@Column(name = "EXECUTED_ON")
	private Date executedOn;
	
}
