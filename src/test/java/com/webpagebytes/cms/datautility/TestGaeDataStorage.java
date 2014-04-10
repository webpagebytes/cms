package com.webpagebytes.cms.datautility;

import com.google.appengine.api.datastore.DatastoreService;





import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import com.webpagebytes.cms.cmsdata.WBUri;
import com.webpagebytes.cms.datautility.*;
import com.webpagebytes.cms.datautility.AdminDataStorage.AdminQueryOperator;
import com.webpagebytes.cms.datautility.AdminDataStorageListener.AdminDataStorageOperation;
import com.webpagebytes.cms.exception.*;

import org.junit.Test;
import org.junit.Before;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;
import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.core.classloader.annotations.PrepareForTest;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {Entity.class, Key.class, Query.class, PreparedQuery.class} )
public class TestGaeDataStorage {

	private GaeAdminDataStorage gaeAdminDataStorage;
	
	private GaeDataStoreUtility gaeDataStoreUtilityMock;
	
	private WBGaeDataFactory gaeDataFactoryMock;
	
	private DatastoreService dataStoreServiceMock;
	
	private Transaction transactionMock;
	
	private Entity entityMock;
	
	private Key keyMock;
	
	@Before
	public void setUp()
	{
		gaeAdminDataStorage = new GaeAdminDataStorage();
		gaeDataStoreUtilityMock = EasyMock.createMock(GaeDataStoreUtility.class);
		gaeDataFactoryMock = EasyMock.createMock(WBGaeDataFactory.class);
		dataStoreServiceMock = EasyMock.createMock(DatastoreService.class);
		transactionMock = EasyMock.createMock(Transaction.class);
		entityMock = PowerMock.createMock(Entity.class);
		keyMock = PowerMock.createMock(Key.class);
	}
	
	@Test
	public void testAdd_OK()
	{
		try
		{
			Object objectToAdd = new Object();
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createDatastoreService()).andReturn(dataStoreServiceMock);
			EasyMock.expect(dataStoreServiceMock.beginTransaction()).andReturn(transactionMock);
			
			EasyMock.expect(gaeDataStoreUtilityMock.getEmptyEntityWithKey(objectToAdd)).andReturn(entityMock);
			EasyMock.expect(gaeDataStoreUtilityMock.entityFromObject(entityMock, objectToAdd)).andReturn(entityMock);
			EasyMock.expect(dataStoreServiceMock.put(entityMock)).andReturn(keyMock);
			transactionMock.commit();
			Capture<Object> captureObject = new Capture<Object>();
			Capture<Key> captureKey = new Capture<Key>();
			gaeDataStoreUtilityMock.populateObjectWithKey(EasyMock.capture(captureObject),EasyMock.capture(captureKey));
			
			EasyMock.expect(dataStoreServiceMock.get(keyMock)).andReturn(null);
			
			gaeAdminDataStorage.setGaeDataStoreUtility(gaeDataStoreUtilityMock);
			EasyMock.replay(gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock, transactionMock);
			PowerMock.replay(entityMock, keyMock);
			Object ret = gaeAdminDataStorage.add(objectToAdd);
			
			assertTrue (captureKey.getValue() == keyMock);
			assertTrue (captureObject.getValue() == objectToAdd);
			assertTrue (ret == objectToAdd); 
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testAdd_exception_getEmptyEntityWithKey()
	{
		try
		{
			Object objectToAdd = new Object();
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createDatastoreService()).andReturn(dataStoreServiceMock);
			EasyMock.expect(dataStoreServiceMock.beginTransaction()).andReturn(transactionMock);
			
			EasyMock.expect(gaeDataStoreUtilityMock.getEmptyEntityWithKey(objectToAdd)).andThrow(new WBException("test"));
			EasyMock.expect(transactionMock.isActive()).andReturn(true);
			transactionMock.rollback();
			
			gaeAdminDataStorage.setGaeDataStoreUtility(gaeDataStoreUtilityMock);
			EasyMock.replay(gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock, transactionMock);
			Object ret = gaeAdminDataStorage.add(objectToAdd);
			
			// we should not get here
			assertTrue(false); 
		} catch (Exception e)
		{
			if (!(e instanceof WBIOException))
			{
				assertTrue(false);
			}
		}
	}

