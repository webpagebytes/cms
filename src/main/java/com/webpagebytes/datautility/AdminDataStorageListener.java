package com.webpagebytes.datautility;

public interface AdminDataStorageListener<T> {

	enum AdminDataStorageOperation
	{
		CREATE,
		UPDATE,
		DELETE
	}
	public void notify (T t, AdminDataStorageOperation o);
}
