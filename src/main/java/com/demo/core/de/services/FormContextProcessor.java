package com.demo.core.de.services;

import com.demo.core.domain.FormContextBean;

public interface FormContextProcessor {
	public void onSaveOrUpdate(FormContextBean formCtxt);

	public void onRemove(FormContextBean formCtxt);
}