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

package com.webpagebytes.cms;

import com.webpagebytes.cms.exception.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * WPBAdminDataStorage interface provides an abstract access layer to the CMS resources, exception is the site binary files content.
 * </p>
 * <p>
 * The CMS files binary content is stored and accessed through WPBCloudFileStorage interface.
 * </p>
 * <p>
 * An application that uses Webpagebytes CMS will have to use a concrete implementation of WPBAdminDataStorage in the application configuration file.
 * </p>
 * <p>
 * The CMS resources(site pages, site urls, page modules, site files, parameters) are identified by class name and member annotation of WPBAdminField, WPBAdminFieldKey, WPBAdminFieldStore.
 * For example site urls are represented as WPBUri class, site pages are represented WPBWebPage class and so on (see classes from com.webpagebytes.cms.cmsdata package).
 * </p>
 * <p>
 * An implementation of WPBAdminDataStorage should do the following for storing records in the data storage:
 * <ul>
 *     <li> take the object(for example this can be WPBUri) and enumerate through the members </li>
 *     <li> identify the members with the following annotations: WPBAdminFieldStore, WPBAdminFieldTextStore. These members will need to be stored in the database. </li>
 *     <li> store the record with the identified members in the database (the database can contain a table named WPBUri) </li>
 * </ul>
 * <p>
 * An implementation of WPBAdminDataStorage should do the following for retrieval of records from the data storage:
  * <ul>
 *     <li> identify the database table/collection from the Class parameter provided in the method parameter (for example WPBUri.class)   </li>
 *     <li> fetch the record(s) from the database (this can be to query all records from a table named WPBUri) </li>
 *     <li> create new object(s) with the type identified by the Class parameter (this can be to create a list of objects with type WPBUri) provided in the method parameter </li>
 *     <li> from the database records returned, identify the object members based on the annotations WPBAdminFieldKey, WPBAdminFieldStore, WPBAdminFieldTextStore and set their values in the objects created </li>
 *     <li> return the created object(s) </li>
 * </ul>
 * <p>
 * Webpagebytes CMS engine completely abstracts the data storage so that the content can be migrated very easy from a storage to another.
 * </p>
 * 
 */
public interface WPBAdminDataStorage {

	public static enum AdminQueryOperator{
		LESS_THAN,
		GREATER_THAN,
		EQUAL,
		NOT_EQUAL,
		LESS_THAN_OR_EQUAL,
		GREATER_THAN_OR_EQUAL		
	};
	public enum AdminSortOperator {
		NO_SORT,
		ASCENDING,
		DESCENDING
	};

	/**
	 * Initializes the data storage with parameters from the CMS configuration xml file that corresponds to wpbadmindatastorage section.
	 * @param params Map of keys and values representing configuration parameters
	 * @throws WPBIOException Exception
	 */
	public void initialize(Map<String, String> params) throws WPBIOException;
	/**
	 * Deletes a record with the provided record id and the resource class identifier.
	 * @param recordid Resource record id as String
	 * @param dataClass Resource class identifier (for example WPBUri.class, WPBWebPage.class)
	 * @param <T> Generic type of data (see com.webpagebytes.cms.cmsdata package)
	 * @throws WPBIOException Exception
	 */
	public<T> void delete(String recordid, Class<T> dataClass) throws WPBIOException;
	
	/**
     * Deletes a set of records that match a search criteria of type (property operator value)
     * @param dataClass Resource class identifier (for example WPBUri.class, WPBWebPage.class)
     * @param property Member field from dataClass that is annotated with one of the following: WPBAdminFieldKey, WPBAdminFieldStore, WPBAdminFieldTextStore
     * @param operator The search criteria operator
     * @param parameter The value used in the search criteria. It's type depends on the property member type. For example if property is type Date then parameter type must be Date too.
     * @param <T> Generic type of data (see com.webpagebytes.cms.cmsdata package)  
     * @throws WPBIOException Exception
     */
	public<T> void delete(Class<T> dataClass, String property, AdminQueryOperator operator, Object parameter) throws WPBIOException;
	
	/**
	 * Returns all records for the resource identified by a class.
	 * @param dataClass Resource class identifier (for example WPBUri.class, WPBWebPage.class)
	 * @param <T> Generic type of data (see com.webpagebytes.cms.cmsdata package)
	 * @return Returns the list of records, or empty list if there are no records of that type.
	 * @throws WPBIOException Exception
	 */
	public<T> List<T> getAllRecords(Class<T> dataClass) throws WPBIOException;
	
	/**
	 * Returns all records sorted for the resource identified by a class.
	 * @param dataClass Resource class identifier (for example WPBUri.class, WPBWebPage.class)
	 * @param property The property on which the sort will be performed
	 * @param operator Sort operator
	 * @param <T> Generic type of data (see com.webpagebytes.cms.cmsdata package)
	 * @return Returns the list of ordered records, or empty list if there are no records of that type.
	 * @throws WPBIOException Exception 
	 */
	public<T> List<T> getAllRecords(Class<T> dataClass, String property, AdminSortOperator operator) throws WPBIOException;

