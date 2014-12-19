package com.webpagebytes.cms.engine;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.*;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.webpagebytes.cms.WPBFilePath;
import com.webpagebytes.cms.WPBFileStorage;
import com.webpagebytes.cms.WPBFilesCache;
import com.webpagebytes.cms.cmsdata.WPBFile;
import com.webpagebytes.cms.engine.FileContentBuilder;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.exception.WPBIOException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FileContentBuilder.class})
public class TestFileContentBuilder {

private WPBCacheInstances cacheInstancesMock;
private WPBFilesCache filesCacheMock;
private FileContentBuilder fileContentBuilder;
private WPBFileStorage cloudFileStorageMock;

@Before
public void setUp()
{
	cloudFileStorageMock = EasyMock.createMock(WPBFileStorage.class);
	Whitebox.setInternalState(WPBCloudFileStorageFactory.class, "instance", cloudFileStorageMock);
	cacheInstancesMock = EasyMock.createMock(WPBCacheInstances.class);
	filesCacheMock = EasyMock.createMock(WPBFilesCache.class);
	EasyMock.expect(cacheInstancesMock.getWBFilesCache()).andReturn(filesCacheMock);
}

@After
public void tearDown()
{
	Whitebox.setInternalState(WPBCloudFileStorageFactory.class, "instance", (WPBFileStorage)null);
}

@Test
public void test_initialize()
{
	EasyMock.replay(cloudFileStorageMock, cacheInstancesMock, filesCacheMock);
	fileContentBuilder = new FileContentBuilder(cacheInstancesMock);
	fileContentBuilder.initialize();
	EasyMock.verify(cloudFileStorageMock, cacheInstancesMock, filesCacheMock);
}

@Test
public void test_find()
{
	WPBFile fileMock = EasyMock.createMock(WPBFile.class);
	String externalKey = "1234";
	
	try
	{		
		EasyMock.expect(filesCacheMock.getByExternalKey(externalKey)).andReturn(fileMock);		
		EasyMock.replay(cloudFileStorageMock, cacheInstancesMock, filesCacheMock, fileMock);
		
		fileContentBuilder = new FileContentBuilder(cacheInstancesMock);
		WPBFile result = fileContentBuilder.find(externalKey);
		EasyMock.verify(cloudFileStorageMock, cacheInstancesMock, filesCacheMock, fileMock);
		
		assertTrue (result == fileMock);
	} catch (Exception e)
	{
		assertTrue (false);
	}
}

@Test
public void test_getFileContent()
{
	try
	{
		String key = "abc";
		WPBFile fileMock = EasyMock.createMock(WPBFile.class);
		EasyMock.expect(fileMock.getBlobKey()).andReturn(key);
		
		fileContentBuilder = new FileContentBuilder(cacheInstancesMock);
		
		WPBFileStorage fileStorageMock = EasyMock.createMock(WPBFileStorage.class);
		Whitebox.setInternalState(fileContentBuilder, "cloudFileStorage", fileStorageMock);
		
		InputStream isMock = EasyMock.createMock(InputStream.class);
		EasyMock.expect(fileStorageMock.getFileContent(EasyMock.anyObject(WPBFilePath.class))).andReturn(isMock);
		
		EasyMock.replay(cloudFileStorageMock, fileMock, fileStorageMock, isMock);
		InputStream is = fileContentBuilder.getFileContent(fileMock);
		
		assertTrue (isMock == is);
	} catch (Exception e)
	{
		assertTrue(false);
	}	
}

@Test
public void test_getFileContent_exception()
{
	try
	{
		String key = "abc";
		WPBFile fileMock = EasyMock.createMock(WPBFile.class);
		EasyMock.expect(fileMock.getBlobKey()).andReturn(key);
		
		fileContentBuilder = new FileContentBuilder(cacheInstancesMock);
		
		WPBFileStorage fileStorageMock = EasyMock.createMock(WPBFileStorage.class);
		Whitebox.setInternalState(fileContentBuilder, "cloudFileStorage", fileStorageMock);
		
		EasyMock.expect(fileStorageMock.getFileContent(EasyMock.anyObject(WPBFilePath.class))).andThrow(new IOException());
		EasyMock.replay(cloudFileStorageMock, fileMock, fileStorageMock);
		fileContentBuilder.getFileContent(fileMock);
		assertTrue (false);
	} 
	catch (WPBException e)
	{
		assertTrue(true);
		// this is fine
	}
	catch (Exception e)
	{
		assertTrue(false);
	}
	
}


@Test
public void test_writeFileContent()
{
	try
	{
		String content = "value";
		WPBFile fileMock = EasyMock.createMock(WPBFile.class);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ByteArrayInputStream bais = new ByteArrayInputStream(content.getBytes());
		stub(method(FileContentBuilder.class, "getFileContent")).andReturn(bais);
		fileContentBuilder = new FileContentBuilder(cacheInstancesMock);
		fileContentBuilder.writeFileContent(fileMock, bos);
		assertTrue (bos.toString().equals(content));
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_writeFileContent_exception()
{
	try
	{
		WPBFile fileMock = EasyMock.createMock(WPBFile.class);
		InputStream isMock = EasyMock.createMock(InputStream.class);
		OutputStream osMock = EasyMock.createMock(OutputStream.class);
		stub(method(FileContentBuilder.class, "getFileContent")).andReturn(isMock);
		EasyMock.expect(isMock.read(EasyMock.anyObject(byte[].class))).andThrow(new IOException());
		
		EasyMock.replay(cloudFileStorageMock, isMock);
		fileContentBuilder = new FileContentBuilder(cacheInstancesMock);
		fileContentBuilder.writeFileContent(fileMock, osMock);
		assertTrue(false); // we should not gete here
	} 
	catch (WPBIOException e)
	{
		// all good here
	}
	catch (Exception e)
	{
		assertTrue(false);
	}
}

}
