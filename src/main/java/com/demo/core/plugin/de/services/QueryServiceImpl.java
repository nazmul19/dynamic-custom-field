package com.demo.core.plugin.de.services;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.demo.core.common.DeException;
import com.demo.core.domain.User;
import com.demo.core.plugin.de.events.ExecuteQueryEventOp;
import com.demo.core.plugin.de.events.QueryExecResult;

import edu.common.dynamicextensions.query.Query;
import edu.common.dynamicextensions.query.QueryException;
import edu.common.dynamicextensions.query.QueryParserException;
import edu.common.dynamicextensions.query.QueryResponse;
import edu.common.dynamicextensions.query.QueryResultData;
import edu.common.dynamicextensions.query.QueryResultScreener;
import edu.common.dynamicextensions.query.WideRowMode;

@Component
public class QueryServiceImpl implements QueryService{
	private static final String cprForm = "Account"; 
	private static final String cpForm = "Account";
	private static final Pattern SELECT_PATTERN = Pattern.compile("^(select\\s+distinct|select)\\s+.*$");
	private int maxConcurrentQueries = 10;
	private AtomicInteger concurrentQueriesCnt = new AtomicInteger(0);
	
	
	@Override
	public QueryExecResult executeQuery(ExecuteQueryEventOp opDetail) {
		QueryResultData queryResult = null;

		boolean queryCntIncremented = false;
		try {
			queryCntIncremented = incConcurrentQueriesCnt();

			Query query = getQuery(opDetail);

			QueryResponse resp = query.getData();
			//insertAuditLog(AuthUtil.getCurrentUser(), opDetail, resp);
			
			queryResult = resp.getResultData();
			queryResult.setScreener(getResultScreener(query));
			
			Integer[] indices = null;
			if (opDetail.getIndexOf() != null && !opDetail.getIndexOf().isEmpty()) {
				indices = queryResult.getColumnIndices(opDetail.getIndexOf());
			}

			return 
				new QueryExecResult()
					.setColumnMetadata(queryResult.getColumnMetadata())
					.setColumnLabels(queryResult.getColumnLabels())
					.setColumnTypes(queryResult.getColumnTypes())
					.setColumnUrls(queryResult.getColumnUrls())
					.setRows(queryResult.getStringifiedRows())
					.setDbRowsCount(queryResult.getDbRowsCount())
					.setColumnIndices(indices);
			
		} catch (QueryParserException qpe) {
			throw new DeException("Parsing exception" + qpe.getMessage());
		} catch (QueryException qe) {
			throw new DeException(qe.getMessage());
		} catch (IllegalAccessError iae) {
			throw new DeException("OP_NOT_ALLOWED" + iae.getMessage());
		} catch (DeException de) {
			throw de;
		} catch (Exception e) {
			throw new DeException("Error:"+ e.getMessage());
		} finally {
			if (queryCntIncremented) {
				decConcurrentQueriesCnt();
			}

			if (queryResult != null) {
				try {
					queryResult.close();
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}
		}
	}
	
	private Query getQuery(ExecuteQueryEventOp op) {
		boolean countQuery = "Count".equals(op.getRunType());
		//User user = AuthUtil.getCurrentUser();
		User user = new User();
		user.setId(2L);

		String rootForm = cprForm;
		if (StringUtils.isNotBlank(op.getDrivingForm())) {
			rootForm = op.getDrivingForm();
		}

//		TimeZone tz = AuthUtil.getUserTimeZone();
		String dateFormat = "yyyy-MM-DD";
		String timeFormat = "hh:mm";
		
		Query query = Query.createQuery()
			.wideRowMode(WideRowMode.valueOf(op.getWideRowMode()))
			.ic(true)
			.outputIsoDateTime(op.isOutputIsoDateTime())
			.outputExpression(op.isOutputColumnExprs())
//			.dateFormat(ConfigUtil.getInstance().getDeDateFmt())
//			.timeFormat(ConfigUtil.getInstance().getTimeFmt())R
			.dateFormat(dateFormat)
			.timeFormat(timeFormat)
		//	.timeZone(tz != null ? tz.getID() : null);
			.timeZone(null);
		query.compile(rootForm, op.getAql());

		String aql = op.getAql();
//		if (query.isPhiResult(true) && !AuthUtil.isAdmin()) {
//			if (query.isAggregateQuery() || StringUtils.isNotBlank(query.getResultProcessorName())) {
//				throw OpenSpecimenException.userError(SavedQueryErrorCode.PHI_NOT_ALLOWED_IN_AGR);
//			}
//
//			aql = getAqlWithCpIdInSelect(user, countQuery, aql);
//		}
		
		aql = getAqlWithCpIdInSelect(user, countQuery, aql);

		query.compile(rootForm, aql, getRestriction(user, op.getCpId(), op.getCpGroupId()));
		return query;
	}
	
	private String getAqlWithCpIdInSelect(User user, boolean isCount, String aql) {
		return aql;
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
	}
	
	
	private String getRestriction(User user, Long cpId, Long groupId) {
		return null;
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
	}
	
	private boolean incConcurrentQueriesCnt() {
		while (true) {
			int current = concurrentQueriesCnt.get();
			if (current >= maxConcurrentQueries) {
				throw new DeException("QueryService too busy");
			}

			if (concurrentQueriesCnt.compareAndSet(current, current + 1)) {
				break;
			}
		}

		return true;
	}
	
	private QueryResultScreener getResultScreener(Query query) {
//		if (query.isPhiResult(true) && !AuthUtil.isAdmin()) {
//			return new QueryResultScreenerImpl(AuthUtil.getCurrentUser(), false);
//		}

		return null;
	}
	private int decConcurrentQueriesCnt() {
		return concurrentQueriesCnt.decrementAndGet();
	}

}
