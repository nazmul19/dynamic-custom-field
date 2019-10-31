package com.demo.core.init;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import com.demo.core.plugin.de.UserContextImpl;

import edu.common.dynamicextensions.napi.FormDataFilter;
import edu.common.dynamicextensions.napi.FormDataManager;
import edu.common.dynamicextensions.nutility.DeConfiguration;
import edu.wustl.dynamicextensions.formdesigner.usercontext.CSDProperties;

@Component
public class DeInitializer implements InitializingBean {
	private static final String QUERY_PATH_CFG = "/com/krishagni/catissueplus/core/de/query/paths.xml";
	
	@Autowired
	private PlatformTransactionManager transactionManager;
	
	//private ConfigurationService cfgSvc;
	
	@Autowired
	private DataSource dataSource;

	private FormDataManager formDataMgr;

	private Map<String, FormDataFilter> preFormSaveFilters = new HashMap<String, FormDataFilter>();

	private Map<String, FormDataFilter> postFormSaveFilters = new HashMap<String, FormDataFilter>();

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

//	public void setCfgSvc(ConfigurationService cfgSvc) {
//		this.cfgSvc = cfgSvc;
//	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setFormDataMgr(FormDataManager formDataMgr) {
		this.formDataMgr = formDataMgr;
	}

	public void setPreFormSaveFilters(Map<String, FormDataFilter> preFormSaveFilters) {
		this.preFormSaveFilters = preFormSaveFilters;
	}

	public void setPostFormSaveFilters(Map<String, FormDataFilter> postFormSaveFilters) {
		this.postFormSaveFilters = postFormSaveFilters;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
//		Map<String, Object> localeSettings = cfgSvc.getLocaleSettings();		
//		String dateFormat = (String)localeSettings.get("deBeDateFmt");
//		String timeFormat = (String)localeSettings.get("timeFmt");
//		String dataDir = cfgSvc.getDataDir();
//		
		System.out.println("****************  Initializing DE *********************");
		String dateFormat = "yyyy-MM-DD";
		String timeFormat = "hh:mm";
		String dataDir = "D:\\Exploration\\de_data";
		
		String dir = new StringBuilder(dataDir).append(File.separator)
			.append("de-file-data").append(File.separator)
			.toString();
		
		File dirFile = new File(dir);
		if (!dirFile.exists()) {
			if (!dirFile.mkdirs()) {
				throw new RuntimeException("Error couldn't create directory for storing de file data");
			}
		}
		
		CSDProperties.getInstance().setUserContextProvider(new UserContextImpl());

		DeConfiguration.getInstance()
			.dataSource(dataSource, transactionManager)
			.fileUploadDir(dir)
			.dateFormat(dateFormat)
			.timeFormat(timeFormat);

//		ControlManager.getInstance().registerFactory(UserControlFactory.getInstance());			
//		ControlMapper.getInstance().registerControlMapper("userField", new UserFieldMapper());
//		
//		ControlManager.getInstance().registerFactory(StorageContainerControlFactory.getInstance());
//		ControlMapper.getInstance().registerControlMapper("storageContainer", new StorageContainerMapper());
//		
//		ControlManager.getInstance().registerFactory(SiteControlFactory.getInstance());
//		ControlMapper.getInstance().registerControlMapper("siteField", new SiteFieldMapper());
//
//		ControlManager.getInstance().registerFactory(PvControlFactory.getInstance());
//		ControlMapper.getInstance().registerControlMapper("pvField", new PvFieldMapper());

//		InputStream in = null;
//		try {
//			in = Utility.getResourceInputStream(QUERY_PATH_CFG);
//			PathConfig.initialize(in);			
//		} finally {
//			IOUtils.closeQuietly(in);
//		}
		
//		cfgSvc.registerChangeListener("common", new ConfigChangeListener() {			
//			@Override
//			public void onConfigChange(String name, String value) {
//				if (!name.equals("locale")) {
//					return;
//				}
//				
//				Map<String, Object> localeSettings = cfgSvc.getLocaleSettings();
//				DeConfiguration.getInstance()
//					.dateFormat((String)localeSettings.get("deBeDateFmt"))
//					.timeFormat((String)localeSettings.get("timeFmt"));
//			}
//		});

		setFormFilters();
		
		System.out.println("*******************  DE Initialization Complete *******************");
	}

	private void setFormFilters() {
		for (Map.Entry<String, FormDataFilter> filterEntry : preFormSaveFilters.entrySet()) {
			if (filterEntry.getKey().equals("all")) {
				formDataMgr.getFilterMgr().addPreFilter(filterEntry.getValue());
			} else {
				formDataMgr.getFilterMgr().addPreFilter(filterEntry.getKey(), filterEntry.getValue());
			}
		}

		for (Map.Entry<String, FormDataFilter> filterEntry : postFormSaveFilters.entrySet()) {
			if (filterEntry.getKey().equals("all")) {
				formDataMgr.getFilterMgr().addPostFilter(filterEntry.getValue());
			} else {
				formDataMgr.getFilterMgr().addPostFilter(filterEntry.getKey(), filterEntry.getValue());
			}
		}
	}
}
