package com.webpagebytes.cms.datautility;

import java.io.IOException;

import java.io.InputStream;
import java.util.Map;

public interface WPBCloudFileStorage {
	public void storeFile(InputStream is, WPBCloudFile file) throws IOException;
	public WPBCloudFileInfo getFileInfo(WPBCloudFile file) throws IOException;
	public boolean deleteFile(WPBCloudFile file) throws IOException;
	public InputStream getFileContent(WPBCloudFile file) throws IOException;
	public void updateFileCustomProperties(WPBCloudFile file, Map<String, String> customProps) throws IOException;
	public void updateContentType(WPBCloudFile file, String contentType) throws IOException;	
	public String getPublicFileUrl(WPBCloudFile file);	
}
