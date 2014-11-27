package com.webpagebytes.cms.datautility;

public class WPBCloudFile {
	private String bucket;
	private String path;
	
	public WPBCloudFile(String bucket, String path)
	{
		this.bucket = bucket;
		this.path = path;
	}
	public String getPath()
	{
		return path;
	}
	public String getBucket()
	{
		return bucket;
	}

}
