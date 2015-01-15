package com.webpagebytes.cms.local;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.powermock.modules.junit4.PowerMockRunner;

import com.webpagebytes.cms.cmsdata.WPBProject;
import com.webpagebytes.cms.cmsdata.WPBUri;
import com.webpagebytes.cms.local.WPBLocalDataStoreDao;
import com.webpagebytes.cms.local.WPBLocalDataStoreDao.WBLocalQueryOperator;
import com.webpagebytes.cms.local.WPBLocalDataStoreDao.WBLocalSortDirection;

import org.powermock.api.easymock.PowerMock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.mockito.Matchers.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({WPBLocalDataStoreDao.class})
public class TestWBLocalDataStoreDao {
private Map<String, String> dbProps = new HashMap<String, String>();

@Before
public void before()
{
    // just put a anexisting class for the driver
    dbProps.put("driverClass", "java.lang.String");
    }
@Test
public void test_addRecord()
{
	try
	{
		WPBLocalDataStoreDao dao = PowerMockito.spy(new WPBLocalDataStoreDao(dbProps));
		WPBUri uri = new WPBUri();
		
		Connection connectionMock = PowerMock.createMock(Connection.class);
		connectionMock.setAutoCommit(true);
		PowerMockito.doReturn(connectionMock).when(dao, "getConnection");
		
		String sqlStatement = "sql statement";
		PowerMockito.doReturn(sqlStatement).when(dao, "getSQLStringForInsert", Matchers.any(Object.class), Matchers.any(Set.class));
		
		PreparedStatement statementMock = PowerMock.createMock(PreparedStatement.class);
		EasyMock.expect(connectionMock.prepareStatement(sqlStatement)).andReturn(statementMock);
		PowerMockito.doReturn(1).when(dao, "buildStatementForInsertUpdate", any(), any(), any(), any());
		
		EasyMock.expect(statementMock.execute()).andReturn(true);
		
		ResultSet resultSetMock = PowerMock.createMock(ResultSet.class);
		EasyMock.expect(statementMock.getGeneratedKeys()).andReturn(resultSetMock);
		EasyMock.expect(resultSetMock.next()).andReturn(true);
		EasyMock.expect(resultSetMock.getLong(1)).andReturn(1L);
		PowerMockito.doNothing().when(dao, "setObjectProperty", any(), any(), any());
		statementMock.close();
		connectionMock.close();
		PowerMock.replay(connectionMock, statementMock, resultSetMock);
		WPBUri newUri = dao.addRecord(uri, "key");		
		PowerMock.verify(connectionMock, statementMock, resultSetMock);
		assertTrue (newUri == uri);
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
	assertTrue(true);
}

@Test
public void test_getRecord()
{
	try
	{
		Object key = "123";
		Object result = "result";
		WPBLocalDataStoreDao dao = PowerMockito.spy(new WPBLocalDataStoreDao(dbProps));
		
		Connection connectionMock = PowerMock.createMock(Connection.class);
		PowerMockito.doReturn(connectionMock).when(dao, "getConnection");
		
		PreparedStatement statementMock = PowerMock.createMock(PreparedStatement.class);
		String sqlStatement = "SELECT * FROM WPBUri WHERE key=?";
		EasyMock.expect(connectionMock.prepareStatement(sqlStatement)).andReturn(statementMock);
		PowerMockito.doNothing().when(dao, "setPrepareStatementParameter", any(PreparedStatement.class), any(int.class), any(Object.class));
		
		ResultSet resultSetMock = PowerMock.createMock(ResultSet.class);
		EasyMock.expect(statementMock.executeQuery()).andReturn(resultSetMock);	
		EasyMock.expect(resultSetMock.next()).andReturn(true);
		
		PowerMockito.doReturn(result).when(dao, "copyResultSetToObject", any(), any());
		statementMock.close();
		connectionMock.close();
		resultSetMock.close();
		PowerMock.replay(connectionMock, statementMock, resultSetMock);
		Object res = dao.getRecord(WPBUri.class, "key", key);		
		PowerMock.verify(connectionMock, statementMock, resultSetMock);
		assertTrue (res == result);
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
	assertTrue(true);
}

@Test
public void test_addRecordWithKey()
{
	
	try
	{
		WPBLocalDataStoreDao dao = PowerMockito.spy(new WPBLocalDataStoreDao(dbProps));
		
		Connection connectionMock = PowerMock.createMock(Connection.class);
		connectionMock.setAutoCommit(true);
		PowerMockito.doReturn(connectionMock).when(dao, "getConnection");
		
		String sqlStatement = "sql statement";
		PowerMockito.doReturn(sqlStatement).when(dao, "getSQLStringForInsert", any(Object.class), any(Set.class));
		
		PreparedStatement statementMock = PowerMock.createMock(PreparedStatement.class);
		EasyMock.expect(connectionMock.prepareStatement(sqlStatement)).andReturn(statementMock);
		PowerMockito.doReturn(1).when(dao, "buildStatementForInsertUpdate", any(), any(), any(), any());
		
		EasyMock.expect(statementMock.execute()).andReturn(true);
		
		statementMock.close();
		connectionMock.close();
		PowerMock.replay(connectionMock, statementMock);
		WPBProject newProject = new WPBProject();
		WPBProject result = dao.addRecordWithKey(newProject, "key");		
		PowerMock.verify(connectionMock, statementMock);
		assertTrue (result == newProject);
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
	assertTrue(true);

}


@Test
public void test_updateRecord()
{

	try
	{
		WPBLocalDataStoreDao dao = PowerMockito.spy(new WPBLocalDataStoreDao(dbProps));
		WPBUri uri = new WPBUri();
		
		Connection connectionMock = PowerMock.createMock(Connection.class);
		connectionMock.setAutoCommit(true);
		PowerMockito.doReturn(connectionMock).when(dao, "getConnection");
		
		String sqlStatement = "UPDATE WPBUri SET ENABLED=?,URI=?,LASTMODIFIED=?,HTTPOPERATION=?,CONTROLLERCLASS=?,RESOURCETYPE=?,RESOURCEEXTERNALKEY=?,EXTERNALKEY=? WHERE key=?";
		PowerMockito.doReturn(sqlStatement).when(dao, "getSQLStringForInsert", any(Object.class), any(Set.class));
		
		PreparedStatement statementMock = PowerMock.createMock(PreparedStatement.class);
		EasyMock.expect(connectionMock.prepareStatement(sqlStatement)).andReturn(statementMock);
		PowerMockito.doReturn(1).when(dao, "buildStatementForInsertUpdate", any(), any(), any(), any());
		Object keyValue = "123";
		PowerMockito.doReturn(keyValue).when(dao, "getObjectProperty", any(Object.class), any(String.class));
		PowerMockito.doNothing().when(dao, "setPrepareStatementParameter", any(PreparedStatement.class), any(int.class), any(Object.class));
		
		EasyMock.expect(statementMock.execute()).andReturn(true);
		
		statementMock.close();
		connectionMock.close();
		PowerMock.replay(connectionMock, statementMock);
		dao.updateRecord(uri, "key");		
		PowerMock.verify(connectionMock, statementMock);
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
	assertTrue(true);	
}


@Test
public void test_query_more_properties()
{
	
	try
	{
		WPBLocalDataStoreDao dao = PowerMockito.spy(new WPBLocalDataStoreDao(dbProps));
		List<WPBUri> result = new ArrayList<WPBUri>();
		
		PowerMockito.doReturn(result).when(dao, "advanceQuery", any(Class.class), any(Set.class), any(Map.class), any(Map.class), any(String.class), any(WBLocalSortDirection.class));
		
		Set<String> properties = new HashSet<String>();
		Map<String, WBLocalQueryOperator> operators = new HashMap<String, WBLocalQueryOperator>();
		Map<String, Object> values = new HashMap<String, Object>();
		
		List<WPBUri> records = dao.query(WPBUri.class, properties, operators, values);
		assertTrue(records == result);
	} catch (Exception e)
	{
		assertTrue(false);
	}
	assertTrue(true);	
}


@Test
public void test_query_no_conditions()
{
	
	try
	{
		WPBLocalDataStoreDao dao = PowerMockito.spy(new WPBLocalDataStoreDao(dbProps));
		List<WPBUri> result = new ArrayList<WPBUri>();
		
		PowerMockito.doReturn(result).when(dao, "advanceQuery", any(Class.class), any(Set.class), any(Map.class), any(Map.class), any(String.class), any(WBLocalSortDirection.class));
		
		Set<String> properties = new HashSet<String>();
		Map<String, WBLocalQueryOperator> operators = new HashMap<String, WBLocalQueryOperator>();
		Map<String, Object> values = new HashMap<String, Object>();
		
		List<WPBUri> records = dao.queryWithSort(WPBUri.class, properties, operators, values, "uri", WBLocalSortDirection.ASCENDING);
		assertTrue(records == result);
	} catch (Exception e)
	{
		assertTrue(false);
	}
	assertTrue(true);	
	
}

@Test
public void test_deleteRecord()
{
	try
	{
		WPBLocalDataStoreDao dao = PowerMockito.spy(new WPBLocalDataStoreDao(dbProps));
		
		Connection connectionMock = PowerMock.createMock(Connection.class);
		connectionMock.setAutoCommit(true);
		PowerMockito.doReturn(connectionMock).when(dao, "getConnection");
		
		String sqlStatement = "UPDATE WBUri SET ENABLED=?,URI=?,LASTMODIFIED=?,HTTPOPERATION=?,CONTROLLERCLASS=?,RESOURCETYPE=?,RESOURCEEXTERNALKEY=?,EXTERNALKEY=? WHERE key=?";
		PowerMockito.doReturn(sqlStatement).when(dao, "getSQLStringForDelete", any(Object.class), any(Set.class));
		
		PreparedStatement statementMock = PowerMock.createMock(PreparedStatement.class);
		EasyMock.expect(connectionMock.prepareStatement(sqlStatement)).andReturn(statementMock);
		Object keyValue = "123";
		PowerMockito.doNothing().when(dao, "setPrepareStatementParameter", any(PreparedStatement.class), any(int.class), any(Object.class));
		
		EasyMock.expect(statementMock.execute()).andReturn(true);
		
		statementMock.close();
		connectionMock.close();
		PowerMock.replay(connectionMock, statementMock);
		dao.deleteRecord(WPBUri.class, "key", keyValue);		
		PowerMock.verify(connectionMock, statementMock);
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
	assertTrue(true);	
	
}


}
