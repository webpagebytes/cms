package com.webbricks.datautility;

public interface WBBlobInfo {
	public String getBlobKey();
	public Long getSize();
	public String getFileName();
	public String getContentType();
	public Long getHash();
}
