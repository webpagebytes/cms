package com.webpagebytes.cms.engine;

import com.webpagebytes.cms.cmsdata.WPBAdminFieldKey;
import com.webpagebytes.cms.cmsdata.WPBAdminFieldStore;

public class DummyClientK {
	@WPBAdminFieldKey
	protected Integer id;
	@WPBAdminFieldStore
	protected int age;
	@WPBAdminFieldStore
	private String name;
	@WPBAdminFieldStore
	public long balance;
	@WPBAdminFieldStore
	public byte binary;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getBalance() {
		return balance;
	}
	public void setBalance(long balance) {
		this.balance = balance;
	}
	public byte getBinary() {
		return binary;
	}
	public void setBinary(byte binary) {
		this.binary = binary;
	}

}
