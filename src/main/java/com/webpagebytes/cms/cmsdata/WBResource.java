package com.webpagebytes.cms.cmsdata;

import java.io.Serializable;
import java.util.Date;

import com.webpagebytes.cms.datautility.WPBAdminFieldKey;
import com.webpagebytes.cms.datautility.WPBAdminFieldStore;

public class WBResource implements Serializable {
	public static final int URI_TYPE = 1;
	public static final int PAGE_TYPE = 2;
	public static final int PAGE_MODULE_TYPE = 3;
	public static final int MESSAGE_TYPE = 4;
	public static final int ARTICLE_TYPE = 5;
	public static final int FILE_TYPE = 6;
	public static final int GLOBAL_PARAMETER_TYPE = 7;
	

	@WPBAdminFieldKey
	private String rkey;
				
	@WPBAdminFieldStore
	private String name;
	
	@WPBAdminFieldStore
	private Integer type;

	public WBResource()
	{
		
	}
	public WBResource(String key, String name, int type)
	{
		this.rkey = key;
		this.name = name;
		this.type = type;
	}
	public String getRkey() {
		return rkey;
	}

	public void setRkey(String key) {
		this.rkey = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	
}
