package com.webpagebytes.cms.appinterfaces;

import java.io.OutputStream;

public interface WBContentProvider {

	public boolean writeFileContent(String externalKey, OutputStream os);
	
	public boolean writePageContent(String externalKey, WBModel model, OutputStream os);
}
