package com.demo.core.init;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.DigestUtils;

import com.demo.core.Utility;
import com.demo.core.domain.User;
import com.demo.core.plugin.de.domain.Form;
import com.demo.core.plugin.de.domain.FormContextBean;
import com.demo.core.plugin.de.domain.ImportFormLog;
import com.demo.core.plugin.de.repository.FormContextBeanRepository;
import com.demo.core.plugin.de.repository.FormRepository;
import com.demo.core.plugin.de.repository.ImportFormLogRepository;

import edu.common.dynamicextensions.domain.nui.Container;
import edu.common.dynamicextensions.domain.nui.UserContext;
import edu.common.dynamicextensions.nutility.ContainerParser;

public abstract class ImportForms implements InitializingBean {
	
	@Autowired
	private FormContextBeanRepository formContextBeanRepository;
	
	@Autowired
	private ImportFormLogRepository importFormLogRepository;
	
	@Autowired
	private FormRepository formRepository;
	
	@Autowired
	private PlatformTransactionManager txnMgr;
	//private DeInitializer deInitializer;

	private boolean createTable = false;
	
	
	public FormRepository getFormRepository() {
		return formRepository;
	}

	public void setFormRepository(FormRepository formRepository) {
		this.formRepository = formRepository;
	}

	public FormContextBeanRepository getFormContextBeanRepository() {
		return formContextBeanRepository;
	}

	public void setFormContextBeanRepository(FormContextBeanRepository formContextBeanRepository) {
		this.formContextBeanRepository = formContextBeanRepository;
	}

	public ImportFormLogRepository getImportFormLogRepository() {
		return importFormLogRepository;
	}

	public void setImportFormLogRepository(ImportFormLogRepository importFormLogRepository) {
		this.importFormLogRepository = importFormLogRepository;
	}

	public PlatformTransactionManager getTxnMgr() {
		return txnMgr;
	}

	public void setTxnMgr(PlatformTransactionManager txnMgr) {
		this.txnMgr = txnMgr;
	}

//	public DeInitializer getDeInitializer() {
//		return deInitializer;
//	}
//
//	public void setDeInitializer(DeInitializer deInitializer) {
//		this.deInitializer = deInitializer;
//	}

	public boolean isCreateTable() {
		return createTable;
	}

	public void setCreateTable(boolean createTable) {
		this.createTable = createTable;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		try {
			System.out.println("*****************************   Import Forms ****************************");
			importForms();
		} finally {
			cleanup();
		}
	}

	public void importForms() throws Exception {
		final Collection<String> formFiles = listFormFiles();

		TransactionTemplate txnTmpl = new TransactionTemplate(txnMgr);
		txnTmpl.setPropagationBehavior(TransactionTemplate.PROPAGATION_REQUIRED);
		txnTmpl.execute(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(TransactionStatus status) {
				try {
					//AuthUtil.setCurrentUser(getSystemUser());
					importForms(formFiles);
					return null;
				} catch (Exception e) {
					status.setRollbackOnly();
					throw new RuntimeException(e);
				} finally {
					//AuthUtil.clearCurrentUser();
				}
			}
		});
	}

	protected abstract Collection<String> listFormFiles() throws IOException;

	protected abstract boolean isSysForm(String formFile);

	protected abstract FormContextBean getFormContext(String formFile, Long formId);

	protected void saveOrUpdateFormCtx(String formFile, Long formId) {
		//daoFactory.getFormDao().saveOrUpdate(getFormContext(formFile, formId));
		formContextBeanRepository.save(getFormContext(formFile, formId));
	}

	protected abstract void cleanup();

	protected Map<String, Object> getTemplateProps() {
		return new HashMap<>();
	}

	private void importForms(Collection<String> formFiles) throws Exception {
		UserContext userCtx = getUserContext(getSystemUser());

		for (String formFile : formFiles) {
			InputStream in = null;
			try {
				in = preprocessForms(formFile);
				ImportFormLog changeLog = importFormLogRepository.findFirstByFilenameOrderByExecutedOnDesc(formFile);
				//Object[] changeLog = daoFactory.getFormDao().getLatestFormChangeLog(formFile);
				//String existingDigest = (changeLog != null) ? (String) changeLog[2] : null;
				String existingDigest = (changeLog != null) ? changeLog.getDigest() : null;
				//String newDigest = Utility.getInputStreamDigest(in);
				String newDigest = DigestUtils.md5DigestAsHex(Utility.getInputStreamBytes(in));
				if (existingDigest != null && existingDigest.equals(newDigest)) {
					continue; // form XML has not got modified since last import
				}

				Long formId = (changeLog != null) ? changeLog.getFormId() : null;
				Date importDate = (changeLog != null) ? changeLog.getExecutedOn() : null;
				if (!isSysForm(formFile) && formId != null && importDate != null) {
					//
					// Non system form. Need to check whether the form got modified since last
					// import
					//
					Optional<Form> formFetch = formRepository.findById(formId);
					Date updateDate = (formFetch.isPresent() ? formFetch.get().getUpdateTime(): null);
					if(formFetch.isPresent() && updateDate.after(importDate)) {
						continue;
					}
//					Date updateDate = daoFactory.getFormDao().getUpdateTime(formId);
//					if (updateDate != null && updateDate.after(importDate)) {
//						continue;
//					}
				}

				in.reset();

				Container form = new ContainerParser(in).parse();
				form.disableDeletedCtrlsTracking(true);

				formId = Container.createContainer(userCtx, form, isCreateTable());
				saveOrUpdateFormCtx(formFile, formId);
				ImportFormLog log = new ImportFormLog();
				log.setDigest(newDigest);
				log.setFilename(formFile);
				log.setFormId(formId);
				importFormLogRepository.save(log);
				//daoFactory.getFormDao().insertFormChangeLog(formFile, newDigest, formId);
			} finally {
				IOUtils.closeQuietly(in);
			}
		}
	}

	private UserContext getUserContext(final User user) {
		return new UserContext() {
			@Override
			public String getUserName() {
				return user.getUsername();
			}

			@Override
			public Long getUserId() {
				return user.getId();
			}

			@Override
			public String getIpAddress() {
				return null;
			}
		};
	}

	private User getSystemUser() {
		//return userDao.getSystemUser();
		User u = new User();
		u.setId(2L);
		u.setUsername("Myusername");
		return u;
	}

	private InputStream preprocessForms(String formFile) {
		//String template = templateService.render(formFile, getTemplateProps());
		InputStream is = Utility.getResourceInputStream(formFile);
		
		try {
			return new ByteArrayInputStream(Utility.getInputStreamBytes(is));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
}
