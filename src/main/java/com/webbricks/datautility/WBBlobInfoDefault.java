package com.webbricks.datautility;

public class WBBlobInfoDefault implements WBBlobInfo {
	public String blobKey;
	public Long size;
	public String fileName;
	public String contentType;
	public Long hash;
	public WBBlobInfoDefault()
	{
		
	}
	public WBBlobInfoDefault(String blobKey, Long size, String fileName, String contentType, Long hash)
	{
		this.blobKey = blobKey;
		this.size = size;
		this.fileName = fileName;
		this.contentType = contentType;
		this.hash = hash;
	}
	
	public String getBlobKey()
	{
		return blobKey;
	}
	public Long getSize()
	{
		return size;
	}
	public String getFileName()
	{
		return fileName;
	}
	public String getContentType()
	{
		return contentType;
	}
	public Long getHash()
	{
		return hash;
	}

}
