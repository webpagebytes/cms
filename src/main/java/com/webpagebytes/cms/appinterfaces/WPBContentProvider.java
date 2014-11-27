package com.webpagebytes.cms.appinterfaces;

import java.io.OutputStream;

public interface WPBContentProvider {

	public boolean writeFileContent(String externalKey, OutputStream os);
	
	public boolean writePageContent(String externalKey, WPBModel model, OutputStream os);
}
