package com.demo.core.plugin.de.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.demo.core.plugin.de.domain.FormContextBean;
import com.demo.core.plugin.de.events.DeAccountSummary;
import com.demo.core.plugin.de.events.FormContextDetail;

@Repository
public class FormContextBeanRepositoryCustomImpl implements FormContextBeanRepositoryCustom{

	private static final String GET_FORM_CTXTS = "select " + 
			"        fctxt.identifier as ctxtId, fctxt.container_id as formId, fctxt.entity_type as entityLevel, " + 
			"        fctxt.entity_id as entityId, fctxt.is_multirecord as multirecord, fctxt.IS_SYSFORM as sysForm, " + 
			"        cp.identifier as cpId, cp.name as shortTitle, cp.name as title " + 
			"      from " + 
			"        app_form_context fctxt " + 
			"        inner join dyextn_containers c on c.identifier = fctxt.container_id	" + 
			"        left join accounts cp on cp.identifier = fctxt.cp_id " + 
			"      where " + 
			"        c.deleted_on is null and " + 
			//"        (cp.activity_status != 'Disabled' or fctxt.cp_id = -1) and " + 
			"        fctxt.deleted_on is null and " + 
			"        fctxt.container_id = :formId";
	@Autowired
	private EntityManager em;
	
	@Override
	public FormContextBean getFormContext(boolean cpBased, String entityType, Long entityId, Long formId) {
		entityId = entityId == null ? -1L : entityId;
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
	    CriteriaQuery<FormContextBean> cq = cb.createQuery(FormContextBean.class);
	    
	    Root<FormContextBean> fcb = cq.from(FormContextBean.class);
	    List<Predicate> predicates = new ArrayList<>();
	    
	    predicates.add(cb.equal(fcb.get("entityType"), entityType));
	    predicates.add(cb.isNull(fcb.get("deletedOn")));
	    
	    if (cpBased) {
	    	predicates.add(cb.or(cb.equal(fcb.get("cpId"), -1L), cb.equal(fcb.get("cpId"), entityId)));
		} else {
			predicates.add(cb.equal(fcb.get("cpId"), entityId));
		}

		if (formId != null) {
			predicates.add(cb.equal(fcb.get("containerId"), formId));
		}
		cq.where(predicates.toArray(new Predicate[0]));
		try {
			return (FormContextBean) em.createQuery(cq).getSingleResult();
	    } catch (NoResultException nre) {
	        return null;
	    }
		
	}

	@Override
	public List<FormContextDetail> getFormContexts(Long formId) {
		Query query = (Query) em.createNativeQuery(GET_FORM_CTXTS);
		List<Object[]> rows = query.setParameter("formId", formId).list();;
		
		List<FormContextDetail> formCtxts = new ArrayList<>();
		for (Object[] row : rows) {
			int idx = -1;
			FormContextDetail formCtxt = new FormContextDetail();
			formCtxt.setFormCtxtId((Long)row[++idx]);
			formCtxt.setFormId((Long)row[++idx]);
			formCtxt.setLevel((String)row[++idx]);
			formCtxt.setEntityId((Long)row[++idx]);
			formCtxt.setMultiRecord((Boolean)row[++idx]);
			formCtxt.setSysForm((Boolean)row[++idx]);

			DeAccountSummary cp = new DeAccountSummary();
			cp.setId((Long)row[++idx]);
			cp.setShortTitle((String)row[++idx]);
			cp.setTitle((String)row[++idx]);
			formCtxt.setCollectionProtocol(cp);
			
			formCtxts.add(formCtxt);
		}
		
		return formCtxts;
	}

}
