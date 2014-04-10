package com.webpagebytes.datautility;

public class WBCloudFile {
	private String bucket;
	private String path;
	
	public WBCloudFile(String bucket, String path)
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
