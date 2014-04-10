package com.webpagebytes.cms;

import com.webpagebytes.cms.datautility.AdminFieldKey;
import com.webpagebytes.cms.datautility.AdminFieldStore;
import com.webpagebytes.cms.datautility.AdminFieldTextStore;

public class DummyClientL {

	@AdminFieldKey
	protected Long id;
	@AdminFieldStore
	protected Integer age;
	@AdminFieldStore
	private String name;
	@AdminFieldStore
	public Long balance;
	@AdminFieldStore
	public byte binary;
	@AdminFieldTextStore
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
