package com.webbricks.datautility;

import java.util.HashMap;
import java.util.Map;

public interface WBCloudFileInfo {
	
	public WBCloudFile getCloudFile();
	public long getSize();
	public String getContentType();
	public void setContentType(String contentType);
	public Map<String, String> getCustomProperties();
	public void setCustomProperties(Map<String, String> customProperties);
	public void setProperty(String name, String value);
	public String getProperty(String name);
	public String getMd5();
	public long getCrc32();
	public long getCreationDate();
	
}
