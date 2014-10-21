package com.webpagebytes.cms.appinterfaces;

import java.util.Locale;

import com.webpagebytes.cms.exception.WBException;
import com.webpagebytes.cms.exception.WBLocaleException;

public interface WPBContentService {
	public WBModel createModel(String language, String country) throws WBException;
	public WBModel createModel() throws WBException;
	public WBContentProvider getContentProvider() throws WBException;
}
