package com.webpagebytes.cms.cmsdata;

import java.io.Serializable;
import java.util.Date;

import com.webpagebytes.cms.datautility.WPBAdminFieldKey;
import com.webpagebytes.cms.datautility.WPBAdminFieldStore;
import com.webpagebytes.cms.datautility.WPBAdminFieldTextStore;

public class WBArticle implements Serializable {
	@WPBAdminFieldKey
	private Long privkey;
	
	@WPBAdminFieldStore
	private Date lastModified;

	@WPBAdminFieldStore
	private String title;

	@WPBAdminFieldTextStore
	private String htmlSource;
		
	@WPBAdminFieldStore
	private String externalKey;

	public Long getPrivkey() {
		return privkey;
	}

	public void setPrivkey(Long key) {
		this.privkey = key;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getHtmlSource() {
		return htmlSource;
	}

	public void setHtmlSource(String htmlSource) {
		this.htmlSource = htmlSource;
	}

	public String getExternalKey() {
		return externalKey;
	}

	public void setExternalKey(String externalKey) {
		this.externalKey = externalKey;
	}

	
}
