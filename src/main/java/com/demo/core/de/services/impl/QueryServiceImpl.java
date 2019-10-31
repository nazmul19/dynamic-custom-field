//package com.krishagni.catissueplus.core.de.services.impl;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Set;
//import java.util.TimeZone;
//import java.util.TreeSet;
//import java.util.UUID;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeoutException;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.function.BiConsumer;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.io.IOUtils;
//import org.apache.commons.lang3.ArrayUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import com.krishagni.catissueplus.core.administrative.domain.ScheduledJob;
//import com.krishagni.catissueplus.core.administrative.domain.User;
//import com.krishagni.catissueplus.core.administrative.repository.UserDao;
//import com.krishagni.catissueplus.core.common.OpenSpecimenAppCtxProvider;
//import com.krishagni.catissueplus.core.common.PlusTransactional;
//import com.krishagni.catissueplus.core.common.access.AccessCtrlMgr;
//import com.krishagni.catissueplus.core.common.access.AccessCtrlMgr.ParticipantReadAccess;
//import com.krishagni.catissueplus.core.common.access.SiteCpPair;
//import com.krishagni.catissueplus.core.common.domain.Notification;
//import com.krishagni.catissueplus.core.common.errors.OpenSpecimenException;
//import com.krishagni.catissueplus.core.common.events.RequestEvent;
//import com.krishagni.catissueplus.core.common.events.ResponseEvent;
//import com.krishagni.catissueplus.core.common.events.UserSummary;
//import com.krishagni.catissueplus.core.common.service.ConfigChangeListener;
//import com.krishagni.catissueplus.core.common.service.ConfigurationService;
//import com.krishagni.catissueplus.core.common.service.EmailService;
//import com.krishagni.catissueplus.core.common.service.TemplateService;
//import com.krishagni.catissueplus.core.common.util.AuthUtil;
//import com.krishagni.catissueplus.core.common.util.ConfigUtil;
//import com.krishagni.catissueplus.core.common.util.MessageUtil;
//import com.krishagni.catissueplus.core.common.util.NotifUtil;
//import com.krishagni.catissueplus.core.common.util.Utility;
//import com.krishagni.catissueplus.core.de.domain.AqlBuilder;
//import com.krishagni.catissueplus.core.de.domain.Filter;
//import com.krishagni.catissueplus.core.de.domain.QueryAuditLog;
//import com.krishagni.catissueplus.core.de.domain.QueryFolder;
//import com.krishagni.catissueplus.core.de.domain.SavedQuery;
//import com.krishagni.catissueplus.core.de.domain.factory.QueryFolderFactory;
//import com.krishagni.catissueplus.core.de.events.ExecuteQueryEventOp;
//import com.krishagni.catissueplus.core.de.events.ExecuteSavedQueryOp;
//import com.krishagni.catissueplus.core.de.events.FacetDetail;
//import com.krishagni.catissueplus.core.de.events.GetFacetValuesOp;
//import com.krishagni.catissueplus.core.de.events.ListFolderQueriesCriteria;
//import com.krishagni.catissueplus.core.de.events.ListQueryAuditLogsCriteria;
//import com.krishagni.catissueplus.core.de.events.ListSavedQueriesCriteria;
//import com.krishagni.catissueplus.core.de.events.QueryAuditLogDetail;
//import com.krishagni.catissueplus.core.de.events.QueryAuditLogSummary;
//import com.krishagni.catissueplus.core.de.events.QueryDataExportResult;
//import com.krishagni.catissueplus.core.de.events.QueryExecResult;
//import com.krishagni.catissueplus.core.de.events.QueryFolderDetails;
//import com.krishagni.catissueplus.core.de.events.QueryFolderSummary;
//import com.krishagni.catissueplus.core.de.events.SavedQueriesList;
//import com.krishagni.catissueplus.core.de.events.SavedQueryDetail;
//import com.krishagni.catissueplus.core.de.events.SavedQuerySummary;
//import com.krishagni.catissueplus.core.de.events.ShareQueryFolderOp;
//import com.krishagni.catissueplus.core.de.events.UpdateFolderQueriesOp;
//import com.krishagni.catissueplus.core.de.repository.DaoFactory;
//import com.krishagni.catissueplus.core.de.repository.SavedQueryDao;
//import com.krishagni.catissueplus.core.de.services.QueryService;
//import com.krishagni.catissueplus.core.de.services.SavedQueryErrorCode;
//import com.krishagni.catissueplus.core.init.ImportQueryForms;
//import com.krishagni.catissueplus.core.init.ImportSpecimenEventForms;
//import com.krishagni.rbac.common.errors.RbacErrorCode;
//
//import edu.common.dynamicextensions.domain.nui.Container;
//import edu.common.dynamicextensions.domain.nui.Control;
//import edu.common.dynamicextensions.nutility.DeConfiguration;
//import edu.common.dynamicextensions.query.Query;
//import edu.common.dynamicextensions.query.QueryException;
//import edu.common.dynamicextensions.query.QueryParserException;
//import edu.common.dynamicextensions.query.QueryResponse;
//import edu.common.dynamicextensions.query.QueryResultCsvExporter;
//import edu.common.dynamicextensions.query.QueryResultData;
//import edu.common.dynamicextensions.query.QueryResultExporter;
//import edu.common.dynamicextensions.query.QueryResultScreener;
//import edu.common.dynamicextensions.query.ResultColumn;
//import edu.common.dynamicextensions.query.WideRowMode;
//
//public class QueryServiceImpl implements QueryService {
//	private static final Log logger = LogFactory.getLog(QueryServiceImpl.class);
//
//	private static final String cpForm = "CollectionProtocol";
//
//	private static final String cprForm = "Participant";
//
//	private static final Pattern SELECT_PATTERN = Pattern.compile("^(select\\s+distinct|select)\\s+.*$");
//	
//	private static String QUERY_DATA_EXPORTED_EMAIL_TMPL = "query_export_data";
//	
//	private static String SHARE_QUERY_FOLDER_EMAIL_TMPL = "query_share_query_folder";
//
//	private static String CFG_MOD = "query";
//
//	private static String MAX_CONCURRENT_QUERIES = "max_concurrent_queries";
//
//	private static int DEF_MAX_CONCURRENT_QUERIES = 10;
//
//	private static String MAX_RECS_IN_MEM = "max_recs_in_memory";
//
//	private static int DEF_MAX_RECS_IN_MEM = 100;
//
//	private static final int EXPORT_THREAD_POOL_SIZE = getThreadPoolSize();
//	
//	private static final int ONLINE_EXPORT_TIMEOUT_SECS = 30;
//
//	private static ExecutorService exportThreadPool = Executors.newFixedThreadPool(EXPORT_THREAD_POOL_SIZE);
//
//	private DaoFactory daoFactory;
//
//	private UserDao userDao;
//	
//	private QueryFolderFactory queryFolderFactory;
//	
//	private EmailService emailService;
//	
//	private TemplateService templateService;
//
//	private ConfigurationService cfgService;
//
//	private int maxConcurrentQueries = DEF_MAX_CONCURRENT_QUERIES;
//
//	private int maxRecsInMemory = DEF_MAX_RECS_IN_MEM;
//
//	private AtomicInteger concurrentQueriesCnt = new AtomicInteger(0);
//
//	static {
//		initExportFileCleaner();
//	}
//
//	public void setDaoFactory(DaoFactory daoFactory) {
//		this.daoFactory = daoFactory;
//	}
//
//	public void setUserDao(UserDao userDao) {
//		this.userDao = userDao;
//	}
//
//	public void setQueryFolderFactory(QueryFolderFactory queryFolderFactory) {
//		this.queryFolderFactory = queryFolderFactory;
//	}
//
//	public void setEmailService(EmailService emailService) {
//		this.emailService = emailService;
//	}
//
//	public void setTemplateService(TemplateService templateService) {
//		this.templateService = templateService;
//	}
//
//	public void setCfgService(ConfigurationService cfgService) {
//		this.cfgService = cfgService;
//		refreshConfig();
//		cfgService.registerChangeListener("query", new ConfigChangeListener() {
//			@Override
//			public void onConfigChange(String name, String value) {
//				refreshConfig();
//
//				if (!StringUtils.equals(name, "floating_point_precision")) {
//					return;
//				}
//
//				try {
//					logger.info("Reloading query forms ...");
//					ImportQueryForms importQueryForms = OpenSpecimenAppCtxProvider.getBean("importQueryForms");
//					importQueryForms.afterPropertiesSet();
//				} catch (Exception e) {
//					logger.error("Error reloading query forms ... ", e);
//				}
//
//				try {
//					logger.info("Reloading specimen event forms ...");
//					ImportSpecimenEventForms importSpeForms = OpenSpecimenAppCtxProvider.getBean("importSpeForms");
//					importSpeForms.afterPropertiesSet();
//				} catch (Exception e) {
//					logger.error("Error reloading specimen event forms ...", e);
//				}
//			}
//		});
//	}
//
//	@Override
//	@PlusTransactional
//	public ResponseEvent<SavedQueriesList> getSavedQueries(RequestEvent<ListSavedQueriesCriteria> req) {
//		try {
//			ListSavedQueriesCriteria crit = req.getPayload();
//			if (crit.startAt() < 0 || crit.maxResults() <= 0) {
//				return ResponseEvent.userError(SavedQueryErrorCode.INVALID_PAGINATION_FILTER);
//			}
//
//			crit.userId(AuthUtil.getCurrentUser().getId());
//			List<SavedQuerySummary> queries = daoFactory.getSavedQueryDao().getQueries(crit);
//			
//			Long count = null;
//			if (crit.countReq()) {
//				count = daoFactory.getSavedQueryDao().getQueriesCount(crit);
//			}
//			
//			return ResponseEvent.response(SavedQueriesList.create(queries, count));
//		} catch (Exception e) {
//			return ResponseEvent.serverError(e);
//		}
//	}
//
//	@Override
//	@PlusTransactional
//	public ResponseEvent<SavedQueryDetail> getSavedQuery(RequestEvent<Long> req) {
//		try {
//			SavedQuery savedQuery = daoFactory.getSavedQueryDao().getQuery(req.getPayload());
//			if (savedQuery == null) {
//				return ResponseEvent.userError(SavedQueryErrorCode.NOT_FOUND, req.getPayload());
//			}
//			
//			return ResponseEvent.response(SavedQueryDetail.fromSavedQuery(savedQuery));
//		} catch (Exception e) {
//			return ResponseEvent.serverError(e);
//		}
//	}
//
//	@Override
//	@PlusTransactional
//	public ResponseEvent<SavedQueryDetail> saveQuery(RequestEvent<SavedQueryDetail> req) {
//		try {
//			SavedQueryDetail queryDetail = req.getPayload();
//			queryDetail.setId(null);
//
//			WideRowMode mode = WideRowMode.DEEP;
//			if (StringUtils.isNotBlank(queryDetail.getWideRowMode())) {
//				mode = WideRowMode.valueOf(queryDetail.getWideRowMode());
//			}
//
//			Query.createQuery()
//				.wideRowMode(mode)
//				.ic(true)
//				.dateFormat(ConfigUtil.getInstance().getDeDateFmt())
//				.timeFormat(ConfigUtil.getInstance().getTimeFmt())
//				.compile(cprForm, getAql(queryDetail));
//			SavedQuery savedQuery = getSavedQuery(queryDetail);
//			daoFactory.getSavedQueryDao().saveOrUpdate(savedQuery);
//			return ResponseEvent.response(SavedQueryDetail.fromSavedQuery(savedQuery));
//		} catch (QueryParserException | IllegalArgumentException ex) {
//			return ResponseEvent.userError(SavedQueryErrorCode.MALFORMED, ex.getMessage());
//		} catch (QueryException qe) {
//			return ResponseEvent.userError(getErrorCode(qe.getErrorCode()), qe.getMessage());
//		} catch (OpenSpecimenException ose) {
//			return ResponseEvent.error(ose);
//		} catch (Exception e) {
//			return ResponseEvent.serverError(e);
//		}	
//	}
//
//	@Override
//	@PlusTransactional
//	public ResponseEvent<SavedQueryDetail> updateQuery(RequestEvent<SavedQueryDetail> req) {
//		try {
//			SavedQueryDetail queryDetail = req.getPayload();
//
//			Query.createQuery()
//				.wideRowMode(WideRowMode.DEEP)
//				.ic(true)
//				.dateFormat(ConfigUtil.getInstance().getDeDateFmt())
//				.timeFormat(ConfigUtil.getInstance().getTimeFmt())
//				.compile(cprForm, getAql(queryDetail));
//			SavedQuery savedQuery = getSavedQuery(queryDetail);
//			SavedQuery existing = daoFactory.getSavedQueryDao().getQuery(queryDetail.getId());
//			if (existing == null) {
//				return ResponseEvent.userError(SavedQueryErrorCode.NOT_FOUND, queryDetail.getId());
//			}
//			
//			User user = AuthUtil.getCurrentUser();
//			if (!user.isAdmin() && !existing.getCreatedBy().equals(user)) {
//				return ResponseEvent.userError(SavedQueryErrorCode.OP_NOT_ALLOWED);
//			}
//			
//			existing.update(savedQuery);
//			daoFactory.getSavedQueryDao().saveOrUpdate(existing);	
//			return ResponseEvent.response(SavedQueryDetail.fromSavedQuery(existing));
//		} catch (QueryParserException qpe) {
//			return ResponseEvent.userError(SavedQueryErrorCode.MALFORMED, qpe.getMessage());
//		} catch (QueryException qe) {
//			return ResponseEvent.userError(getErrorCode(qe.getErrorCode()), qe.getMessage());
//		} catch (IllegalArgumentException iae) {
//			return ResponseEvent.userError(SavedQueryErrorCode.MALFORMED, iae.getMessage());
//		} catch (OpenSpecimenException ose) {
//			return ResponseEvent.error(ose);
//		} catch (Exception e) {
//			return ResponseEvent.serverError(e);
//		}
//	}
//
//	@Override
//	@PlusTransactional
//	public ResponseEvent<Long> deleteQuery(RequestEvent<Long> req) {
//		try {
//			Long queryId = req.getPayload();
//			SavedQuery query = daoFactory.getSavedQueryDao().getQuery(queryId);
//			if (query == null) {
//				return ResponseEvent.userError(SavedQueryErrorCode.NOT_FOUND, queryId);
//			}
//
//			User user = AuthUtil.getCurrentUser();
//			if (!user.isAdmin() && !query.getCreatedBy().equals(user)) {
//				return ResponseEvent.userError(SavedQueryErrorCode.OP_NOT_ALLOWED);
//			}
//
//			if (!query.getDependentQueries().isEmpty()) {
//				return ResponseEvent.userError(
//					SavedQueryErrorCode.DEPS_FOUND,
//					query.getDependentQueries().stream().limit(5).collect(Collectors.toSet()));
//			}
//
//			query.getScheduledJobs().forEach(ScheduledJob::delete);
//			query.getSubQueries().clear();
//			query.setDeletedOn(Calendar.getInstance().getTime());
//			daoFactory.getSavedQueryDao().saveOrUpdate(query);
//			return ResponseEvent.response(queryId);
//		} catch (Exception e) {
//			return ResponseEvent.serverError(e);
//		}
//	}
//
//	@Override
//	@PlusTransactional
//	public ResponseEvent<QueryExecResult> executeQuery(RequestEvent<ExecuteQueryEventOp> req) {
//		QueryResultData queryResult = null;
//
//		boolean queryCntIncremented = false;
//		try {
//			queryCntIncremented = incConcurrentQueriesCnt();
//
//			ExecuteQueryEventOp opDetail = req.getPayload();
//			Query query = getQuery(opDetail);
//
//			QueryResponse resp = query.getData();
//			insertAuditLog(AuthUtil.getCurrentUser(), opDetail, resp);
//			
//			queryResult = resp.getResultData();
//			queryResult.setScreener(getResultScreener(query));
//			
//			Integer[] indices = null;
//			if (opDetail.getIndexOf() != null && !opDetail.getIndexOf().isEmpty()) {
//				indices = queryResult.getColumnIndices(opDetail.getIndexOf());
//			}
//
//			return ResponseEvent.response(
//				new QueryExecResult()
//					.setColumnMetadata(queryResult.getColumnMetadata())
//					.setColumnLabels(queryResult.getColumnLabels())
//					.setColumnTypes(queryResult.getColumnTypes())
//					.setColumnUrls(queryResult.getColumnUrls())
//					.setRows(queryResult.getStringifiedRows())
//					.setDbRowsCount(queryResult.getDbRowsCount())
//					.setColumnIndices(indices)
//			);
//		} catch (QueryParserException | IllegalArgumentException qpe) {
//			return ResponseEvent.userError(SavedQueryErrorCode.MALFORMED, qpe.getMessage());
//		} catch (QueryException qe) {
//			return ResponseEvent.userError(getErrorCode(qe.getErrorCode()), qe.getMessage());
//		} catch (IllegalAccessError iae) {
//			return ResponseEvent.userError(SavedQueryErrorCode.OP_NOT_ALLOWED, iae.getMessage());
//		} catch (OpenSpecimenException ose) {
//			return ResponseEvent.error(ose);
//		} catch (Exception e) {
//			return ResponseEvent.serverError(e);
//		} finally {
//			if (queryCntIncremented) {
//				decConcurrentQueriesCnt();
//			}
//
//			if (queryResult != null) {
//				try {
//					queryResult.close();
//				} catch (Exception e) {
//					logger.error("Error closing query result stream", e);
//				}				
//			}
//		}
//	}
//
//	@Override
//	@PlusTransactional
//	public ResponseEvent<QueryExecResult> executeSavedQuery(RequestEvent<ExecuteSavedQueryOp> req) {
//		try {
//			ExecuteSavedQueryOp input = req.getPayload();
//			SavedQuery query = daoFactory.getSavedQueryDao().getQuery(input.getSavedQueryId());
//			if (query == null) {
//				throw OpenSpecimenException.userError(SavedQueryErrorCode.NOT_FOUND, input.getSavedQueryId());
//			}
//
//			query = query.copy();
//			for (Filter filter : query.getFilters()) {
//				ExecuteSavedQueryOp.Criterion criterion = input.getCriterion(filter.getId());
//				if (criterion == null || CollectionUtils.isEmpty(criterion.getValues())) {
//					continue;
//				}
//
//				switch (criterion.getSearchType()) {
//					case EQUALS:
//						filter.setEqValues(criterion.getValues());
//						break;
//
//					case RANGE:
//						filter.setRangeValues(criterion.getValues());
//						break;
//				}
//			}
//
//			ExecuteQueryEventOp op = new ExecuteQueryEventOp();
//			op.setCpId(query.getCpId());
//			op.setCpGroupId(query.getCpGroupId());
//			op.setDrivingForm(input.getDrivingForm());
//			op.setRunType(input.getRunType());
//			op.setWideRowMode(input.getWideRowMode());
//			op.setSavedQueryId(query.getId());
//			op.setAql(query.getAql() + " limit " + input.getStartAt() + ", " + input.getMaxResults());
//			return executeQuery(new RequestEvent<>(op));
//		} catch (OpenSpecimenException ose) {
//			return ResponseEvent.error(ose);
//		} catch (Exception e) {
//			return ResponseEvent.serverError(e);
//		}
//	}
//
//	@Override
//	@PlusTransactional
//	public ResponseEvent<QueryDataExportResult> exportQueryData(RequestEvent<ExecuteQueryEventOp> req) {
//		boolean queryCntIncremented = false;
//		try {
//			queryCntIncremented = incConcurrentQueriesCnt();
//			return ResponseEvent.response(exportData(req.getPayload(), null, null));
//		} catch (QueryParserException | IllegalArgumentException qpe) {
//			return ResponseEvent.userError(SavedQueryErrorCode.MALFORMED, qpe.getMessage());
//		} catch (QueryException qe) {
//			return ResponseEvent.userError(getErrorCode(qe.getErrorCode()), qe.getMessage());
//		} catch (IllegalAccessError iae) {
//			return ResponseEvent.userError(SavedQueryErrorCode.OP_NOT_ALLOWED, iae.getMessage());
//		} catch (OpenSpecimenException ose) {
//			return ResponseEvent.error(ose);
//		} catch (Exception e) {
//			return ResponseEvent.serverError(e);
//		} finally {
//			if (queryCntIncremented) {
//				decConcurrentQueriesCnt();
//			}
//		}
//	}
//	
//	@Override
//	public ResponseEvent<File> getExportDataFile(RequestEvent<String> req) {
//		String fileId = req.getPayload();
//		try {
//			String path = getExportDataDir() + File.separator + fileId;
//			File f = new File(path);
//			if (f.exists()) {
//				return ResponseEvent.response(f);
//			} else {
//				return ResponseEvent.userError(SavedQueryErrorCode.EXPORT_DATA_FILE_NOT_FOUND);
//			}
//		} catch (Exception e) {
//			return ResponseEvent.serverError(e);
//		}
//	}
//		
//	@Override
//	@PlusTransactional
//	public ResponseEvent<List<QueryFolderSummary>> getUserFolders(RequestEvent<?> req) {
//		try {
//			Long userId = AuthUtil.getCurrentUser().getId();			
//			List<QueryFolder> folders = daoFactory.getQueryFolderDao().getUserFolders(userId);			
//			return ResponseEvent.response(QueryFolderSummary.from(folders));
//		} catch (Exception e) {
//			return ResponseEvent.serverError(e);
//		}
//	}
//	
//	@Override
//	@PlusTransactional
//	public ResponseEvent<QueryFolderDetails> getFolder(RequestEvent<Long> req) {
//		try {
//			Long folderId = req.getPayload();
//			QueryFolder folder = daoFactory.getQueryFolderDao().getQueryFolder(folderId);
//			if (folder == null) {
//				return ResponseEvent.userError(SavedQueryErrorCode.FOLDER_NOT_FOUND);
//			}
//			
//			User user = AuthUtil.getCurrentUser();
//			if (!user.isAdmin() && !folder.canUserAccess(user.getId())) {
//				return ResponseEvent.userError(SavedQueryErrorCode.OP_NOT_ALLOWED);
//			}
//			
//			return ResponseEvent.response(QueryFolderDetails.from(folder));			
//		} catch (Exception e) {
//			return ResponseEvent.serverError(e);			
//		}
//	}	
//	
//	
//	@Override
//	@PlusTransactional
//	public ResponseEvent<QueryFolderDetails> createFolder(RequestEvent<QueryFolderDetails> req) {
//		try {
//			QueryFolderDetails folderDetails = req.getPayload();
//			
//			UserSummary owner = new UserSummary();
//			owner.setId(AuthUtil.getCurrentUser().getId());
//			folderDetails.setOwner(owner);
//			
//			QueryFolder queryFolder = queryFolderFactory.createQueryFolder(folderDetails);			
//			daoFactory.getQueryFolderDao().saveOrUpdate(queryFolder);
//			
//			if (!queryFolder.getSharedWith().isEmpty()) {
//				notifyFolderShared(queryFolder.getOwner(), queryFolder, queryFolder.getSharedWith());
//			}			
//			return ResponseEvent.response(QueryFolderDetails.from(queryFolder));
//		} catch (OpenSpecimenException ose) {
//			return ResponseEvent.error(ose);
//		} catch (Exception e) {
//			return ResponseEvent.serverError(e);
//		}
//	}
//
//	@Override
//	@PlusTransactional
//	public ResponseEvent<QueryFolderDetails> updateFolder(RequestEvent<QueryFolderDetails> req) {
//		try {
//			QueryFolderDetails folderDetails = req.getPayload();
//			Long folderId = folderDetails.getId();
//			if (folderId == null) {
//				return ResponseEvent.userError(SavedQueryErrorCode.FOLDER_NOT_FOUND);
//			}
//						
//			QueryFolder existing = daoFactory.getQueryFolderDao().getQueryFolder(folderId);
//			if (existing == null) {
//				return ResponseEvent.userError(SavedQueryErrorCode.FOLDER_NOT_FOUND);
//			}
//			
//			User user = AuthUtil.getCurrentUser();
//			if (!user.isAdmin() && !existing.getOwner().equals(user)) {
//				return ResponseEvent.userError(SavedQueryErrorCode.OP_NOT_ALLOWED);
//			}
//			
//			UserSummary owner = new UserSummary();
//			owner.setId(existing.getOwner().getId());
//			folderDetails.setOwner(owner);
//			
//			QueryFolder queryFolder = queryFolderFactory.createQueryFolder(folderDetails);
//			Set<User> newUsers = new HashSet<>(queryFolder.getSharedWith());
//			newUsers.removeAll(existing.getSharedWith());
//			existing.update(queryFolder);
//			
//			daoFactory.getQueryFolderDao().saveOrUpdate(existing);
//			
//			if (!newUsers.isEmpty()) {
//				notifyFolderShared(user, existing, newUsers);
//			}
//			return ResponseEvent.response(QueryFolderDetails.from(existing));			
//		} catch (OpenSpecimenException ose) {
//			return ResponseEvent.error(ose);
//		} catch (Exception e) {
//			return ResponseEvent.serverError(e);
//		}
//	}
//
//	@Override
//	@PlusTransactional
//	public ResponseEvent<Long> deleteFolder(RequestEvent<Long> req) {
//		try {
//			Long folderId = req.getPayload();
//			if (folderId == null) {
//				return ResponseEvent.userError(SavedQueryErrorCode.FOLDER_NOT_FOUND);
//			}
//
//			QueryFolder existing = daoFactory.getQueryFolderDao().getQueryFolder(folderId);
//			if (existing == null) {
//				return ResponseEvent.userError(SavedQueryErrorCode.FOLDER_NOT_FOUND);								
//			}
//			
//			User user = AuthUtil.getCurrentUser();			
//			if (!user.isAdmin() && !existing.getOwner().equals(user)) {
//				return ResponseEvent.userError(SavedQueryErrorCode.OP_NOT_ALLOWED);
//			}
//			
//			daoFactory.getQueryFolderDao().deleteFolder(existing);
//			return ResponseEvent.response(folderId);
//		} catch (Exception e) {
//			return ResponseEvent.serverError(e);
//		}
//	}
//	
//	@Override
//	@PlusTransactional
//	public ResponseEvent<SavedQueriesList> getFolderQueries(RequestEvent<ListFolderQueriesCriteria> req) {
//		try {
//			ListFolderQueriesCriteria crit = req.getPayload();
//			QueryFolder folder = daoFactory.getQueryFolderDao().getQueryFolder(crit.folderId());
//			if (folder == null) {
//				return ResponseEvent.userError(SavedQueryErrorCode.FOLDER_NOT_FOUND);
//			}
//			
//			User user = AuthUtil.getCurrentUser();
//			if (!user.isAdmin() && !folder.canUserAccess(user.getId())) {
//				return ResponseEvent.userError(SavedQueryErrorCode.OP_NOT_ALLOWED);
//			}
//			
//			List<SavedQuerySummary> queries = daoFactory.getSavedQueryDao().getQueriesByFolderId(
//					crit.folderId(),
//					crit.startAt(),
//					crit.maxResults(),
//					crit.query());
//			
//			Long count = null;
//			if (crit.countReq()) {
//				count = daoFactory.getSavedQueryDao().getQueriesCountByFolderId(crit.folderId(), crit.query());
//			}
//			
//			return ResponseEvent.response(SavedQueriesList.create(queries, count));
//		} catch (Exception e) {
//			return ResponseEvent.serverError(e);
//		}
//	}
//	
//	@Override
//	@PlusTransactional
//	public ResponseEvent<List<SavedQuerySummary>> updateFolderQueries(RequestEvent<UpdateFolderQueriesOp> req) {
//		try {
//			UpdateFolderQueriesOp opDetail = req.getPayload();
//			
//			Long folderId = opDetail.getFolderId();
//			QueryFolder queryFolder = daoFactory.getQueryFolderDao().getQueryFolder(folderId);			
//			if (queryFolder == null) {
//				return ResponseEvent.userError(SavedQueryErrorCode.FOLDER_NOT_FOUND);
//			}
//			
//			User user = AuthUtil.getCurrentUser();
//			if (!user.isAdmin() && !queryFolder.getOwner().equals(user)) {
//				return ResponseEvent.userError(SavedQueryErrorCode.OP_NOT_ALLOWED);
//			}
//			
//			List<SavedQuery> savedQueries = null;
//			List<Long> queryIds = opDetail.getQueries();
//			
//			if (queryIds == null || queryIds.isEmpty()) {
//				savedQueries = new ArrayList<>();
//			} else {
//				savedQueries = daoFactory.getSavedQueryDao().getQueriesByIds(queryIds);
//			}
//			
//			switch (opDetail.getOp()) {
//				case ADD:
//					queryFolder.addQueries(savedQueries);
//					break;
//				
//				case UPDATE:
//					queryFolder.updateQueries(savedQueries);
//					break;
//				
//				case REMOVE:
//					queryFolder.removeQueries(savedQueries);
//					break;				
//			}
//			
//			daoFactory.getQueryFolderDao().saveOrUpdate(queryFolder);			
//
//			List<SavedQuerySummary> result = queryFolder.getSavedQueries().stream()
//				.map(SavedQuerySummary::fromSavedQuery)
//				.collect(Collectors.toList());
//			return ResponseEvent.response(result);
//		} catch (Exception e) {
//			return ResponseEvent.serverError(e);
//		}
//	}
//	
//	@Override
//	@PlusTransactional
//	public ResponseEvent<List<UserSummary>> shareFolder(RequestEvent<ShareQueryFolderOp> req) {
//		try {
//			ShareQueryFolderOp opDetail = req.getPayload();
//			
//			Long folderId = opDetail.getFolderId();
//			QueryFolder queryFolder = daoFactory.getQueryFolderDao().getQueryFolder(folderId);
//			if (queryFolder == null) {
//				return ResponseEvent.userError(SavedQueryErrorCode.FOLDER_NOT_FOUND);
//			}
//			
//			User user = AuthUtil.getCurrentUser();
//			if (!user.isAdmin() && !queryFolder.getOwner().equals(user)) {
//				return ResponseEvent.userError(SavedQueryErrorCode.OP_NOT_ALLOWED);
//			}
//			
//			
//			List<User> users = null;
//			List<Long> userIds = opDetail.getUserIds();
//			if (userIds == null || userIds.isEmpty()) {
//				users = new ArrayList<>();
//			} else {
//				users = userDao.getUsersByIds(userIds);
//			}
//			
//			Collection<User> newUsers = null; 
//			switch (opDetail.getOp()) {
//				case ADD:
//					newUsers = queryFolder.addSharedUsers(users);
//					break;
//					
//				case UPDATE:
//					newUsers = queryFolder.updateSharedUsers(users);
//					break;
//					
//				case REMOVE:
//					queryFolder.removeSharedUsers(users);
//					break;					
//			}
//											
//			daoFactory.getQueryFolderDao().saveOrUpdate(queryFolder);			
//			List<UserSummary> result = queryFolder.getSharedWith().stream()
//				.map(UserSummary::from)
//				.collect(Collectors.toList());
//
//			if (newUsers != null && !newUsers.isEmpty()) {
//				notifyFolderShared(user, queryFolder, newUsers);
//			}
//			
//			return ResponseEvent.response(result);
//		} catch (Exception e) {
//			return ResponseEvent.serverError(e);
//		}
//	}
//
//	@Override
//	@PlusTransactional
//	public ResponseEvent<Long> getAuditLogsCount(RequestEvent<ListQueryAuditLogsCriteria> req) {
//		try {
//			ListQueryAuditLogsCriteria crit = req.getPayload();
//			if (!AuthUtil.isAdmin() && !AuthUtil.isInstituteAdmin()) {
//				crit.userId(AuthUtil.getCurrentUser().getId());
//			} else if (AuthUtil.isInstituteAdmin()) {
//				crit.instituteId(AuthUtil.getCurrentUserInstitute().getId());
//			}
//
//			return ResponseEvent.response(daoFactory.getQueryAuditLogDao().getLogsCount(crit));
//		} catch (OpenSpecimenException ose) {
//			return ResponseEvent.error(ose);
//		} catch (Exception e) {
//			return ResponseEvent.serverError(e);
//		}
//	}
//
//	@Override
//    @PlusTransactional
//    public ResponseEvent<List<QueryAuditLogSummary>> getAuditLogs(RequestEvent<ListQueryAuditLogsCriteria> req){
//		try {
//			ListQueryAuditLogsCriteria crit = req.getPayload();
//			if (!AuthUtil.isAdmin() && !AuthUtil.isInstituteAdmin()) {
//				crit.userId(AuthUtil.getCurrentUser().getId());
//			} else if (AuthUtil.isInstituteAdmin()) {
//				crit.instituteId(AuthUtil.getCurrentUserInstitute().getId());
//			}
//
//			List<QueryAuditLog> logs = daoFactory.getQueryAuditLogDao().getLogs(crit);
//			return ResponseEvent.response(QueryAuditLogSummary.from(logs));
//		} catch (OpenSpecimenException ose) {
//			return ResponseEvent.error(ose);
//		} catch (Exception e) {
//			return ResponseEvent.serverError(e);
//		}
//    }
//    
//    @Override
//    @PlusTransactional
//    public ResponseEvent<QueryAuditLogDetail> getAuditLog(RequestEvent<Long> req) {
//		try {
//			Long auditLogId = req.getPayload();
//			QueryAuditLog log = daoFactory.getQueryAuditLogDao().getById(auditLogId);
//			if (log == null) {
//				return ResponseEvent.userError(SavedQueryErrorCode.AUDIT_LOG_NOT_FOUND, auditLogId);
//			}
//
//			return ResponseEvent.response(QueryAuditLogDetail.from(log));
//		} catch (OpenSpecimenException ose) {
//			return ResponseEvent.error(ose);
//		} catch (Exception e) {
//			return ResponseEvent.serverError(e);
//		}
//    }
//    
//	@Override
//	@PlusTransactional
//	public ResponseEvent<String> getQueryDef(RequestEvent<Long> req) {
//		try {
//			SavedQueryDao queryDao = daoFactory.getSavedQueryDao();
//			
//			Long queryId = req.getPayload();			
//			SavedQuery query = queryDao.getQuery(queryId);			
//			if (query == null) {
//				return ResponseEvent.userError(SavedQueryErrorCode.NOT_FOUND, queryId);
//			}
//			
//			User user = AuthUtil.getCurrentUser();
//			if (!query.getCreatedBy().equals(user) && 
//					!queryDao.isQuerySharedWithUser(queryId, user.getId())) {
//				return ResponseEvent.userError(SavedQueryErrorCode.OP_NOT_ALLOWED);
//			}
//			
//			String queryDef = query.getQueryDefJson(true);
//			return ResponseEvent.response(queryDef);			
//		} catch (Exception e) {
//			return ResponseEvent.serverError(e);
//		}
//	}
//
//	@Override
//	@PlusTransactional
//	public QueryDataExportResult exportQueryData(final ExecuteQueryEventOp opDetail, final ExportProcessor processor) {
//		return exportData(opDetail, processor, null);
//	}
//
//	@Override
//	@PlusTransactional
//	public QueryDataExportResult exportQueryData(final ExecuteQueryEventOp opDetail, BiConsumer<QueryResultData, OutputStream> qdConsumer) {
//		return exportData(opDetail, null, qdConsumer);
//	}
//
//	@Override
//	@PlusTransactional
//	public ResponseEvent<List<FacetDetail>> getFacetValues(RequestEvent<GetFacetValuesOp> req) {
//		try {
//			GetFacetValuesOp op = req.getPayload();
//			List<FacetDetail> result = op.getFacets().stream()
//				.map(facet -> getFacetDetail(op.getCpId(), op.getCpGroupId(), facet, op.getRestriction(), op.getSearchTerm()))
//				.collect(Collectors.toList());
//			return ResponseEvent.response(result);
//		} catch (OpenSpecimenException ose) {
//			return ResponseEvent.error(ose);
//		} catch (Exception e) {
//			return ResponseEvent.serverError(e);
//		}
//	}
//
//	@Override
//	public String insertCustomQueryForms(String dirName) {
//		StringBuilder templates = new StringBuilder();
//		try {
//			PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(getClass().getClassLoader());
//			Resource[] resources = resolver.getResources("classpath*:/query-forms/" + dirName + "/*.xml");
//
//			for (Resource resource : resources) {
//				String filename = "query-forms/" + dirName + "/" + resource.getFilename();
//				templates.append(templateService.render(filename, new HashMap<>()));
//			}
//		} catch (FileNotFoundException fe) {
//			logger.debug("Error rendering custom query forms", fe);
//		} catch (Exception e) {
//			logger.error("Error rendering custom query forms", e);
//		}
//		
//		return templates.toString();
//	}
//		
//	private SavedQuery getSavedQuery(SavedQueryDetail detail) {
//		SavedQuery savedQuery = new SavedQuery();		
//		savedQuery.setTitle(detail.getTitle());
//		savedQuery.setCpId(detail.getCpId());
//		savedQuery.setCpGroupId(detail.getCpGroupId());
//		savedQuery.setSelectList(detail.getSelectList());
//		savedQuery.setFilters(detail.getFilters());
//		savedQuery.setSubQueries(Arrays.stream(detail.getFilters())
//			.map(Filter::getSubQueryId)
//			.filter(Objects::nonNull)
//			.collect(Collectors.toSet()));
//		savedQuery.setQueryExpression(detail.getQueryExpression());
//		if (detail.getId() == null) {
//			savedQuery.setCreatedBy(AuthUtil.getCurrentUser());
//		}
//		
//		savedQuery.setLastUpdatedBy(AuthUtil.getCurrentUser());
//		savedQuery.setLastUpdated(Calendar.getInstance().getTime());
//		savedQuery.setHavingClause(detail.getHavingClause());
//		savedQuery.setReporting(detail.getReporting());
//		savedQuery.setWideRowMode(detail.getWideRowMode());
//		savedQuery.setOutputColumnExprs(detail.isOutputColumnExprs());
//		return savedQuery;
//	}
//
//	private String getAql(SavedQueryDetail queryDetail) {
//		return AqlBuilder.getInstance().getQuery(
//			queryDetail.getSelectList(),
//			queryDetail.getFilters(),
//			queryDetail.getQueryExpression(),
//			queryDetail.getHavingClause());
//	}
//
//	private Query getQuery(ExecuteQueryEventOp op) {
//		boolean countQuery = "Count".equals(op.getRunType());
//		User user = AuthUtil.getCurrentUser();
//
//		String rootForm = cprForm;
//		if (StringUtils.isNotBlank(op.getDrivingForm())) {
//			rootForm = op.getDrivingForm();
//		}
//
//		TimeZone tz = AuthUtil.getUserTimeZone();
//		Query query = Query.createQuery()
//			.wideRowMode(WideRowMode.valueOf(op.getWideRowMode()))
//			.ic(true)
//			.outputIsoDateTime(op.isOutputIsoDateTime())
//			.outputExpression(op.isOutputColumnExprs())
//			.dateFormat(ConfigUtil.getInstance().getDeDateFmt())
//			.timeFormat(ConfigUtil.getInstance().getTimeFmt())
//			.timeZone(tz != null ? tz.getID() : null);
//		query.compile(rootForm, op.getAql());
//
//		String aql = op.getAql();
//		if (query.isPhiResult(true) && !AuthUtil.isAdmin()) {
//			if (query.isAggregateQuery() || StringUtils.isNotBlank(query.getResultProcessorName())) {
//				throw OpenSpecimenException.userError(SavedQueryErrorCode.PHI_NOT_ALLOWED_IN_AGR);
//			}
//
//			aql = getAqlWithCpIdInSelect(user, countQuery, aql);
//		}
//
//		query.compile(rootForm, aql, getRestriction(user, op.getCpId(), op.getCpGroupId()));
//		return query;
//	}
//
//	private QueryResultScreener getResultScreener(Query query) {
//		if (query.isPhiResult(true) && !AuthUtil.isAdmin()) {
//			return new QueryResultScreenerImpl(AuthUtil.getCurrentUser(), false);
//		}
//
//		return null;
//	}
//
//	private String getRestriction(User user, Long cpId, Long groupId) {
//		if (groupId != null && groupId != -1) {
//			Set<Long> cpIds = AccessCtrlMgr.getInstance().getReadAccessGroupCpIds(groupId);
//			if (CollectionUtils.isEmpty(cpIds)) {
//				throw OpenSpecimenException.userError(RbacErrorCode.ACCESS_DENIED);
//			}
//
//			return cpForm + ".id in (" + Utility.join(cpIds, Objects::toString, ",") + ")";
//		} else if (cpId != null && cpId != -1) {
//			if (!user.isAdmin()) {
//				AccessCtrlMgr.getInstance().ensureReadCpRights(cpId);
//			}
//
//			return cpForm + ".id = " + cpId;
//		} else if (!user.isAdmin()) {
//			Set<SiteCpPair> siteCps = AccessCtrlMgr.getInstance().getReadableSiteCps();
//			if (CollectionUtils.isEmpty(siteCps)) {
//				throw OpenSpecimenException.userError(RbacErrorCode.ACCESS_DENIED);
//			}
//
//			List<String> cpSitesConds = new ArrayList<>(); // joined by or
//			for (SiteCpPair siteCp : siteCps) {
//				String cond = getAqlSiteIdRestriction("CollectionProtocol.cpSites.siteId", siteCp);
//				if (siteCp.getCpId() != null) {
//					cond += " and CollectionProtocol.id = " + siteCp.getCpId();
//				}
//
//				cpSitesConds.add("(" + cond + ")");
//			}
//
//			return "(" + StringUtils.join(cpSitesConds, " or ") + ")";
//		} else {
//			return null;
//		}
//	}
//	
//	private String getAqlWithCpIdInSelect(User user, boolean isCount, String aql) {
//		if (user.isAdmin() || isCount) {
//			return aql;
//		} else {
//			aql = aql.trim();
//			Matcher matcher = SELECT_PATTERN.matcher(aql);
//			if (matcher.matches()) {
//				String select = matcher.group(1);
//				return select + " " + cpForm + ".id, " + aql.substring(select.length());
//			} else {
//				String afterSelect = aql.trim().substring("select".length());
//				return "select " + cpForm + ".id, " + afterSelect;
//			}
//		}
//	}
//	
//	private static int getThreadPoolSize() {
//		return 5; // TODO: configurable property
//	}
//	
//	private static String getExportDataDir() {
//		String dir = new StringBuilder()
//			.append(ConfigUtil.getInstance().getDataDir()).append(File.separator)
//			.append("query-exported-data").append(File.separator)
//			.toString();
//		
//		File dirFile = new File(dir);
//		if (!dirFile.exists()) {
//			if (!dirFile.mkdirs()) {
//				throw new RuntimeException("Error couldn't create directory for exporting query data");
//			}
//		}
//		
//		return dir;
//	}
//
//	private void insertAuditLog(User user, ExecuteQueryEventOp opDetail, QueryResponse resp) {
//		SavedQuery query = null;
//		if (opDetail.getSavedQueryId() != null) {
//			query = new SavedQuery();
//			query.setId(opDetail.getSavedQueryId());
//		}
//
//		QueryAuditLog auditLog = new QueryAuditLog();
//		auditLog.setQuery(query);
//		auditLog.setRunBy(user);
//		auditLog.setTimeOfExecution(resp.getTimeOfExecution());
//		auditLog.setTimeToFinish(resp.getExecutionTime());
//		auditLog.setRunType(opDetail.getRunType());
//		auditLog.setSql(resp.getSql());
//		daoFactory.getQueryAuditLogDao().saveOrUpdate(auditLog);
//	}
//	
//	private class QueryResultScreenerImpl implements QueryResultScreener {
//		private User user;
//		
//		private boolean countQuery;
//
//		private String mask;
//		
//		private Map<Long, ParticipantReadAccess> phiAccessMap = new HashMap<Long, ParticipantReadAccess>();
//		
//		private static final String MASK_MARKER = "##########";
//		
//		public QueryResultScreenerImpl(User user, boolean countQuery) {
//			this(user, countQuery, null);
//		}
//
//		public QueryResultScreenerImpl(User user, boolean countQuery, String mask) {
//			this.user = user;
//			this.countQuery = countQuery;
//			this.mask = (mask == null) ? MASK_MARKER : mask;
//		}
//
//		@Override
//		public List<ResultColumn> getScreenedResultColumns(List<ResultColumn> preScreenedResultCols) {
//			if (user.isAdmin() || this.countQuery) {
//				return preScreenedResultCols;
//			}
//			
//			List<ResultColumn> result = new ArrayList<ResultColumn>(preScreenedResultCols);
//			result.remove(0);
//			return result;
//		}
//
//		@Override
//		public Object[] getScreenedRowData(List<ResultColumn> preScreenedResultCols, Object[] rowData) {
//			if (user.isAdmin() || this.countQuery || rowData.length == 0) {
//				return rowData;
//			}
//						
//			Long cpId = ((Number)rowData[0]).longValue();
//			Object[] screenedData = ArrayUtils.remove(rowData, 0);
//			
//			ParticipantReadAccess access = phiAccessMap.get(cpId);
//			if (access == null) {
//				access = AccessCtrlMgr.getInstance().getParticipantReadAccess(cpId);
//				phiAccessMap.put(cpId, access);
//			}
//			
//			if (access.phiAccess) {
//				return screenedData;
//			}
//			
//			int i = 0; 
//			boolean first = true;
//			for (ResultColumn col : preScreenedResultCols) {
//				if (first) {
//					first = false;
//					continue;
//				}
//				
//				if (col.isSimpleExpr() && col.isPhi()) {
//					screenedData[i] = mask;
//				}
//				
//				++i;
//			}
//			
//			return screenedData;
//		}		
//	}
//	
//	private static void initExportFileCleaner() {
//		long currentTime = Calendar.getInstance().getTime().getTime();
//		
//		Calendar nextDayStart = Calendar.getInstance();
//		nextDayStart.set(Calendar.HOUR, 0);
//		nextDayStart.set(Calendar.MINUTE, 0);
//		nextDayStart.set(Calendar.SECOND, 0);
//		nextDayStart.set(Calendar.MILLISECOND, 0);
//		nextDayStart.add(Calendar.DATE, 1);
//				
//		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
//				new Runnable() {					
//					@Override
//					public void run() {
//						try {
//							Calendar cal = Calendar.getInstance();
//							cal.add(Calendar.DAY_OF_MONTH, -30);
//							long deleteBeforeTime = cal.getTimeInMillis();
//							
//							File dir = new File(getExportDataDir());							
//							for (File file : dir.listFiles()) {								
//								if (file.lastModified() <= deleteBeforeTime) {
//									file.delete();
//								}
//							}
//						} catch (Exception e) {
//							
//						}						
//					}
//				},
//				nextDayStart.getTimeInMillis() - currentTime, 
//				24 * 60 * 60 * 1000, 
//				TimeUnit.MILLISECONDS);						
//	}
//	
//	private void sendQueryDataExportedEmail(User user, SavedQuery query, String filename) {
//		String title = query != null ? query.getTitle() : "Unsaved query";
//		Map<String, Object> props = new HashMap<>();
//		props.put("user", user);
//		props.put("query", query);
//		props.put("filename", filename);
//		props.put("appUrl", ConfigUtil.getInstance().getAppUrl());
//		props.put("$subject", new String[] {title});
//		
//		emailService.sendEmail(QUERY_DATA_EXPORTED_EMAIL_TMPL, new String[] {user.getEmailAddress()}, props);
//	}
//
//	private void notifyQueryDataExported(User user, SavedQuery query, String filename) {
//		String[] params = {query != null ? query.getTitle() : "Unsaved query"};
//		String msg = MessageUtil.getInstance().getMessage(QUERY_DATA_EXPORTED_EMAIL_TMPL + "_subj", params);
//
//		Notification notif = new Notification();
//		notif.setEntityId(query != null ? query.getId() : -1L);
//		notif.setEntityType("query");
//		notif.setOperation("EXPORT");
//		notif.setCreatedBy(AuthUtil.getCurrentUser());
//		notif.setCreationTime(Calendar.getInstance().getTime());
//		notif.setMessage(msg);
//
//		String url = ConfigUtil.getInstance().getAppUrl() + "/rest/ng/query/export?fileId=" + filename;
//		NotifUtil.getInstance().notify(notif, Collections.singletonMap(url, Collections.singleton(user)));
//	}
//	
//	private void notifyFolderShared(User sharedBy, QueryFolder folder, Collection<User> sharedUsers) {
//		String[] subjParams = {folder.getName()};
//
//		//
//		// Step 1: Send email notifications
//		//
//		Map<String, Object> props = new HashMap<>();
//		props.put("user", sharedBy);
//		props.put("folder", folder);
//		props.put("appUrl", ConfigUtil.getInstance().getAppUrl());
//		props.put("$subject", subjParams);
//		
//		for (User sharedWith : sharedUsers) {
//			props.put("sharedWith", sharedWith);
//			emailService.sendEmail(SHARE_QUERY_FOLDER_EMAIL_TMPL, new String[] {sharedWith.getEmailAddress()}, props);
//		}
//
//		//
//		// Step 2: Add UI notifications
//		//
//		Notification notif = new Notification();
//		notif.setOperation("UPDATE");
//		notif.setEntityType(QueryFolder.getEntityName());
//		notif.setEntityId(folder.getId());
//		notif.setCreationTime(Calendar.getInstance().getTime());
//		notif.setCreatedBy(AuthUtil.getCurrentUser());
//		notif.setMessage(MessageUtil.getInstance().getMessage(SHARE_QUERY_FOLDER_EMAIL_TMPL + "_subj", subjParams));
//		NotifUtil.getInstance().notify(notif, Collections.singletonMap("folder-queries", sharedUsers));
//	}
//
//	private FacetDetail getFacetDetail(Long cpId, Long cpGroupId, String facet, String restriction, String searchTerm) {
//		String[] fieldParts = facet.split("\\.");
//		String rootForm = fieldParts[0];
//
//		int idx = fieldParts.length - 1;
//		while (idx >= 0) {
//			if (fieldParts[idx].equals("extensions") || fieldParts[idx].equals("customFields")) {
//				break;
//			}
//
//			--idx;
//		}
//
//		if ((idx + 2) >= fieldParts.length) {
//			throw new IllegalArgumentException("Invalid facet: " + facet);
//		}
//
//		String formName = null, fieldName = null;
//		if (idx == -1) {
//			formName = fieldParts[0];
//			fieldName = StringUtils.join(fieldParts, ".", 1, fieldParts.length);
//		} else {
//			formName = fieldParts[idx + 1];
//			fieldName = StringUtils.join(fieldParts, ".", idx + 2, fieldParts.length);
//		}
//
//		Container form = Container.getContainer(formName);
//		if (form == null) {
//			throw new IllegalArgumentException("Invalid facet: " + facet);
//		}
//
//		Control field = form.getControlByUdn(fieldName, "\\.");
//		if (field == null) {
//			throw new IllegalArgumentException("Invalid facet: " + facet);
//		}
//
//		String aqlFmt = "select distinct %s %s where %s %s limit 0, 500";
//		List<Object> aqlFmtArgs = new ArrayList<>();
//
//		QueryResultScreener screener = null;
//		if (!AuthUtil.isAdmin() && field.isPhi()) {
//			aqlFmtArgs.add(cpForm + ".id, ");
//			screener = new QueryResultScreenerImpl(AuthUtil.getCurrentUser(), false, "");
//		} else {
//			aqlFmtArgs.add("");
//		}
//
//		aqlFmtArgs.add(facet);
//
//		String searchTermCond = facet;
//		if (StringUtils.isNotBlank(searchTerm)) {
//			searchTermCond += " contains \"" + searchTerm.trim() + "\"";
//		} else {
//			searchTermCond += " exists";
//		}
//		aqlFmtArgs.add(searchTermCond);
//
//		String restrictionCond = "";
//		if (StringUtils.isNotBlank(restriction)) {
//			restrictionCond = " and (" + restriction + ")";
//			rootForm = cprForm;
//		}
//		aqlFmtArgs.add(restrictionCond);
//
//		TimeZone tz = AuthUtil.getUserTimeZone();
//		String aql = String.format(aqlFmt, aqlFmtArgs.toArray());
//		Query query = Query.createQuery()
//			.ic(true)
//			.dateFormat(ConfigUtil.getInstance().getDeDateFmt())
//			.timeFormat(ConfigUtil.getInstance().getTimeFmt())
//			.timeZone(tz != null ? tz.getID() : null)
//			.wideRowMode(WideRowMode.OFF);
//		query.compile(rootForm, aql, getRestriction(AuthUtil.getCurrentUser(), cpId, cpGroupId));
//
//		QueryResponse queryResp = query.getData();
//		QueryResultData queryResult = queryResp.getResultData();
//		queryResult.setScreener(screener);
//
//		Collection<Object> values = new TreeSet<>();
//		for (Object[] row : queryResult.getRows()) {
//			if (row[0] != null && !row[0].toString().isEmpty()) {
//				values.add(row[0]);
//			}
//		}
//
//		String[] columnLabels = queryResp.getResultData().getColumnLabels()[0].split("#");
//		FacetDetail result = new FacetDetail();
//		result.setExpr(facet);
//		result.setCaption(columnLabels[columnLabels.length - 1]);
//		result.setValues(values);
//		return result;
//	}
//
//	private void refreshConfig() {
//		maxConcurrentQueries = cfgService.getIntSetting(CFG_MOD, MAX_CONCURRENT_QUERIES, DEF_MAX_CONCURRENT_QUERIES);
//		maxRecsInMemory = cfgService.getIntSetting(CFG_MOD, MAX_RECS_IN_MEM, DEF_MAX_RECS_IN_MEM);
//		DeConfiguration.getInstance().maxCacheElementsInMemory(maxRecsInMemory);
//	}
//
//	private boolean incConcurrentQueriesCnt() {
//		while (true) {
//			int current = concurrentQueriesCnt.get();
//			if (current >= maxConcurrentQueries) {
//				throw OpenSpecimenException.userError(SavedQueryErrorCode.TOO_BUSY);
//			}
//
//			if (concurrentQueriesCnt.compareAndSet(current, current + 1)) {
//				break;
//			}
//		}
//
//		return true;
//	}
//
//	private int decConcurrentQueriesCnt() {
//		return concurrentQueriesCnt.decrementAndGet();
//	}
//
//	private String getExportFilename(ExecuteQueryEventOp op, ExportProcessor proc) {
//		String filename = null;
//		if (proc != null) {
//			filename = proc.filename();
//		}
//
//		if (StringUtils.isBlank(filename)) {
//			Long queryId = op.getSavedQueryId();
//			if (queryId == null) {
//				queryId = 0L; // to indicate unsaved query
//			}
//
//			filename = "query_" + queryId + "_" + UUID.randomUUID().toString();
//		}
//
//		return filename;
//	}
//
//	private SavedQueryErrorCode getErrorCode(QueryException.Code error) {
//		if (error == QueryException.Code.CYCLES_IN_QUERY) {
//			return SavedQueryErrorCode.CYCLES_IN_QUERY;
//		}
//
//		return SavedQueryErrorCode.MALFORMED;
//	}
//
//	private QueryDataExportResult exportData(ExecuteQueryEventOp op, ExportProcessor proc, BiConsumer<QueryResultData, OutputStream> procFn) {
//		OutputStream out = null;
//
//		try {
//			if (proc == null) {
//				proc = new DefaultQueryExportProcessor(op.getCpId(), op.getCpGroupId());
//			}
//
//			ExportQueryDataTask task = new ExportQueryDataTask();
//			task.op = op;
//			task.auth = AuthUtil.getAuth();
//			task.user = AuthUtil.getCurrentUser();
//			task.filename = getExportFilename(op, procFn == null ? proc : null);
//			task.query = getQuery(op);
//			task.procFn = procFn;
//			task.fout = new FileOutputStream(getExportDataDir() + File.separator + task.filename);
//			out = task.fout;
//
//			if (procFn == null) {
//				proc.headers(out);
//			}
//
//			Future<Boolean> promise = null;
//			boolean completed;
//			if (op.isSynchronous()) {
//				completed = task.call();
//			} else {
//				promise = exportThreadPool.submit(task);
//				try {
//					out = null;
//					completed = promise.get(ONLINE_EXPORT_TIMEOUT_SECS, TimeUnit.SECONDS);
//					out = task.fout;
//				} catch (TimeoutException te) {
//					completed = false;
//				}
//			}
//
//			return QueryDataExportResult.create(task.filename, completed, promise);
//		} catch (OpenSpecimenException ose) {
//			throw ose;
//		} catch (Exception e) {
//			throw OpenSpecimenException.serverError(e);
//		} finally {
//			if (out != null) {
//				IOUtils.closeQuietly(out);
//			}
//		}
//	}
//
//	private class ExportQueryDataTask implements Callable<Boolean> {
//		private Authentication auth;
//
//		private User user;
//
//		private String filename;
//
//		private OutputStream fout;
//
//		private Query query;
//
//		private ExecuteQueryEventOp op;
//
//		private BiConsumer<QueryResultData, OutputStream> procFn;
//
//		@Override
//		@PlusTransactional
//		public Boolean call() {
//			SecurityContextHolder.getContext().setAuthentication(auth);
//
//			try {
//				QueryResponse resp;
//				if (procFn == null) {
//					QueryResultExporter exporter = new QueryResultCsvExporter(Utility.getFieldSeparator());
//					resp = exporter.export(fout, query, getResultScreener(query));
//				} else {
//					resp = query.getData();
//					QueryResultData data = resp.getResultData();
//					data.setScreener(getResultScreener(query));
//					procFn.accept(data, fout);
//				}
//
//				insertAuditLog(user, op, resp);
//				notifyExportCompleted();
//			} catch (Exception e) {
//				logger.error("Error exporting query data", e);
//				throw OpenSpecimenException.serverError(e);
//			} finally {
//				IOUtils.closeQuietly(fout);
//			}
//
//			return true;
//		}
//
//		private void notifyExportCompleted() {
//			try {
//				if (op.isSynchronous()) {
//					return;
//				}
//
//				User user = userDao.getById(AuthUtil.getCurrentUser().getId());
//				if (user.isSysUser()) {
//					return;
//				}
//
//				SavedQuery savedQuery = null;
//				Long queryId = op.getSavedQueryId();
//				if (queryId != null) {
//					savedQuery = daoFactory.getSavedQueryDao().getQuery(queryId);
//				}
//				sendQueryDataExportedEmail(user, savedQuery, filename);
//				notifyQueryDataExported(user, savedQuery, filename);
//			} catch (Exception e) {
//				logger.error("Error sending email with query exported data", e);
//			}
//		}
//	}
//
//	private String getAqlSiteIdRestriction(String property, SiteCpPair siteCp) {
//		if (siteCp.getSiteId() != null) {
//			return property + " = " + siteCp.getSiteId();
//		} else {
//			return property + " in " + sql(String.format(INSTITUTE_SITE_IDS_SQL, siteCp.getInstituteId()));
//		}
//	}
//
//	private String sql(String sql) {
//		return "sql(\"" + sql + "\")";
//	}
//
//	private static final String INSTITUTE_SITE_IDS_SQL =
//		"select identifier from catissue_site where institute_id = %d and activity_status != 'Disabled'";
//}