	/**
	 * Adds a record in the storage. The record fields are the instance member fields with WPBAdminFieldStore, WPBAdminFieldTextStore annotations.  
	 * The record key(the field with WPBAdminFieldKey annotation) should not be set because the key will be generated by the data storage.
	 * @param record The record to be added in the storage 
	 * @param <T> Generic type of data (see com.webpagebytes.cms.cmsdata package)
	 * @return Returns the same instance of record but with the key member(the member annotated with WPBAdminFieldKey) populated with the data storage key added as part of this operation. 
	 * @throws WPBIOException Exception 
	 */
	public<T> T add(T record) throws WPBIOException;
	
	/**
	 * Adds a record in the storage with the key specified by the caller. The record fields are the instance member fields with WPBAdminFieldKey, WPBAdminFieldStore, WPBAdminFieldTextStore annotations.
	 * The record key is the instance member annotated with WPBAdminFieldKey.
	 * @param record The record to be added in the storage 
	 * @param <T> Generic type of data (see com.webpagebytes.cms.cmsdata package)
     * @return Returns the same instance of record  
     * @throws WPBIOException Exception
	 */
	public<T> T addWithKey(T record) throws WPBIOException;
	
    /**
     * Returns a record from the storage based on record id and resource type.
     * @param recordid the record id
     * @param dataClass Resource class identifier (for example WPBUri.class, WPBWebPage.class) 
     * @param <T> Generic type of data (see com.webpagebytes.cms.cmsdata package)
     * @return Returns the record on which id and type was specified in the input parameters. Returns null if no record is found.
     * @throws WPBIOException Exception 
     */
	public<T> T get(String recordid, Class<T> dataClass) throws WPBIOException;
	
	/**
	 * Updates a record in the storage, the record is identified by a field annotated with WPBAdminFieldKey
	 * @param record The record that will be updated
	 * @param <T> Generic type of data (see com.webpagebytes.cms.cmsdata package)
	 * @return Returns the same record instance
	 * @throws WPBIOException Exception 
	 */
	public<T> T update(T record) throws WPBIOException;
	
	/**
	 * Query for a list of records that match a search criteria of type (property operator value)
	 * @param dataClass Resource class identifier (for example WPBUri.class, WPBWebPage.class) 
	 * @param property Member field from dataClass that is annotated with one of the following: WPBAdminFieldKey, WPBAdminFieldStore, WPBAdminFieldTextStore
	 * @param operator Query operator
	 * @param parameter The value used in the query criteria. It's type depends on the property member type. For example if property is type Date then parameter type must be Date too.
	 * @param <T> Generic type of data (see com.webpagebytes.cms.cmsdata package)
	 * @return The list of records that match the query criteria, or empty list if no records match the query criteria. 
	 * @throws WPBIOException Exception 
	 */
	public<T> List<T> query(Class<T> dataClass, String property, AdminQueryOperator operator, Object parameter) throws WPBIOException;
	
	/**
	 * Query for a list of records that match a multiple search criterias of type (property operator value). Records that match all criterias will be returned.
	 * @param dataClass Resource class identifier (for example WPBUri.class, WPBWebPage.class) 
	 * @param propertyNames A set of member field names from dataClass that are annotated with one of the following: WPBAdminFieldKey, WPBAdminFieldStore, WPBAdminFieldTextStore
	 * @param operators A map of property names and operators that will be applied in the query
	 * @param values A map of property names and values that will be applies in the query
	 * @param <T> Generic type of data (see com.webpagebytes.cms.cmsdata package)
	 * @return The list of records that match the query criterias, or empty list if no records match the query criteria.
	 * @throws WPBIOException Exception 
	 */
	public<T> List<T> queryEx(Class<T> dataClass, Set<String> propertyNames, Map<String, AdminQueryOperator> operators, Map<String, Object> values) throws WPBIOException;

	/**
	 * A query of type (property operator value) that will return sorted results 
	 * @param dataClass Resource class identifier (for example WPBUri.class, WPBWebPage.class) 
	 * @param property Member field from dataClass that is annotated with one of the following: WPBAdminFieldKey, WPBAdminFieldStore, WPBAdminFieldTextStore
	 * @param operator Query operator
	 * @param parameter The value used in the query criteria. It's type depends on the property member type. For example if property is type Date then parameter type must be Date too.
	 * @param sortProperty Member field from dataClass that is annotated with one of the following: WPBAdminFieldKey, WPBAdminFieldStore, WPBAdminFieldTextStore and used to sort the result
	 * @param sortOperator Sort operator
	 * @param <T> Generic type of data (see com.webpagebytes.cms.cmsdata package)
	 * @return The list of records ordered that match the query criteria, or empty list if no records match the query criteria.
	 * @throws WPBIOException Exception
	 */
	public<T> List<T> queryWithSort(Class<T> dataClass, String property, AdminQueryOperator operator, Object parameter, String sortProperty, AdminSortOperator sortOperator) throws WPBIOException;
		
	/**
	 * Deletes all records for a particular resource type.
	 * @param dataClass Resource class identifier (for example WPBUri.class, WPBWebPage.class) 
	 * @param <T> Generic type of data (see com.webpagebytes.cms.cmsdata package)
	 * @throws WPBIOException Exception 
	 */
	public<T> void deleteAllRecords(Class<T> dataClass) throws WPBIOException;
	
}
