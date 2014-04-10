package com.webpagebytes.cms.cmsdata;

import java.io.Serializable;
import java.util.Date;
import java.util.zip.CRC32;

import com.webpagebytes.cms.datautility.AdminFieldKey;
import com.webpagebytes.cms.datautility.AdminFieldStore;
import com.webpagebytes.cms.datautility.AdminFieldTextStore;

public class WBWebPage implements Serializable {
	@AdminFieldKey
	private Long key;

	@AdminFieldStore
	private String name;

	@AdminFieldStore
	private Date lastModified;

	@AdminFieldTextStore
	private String htmlSource;
	
	@AdminFieldStore
	private Integer isTemplateSource;
	
	@AdminFieldStore
	private String contentType;
	
	@AdminFieldStore
	private String externalKey;
	
	@AdminFieldStore
	private Long hash;
	
	@AdminFieldStore
	private String pageModelProvider;
	
	public Long getKey() {
		return key;
	}

	
	public void setKey(Long key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getHtmlSource() {
		return htmlSource;
	}

	public void setHtmlSource(String htmlSource) {
		this.htmlSource = htmlSource;
	}


	public Integer getIsTemplateSource() {
		return isTemplateSource;
	}


	public void setIsTemplateSource(Integer isTemplateSource) {
		this.isTemplateSource = isTemplateSource;
	}

	public String getExternalKey() {
		return externalKey;
	}

	public void setExternalKey(String externalKey) {
		this.externalKey = externalKey;
	}

	public String getContentType() {
		return contentType;
	}


	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Long getHash() {
		return hash;
	}

	public void setHash(Long hash) {
		this.hash = hash;
	}


	public String getPageModelProvider() {
		return pageModelProvider;
	}


	public void setPageModelProvider(String pageModelProvider) {
		this.pageModelProvider = pageModelProvider;
	}

	public static Long crc32(String str)
	{
		str = (str != null) ? str : "";
		CRC32 crc = new CRC32();
		crc.update(str.getBytes());
		return crc.getValue();
	}
	
}
