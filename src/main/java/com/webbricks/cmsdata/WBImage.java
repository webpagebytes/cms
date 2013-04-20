package com.webbricks.cmsdata;

import java.io.Serializable;
import java.util.Date;

import com.webbricks.datautility.AdminFieldKey;
import com.webbricks.datautility.AdminFieldStore;

public class WBImage implements Serializable {

	@AdminFieldKey
	private Long key;

	@AdminFieldStore
	private String name;

	@AdminFieldStore
	private Long externalKey;
	
	@AdminFieldStore
	private String blobKey;

	@AdminFieldStore
	private Integer enabled;
	
	@AdminFieldStore
	private Date lastModified;
	
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

	public Long getExternalKey() {
		return externalKey;
	}

	public void setExternalKey(Long externalKey) {
		this.externalKey = externalKey;
	}

	public String getBlobKey() {
		return blobKey;
	}

	public void setBlobKey(String blobKey) {
		this.blobKey = blobKey;
	}

	public Integer getEnabled() {
		return enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}
	
	
}
