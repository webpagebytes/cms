package com.webpagebytes.cms.datautility;

import java.io.IOException;

import java.io.InputStream;
import java.util.Map;

public interface WBCloudFileStorage {
	public void storeFile(InputStream is, WBCloudFile file) throws IOException;
	public WBCloudFileInfo getFileInfo(WBCloudFile file) throws IOException;
	public boolean deleteFile(WBCloudFile file) throws IOException;
	public InputStream getFileContent(WBCloudFile file) throws IOException;
	public byte[] getFileContent(WBCloudFile file, int startIndex, int endIndex) throws IOException;
	public void updateFileCustomProperties(WBCloudFile file, Map<String, String> customProps) throws IOException;
	public void updateContentType(WBCloudFile file, String contentType) throws IOException;	
	public String getPublicFileUrl(WBCloudFile file);	
}
