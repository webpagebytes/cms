/*
 *   Copyright 2014 Webpagebytes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.webpagebytes.cms.appinterfaces;

import com.webpagebytes.cms.datautility.WPBAdminDataStorageListener;
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
