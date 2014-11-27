package com.webpagebytes.cms;

import com.webpagebytes.cms.datautility.WPBAdminFieldKey;
import com.webpagebytes.cms.datautility.WPBAdminFieldStore;
import com.webpagebytes.cms.datautility.WPBAdminFieldTextStore;

public class DummyClientL {

	@WPBAdminFieldKey
	protected Long id;
	@WPBAdminFieldStore
	protected Integer age;
	@WPBAdminFieldStore
	private String name;
	@WPBAdminFieldStore
	public Long balance;
	@WPBAdminFieldStore
	public byte binary;
	@WPBAdminFieldTextStore
	public String summary;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getBalance() {
		return balance;
	}
	public void setBalance(Long balance) {
		this.balance = balance;
	}
	public byte getBinary() {
		return binary;
	}
	public void setBinary(byte binary) {
		this.binary = binary;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	
}
