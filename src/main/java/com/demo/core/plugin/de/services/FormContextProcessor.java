package com.demo.core.plugin.de.services;

import com.demo.core.plugin.de.domain.FormContextBean;

public interface FormContextProcessor {
	public void onSaveOrUpdate(FormContextBean formCtxt);

	public void onRemove(FormContextBean formCtxt);
}