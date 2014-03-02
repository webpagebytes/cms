package com.webbricks.datautility.local;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import com.webbricks.cmsdata.WBProject;
import com.webbricks.cmsdata.WBUri;
import com.webbricks.cmsdata.WBWebPage;
import com.webbricks.datautility.local.WBLocalDataStoreDao.WBLocalQueryOperator;
import com.webbricks.datautility.local.WBLocalDataStoreDao.WBLocalSortDirection;

@RunWith(PowerMockRunner.class)
public class TestWBLocalDataStoreDao {
private String dbPath = "~/testUnits";

@Test
public void test_getRecord()
{
	WBLocalDataStoreDao dao = new WBLocalDataStoreDao(dbPath);
	try
	{
		WBUri uri = new WBUri();
		uri.setExternalKey("123");
		uri.setUri("/test");
		uri.setEnabled(1);
		uri.setLastModified(Calendar.getInstance().getTime());
		uri.setResourceType(1);
		uri.setHttpOperation("GET");
		WBUri newUri = dao.addRecord(uri, "key");

		WBUri uriGet = (WBUri) dao.getRecord(WBUri.class, "key", newUri.getKey());
		assertTrue(uriGet.getLastModified().getTime() == uri.getLastModified().getTime());
		assertTrue(uri.getUri().equals(uriGet.getUri()));
		assertTrue(uri.getEnabled().equals(uriGet.getEnabled()));
	} catch (Exception e)
	{
		assertTrue(false);
	}
	assertTrue(true);
}

@Test
public void test_addRecord()
{
	WBLocalDataStoreDao dao = new WBLocalDataStoreDao(dbPath);
	try
	{
		WBUri uri = new WBUri();
		uri.setExternalKey("123");
		uri.setUri("/test");
		uri.setEnabled(1);
		uri.setLastModified(Calendar.getInstance().getTime());
		uri.setResourceType(1);
		uri.setHttpOperation("GET");

		WBUri newUri = dao.addRecord(uri, "key");
		assertTrue (newUri.getKey() != null);
	} catch (Exception e)
	{
		assertTrue(false);
	}
	assertTrue(true);
}

@Test
public void test_addRecordWithKey()
{
	WBLocalDataStoreDao dao = new WBLocalDataStoreDao(dbPath);
	try
	{
		WBProject project = new WBProject();
		project.setKey(UUID.randomUUID().toString());
		project.setLastModified(Calendar.getInstance().getTime());
		project.setDefaultLanguage("en");
		project.setSupportedLanguages("en");
	
		WBProject newProject = dao.addRecordWithKey(project, "key");
		assertTrue (newProject.getKey() != null);
	} catch (Exception e)
	{
		assertTrue(false);
	}
	assertTrue(true);
}

@Test
public void test_addRecordClob()
{
	WBLocalDataStoreDao dao = new WBLocalDataStoreDao(dbPath);
	try
	{
		WBWebPage page = new WBWebPage();
		page.setExternalKey("123");
		page.setName("xyz");
		page.setContentType("text/html");
		page.setHtmlSource("");
		page.setIsTemplateSource(1);
		page.setHash(1L);
		page.setLastModified(Calendar.getInstance().getTime());
	
		WBWebPage newPage = dao.addRecord(page, "key");
		assertTrue (newPage.getKey() != null);
	} catch (Exception e)
	{
		assertTrue(false);
	}
	assertTrue(true);
}

@Test
public void test_updateRecord()
{
	WBLocalDataStoreDao dao = new WBLocalDataStoreDao(dbPath);
	try
	{
		WBUri uri = new WBUri();
		uri.setExternalKey("123");
		uri.setUri("/test");
		uri.setEnabled(1);
		uri.setLastModified(Calendar.getInstance().getTime());
		uri.setResourceType(1);
		uri.setHttpOperation("GET");

		WBUri newUri = dao.addRecord(uri, "key");
		assertTrue (newUri.getKey() != null);
		
		newUri.setUri("/newtest");
		dao.updateRecord(newUri, "key");
		
		WBUri tempUri = (WBUri) dao.getRecord(WBUri.class, "key", newUri.getKey());
		assertTrue(tempUri.getUri().equals(newUri.getUri()));
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
	assertTrue(true);
}


@Test
public void test_query_more_properties()
{
	WBLocalDataStoreDao dao = new WBLocalDataStoreDao(dbPath);
	try
	{
		WBUri uri = new WBUri();
		String guid = UUID.randomUUID().toString();
		uri.setExternalKey(guid);
		uri.setUri("/test");
		uri.setEnabled(1);
		uri.setLastModified(Calendar.getInstance().getTime());
		uri.setResourceType(1);
		uri.setHttpOperation("GET");

		WBUri newUri = dao.addRecord(uri, "key");
		assertTrue (newUri.getKey() != null);
		
		Set<String> properties = new HashSet<String>();
		properties.add("externalKey");
		properties.add("uri");
		
		Map<String, WBLocalQueryOperator> operators = new HashMap<String, WBLocalQueryOperator>();
		operators.put("externalKey", WBLocalQueryOperator.EQUAL);
		operators.put("uri", WBLocalQueryOperator.NOT_EQUAL);
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("externalKey", guid);
		values.put("uri", "xyz");
		
		List<Object> records = dao.query(WBUri.class, properties, operators, values);
		
		assertTrue(records.size() == 1);
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
	assertTrue(true);
}

@Test
public void test_query_with_sort_desc()
{
	WBLocalDataStoreDao dao = new WBLocalDataStoreDao(dbPath);
	try
	{
		String guidUri = UUID.randomUUID().toString();

		WBUri uri1 = new WBUri();
		uri1.setExternalKey(UUID.randomUUID().toString());
		uri1.setUri(guidUri);
		uri1.setEnabled(1);
		uri1.setLastModified(Calendar.getInstance().getTime());
		uri1.setResourceType(1);
		uri1.setHttpOperation("GET");

		WBUri newUri1 = dao.addRecord(uri1, "key");
		assertTrue (newUri1.getKey() != null);

		WBUri uri2 = new WBUri();
		uri2.setExternalKey(UUID.randomUUID().toString());
		uri2.setUri(guidUri);
		uri2.setEnabled(1);
		uri2.setLastModified(Calendar.getInstance().getTime());
		uri2.setResourceType(1);
		uri2.setHttpOperation("POST");

		WBUri newUri2 = dao.addRecord(uri2, "key");
		assertTrue (newUri2.getKey() != null);

		Set<String> properties = new HashSet<String>();
		properties.add("uri");
		
		Map<String, WBLocalQueryOperator> operators = new HashMap<String, WBLocalQueryOperator>();
		operators.put("uri", WBLocalQueryOperator.EQUAL);
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("uri", guidUri);
		
		List<Object> records = dao.queryWithSort(WBUri.class, properties, operators, values, "httpOperation", WBLocalSortDirection.DESCENDING);
		
		assertTrue(records.size() == 2);
		WBUri result1 = (WBUri)records.get(0);
		WBUri result2 = (WBUri)records.get(1);
		
		assertTrue(result1.getHttpOperation().equals("POST"));
		assertTrue(result2.getHttpOperation().equals("GET"));
		
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
	assertTrue(true);
}

@Test
public void test_query_with_sort_asc()
{
	WBLocalDataStoreDao dao = new WBLocalDataStoreDao(dbPath);
	try
	{
		String guidUri = UUID.randomUUID().toString();

		WBUri uri1 = new WBUri();
		uri1.setExternalKey(UUID.randomUUID().toString());
		uri1.setUri(guidUri);
		uri1.setEnabled(1);
		uri1.setLastModified(Calendar.getInstance().getTime());
		uri1.setResourceType(1);
		uri1.setHttpOperation("GET");

		WBUri newUri1 = dao.addRecord(uri1, "key");
		assertTrue (newUri1.getKey() != null);

		WBUri uri2 = new WBUri();
		uri2.setExternalKey(UUID.randomUUID().toString());
		uri2.setUri(guidUri);
		uri2.setEnabled(1);
		uri2.setLastModified(Calendar.getInstance().getTime());
		uri2.setResourceType(1);
		uri2.setHttpOperation("POST");

		WBUri newUri2 = dao.addRecord(uri2, "key");
		assertTrue (newUri2.getKey() != null);

		Set<String> properties = new HashSet<String>();
		properties.add("uri");
		
		Map<String, WBLocalQueryOperator> operators = new HashMap<String, WBLocalQueryOperator>();
		operators.put("uri", WBLocalQueryOperator.EQUAL);
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("uri", guidUri);
		
		List<Object> records = dao.queryWithSort(WBUri.class, properties, operators, values, "httpOperation", WBLocalSortDirection.ASCENDING);
		
		assertTrue(records.size() == 2);
		WBUri result1 = (WBUri)records.get(0);
		WBUri result2 = (WBUri)records.get(1);
		
		assertTrue(result1.getHttpOperation().equals("GET"));
		assertTrue(result2.getHttpOperation().equals("POST"));
		
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
	assertTrue(true);
}

@Test
public void test_query_no_conditions()
{
	WBLocalDataStoreDao dao = new WBLocalDataStoreDao(dbPath);
	try
	{
		String guidUri = UUID.randomUUID().toString();

		Set<String> properties = new HashSet<String>();
		
		Map<String, WBLocalQueryOperator> operators = new HashMap<String, WBLocalQueryOperator>();
		Map<String, Object> values = new HashMap<String, Object>();
		
		List<Object> records = dao.queryWithSort(WBUri.class, properties, operators, values, "uri", WBLocalSortDirection.ASCENDING);
		
		assertTrue(records.size() >= 0);
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
	assertTrue(true);
}

@Test
public void test_deleteRecord()
{
	WBLocalDataStoreDao dao = new WBLocalDataStoreDao(dbPath);
	try
	{
		WBUri uri = new WBUri();
		uri.setExternalKey("123");
		uri.setUri("/test");
		uri.setEnabled(1);
		uri.setLastModified(Calendar.getInstance().getTime());
		uri.setResourceType(1);
		uri.setHttpOperation("GET");

		WBUri newUri = dao.addRecord(uri, "key");
		assertTrue (newUri.getKey() != null);
		
		dao.deleteRecord(newUri.getClass(), "key", newUri.getKey());
			
	} catch (Exception e)
	{
		assertTrue(false);
	}
	assertTrue(true);
}


}
