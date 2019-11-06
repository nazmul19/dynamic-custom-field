/**
 * 
 */
package com.demo.core.plugin.de.services;

import com.demo.core.plugin.de.events.ExecuteQueryEventOp;
import com.demo.core.plugin.de.events.QueryExecResult;

/**
 * @author Nazmul Hassan
 *
 */
public interface QueryService {
	QueryExecResult executeQuery(ExecuteQueryEventOp op);
}
