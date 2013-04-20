package com.webbricks.datautility;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.webbricks.datautility.WBDefaultGaeDataFactory;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {Entity.class, Key.class, Query.class, PreparedQuery.class} )
public class TestWBDefaultGaeDataFactory {

	private WBDefaultGaeDataFactory gaeDataFactory;
	private final LocalServiceTestHelper helper =
	        new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	@Before
	public void setUp()
	{
		helper.setUp();
		gaeDataFactory = new WBDefaultGaeDataFactory();		
	}
	
	@After
	public void tearDown()
	{
		helper.tearDown();
	}
	@Test
	public void testCreateEntityWithKey()
	{
		Key key = PowerMock.createMock(Key.class);		
		PowerMock.replay(key);		
		Entity entity = gaeDataFactory.createEntity(key);
		assertTrue(entity != null);
	}
	
	@Test
	public void testCreateEntityWithId()
	{
		Entity entity = gaeDataFactory.createEntity("com.webbricks.cms.DummyClientL", 11L);
		assertTrue(entity != null);
	}
	
	@Test
	public void testCreateEntityFromString()
	{
		Entity entity = gaeDataFactory.createEntity("com.webbricks.cms.DummyClientL");
		assertTrue(entity != null);
	}

	@Test
	public void testCreateEntityWithString()
	{
		Entity entity = gaeDataFactory.createEntity("com.webbricks.cms.DummyClientL", "key");
		assertTrue(entity != null);
	}

	@Test
	public void testCreateKeyWithId()
	{
		Key key = gaeDataFactory.createKey("com.webbricks.cms.DummyClientL", 11L);
		assertTrue(key != null);
	}

	@Test
	public void testCreateKeyWithString()
	{
		Key key = gaeDataFactory.createKey("com.webbricks.cms.DummyClientL", "test");
		assertTrue(key != null);
	}
	
	@Test
	public void testCreateQuery()
	{
		Query query = gaeDataFactory.createQuery("com.webbricks.cms.DummyClientL");
		assertTrue(query != null);
	}

	@Test
	public void testCreateDataStoreService()
	{
		DatastoreService ds = gaeDataFactory.createDatastoreService();
		assertTrue(ds != null);
	}

	
}
