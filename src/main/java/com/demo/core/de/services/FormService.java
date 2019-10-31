//package com.demo.core.de.services;
//
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.web.multipart.MultipartFile;
//
//import com.demo.core.common.RequestEvent;
//import com.demo.core.common.ResponseEvent;
//import com.demo.core.de.events.AddRecordEntryOp;
//import com.demo.core.de.events.EntityFormRecords;
//import com.demo.core.de.events.FileDetail;
//import com.demo.core.de.events.FormContextDetail;
//import com.demo.core.de.events.FormCtxtSummary;
//import com.demo.core.de.events.FormDataDetail;
//import com.demo.core.de.events.FormFieldSummary;
//import com.demo.core.de.events.FormRecordCriteria;
//import com.demo.core.de.events.FormRecordsList;
//import com.demo.core.de.events.FormSummary;
//import com.demo.core.de.events.GetEntityFormRecordsOp;
//import com.demo.core.de.events.GetFileDetailOp;
//import com.demo.core.de.events.GetFormFieldPvsOp;
//import com.demo.core.de.events.GetFormRecordsListOp;
//import com.demo.core.de.events.ListEntityFormsOp;
//import com.demo.core.de.events.ListFormFields;
//import com.demo.core.de.events.RemoveFormContextOp;
////import com.krishagni.catissueplus.core.administrative.repository.FormListCriteria;
////import com.krishagni.catissueplus.core.common.events.BulkDeleteEntityOp;
////import com.krishagni.catissueplus.core.common.events.DependentEntityDetail;
//
//import edu.common.dynamicextensions.domain.nui.Container;
//import edu.common.dynamicextensions.domain.nui.PermissibleValue;
//import edu.common.dynamicextensions.napi.FormData;
//
//public interface FormService {
////	public ResponseEvent<List<FormSummary>> getForms(RequestEvent<FormListCriteria> req);
////	
////	public ResponseEvent<Long> getFormsCount(RequestEvent<FormListCriteria> req);
////	
////	public ResponseEvent<Container> getFormDefinition(RequestEvent<Long> req);
////	
////	public ResponseEvent<Boolean> deleteForms(RequestEvent<BulkDeleteEntityOp> req);
////	
//	public ResponseEvent<List<FormFieldSummary>> getFormFields(RequestEvent<ListFormFields> req);
//	
//	public ResponseEvent<List<FormContextDetail>> getFormContexts(RequestEvent<Long> req);
//	
//	public ResponseEvent<List<FormContextDetail>> addFormContexts(RequestEvent<List<FormContextDetail>> req);
//	
//	public ResponseEvent<Boolean> removeFormContext(RequestEvent<RemoveFormContextOp> req);
//	
//	public ResponseEvent<List<FormCtxtSummary>> getEntityForms(RequestEvent<ListEntityFormsOp> req);
//	
//	public ResponseEvent<EntityFormRecords> getEntityFormRecords(RequestEvent<GetEntityFormRecordsOp> req);
//	
//	public ResponseEvent<FormDataDetail> getFormData(RequestEvent<FormRecordCriteria> req);
//
//	public ResponseEvent<List<FormDataDetail>> getLatestRecords(RequestEvent<FormRecordCriteria> req);
//
//	public ResponseEvent<FormDataDetail> saveFormData(RequestEvent<FormDataDetail> req);
//	
//	public ResponseEvent<List<FormData>> saveBulkFormData(RequestEvent<List<FormData>> req);
//
//	public ResponseEvent<FileDetail> getFileDetail(RequestEvent<GetFileDetailOp> req);
//
//	public ResponseEvent<FileDetail> uploadFile(RequestEvent<MultipartFile> req);
//
//	public ResponseEvent<Long> deleteRecord(RequestEvent<FormRecordCriteria> req);
//
//	public ResponseEvent<Long> addRecordEntry(RequestEvent<AddRecordEntryOp> req);
//
//	public ResponseEvent<List<FormRecordsList>> getFormRecords(RequestEvent<GetFormRecordsListOp> req);
//	
////	public ResponseEvent<List<DependentEntityDetail>> getDependentEntities(RequestEvent<Long> req);
//
//	/**
//	 * Internal usage
//	 */
//	List<FormData> getSummaryRecords(Long formId, List<Long> recordIds);
//
//	FormData getRecord(Container form, Long recordId);
//
//	List<FormData> getRecords(Container form, List<Long> recordIds);
//	
//	ResponseEvent<List<PermissibleValue>> getPvs(RequestEvent<GetFormFieldPvsOp> req);
//
//	void addFormContextProc(String entity, FormContextProcessor proc);
//
//	Map<String, Object> getExtensionInfo(Long cpId, String entityType);
//
//	Map<String, Object> getExtensionInfo(boolean cpBased, String entityType, Long entityId);
//
//	List<FormSummary> getEntityForms(Long cpId, String[] entityTypes);
//
//	void anonymizeRecord(Container form, Long recordId);
//}
