package com.demo.core.plugin.de;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.demo.core.plugin.de.events.ExecuteQueryEventOp;
import com.demo.core.plugin.de.events.QueryExecResult;
import com.demo.core.plugin.de.services.QueryService;

@RestController
@RequestMapping("/query")
public class QueryController {
	
	@Autowired
	private QueryService querySvc;
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody	
	public QueryExecResult executeQuery(@RequestBody ExecuteQueryEventOp opDetail) {
		return querySvc.executeQuery(opDetail);
	}
}
