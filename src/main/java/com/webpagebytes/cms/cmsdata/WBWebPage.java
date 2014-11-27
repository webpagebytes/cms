package com.webpagebytes.cms.cmsdata;

import java.io.Serializable;
import java.util.Date;
import java.util.zip.CRC32;

import com.webpagebytes.cms.datautility.WPBAdminFieldKey;
import com.webpagebytes.cms.datautility.WPBAdminFieldStore;
import com.webpagebytes.cms.datautility.WPBAdminFieldTextStore;

public class WBWebPage implements Serializable {
	@WPBAdminFieldKey
	private Long privkey;

	@WPBAdminFieldStore
	private String name;

	@WPBAdminFieldStore
	private Date lastModified;

	@WPBAdminFieldTextStore
	private String htmlSource;
	
	@WPBAdminFieldStore
	private Integer isTemplateSource;
	
	@WPBAdminFieldStore
	private String contentType;
	
	@WPBAdminFieldStore
	private String externalKey;
	
	@WPBAdminFieldStore
	private Long hash;
	
	@WPBAdminFieldStore
	private String pageModelProvider;
	
	public Long getPrivkey() {
		return privkey;
	}

	
	public void setPrivkey(Long key) {
		this.privkey = key;
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
