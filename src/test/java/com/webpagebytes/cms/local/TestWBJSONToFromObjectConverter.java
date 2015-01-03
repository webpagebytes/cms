package com.webpagebytes.cms.local;
import java.util.ArrayList;
import java.util.Date;

import org.easymock.EasyMock;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.powermock.core.classloader.annotations.PrepareForTest;

import com.webpagebytes.cms.engine.DummyClientS;
import com.webpagebytes.cms.engine.JSONToFromObjectConverter;

@RunWith(PowerMockRunner.class)
@PrepareForTest (JSONToFromObjectConverter.class)
public class TestWBJSONToFromObjectConverter {

	private org.json.JSONObject json;
	private JSONToFromObjectConverter wbObjectfromJson;
	
	@Before
	public void setup()
	{
		json = new org.json.JSONObject();
		wbObjectfromJson = new JSONToFromObjectConverter();
		
	}
	@Test 
	public void testFieldFromJSON_malformedjson()
	{
		try
		{
			String fieldName = "name";
			org.json.JSONObject jsonMock = EasyMock.createMock(org.json.JSONObject.class);
			EasyMock.expect(jsonMock.getString(fieldName)).andThrow(new org.json.JSONException(""));
			EasyMock.replay(jsonMock);
			String objString = (String)Whitebox.invokeMethod(wbObjectfromJson, "fieldFromJSON", jsonMock, "name", fieldName.getClass());
			assertTrue(objString == null);
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}
	@Test
	public void testFieldFromJSON_ok()
	{
		try
		{
			String name = "John";
			Integer age = 10;
			Long amount = 20L;
			Date date = new Date();
			long balance = 3L;
			int weight = 40;
			json.put("name", name);
			json.put("age", age);
			json.put("amount", amount);
			json.put("balance", balance);
			json.put("date", date.getTime());
			json.put("weight", weight);

			String objString = (String)Whitebox.invokeMethod(wbObjectfromJson, "fieldFromJSON", json, "name", name.getClass());
			assertTrue(objString.compareTo(name) == 0);
			
			Integer objInteger = (Integer)Whitebox.invokeMethod(wbObjectfromJson, "fieldFromJSON", json, "age", age.getClass());
			assertTrue(objInteger.compareTo(age) == 0);
			
			Long objLong = (Long)Whitebox.invokeMethod(wbObjectfromJson, "fieldFromJSON", json, "amount", amount.getClass());
			assertTrue(objLong.compareTo(amount) == 0);
				
			Date objDate = (Date)Whitebox.invokeMethod(wbObjectfromJson, "fieldFromJSON", json, "date", date.getClass());
			assertTrue(objDate.compareTo(date) == 0);

			Class longClass = long.class;
			Long objLong2 = (Long)Whitebox.invokeMethod(wbObjectfromJson, "fieldFromJSON", json, "balance", longClass);
			assertTrue(objLong2.compareTo(balance) == 0);

			Class intClass = int.class;
			Integer objInt2 = (Integer)Whitebox.invokeMethod(wbObjectfromJson, "fieldFromJSON", json, "weight", intClass);
			assertTrue(objInt2.compareTo(weight) == 0);

			Object objNull = Whitebox.invokeMethod(wbObjectfromJson, "fieldFromJSON", json, "null", Byte.class);
			assertTrue(objNull == null);
			
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}
	
	@Test
	public void testObjectFromJSONString()
	{
		try
		{
			String id = "1234";
			json.put("id", id);
			Long balance = 34L;
			json.put("balance", balance);
			Integer age = 10;
			json.put("age", age);
			String name = "Agatha";
			json.put("name", name);
			Integer width = 11;
			json.put("width", width);
			Long height = 12L;
			json.put("height", height);
			
			DummyClientS client = (DummyClientS)wbObjectfromJson.objectFromJSONString(json.toString(), DummyClientS.class);
			assertTrue(client.getId().compareTo(id) == 0);
			assertTrue(client.getName().compareTo(name) == 0);
			assertTrue(client.getAge() == age);
			assertTrue(client.getBalance() == balance);
			assertTrue (client.getHeight() == height);
			assertTrue (client.getWidth() == width);
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}
	
	@Test
	public void testObjectFromJSONString_malformedjson()
	{
		try
		{
			String jsonString = "lorem ipsum";
			DummyClientS client = (DummyClientS)wbObjectfromJson.objectFromJSONString(jsonString, DummyClientS.class);
			assertTrue(client == null);
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testWBJSONToFromObjectConverter()
	{
		try
		{
			DummyClientS client = new DummyClientS();
			int age = 10;
			client.setAge(age);
			String id = "234";
			client.setId(id);
			Long balance = 88L;
			client.setBalance(balance);
			long height = 19L;
			client.setHeight(height);
			int width = 21;
			client.setWidth(width);
			String jsonString = wbObjectfromJson.JSONStringFromObject(client, null);
			org.json.JSONObject jsonObj = new org.json.JSONObject(jsonString);
			assertTrue (jsonObj.getString("id").compareTo(id) == 0);
			assertTrue (age == jsonObj.getInt("age"));
			assertTrue (balance == jsonObj.getLong("balance"));
			assertTrue (height == jsonObj.getLong("height"));
			assertTrue (width == jsonObj.getLong("width"));
		} catch (Exception e)
		{
			assertTrue(false);
		}		
	}
	
	@Test
	public void testJSONStringFromField()
	{
		try
		{
			Integer intValue = 123;
			String objString = (String)Whitebox.invokeMethod(wbObjectfromJson, "JSONStringFromField", intValue, intValue.getClass());
			assertTrue(objString.compareTo("123") == 0);
			
			Long longValue = -1234L;
			objString = (String)Whitebox.invokeMethod(wbObjectfromJson, "JSONStringFromField", longValue, longValue.getClass());
			assertTrue(objString.compareTo("-1234") == 0);
			
			String stringValue = "abc";
			objString = (String)Whitebox.invokeMethod(wbObjectfromJson, "JSONStringFromField", stringValue, stringValue.getClass());
			assertTrue(objString.compareTo("abc") == 0);
		
			Date dateValue = new Date();
			dateValue.setYear(2012);
			dateValue.setMonth(1);
			objString = (String) Whitebox.invokeMethod(wbObjectfromJson, "JSONStringFromField", dateValue, dateValue.getClass());
			assertTrue(objString != null);
			
			long height = 31;
			objString = (String) Whitebox.invokeMethod(wbObjectfromJson, "JSONStringFromField", height, long.class);
			assertTrue(objString.compareTo("31") == 0);

			long width = 32;
			objString = (String) Whitebox.invokeMethod(wbObjectfromJson, "JSONStringFromField", width, int.class);
			assertTrue(objString.compareTo("32") == 0);

			
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}
	
	@Test
	public void testJSONStringFromListObjects()
	{
		ArrayList list = new ArrayList();
		DummyClientS s1 = new DummyClientS();
		s1.setAge(1);
		s1.setBalance(12L);
		DummyClientS s2 = new DummyClientS();
		s2.setId("123");	
		s2.setSummary("test");
		
		list.add(s1);
		list.add(s2);
		String jsonStr = wbObjectfromJson.JSONStringFromListObjects(list);
		try
		{
    		org.json.JSONArray jsonArray = new org.json.JSONArray(jsonStr);
    		assertTrue (jsonArray.length() == 2);
    		org.json.JSONObject obj1 = jsonArray.getJSONObject(0);
    		assertTrue (obj1.getInt("age") == 1);
    		assertTrue (obj1.getLong("balance") == 12L);

            org.json.JSONObject obj2 = jsonArray.getJSONObject(1);
            assertTrue (obj2.getString("id").equals("123"));
            assertTrue (obj2.getString("summary").equals("test"));

    	} catch (JSONException e)
		{
		    assertTrue(false);
		}
	}
}
