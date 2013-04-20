package com.webbricks.cmsdata;
import com.webbricks.datautility.*;

import java.io.Serializable;
import java.util.Date;

public class WBUri implements Serializable {
	
	@AdminFieldKey
	private Long key;

	@AdminFieldStore
	private Integer version;
	
	@AdminFieldStore
	private Integer enabled;

	@AdminFieldStore
	private String uri;

	@AdminFieldStore
	private Long dataSourceKey;
	
	@AdminFieldStore
	private Date lastModified;
	
	@AdminFieldStore
	private String httpOperation;
	
	@AdminFieldStore
	private String controllerClass;
	
	@AdminFieldStore
	private String pageName;
	
	@AdminFieldStore
	private Long externalKey;
	
	@AdminFieldStore
	private Long pageKey;

	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getEnabled() {
		return enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Long getDataSourceKey() {
		return dataSourceKey;
	}

	public void setDataSourceKey(Long dataSourceKey) {
		this.dataSourceKey = dataSourceKey;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getHttpOperation() {
		return httpOperation;
	}

	public void setHttpOperation(String httpOperation) {
		this.httpOperation = httpOperation;
	}

	public String getControllerClass() {
		return controllerClass;
	}

	public void setControllerClass(String controllerClass) {
		this.controllerClass = controllerClass;
	}

	public Long getPageKey() {
		return pageKey;
	}

	public void setPageKey(Long pageKey) {
		this.pageKey = pageKey;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	
	public Long getExternalKey() {
		return externalKey;
	}

	public void setExternalKey(Long externalKey) {
		this.externalKey = externalKey;
	}

}