	@Test
	public void testAdd_exception_entityFromObject()
	{
		try
		{
			Object objectToAdd = new Object();
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createDatastoreService()).andReturn(dataStoreServiceMock);
			EasyMock.expect(dataStoreServiceMock.beginTransaction()).andReturn(transactionMock);
			
			EasyMock.expect(gaeDataStoreUtilityMock.getEmptyEntityWithKey(objectToAdd)).andReturn(entityMock);
			EasyMock.expect(gaeDataStoreUtilityMock.entityFromObject(entityMock, objectToAdd)).andThrow(new WBIOException(""));
			EasyMock.expect(transactionMock.isActive()).andReturn(true);
			transactionMock.rollback();
			
			gaeAdminDataStorage.setGaeDataStoreUtility(gaeDataStoreUtilityMock);
			EasyMock.replay(gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock, transactionMock);
			PowerMock.replay(entityMock);
			Object ret = gaeAdminDataStorage.add(objectToAdd);
			
			// we should not get here
			assertTrue(false); 
		} catch (Exception e)
		{
			if (!(e instanceof WBIOException))
			{
				assertTrue(false);
			}
		}
	}

	@Test
	public void testAdd_exception_populateObjectWithKey()
	{
		try
		{
			Object objectToAdd = new Object();
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createDatastoreService()).andReturn(dataStoreServiceMock);
			EasyMock.expect(dataStoreServiceMock.beginTransaction()).andReturn(transactionMock);
			
			EasyMock.expect(gaeDataStoreUtilityMock.getEmptyEntityWithKey(objectToAdd)).andReturn(entityMock);
			EasyMock.expect(gaeDataStoreUtilityMock.entityFromObject(entityMock, objectToAdd)).andReturn(entityMock);
			EasyMock.expect(dataStoreServiceMock.put(entityMock)).andReturn(keyMock);
			gaeDataStoreUtilityMock.populateObjectWithKey(objectToAdd, keyMock);
			
			EasyMock.expectLastCall().andThrow(new WBException(""));
			
			EasyMock.expect(transactionMock.isActive()).andReturn(false);
			transactionMock.rollback();
			
			
			gaeAdminDataStorage.setGaeDataStoreUtility(gaeDataStoreUtilityMock);
			EasyMock.replay(gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock, transactionMock);
			PowerMock.replay(entityMock, keyMock);
			Object ret = gaeAdminDataStorage.add(objectToAdd);
			
			// we should not get here
			assertTrue(false); 
		} catch (Exception e)
		{
			if (!(e instanceof WBIOException))
			{
				assertTrue(false);
			}
		}
	}

	@Test
	public void testLDelete_OK()
	{
		try
		{
			Long recordid = 1234L;
			String dataClass = String.class.getName(); 
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createDatastoreService()).andReturn(dataStoreServiceMock);
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createKey(dataClass, recordid)).andReturn(keyMock);
			EasyMock.expect(dataStoreServiceMock.beginTransaction()).andReturn(transactionMock);
			
			transactionMock.commit();
			Capture<Key> captureKey = new Capture<Key>();
			dataStoreServiceMock.delete(EasyMock.capture(captureKey));
			
			EasyMock.expect(dataStoreServiceMock.get(keyMock)).andReturn(null);
			
			gaeAdminDataStorage.setGaeDataStoreUtility(gaeDataStoreUtilityMock);
			EasyMock.replay(gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock, transactionMock);
			PowerMock.replay(keyMock);
			gaeAdminDataStorage.delete(recordid, dataClass.getClass());		
			PowerMock.verify(dataStoreServiceMock);
			assertTrue (captureKey.getValue() == keyMock);
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testSDelete_OK()
	{
		try
		{
			String recordid = "1234";
			String dataClass = String.class.getName(); 
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createDatastoreService()).andReturn(dataStoreServiceMock);
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createKey(dataClass, recordid)).andReturn(keyMock);
			EasyMock.expect(dataStoreServiceMock.beginTransaction()).andReturn(transactionMock);
			
			transactionMock.commit();
			Capture<Key> captureKey = new Capture<Key>();
			dataStoreServiceMock.delete(EasyMock.capture(captureKey));

			EasyMock.expect(dataStoreServiceMock.get(keyMock)).andReturn(null);
			
			gaeAdminDataStorage.setGaeDataStoreUtility(gaeDataStoreUtilityMock);
			EasyMock.replay(gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock, transactionMock);
			PowerMock.replay(keyMock);
			gaeAdminDataStorage.delete(recordid, dataClass.getClass());			
			assertTrue (captureKey.getValue() == keyMock);
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void test_query_exception()
	{
		try
		{
			String dataObject = "java.lang.Long";
			String dataClass = dataObject.getClass().getName();
			Query queryMock = PowerMock.createMock(Query.class);
			PreparedQuery preparedQueryMock = PowerMock.createMock(PreparedQuery.class);
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createDatastoreService()).andReturn(dataStoreServiceMock);
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createQuery(dataClass)).andReturn(queryMock);
						
			EasyMock.expect(queryMock.setFilter(EasyMock.anyObject(Filter.class))).andReturn(null);
			EasyMock.expect(dataStoreServiceMock.prepare(queryMock)).andReturn(preparedQueryMock);
			List<Entity> list = new ArrayList<Entity>();
			Entity entity1 = EasyMock.createMock(Entity.class);
			Entity entity2 = EasyMock.createMock(Entity.class);
			list.add(entity1);
			list.add(entity2);
			
			EasyMock.expect(preparedQueryMock.asList(withLimit(GaeAdminDataStorage.MAX_FETCH_SIZE))).andReturn(list);
			
			EasyMock.expect(gaeDataStoreUtilityMock.objectFromEntity(entity1, dataObject.getClass())).andThrow(new WBIOException(""));
			
			gaeAdminDataStorage.setGaeDataStoreUtility(gaeDataStoreUtilityMock);
			EasyMock.replay(gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock);
			EasyMock.replay(queryMock, preparedQueryMock);
			List<Object> returnArray = gaeAdminDataStorage.query(dataObject.getClass(), "xyz", AdminQueryOperator.EQUAL, 1);			
			assertTrue(false);
		} 
		catch (WBIOException e) { }
		catch (Exception e)
		{
			assertTrue (false);
		}
	}
	
	@Test
	public void test_adminOperatorToGaeOperator()
	{
		assertTrue (Query.FilterOperator.EQUAL.compareTo(gaeAdminDataStorage.adminOperatorToGaeOperator(AdminDataStorage.AdminQueryOperator.EQUAL)) == 0);
		assertTrue (Query.FilterOperator.NOT_EQUAL.compareTo(gaeAdminDataStorage.adminOperatorToGaeOperator(AdminDataStorage.AdminQueryOperator.NOT_EQUAL)) == 0);
		assertTrue (Query.FilterOperator.LESS_THAN.compareTo(gaeAdminDataStorage.adminOperatorToGaeOperator(AdminDataStorage.AdminQueryOperator.LESS_THAN)) == 0);
		assertTrue (Query.FilterOperator.GREATER_THAN.compareTo(gaeAdminDataStorage.adminOperatorToGaeOperator(AdminDataStorage.AdminQueryOperator.GREATER_THAN)) == 0);
		assertTrue (Query.FilterOperator.LESS_THAN_OR_EQUAL.compareTo(gaeAdminDataStorage.adminOperatorToGaeOperator(AdminDataStorage.AdminQueryOperator.LESS_THAN_OR_EQUAL)) == 0);
		assertTrue (Query.FilterOperator.GREATER_THAN_OR_EQUAL.compareTo(gaeAdminDataStorage.adminOperatorToGaeOperator(AdminDataStorage.AdminQueryOperator.GREATER_THAN_OR_EQUAL)) == 0);
	}
	
	@Test
	public void test_query_OK()
	{
		try
		{
			String dataObject = "java.lang.Long";
			String dataClass = dataObject.getClass().getName();
			Query queryMock = PowerMock.createMock(Query.class);
			PreparedQuery preparedQueryMock = PowerMock.createMock(PreparedQuery.class);
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createDatastoreService()).andReturn(dataStoreServiceMock);
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createQuery(dataClass)).andReturn(queryMock);
			
			EasyMock.expect(queryMock.setFilter(EasyMock.anyObject(Filter.class))).andReturn(null);
			EasyMock.expect(dataStoreServiceMock.prepare(queryMock)).andReturn(preparedQueryMock);
			List<Entity> list = new ArrayList<Entity>();
			Entity entity1 = EasyMock.createMock(Entity.class);
			Entity entity2 = EasyMock.createMock(Entity.class);
			list.add(entity1);
			list.add(entity2);
			
			EasyMock.expect(preparedQueryMock.asList(withLimit(GaeAdminDataStorage.MAX_FETCH_SIZE))).andReturn(list);
			
			Object obj1 = new Object();
			EasyMock.expect(gaeDataStoreUtilityMock.objectFromEntity(entity1, dataObject.getClass())).andReturn(obj1);
		
			Object obj2 = new Object();
			EasyMock.expect(gaeDataStoreUtilityMock.objectFromEntity(entity2, dataObject.getClass())).andReturn(obj2);
		
			ArrayList<Object> arrayToCompare = new ArrayList<Object>();
			arrayToCompare.add(obj1);
			arrayToCompare.add(obj2);
			
			gaeAdminDataStorage.setGaeDataStoreUtility(gaeDataStoreUtilityMock);
			EasyMock.replay(gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock);
			PowerMock.replay(queryMock, preparedQueryMock);
			List<Object> returnArray = gaeAdminDataStorage.query(dataObject.getClass(), "xyz", AdminQueryOperator.EQUAL, 1);			
			assertTrue ( returnArray.containsAll(arrayToCompare));
			assertTrue ( arrayToCompare.containsAll(returnArray));
			PowerMock.verify(queryMock, preparedQueryMock);
			EasyMock.verify(gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock);
		} catch (Exception e)
		{
			assertTrue (false);
		}
		
	}
	
	@Test
	public void testGetAllRecords_OK()
	{
		try
		{
			String dataObject = "";
			String dataClass = dataObject.getClass().getName();
			Query queryMock = PowerMock.createMock(Query.class);
			PreparedQuery preparedQueryMock = PowerMock.createMock(PreparedQuery.class);
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createDatastoreService()).andReturn(dataStoreServiceMock);
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createQuery(dataClass)).andReturn(queryMock);
			
			EasyMock.expect(dataStoreServiceMock.prepare(queryMock)).andReturn(preparedQueryMock);
			List<Entity> list = new ArrayList<Entity>();
			Entity entity1 = EasyMock.createMock(Entity.class);
			Entity entity2 = EasyMock.createMock(Entity.class);
			list.add(entity1);
			list.add(entity2);
			
			EasyMock.expect(preparedQueryMock.asList(withLimit(GaeAdminDataStorage.MAX_FETCH_SIZE))).andReturn(list);
			
			Object obj1 = new Object();
			EasyMock.expect(gaeDataStoreUtilityMock.objectFromEntity(entity1, dataObject.getClass())).andReturn(obj1);
		
			Object obj2 = new Object();
			EasyMock.expect(gaeDataStoreUtilityMock.objectFromEntity(entity2, dataObject.getClass())).andReturn(obj2);
		
			ArrayList<Object> arrayToCompare = new ArrayList<Object>();
			arrayToCompare.add(obj1);
			arrayToCompare.add(obj2);
			
			gaeAdminDataStorage.setGaeDataStoreUtility(gaeDataStoreUtilityMock);
			EasyMock.replay(gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock);
			PowerMock.replay(queryMock, preparedQueryMock);
			List<Object> returnArray = gaeAdminDataStorage.getAllRecords(dataObject.getClass());			
			assertTrue ( returnArray.containsAll(arrayToCompare));
			assertTrue ( arrayToCompare.containsAll(returnArray));
			PowerMock.verify(queryMock, preparedQueryMock);
			EasyMock.verify(gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock);
			
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testGetAllRecords_exception_objectFromEntity()
	{
		try
		{
			String dataObject = "";
			String dataClass = dataObject.getClass().getName();
			Query queryMock = PowerMock.createMock(Query.class);
			PreparedQuery preparedQueryMock = PowerMock.createMock(PreparedQuery.class);
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createDatastoreService()).andReturn(dataStoreServiceMock);
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createQuery(dataClass)).andReturn(queryMock);
			
			EasyMock.expect(dataStoreServiceMock.prepare(queryMock)).andReturn(preparedQueryMock);
			List<Entity> list = new ArrayList<Entity>();
			Entity entity1 = EasyMock.createMock(Entity.class);
			Entity entity2 = EasyMock.createMock(Entity.class);
			list.add(entity1);
			list.add(entity2);
			
			EasyMock.expect(preparedQueryMock.asList(withLimit(GaeAdminDataStorage.MAX_FETCH_SIZE))).andReturn(list);
			
			EasyMock.expect(gaeDataStoreUtilityMock.objectFromEntity(entity1, dataObject.getClass())).andThrow(new WBIOException(""));
		
			
			gaeAdminDataStorage.setGaeDataStoreUtility(gaeDataStoreUtilityMock);
			EasyMock.replay(gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock);
			PowerMock.replay(queryMock, preparedQueryMock);
			List<Object> returnArray = gaeAdminDataStorage.getAllRecords(dataObject.getClass());			
			
			
		} catch (Exception e)
		{
			if (!(e instanceof WBIOException))
			{
				assertTrue(false);
			}
		}
	}


	@Test
	public void testLGet_OK()
	{
		try
		{
			Object dataObject = new Object();
			Class dataClass = dataObject.getClass();
			Long dataId = 1234L;
			
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createDatastoreService()).andReturn(dataStoreServiceMock);
			
			EasyMock.expect(dataStoreServiceMock.beginTransaction()).andReturn(transactionMock);
			
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createKey(dataClass.getName(), dataId)).andReturn(keyMock);

			EasyMock.expect(dataStoreServiceMock.get(keyMock)).andReturn(entityMock);
			EasyMock.expect(gaeDataStoreUtilityMock.objectFromEntity(entityMock, dataClass)).andReturn(dataObject);
			gaeDataStoreUtilityMock.populateObjectWithKey(dataObject, keyMock);
			transactionMock.commit();
			
			gaeAdminDataStorage.setGaeDataStoreUtility(gaeDataStoreUtilityMock);
			EasyMock.replay(gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock, transactionMock);
			PowerMock.replay(entityMock, keyMock);
			Object ret = gaeAdminDataStorage.get(dataId, dataClass);
			
			assertTrue (ret == dataObject);
		 
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testLGet_exception_objectFromEntity()
	{
		try
		{
			Object dataObject = new Object();
			Class dataClass = dataObject.getClass();
			Long dataId = 1234L;
			
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createDatastoreService()).andReturn(dataStoreServiceMock);
			
			EasyMock.expect(dataStoreServiceMock.beginTransaction()).andReturn(transactionMock);
			
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createKey(dataClass.getName(), dataId)).andReturn(keyMock);

			EasyMock.expect(dataStoreServiceMock.get(keyMock)).andReturn(entityMock);
			EasyMock.expect(gaeDataStoreUtilityMock.objectFromEntity(entityMock, dataClass)).andThrow(new WBIOException(""));
			EasyMock.expect(transactionMock.isActive()).andReturn(true);
			transactionMock.rollback();
			
			gaeAdminDataStorage.setGaeDataStoreUtility(gaeDataStoreUtilityMock);
			EasyMock.replay(gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock, transactionMock);
			PowerMock.replay(entityMock, keyMock);
			Object ret = gaeAdminDataStorage.get(dataId, dataClass);
			
			assertTrue (false);
		 
		} catch (Exception e)
		{
			if (!(e instanceof WBIOException))
			{
				assertTrue(false);
			}
		}
	}

	@Test
	public void testLGet_exception_populateObjectWithKey()
	{
		try
		{
			Object dataObject = new Object();
			Class dataClass = dataObject.getClass();
			Long dataId = 1234L;
			
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createDatastoreService()).andReturn(dataStoreServiceMock);
			
			EasyMock.expect(dataStoreServiceMock.beginTransaction()).andReturn(transactionMock);
			
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createKey(dataClass.getName(), dataId)).andReturn(keyMock);

			EasyMock.expect(dataStoreServiceMock.get(keyMock)).andReturn(entityMock);
			EasyMock.expect(gaeDataStoreUtilityMock.objectFromEntity(entityMock, dataClass)).andReturn(dataObject);
			gaeDataStoreUtilityMock.populateObjectWithKey(dataObject, keyMock);
			EasyMock.expectLastCall().andThrow(new WBException(""));
			EasyMock.expect(transactionMock.isActive()).andReturn(false);
			
			gaeAdminDataStorage.setGaeDataStoreUtility(gaeDataStoreUtilityMock);
			EasyMock.replay(gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock, transactionMock);
			PowerMock.replay(entityMock, keyMock);
			Object ret = gaeAdminDataStorage.get(dataId, dataClass);
			
			assertTrue (false);
		 
		} catch (Exception e)
		{
			if (!(e instanceof WBIOException))
			{
				assertTrue(false);
			}
		}
	}

	@Test
	public void testSGet_OK()
	{
		try
		{
			Object dataObject = new Object();
			Class dataClass = dataObject.getClass();
			String dataId = "1234";
			
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createDatastoreService()).andReturn(dataStoreServiceMock);
			
			EasyMock.expect(dataStoreServiceMock.beginTransaction()).andReturn(transactionMock);
			
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createKey(dataClass.getName(), dataId)).andReturn(keyMock);

			EasyMock.expect(dataStoreServiceMock.get(keyMock)).andReturn(entityMock);
			EasyMock.expect(gaeDataStoreUtilityMock.objectFromEntity(entityMock, dataClass)).andReturn(dataObject);
			gaeDataStoreUtilityMock.populateObjectWithKey(dataObject, keyMock);
			transactionMock.commit();
			
			gaeAdminDataStorage.setGaeDataStoreUtility(gaeDataStoreUtilityMock);
			EasyMock.replay(gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock, transactionMock);
			PowerMock.replay(entityMock, keyMock);
			Object ret = gaeAdminDataStorage.get(dataId, dataClass);
			
			assertTrue (ret == dataObject);
		 
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testSGet_exception_objectFromEntity()
	{
		try
		{
			Object dataObject = new Object();
			Class dataClass = dataObject.getClass();
			String dataId = "1234";
			
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createDatastoreService()).andReturn(dataStoreServiceMock);
			
			EasyMock.expect(dataStoreServiceMock.beginTransaction()).andReturn(transactionMock);
			
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createKey(dataClass.getName(), dataId)).andReturn(keyMock);

			EasyMock.expect(dataStoreServiceMock.get(keyMock)).andReturn(entityMock);
			EasyMock.expect(gaeDataStoreUtilityMock.objectFromEntity(entityMock, dataClass)).andThrow(new WBIOException(""));
			EasyMock.expect(transactionMock.isActive()).andReturn(true);
			transactionMock.rollback();
			
			gaeAdminDataStorage.setGaeDataStoreUtility(gaeDataStoreUtilityMock);
			EasyMock.replay(gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock, transactionMock);
			PowerMock.replay(entityMock, keyMock);
			Object ret = gaeAdminDataStorage.get(dataId, dataClass);
			
			assertTrue (false);
		 
		} catch (Exception e)
		{
			if (!(e instanceof WBIOException))
			{
				assertTrue(false);
			}
		}
	}

	@Test
	public void testSGet_exception_populateObjectWithKey()
	{
		try
		{
			Object dataObject = new Object();
			Class dataClass = dataObject.getClass();
			String dataId = "1234";
			
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createDatastoreService()).andReturn(dataStoreServiceMock);
			
			EasyMock.expect(dataStoreServiceMock.beginTransaction()).andReturn(transactionMock);
			
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createKey(dataClass.getName(), dataId)).andReturn(keyMock);

			EasyMock.expect(dataStoreServiceMock.get(keyMock)).andReturn(entityMock);
			EasyMock.expect(gaeDataStoreUtilityMock.objectFromEntity(entityMock, dataClass)).andReturn(dataObject);
			gaeDataStoreUtilityMock.populateObjectWithKey(dataObject, keyMock);
			EasyMock.expectLastCall().andThrow(new WBException(""));
			EasyMock.expect(transactionMock.isActive()).andReturn(false);
			
			gaeAdminDataStorage.setGaeDataStoreUtility(gaeDataStoreUtilityMock);
			EasyMock.replay(gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock, transactionMock);
			PowerMock.replay(entityMock, keyMock);
			Object ret = gaeAdminDataStorage.get(dataId, dataClass);
			
			assertTrue (false);
		 
		} catch (Exception e)
		{
			if (!(e instanceof WBIOException))
			{
				assertTrue(false);
			}
		}
	}

	@Test
	public void testUpdate_OK()
	{
		try
		{
			Object dataObject = new Object();
			Class dataClass = dataObject.getClass();
			
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createDatastoreService()).andReturn(dataStoreServiceMock);
			
			EasyMock.expect(dataStoreServiceMock.beginTransaction()).andReturn(transactionMock);
			
			EasyMock.expect(gaeDataStoreUtilityMock.getEmptyEntityWithKey(dataObject)).andReturn(entityMock);
			EasyMock.expect(gaeDataStoreUtilityMock.entityFromObject(entityMock, dataObject)).andReturn(entityMock);
			EasyMock.expect(dataStoreServiceMock.put(entityMock)).andReturn(keyMock);
			gaeDataStoreUtilityMock.populateObjectWithKey(dataObject, keyMock);
			transactionMock.commit();
			
			EasyMock.expect(dataStoreServiceMock.get(keyMock)).andReturn(null);
			
			gaeAdminDataStorage.setGaeDataStoreUtility(gaeDataStoreUtilityMock);
			EasyMock.replay(gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock, transactionMock);
			PowerMock.replay(entityMock, keyMock);
			Object ret = gaeAdminDataStorage.update(dataObject);
			EasyMock.verify(gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock, transactionMock);
			PowerMock.verify(entityMock, keyMock);
			
			assertTrue (ret == dataObject);
		 
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testUpdate_exception_getEmptyEntityWithKey()
	{
		try
		{
			Object dataObject = new Object();
			Class dataClass = dataObject.getClass();
			
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createDatastoreService()).andReturn(dataStoreServiceMock);
			
			EasyMock.expect(dataStoreServiceMock.beginTransaction()).andReturn(transactionMock);
			
			EasyMock.expect(gaeDataStoreUtilityMock.getEmptyEntityWithKey(dataObject)).andThrow(new WBException(""));
			EasyMock.expect(transactionMock.isActive()).andReturn(true);
			transactionMock.rollback();
			
			gaeAdminDataStorage.setGaeDataStoreUtility(gaeDataStoreUtilityMock);
			EasyMock.replay(gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock, transactionMock);
			PowerMock.replay(entityMock, keyMock);
			Object ret = gaeAdminDataStorage.update(dataObject);
			
			assertTrue (false);
		 
		} catch (Exception e)
		{
			if (!(e instanceof WBIOException))
			{
				assertTrue(false);
			}
		}
	}

	@Test
	public void testUpdate_exception_entityFromObject()
	{
		try
		{
			Object dataObject = new Object();
			Class dataClass = dataObject.getClass();
			
			EasyMock.expect(gaeDataStoreUtilityMock.getGaeDataFactory()).andReturn(gaeDataFactoryMock);
			EasyMock.expect(gaeDataFactoryMock.createDatastoreService()).andReturn(dataStoreServiceMock);
			
			EasyMock.expect(dataStoreServiceMock.beginTransaction()).andReturn(transactionMock);
			
			EasyMock.expect(gaeDataStoreUtilityMock.getEmptyEntityWithKey(dataObject)).andReturn(entityMock);
			EasyMock.expect(gaeDataStoreUtilityMock.entityFromObject(entityMock, dataObject)).andThrow(new WBIOException(""));
			EasyMock.expect(transactionMock.isActive()).andReturn(false);
			
			gaeAdminDataStorage.setGaeDataStoreUtility(gaeDataStoreUtilityMock);
			EasyMock.replay(gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock, transactionMock);
			PowerMock.replay(entityMock, keyMock);
			Object ret = gaeAdminDataStorage.update(dataObject);
			
			assertTrue (false);
		 
		} catch (Exception e)
		{
			if (!(e instanceof WBIOException))
			{
				assertTrue(false);
			}
		}
	}

	@Test
	public void test_getGaeDataStoreUtility()
	{
		assertTrue(gaeAdminDataStorage.getGaeDataStoreUtility() != null);
	}
	
	@Test
	public void test_addStorageListener()
	{
		AdminDataStorageListener<WBUri> listener = PowerMock.createMock(AdminDataStorageListener.class);
		EasyMock.replay(listener, gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock, transactionMock);
		gaeAdminDataStorage.addStorageListener(listener);
		
		Vector vectorListeners = Whitebox.getInternalState(gaeAdminDataStorage, "storageListeners", GaeAdminDataStorage.class);
		assertTrue (vectorListeners.size() == 1);
		assertTrue (vectorListeners.get(0) == listener);
	}
	@Test
	public void test_removeEmptyStorageListener()
	{
		AdminDataStorageListener<WBUri> listener = PowerMock.createMock(AdminDataStorageListener.class);
		EasyMock.replay(listener, gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock, transactionMock);
		gaeAdminDataStorage.removeStorageListener(listener);
		
		Vector vectorListeners = Whitebox.getInternalState(gaeAdminDataStorage, "storageListeners", GaeAdminDataStorage.class);
		assertTrue (vectorListeners.size() == 0);
	}
	@Test
	public void test_removeStrangerItemFromStorageListener()
	{
		AdminDataStorageListener<WBUri> listener = PowerMock.createMock(AdminDataStorageListener.class);
		AdminDataStorageListener<WBUri> stranger = PowerMock.createMock(AdminDataStorageListener.class);
		
		EasyMock.replay(listener, gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock, transactionMock);
		gaeAdminDataStorage.addStorageListener(listener);
		gaeAdminDataStorage.removeStorageListener(stranger);
		Vector vectorListeners = Whitebox.getInternalState(gaeAdminDataStorage, "storageListeners", GaeAdminDataStorage.class);
		assertTrue (vectorListeners.size() == 1);
		assertTrue (vectorListeners.get(0) == listener);
	}

	@Test
	public void test_removeItemFromStorageListener()
	{
		AdminDataStorageListener<WBUri> listener = PowerMock.createMock(AdminDataStorageListener.class);
		
		EasyMock.replay(listener, gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock, transactionMock);
		gaeAdminDataStorage.addStorageListener(listener);
		gaeAdminDataStorage.removeStorageListener(listener);
		Vector vectorListeners = Whitebox.getInternalState(gaeAdminDataStorage, "storageListeners", GaeAdminDataStorage.class);
		assertTrue (vectorListeners.size() == 0);
	}

	@Test
	public void test_notifyOperation_create()
	{
		AdminDataStorageListener<WBUri> listener = PowerMock.createMock(AdminDataStorageListener.class);
		WBUri uriMock = PowerMock.createMock(WBUri.class);
		Capture<WBUri> captureInst = new Capture<WBUri>();
		Capture<AdminDataStorageOperation> captureOperation = new Capture<AdminDataStorageOperation>();
		
		listener.notify(EasyMock.capture(captureInst), EasyMock.capture(captureOperation));
		EasyMock.replay(listener, gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock, transactionMock);
		gaeAdminDataStorage.addStorageListener(listener);
		
		gaeAdminDataStorage.notifyOperation(uriMock, AdminDataStorageOperation.CREATE);
		
		assertTrue (captureInst.getValue() == uriMock);
		assertTrue (captureOperation.getValue() == AdminDataStorageOperation.CREATE);
	}

	@Test
	public void test_notifyOperation_update()
	{
		AdminDataStorageListener<WBUri> listener = PowerMock.createMock(AdminDataStorageListener.class);
		WBUri uriMock = PowerMock.createMock(WBUri.class);
		Capture<WBUri> captureInst = new Capture<WBUri>();
		Capture<AdminDataStorageOperation> captureOperation = new Capture<AdminDataStorageOperation>();
		
		listener.notify(EasyMock.capture(captureInst), EasyMock.capture(captureOperation));
		EasyMock.replay(listener, gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock, transactionMock);
		gaeAdminDataStorage.addStorageListener(listener);
		
		gaeAdminDataStorage.notifyOperation(uriMock, AdminDataStorageOperation.UPDATE);
		
		assertTrue (captureInst.getValue() == uriMock);
		assertTrue (captureOperation.getValue() == AdminDataStorageOperation.UPDATE);
	}

	@Test
	public void test_notifyOperation_delete()
	{
		AdminDataStorageListener<WBUri> listener = PowerMock.createMock(AdminDataStorageListener.class);
		WBUri uriMock = PowerMock.createMock(WBUri.class);
		Capture<WBUri> captureInst = new Capture<WBUri>();
		Capture<AdminDataStorageOperation> captureOperation = new Capture<AdminDataStorageOperation>();
		
		listener.notify(EasyMock.capture(captureInst), EasyMock.capture(captureOperation));
		EasyMock.replay(listener, gaeDataStoreUtilityMock, gaeDataFactoryMock, 
					dataStoreServiceMock, transactionMock);
		gaeAdminDataStorage.addStorageListener(listener);
		
		gaeAdminDataStorage.notifyOperation(uriMock, AdminDataStorageOperation.DELETE);
		
		assertTrue (captureInst.getValue() == uriMock);
		assertTrue (captureOperation.getValue() == AdminDataStorageOperation.DELETE);
	}

}
