package com.webpagebytes.cms.datautility;

import com.webpagebytes.cms.exception.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface WPBAdminDataStorage {

	static enum AdminQueryOperator{
		LESS_THAN,
		GREATER_THAN,
		EQUAL,
		NOT_EQUAL,
		LESS_THAN_OR_EQUAL,
		GREATER_THAN_OR_EQUAL		
	};
	enum AdminSortOperator {
		NO_SORT,
		ASCENDING,
		DESCENDING
	};
	public void delete(String recordid, Class dataClass) throws WPBIOException;
	
	public void delete(Long recordid, Class dataClass) throws WPBIOException;
	
	public void delete(Class dataClass, String property, AdminQueryOperator operator, Object parameter) throws WPBIOException;
	
	public<T> List<T> getAllRecords(Class dataClass) throws WPBIOException;
	
	public<T> List<T> getAllRecords(Class dataClass, String property, AdminSortOperator operator) throws WPBIOException;

	public<T> T add(T t) throws WPBIOException;
	
	public<T> T addWithKey(T t) throws WPBIOException;
	
	public<T> T get(Long dataid, Class dataClass) throws WPBIOException;
	
	public<T> T get(String dataid, Class dataClass) throws WPBIOException;
	
	public<T> T update(T data) throws WPBIOException;
	
	public<T> List<T> query(Class dataClass, String property, AdminQueryOperator operator, Object parameter) throws WPBIOException;
	
	public<T> List<T> queryEx(Class dataClass, Set<String> propertyNames, Map<String, AdminQueryOperator> operators, Map<String, Object> values) throws WPBIOException;

	public<T> List<T> queryWithSort(Class dataClass, String property, AdminQueryOperator operator, Object parameter, String sortProperty, AdminSortOperator sortOperator) throws WPBIOException;
	
	public<T> List<T> queryExWithSort(Class dataClass, Set<String> propertyNames, Map<String, AdminQueryOperator> operators, Map<String, Object> values, String sortProperty, AdminSortOperator sortOperator) throws WPBIOException;

	public void addStorageListener(WPBAdminDataStorageListener listener);
	
	public void removeStorageListener(WPBAdminDataStorageListener listener);
	
	public String getUploadUrl(String returnUrl);
	
	public void deleteAllRecords(Class dataClass) throws WPBIOException;
	
	public String getUniqueId();
}
