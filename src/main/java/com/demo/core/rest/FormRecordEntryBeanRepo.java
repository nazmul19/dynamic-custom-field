/**
 * 
 */
package com.demo.core.rest;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.demo.core.domain.FormRecordEntryBean;
import com.demo.core.domain.FormRecordEntryBean.Status;

/**
 * @author Nazmul Hassan
 *
 */
@Repository
public interface FormRecordEntryBeanRepo extends CrudRepository<FormRecordEntryBean, Long>{
	FormRecordEntryBean findByFormCtxtIdAndObjectIdAndRecordId(Long formCtxtId, Long objectId, Long recordId);
	@Query("select " + 
			"        re " + 
			"      from " + 
			"        com.demo.core.domain.FormRecordEntryBean re" + 
			"        join re.formCtxt fc" + 
			"      where " + 
			"        fc.containerId = ?1 and " + 
			"        re.recordId = ?2 and " + 
		    "        fc.deletedOn is null")
	FormRecordEntryBean findByFormIdAndRecordId(Long formId, Long recordId);
	
	List<RecordId> findByFormCtxtIdAndObjectId(Long formCtxtId, Long objectId);
	
	@Query( value = "select " + 
			"        c.name as formName, fc.identifier as ctxtId " + 
			"      from " + 
			"        dyextn_containers c " + 
			"        inner join APP_FORM_CONTEXT fc on fc.container_id = c.identifier " + 
			"      where " + 
			"        c.deleted_on is null and " + 
			"        fc.deleted_on is null and " + 
			"        fc.entity_type = ?2 and " + 
			"        ((?3 is null and fc.cp_id = ?1) or fc.entity_id = ?3)", nativeQuery = true)
	FormNameContextId findByCpIdAndEntityTypeAndEntityId(Long cpId, String entityType, Long entityId);
	
	List<ObjectIdRecordId> findByObjectIdInAndFormCtxtId(Collection<Long> objectIds, Long formCtxtId);
}
