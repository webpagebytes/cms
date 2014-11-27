package com.webpagebytes.cms;

import com.webpagebytes.cms.datautility.WPBAdminFieldKey;
import com.webpagebytes.cms.datautility.WPBAdminFieldStore;

public class DummyPrivateClientL {
	@WPBAdminFieldKey
	protected Long id;
	@WPBAdminFieldStore
	protected int age;
	@WPBAdminFieldStore
	private String name;
	@WPBAdminFieldStore
	private long balance;
	@WPBAdminFieldStore
	private byte binary;
	private Long getId() {
		return id;
	}
	private void setId(Long id) {
		this.id = id;
	}
	private int getAge() {
		return age;
	}
	private void setAge(int age) {
		this.age = age;
	}
	private String getName() {
		return name;
	}
	private void setName(String name) {
		this.name = name;
	}
	private long getBalance() {
		return balance;
	}
	private void setBalance(long balance) {
		this.balance = balance;
	}
	private byte getBinary() {
		return binary;
	}
	private void setBinary(byte binary) {
		this.binary = binary;
	}
}
