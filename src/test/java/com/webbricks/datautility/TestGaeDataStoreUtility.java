package com.webbricks.datautility;

import com.webbricks.cms.DummyClientK;
import com.webbricks.cms.DummyClientL;
import com.webbricks.cms.DummyClientS;
import com.webbricks.cms.DummyPrivateClientL;
import com.webbricks.cms.DummyPrivateClientS;
import com.webbricks.datautility.*;

import java.util.HashMap;
import com.webbricks.exception.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import com.google.appengine.api.datastore.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {Entity.class, Key.class} )
public class TestGaeDataStoreUtility{

	@Test
	public void testLPopulateObjectWithKey_OK()
	{
		try
		{
			long id = 1L;
			GaeDataStoreUtility gaeUtility = new GaeDataStoreUtility();
			Key key = PowerMock.createMock(Key.class);
			EasyMock.expect(key.getId()).andReturn(id);
			EasyMock.replay(key);
			DummyClientL clientL = new DummyClientL();
			
			gaeUtility.populateObjectWithKey(clientL, key);
			assertTrue(clientL.getId() != null && clientL.getId().longValue() == id);
			
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testLPopulateObjectWithKey_keyAlreadySet()
	{
		try
		{
			long id = 1L;
			GaeDataStoreUtility gaeUtility = new GaeDataStoreUtility();
			Key key = PowerMock.createMock(Key.class);
			EasyMock.expect(key.getId()).andReturn(id);
			EasyMock.replay(key);
			DummyClientL clientL = new DummyClientL();
			clientL.setId(5L);
			gaeUtility.populateObjectWithKey(clientL, key);
			
			assertTrue (clientL.getId() != null);
			assertTrue (clientL.getId().compareTo(id)==0);
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testLPopulateObjectWithKey_noDecoration()
	{
		try
		{
			long id = 1L;
			GaeDataStoreUtility gaeUtility = new GaeDataStoreUtility();
			Key key = PowerMock.createMock(Key.class);
			EasyMock.expect(key.getId()).andReturn(id);
			EasyMock.replay(key);
			final String obj = "abc";
			gaeUtility.populateObjectWithKey(obj, key);
			
			
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testSPopulateObjectWithKey_OK()
	{
		try
		{
			String id = "testkey";
			GaeDataStoreUtility gaeUtility = new GaeDataStoreUtility();
			Key key = PowerMock.createMock(Key.class);
			EasyMock.expect(key.getName()).andReturn(id);
			EasyMock.replay(key);
			DummyClientS clientS = new DummyClientS();
			
			gaeUtility.populateObjectWithKey(clientS, key);
			assertTrue(clientS.getId() != null && clientS.getId().compareTo(id) == 0);
			
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testLPopulateObjectWithKey_privateMembers()
	{
		try
		{
			Long id = 1234L;
			GaeDataStoreUtility gaeUtility = new GaeDataStoreUtility();
			Key key = PowerMock.createMock(Key.class);
			EasyMock.expect(key.getId()).andReturn(id);
			EasyMock.replay(key);
			DummyPrivateClientL clientL = new DummyPrivateClientL();
			
			gaeUtility.populateObjectWithKey(clientL, key);
			
			assertTrue(false);
		} catch (Exception e)
		{
			if (!(e instanceof WBSetKeyException))
			{
				assertTrue(false);
			}
		}
	}

	@Test
	public void testSPopulateObjectWithKey_privateMembers()
	{
		try
		{
			String id = "1234";
			GaeDataStoreUtility gaeUtility = new GaeDataStoreUtility();
			Key key = PowerMock.createMock(Key.class);
			EasyMock.expect(key.getName()).andReturn(id);
			EasyMock.replay(key);
			DummyPrivateClientS clientL = new DummyPrivateClientS();
			
			gaeUtility.populateObjectWithKey(clientL, key);
			
			assertTrue(false);
		} catch (Exception e)
		{
			if (!(e instanceof WBSetKeyException))
			{
				assertTrue(false);
			}
		}
	}

	@Test
	public void testSPopulateObjectWithKey_keyAlreadySet()
	{
		try
		{
			String id = "testkey";
			GaeDataStoreUtility gaeUtility = new GaeDataStoreUtility();
			Key key = PowerMock.createMock(Key.class);
			EasyMock.expect(key.getName()).andReturn(id);
			EasyMock.replay(key);
			DummyClientS clientS = new DummyClientS();
			clientS.setId("akey");
			gaeUtility.populateObjectWithKey(clientS, key);
			
			assertTrue (clientS.getId() != null);
			assertTrue (clientS.getId().compareTo(id)==0);
		} catch (Exception e)
		{
			assertFalse(false);
		}
	}

	@Test
	public void testLPopulateObjectWithKey_wrongKeyType()
	{
		try
		{
			long id = 10;
			GaeDataStoreUtility gaeUtility = new GaeDataStoreUtility();
			Key key = PowerMock.createMock(Key.class);
			EasyMock.expect(key.getId()).andReturn(id);
			EasyMock.replay(key);
			DummyClientK clientK = new DummyClientK();
			clientK.setId(10);
			gaeUtility.populateObjectWithKey(clientK, key);			
		} 
		catch (WBSetKeyException e)
		{
			// all fine here
		}
		catch (Exception e)
		{
			assertTrue(false);
		}
	}

	
	@Test
	public void testLGetEmptyEntityWithKey_OK()
	{
		try
		{	
			long id = 10L;
			GaeDataStoreUtility gaeUtility = new GaeDataStoreUtility();
			DummyClientL longClient = new DummyClientL();
			longClient.setId(id);
			
			WBGaeDataFactory mockEntityFactory = EasyMock.createMock(WBGaeDataFactory.class);
			Entity entity = PowerMock.createMock(Entity.class);			
			EasyMock.expect(mockEntityFactory.createEntity(DummyClientL.class.getName(), id)).andReturn(entity);
			EasyMock.replay(mockEntityFactory);
			gaeUtility.setGaeDataFactory(mockEntityFactory);
			
			Entity returnEntity = gaeUtility.getEmptyEntityWithKey(longClient);
			assertTrue (returnEntity == entity);
			EasyMock.verify(mockEntityFactory);
		
		} catch (WBException e)
		{
			assertTrue (false);
		}
	}

	@Test
	public void testLGetEmptyEntityWithKey_wrongKeyType()
	{
		try
		{	
			Long id = 10L;
			GaeDataStoreUtility gaeUtility = new GaeDataStoreUtility();
			DummyClientK client = new DummyClientK();
			client.setId(10);
			
			WBGaeDataFactory mockEntityFactory = EasyMock.createMock(WBGaeDataFactory.class);
			Entity entity = PowerMock.createMock(Entity.class);			
			EasyMock.expect(mockEntityFactory.createEntity(DummyClientK.class.getName(), id)).andReturn(entity);
			EasyMock.replay(mockEntityFactory);
			gaeUtility.setGaeDataFactory(mockEntityFactory);
			
			Entity returnEntity = gaeUtility.getEmptyEntityWithKey(client);
			assertTrue(false);
		} catch (WBException e)
		{
			if (!(e instanceof WBIOException))
			{
				assertTrue (false);
			}
		}
	}

	@Test
	public void testLGetEmptyEntityWithKey_privateMembers()
	{
		try
		{	
			long id = 10L;
			GaeDataStoreUtility gaeUtility = new GaeDataStoreUtility();
			DummyPrivateClientL longClient = new DummyPrivateClientL();
			
			WBGaeDataFactory mockEntityFactory = EasyMock.createMock(WBGaeDataFactory.class);
			Entity entity = PowerMock.createMock(Entity.class);			
			EasyMock.expect(mockEntityFactory.createEntity(DummyPrivateClientL.class.getName(), id)).andReturn(entity);
			EasyMock.replay(mockEntityFactory);
			gaeUtility.setGaeDataFactory(mockEntityFactory);
			
			Entity returnEntity = gaeUtility.getEmptyEntityWithKey(longClient);
			assertTrue (false);
			
		
		} catch (WBException e)
		{
			if (!(e instanceof WBIOException))
			{
				assertTrue (false);
			}
		}
	}

	@Test
	public void testSGetEmptyEntityWithKey_privateMembers()
	{
		try
		{	
			String id = "10";
			GaeDataStoreUtility gaeUtility = new GaeDataStoreUtility();
			DummyPrivateClientS stringClient = new DummyPrivateClientS();
			
			WBGaeDataFactory mockEntityFactory = EasyMock.createMock(WBGaeDataFactory.class);
			Entity entity = PowerMock.createMock(Entity.class);			
			EasyMock.expect(mockEntityFactory.createEntity(DummyClientL.class.getName(), id)).andReturn(entity);
			EasyMock.replay(mockEntityFactory);
			gaeUtility.setGaeDataFactory(mockEntityFactory);
			
			Entity returnEntity = gaeUtility.getEmptyEntityWithKey(stringClient);
			assertTrue (false);
			
		
		} catch (WBException e)
		{
			if (!(e instanceof WBIOException))
			{
				assertTrue (false);
			}
		}
	}

	@Test
	public void testLGetEmptyEntityWithKey_noId()
	{
		try
		{	
			long id = 10L;
			GaeDataStoreUtility gaeUtility = new GaeDataStoreUtility();
			DummyClientL longClient = new DummyClientL();
			
			WBGaeDataFactory mockEntityFactory = EasyMock.createMock(WBGaeDataFactory.class);
			Entity entity = PowerMock.createMock(Entity.class);			
			EasyMock.expect(mockEntityFactory.createEntity(DummyClientL.class.getName())).andReturn(entity);
			EasyMock.replay(mockEntityFactory);
			gaeUtility.setGaeDataFactory(mockEntityFactory);			
			Entity returnEntity = gaeUtility.getEmptyEntityWithKey(longClient);
			assertTrue (returnEntity == entity);
			EasyMock.verify(mockEntityFactory);
		
		} catch (WBException e)
		{
			assertTrue (false);
		}
	}

	@Test
	public void testLGetEmptyEntityWithKey_noDecoration()
	{
		try
		{	
			long id = 10L;
			GaeDataStoreUtility gaeUtility = new GaeDataStoreUtility();
			String testObject = "abc";
			WBGaeDataFactory mockEntityFactory = EasyMock.createMock(WBGaeDataFactory.class);
			gaeUtility.setGaeDataFactory(mockEntityFactory);			
			EasyMock.replay(mockEntityFactory);
			
			Entity returnEntity = gaeUtility.getEmptyEntityWithKey(testObject);
			assertTrue (returnEntity == null);
			EasyMock.verify(mockEntityFactory);
		
		} catch (WBException e)
		{
			assertTrue (false);
		}
	}
	

	@Test
	public void testSGetEmptyEntityWithKey_OK()
	{
		try
		{	
			String id = "xyz";
			GaeDataStoreUtility gaeUtility = new GaeDataStoreUtility();
			DummyClientS longClient = new DummyClientS();
			longClient.setId(id);
			
			WBGaeDataFactory mockEntityFactory = EasyMock.createMock(WBGaeDataFactory.class);
			Entity entity = PowerMock.createMock(Entity.class);			
			EasyMock.expect(mockEntityFactory.createEntity(DummyClientS.class.getName(), id)).andReturn(entity);
			EasyMock.replay(mockEntityFactory);
			gaeUtility.setGaeDataFactory(mockEntityFactory);
			
			Entity returnEntity = gaeUtility.getEmptyEntityWithKey(longClient);
			assertTrue (returnEntity == entity);
			EasyMock.verify(mockEntityFactory);
		
		} catch (WBException e)
		{
			assertTrue (false);
		}
	}
	
	@Test
	public void testSGetEmptyEntityWithKey_noId()
	{
		try
		{	
			String id = "xyz";
			GaeDataStoreUtility gaeUtility = new GaeDataStoreUtility();
			DummyClientS longClient = new DummyClientS();
			
			WBGaeDataFactory mockEntityFactory = EasyMock.createMock(WBGaeDataFactory.class);
			Entity entity = PowerMock.createMock(Entity.class);			
			EasyMock.expect(mockEntityFactory.createEntity(DummyClientS.class.getName())).andReturn(entity);
			EasyMock.replay(mockEntityFactory);
			gaeUtility.setGaeDataFactory(mockEntityFactory);			
			Entity returnEntity = gaeUtility.getEmptyEntityWithKey(longClient);
			assertTrue (returnEntity == entity);
			EasyMock.verify(mockEntityFactory);
		
		} catch (WBException e)
		{
			assertTrue (false);
		}
	}

	@Test
	public void testSGetEmptyEntityWithKey_noDecoration()
	{
		try
		{	
			String id = "xyz";
			GaeDataStoreUtility gaeUtility = new GaeDataStoreUtility();
			String testObject = "abc";
			WBGaeDataFactory mockEntityFactory = EasyMock.createMock(WBGaeDataFactory.class);
			gaeUtility.setGaeDataFactory(mockEntityFactory);			
			EasyMock.replay(mockEntityFactory);
			
			Entity returnEntity = gaeUtility.getEmptyEntityWithKey(testObject);
			assertTrue (returnEntity == null);
			EasyMock.verify(mockEntityFactory);
		
		} catch (WBException e)
		{
			assertTrue (false);
		}
	}

	@Test
	public void testobjectFromEntity_OK_keyString()
	{
		try
		{
			GaeDataStoreUtility gaeUtility = new GaeDataStoreUtility();
			Entity entity = PowerMock.createMock(Entity.class);
			Key key = PowerMock.createMock(Key.class);
			HashMap properties = new HashMap();
			properties.put("name", "John Doe");
			properties.put("balance", 10L);
			properties.put("age", 28);
			properties.put("binary", "a".getBytes()[0]);
			properties.put("isBoolean", false);
			EasyMock.expect(entity.getProperties()).andReturn(properties);
			EasyMock.expect(entity.getKey()).andReturn(key);
			EasyMock.expect(key.getName()).andReturn("10");
			EasyMock.replay(entity, key);
				
			DummyClientS client = (DummyClientS) gaeUtility.objectFromEntity(entity, Class.forName("com.webbricks.cms.DummyClientS"));
			assertTrue (client.getAge().compareTo((Integer) properties.get("age")) == 0);
			assertTrue (client.getName().compareTo( (String) properties.get("name")) == 0);
			assertTrue (client.getBalance().compareTo((Long) properties.get("balance")) == 0);
			assertTrue (client.getBinary() == (Byte) properties.get("binary"));
			assertTrue (client.getId().compareTo("10") == 0);
			assertTrue (client.getIsBoolean() == false);
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testobjectFromEntity_OK_keyLong()
	{
		try
		{
			GaeDataStoreUtility gaeUtility = new GaeDataStoreUtility();
			Entity entity = PowerMock.createMock(Entity.class);
			Key key = PowerMock.createMock(Key.class);
			HashMap properties = new HashMap();
			properties.put("name", "John Doe");
			properties.put("balance", 10L);
			properties.put("age", 28);
			properties.put("binary", "a".getBytes()[0]);
			properties.put("summary", new Text("another summary"));
			EasyMock.expect(entity.getProperties()).andReturn(properties);
			EasyMock.expect(entity.getKey()).andReturn(key);
			EasyMock.expect(key.getId()).andReturn(10L);
			EasyMock.replay(entity, key);
				
			DummyClientL client = (DummyClientL) gaeUtility.objectFromEntity(entity, Class.forName("com.webbricks.cms.DummyClientL"));
			assertTrue (client.getAge().compareTo((Integer)properties.get("age"))==0);
			assertTrue (client.getName().compareTo( (String) properties.get("name")) == 0);
			assertTrue (client.getBalance().compareTo((Long) properties.get("balance"))==0);
			assertTrue (client.getBinary() == (Byte) properties.get("binary"));
			assertTrue (client.getId().compareTo(10L) == 0);
			assertTrue (client.getSummary().compareTo("another summary") == 0);
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testobjectFromEntity_privateMembers()
	{
		try
		{
			GaeDataStoreUtility gaeUtility = new GaeDataStoreUtility();
			Entity entity = PowerMock.createMock(Entity.class);
			HashMap properties = new HashMap();
			properties.put("name", "John Doe");
			properties.put("balance", 10L);
			properties.put("age", 28);
			properties.put("binary", "a".getBytes()[0]);
			EasyMock.expect(entity.getProperties()).andReturn(properties);
			EasyMock.replay(entity);
				
			DummyPrivateClientS client = (DummyPrivateClientS) gaeUtility.objectFromEntity(entity, Class.forName("com.webbricks.cms.DummyPrivateClientS"));
		} catch (Exception e)
		{
			assertTrue (false);
		}
	}

	@Test
	public void testobjectFromEntity_integerProperty()
	{
		try
		{
			GaeDataStoreUtility gaeUtility = new GaeDataStoreUtility();
			Entity entity = PowerMock.createMock(Entity.class);
			Key key = PowerMock.createMock(Key.class);
			HashMap properties = new HashMap();
			properties.put("name", "John Doe");
			properties.put("id", 10L);
			properties.put("age", new Integer(28));
			properties.put("binary", "a".getBytes()[0]);
			properties.put("summary", new Text("integer summary"));
			EasyMock.expect(entity.getProperties()).andReturn(properties);
			EasyMock.expect(entity.getKey()).andReturn(key);
			EasyMock.expect(key.getId()).andReturn(10L);
			EasyMock.replay(entity, key);
			
			DummyClientL client = (DummyClientL) gaeUtility.objectFromEntity(entity, Class.forName("com.webbricks.cms.DummyClientL"));
			assertTrue (10L == client.getId());
			assertTrue (client.getName().compareTo("John Doe") == 0);
			assertTrue (28 == client.getAge());
			assertTrue (client.getSummary().compareTo("integer summary") == 0);
		} catch (Exception e)
		{
			assertTrue (false);
		}
	}

	@Test
	public void testobjectFromEntity_nullProperty()
	{
		try
		{
			GaeDataStoreUtility gaeUtility = new GaeDataStoreUtility();
			Entity entity = PowerMock.createMock(Entity.class);
			Key key = PowerMock.createMock(Key.class);
			HashMap properties = new HashMap();
			properties.put("name", "John Doe");
			properties.put("id", 10L);
			properties.put("age", null);
			properties.put("binary", "a".getBytes()[0]);
			properties.put("summary", null);
			EasyMock.expect(entity.getProperties()).andReturn(properties);
			EasyMock.expect(entity.getKey()).andReturn(key);
			EasyMock.expect(key.getId()).andReturn(10L);
			EasyMock.replay(entity, key);
			
			DummyClientL client = (DummyClientL) gaeUtility.objectFromEntity(entity, Class.forName("com.webbricks.cms.DummyClientL"));
			assertTrue (10L == client.getId());
			assertTrue (client.getName().compareTo("John Doe") == 0);
			assertTrue (null == client.getAge());
			assertTrue (null == client.getSummary());
		} catch (Exception e)
		{
			assertTrue (false);
		}
	}

	@Test
	public void testobjectFromEntity_longProperty()
	{
		try
		{
			GaeDataStoreUtility gaeUtility = new GaeDataStoreUtility();
			Entity entity = PowerMock.createMock(Entity.class);
			Key key = PowerMock.createMock(Key.class);
			HashMap properties = new HashMap();
			properties.put("name", "John Doe");
			properties.put("id", 10L);
			properties.put("age", new Long(28));
			properties.put("binary", "a".getBytes()[0]);
			properties.put("summary", new Text("this is the summary"));
			EasyMock.expect(entity.getProperties()).andReturn(properties);
			EasyMock.expect(entity.getKey()).andReturn(key);
			EasyMock.expect(key.getId()).andReturn(10L);
			EasyMock.replay(entity, key);
			
			DummyClientL client = (DummyClientL) gaeUtility.objectFromEntity(entity, Class.forName("com.webbricks.cms.DummyClientL"));
			assertTrue (10L == client.getId());
			assertTrue (client.getName().compareTo("John Doe") == 0);
			assertTrue (28 == client.getAge());
			assertTrue (client.getSummary().compareTo("this is the summary") == 0);
		} catch (Exception e)
		{
			assertTrue (false);
		}
	}

	
	@Test
	public void testentityFromObject_OK()
	{
		try
		{
			GaeDataStoreUtility gaeUtility = new GaeDataStoreUtility();
			DummyClientS client = new DummyClientS();
			client.setAge(10);
			client.setName("Ion Ion");
			client.setBalance(20L);
			client.setBinary(new Byte("7").byteValue());
			client.setId("testkey");
			client.setSummary("test summary");
			client.setIsBoolean(true);
			Entity entity = PowerMock.createNiceMock(Entity.class);
		
			Capture<String> captureNameName = new Capture<String>();
			Capture<String> captureNameAge = new Capture<String>();
			Capture<String> captureNameBalance = new Capture<String>();
			Capture<String> captureNameBinary = new Capture<String>();
			Capture<String> captureNameSummary = new Capture<String>();
			Capture<String> captureNameBoolean = new Capture<String>();
			Capture<String> captureName = new Capture<String>();
			Capture<Integer> captureAge = new Capture<Integer>();
			Capture<Long> captureBalance = new Capture<Long>();
			Capture<Byte> captureByte = new Capture<Byte>();
			Capture<Text> captureSummary = new Capture<Text>();
			Capture<Boolean> captureBoolean = new Capture<Boolean>();
			// the following order matters as the fields in 
			// DummyClientS are declared in the order: age, name,balance, binary 
			entity.setProperty(EasyMock.capture(captureNameAge), EasyMock.captureInt(captureAge));
			entity.setProperty(EasyMock.capture(captureNameName), EasyMock.capture(captureName));
			entity.setProperty(EasyMock.capture(captureNameBalance), EasyMock.captureLong(captureBalance));
			entity.setProperty(EasyMock.capture(captureNameBinary), EasyMock.captureByte(captureByte));
			entity.setUnindexedProperty(EasyMock.capture(captureNameSummary), EasyMock.capture(captureSummary));
			entity.setProperty(EasyMock.capture(captureNameBoolean), EasyMock.captureBoolean(captureBoolean));
			
			EasyMock.replay(entity);
				
			Entity entityResult = gaeUtility.entityFromObject(entity, client);
			
			assertTrue(captureName.getValue().compareTo(client.getName()) == 0);
			assertTrue(captureAge.getValue().compareTo(client.getAge()) == 0);
			assertTrue(captureBalance.getValue().compareTo(client.getBalance()) == 0);
			assertTrue(captureByte.getValue().compareTo(client.getBinary()) == 0);
			assertTrue(captureSummary.getValue().getValue().compareTo("test summary") == 0);
			assertTrue(captureBoolean.getValue() == true);
			
			assertTrue (captureNameAge.getValue().equals("age"));
			assertTrue (captureNameBoolean.getValue().equals("isBoolean"));
			assertTrue (captureNameName.getValue().equals("name"));
			assertTrue (captureNameBalance.getValue().equals("balance"));
			assertTrue (captureNameBinary.getValue().equals("binary"));
			assertTrue (captureNameSummary.getValue().equals("summary"));
			
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testentityFromObject_privateMembers()
	{
		try
		{
			GaeDataStoreUtility gaeUtility = new GaeDataStoreUtility();
			DummyPrivateClientS client = new DummyPrivateClientS();
			Entity entity = PowerMock.createNiceMock(Entity.class);
		
			PowerMock.replay(entity);
			Entity entityResult = gaeUtility.entityFromObject(entity, client);
			
			PowerMock.verify(entity);
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testGetGaeDataFactory_OK()
	{
		GaeDataStoreUtility gaeUtility = new GaeDataStoreUtility();
		assertTrue (gaeUtility.getGaeDataFactory() != null);
	}
}
