package com.webbricks.cms;

import com.webbricks.datautility.AdminFieldKey;
import com.webbricks.datautility.AdminFieldStore;

public class DummyClientK {
	@AdminFieldKey
	protected Integer id;
	@AdminFieldStore
	protected int age;
	@AdminFieldStore
	private String name;
	@AdminFieldStore
	public long balance;
	@AdminFieldStore
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
