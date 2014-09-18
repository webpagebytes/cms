package com.webpagebytes.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;




import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;

import com.webpagebytes.cms.appinterfaces.WBModel;
import com.webpagebytes.cms.cmsdata.WBFile;
import com.webpagebytes.cms.cmsdata.WBWebPage;
import com.webpagebytes.cms.exception.WBException;

@RunWith(PowerMockRunner.class)
@PrepareForTest ({OutputStream.class})
public class TestWBDefaultContentProvider {

private FileContentBuilder fileContentBuilderMock;
private PageContentBuilder pageContentBuilderMock;
private WBDefaultContentProvider contentProvider;

@Before
public void before()
{
	fileContentBuilderMock = EasyMock.createMock(FileContentBuilder.class);
	pageContentBuilderMock = EasyMock.createMock(PageContentBuilder.class);
	contentProvider = new WBDefaultContentProvider(fileContentBuilderMock, pageContentBuilderMock);
}

@Test
public void test_writeFileContent()
{
	try
	{
		String externalKey = "abc";
		OutputStream osMock  = EasyMock.createMock(OutputStream.class);
		WBFile fileMock = EasyMock.createMock(WBFile.class);
		EasyMock.expect(fileContentBuilderMock.find(externalKey)).andReturn(fileMock);
		fileContentBuilderMock.writeFileContent(fileMock, osMock);
		EasyMock.replay(fileContentBuilderMock, pageContentBuilderMock, osMock);
		boolean result = contentProvider.writeFileContent(externalKey, osMock);
		assertTrue (result);
	} catch (WBException e)
	{
		assertTrue(false);
	}	
}

@Test
public void test_writeFileContent_file_not_found()
{
	try
	{
		String externalKey = "abc";
		OutputStream osMock  = EasyMock.createMock(OutputStream.class);
		EasyMock.expect(fileContentBuilderMock.find(externalKey)).andReturn(null);
		EasyMock.replay(fileContentBuilderMock, pageContentBuilderMock, osMock);
		boolean result = contentProvider.writeFileContent(externalKey, osMock);
		assertTrue (result == false);
	} catch (WBException e)
	{
		assertTrue(false);
	}	
}

@Test
public void test_writeFileContent_exception()
{
	try
	{
		String externalKey = "abc";
		OutputStream osMock  = EasyMock.createMock(OutputStream.class);
		EasyMock.expect(fileContentBuilderMock.find(externalKey)).andThrow(new WBException(""));
		EasyMock.replay(fileContentBuilderMock, pageContentBuilderMock, osMock);
		boolean result = contentProvider.writeFileContent(externalKey, osMock);
		assertTrue (result == false);
	} catch (WBException e)
	{
		assertTrue (false);
	}		
}

@Test
public void test_writePageContent()
{
	try
	{
		String externalKey = "abc";
		String content = "content";
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		OutputStream osMock  = EasyMock.createMock(OutputStream.class);
		WBWebPage pageMock = EasyMock.createMock(WBWebPage.class);
		WBModel modelMock = EasyMock.createMock(WBModel.class);
		EasyMock.expect(pageContentBuilderMock.findWebPage(externalKey)).andReturn(pageMock);
		EasyMock.expect(pageContentBuilderMock.buildPageContent(pageMock, modelMock)).andReturn(content);
		EasyMock.replay(fileContentBuilderMock, pageContentBuilderMock, pageMock, modelMock, osMock);
		boolean result = contentProvider.writePageContent(externalKey, modelMock, bos);
		assertTrue (bos.toString().equals(content));
		assertTrue (result);
	} catch (Exception e)
	{
		assertTrue(false);
	}	
}

@Test
public void test_writePageContent_page_not_found()
{
	try
	{
		String externalKey = "abc";
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		OutputStream osMock  = EasyMock.createMock(OutputStream.class);
		WBWebPage pageMock = EasyMock.createMock(WBWebPage.class);
		WBModel modelMock = EasyMock.createMock(WBModel.class);
		EasyMock.expect(pageContentBuilderMock.findWebPage(externalKey)).andReturn(null);
		EasyMock.replay(fileContentBuilderMock, pageContentBuilderMock, pageMock, modelMock, osMock);
		boolean result = contentProvider.writePageContent(externalKey, modelMock, bos);
		assertTrue (result == false);
	} catch (Exception e)
	{
		assertTrue(false);
	}	
}

@Test
public void test_writePageContent_ioexception()
{
	try
	{
		String externalKey = "abc";
		String content = "content";
		OutputStream osMock  = PowerMock.createMock(OutputStream.class);
		WBWebPage pageMock = EasyMock.createMock(WBWebPage.class);
		WBModel modelMock = EasyMock.createMock(WBModel.class);
		EasyMock.expect(pageContentBuilderMock.findWebPage(externalKey)).andReturn(pageMock);
		EasyMock.expect(pageContentBuilderMock.buildPageContent(pageMock, modelMock)).andReturn(content);
		Capture<byte[]> capture = new Capture<byte[]>();
		osMock.write(EasyMock.capture(capture));
		EasyMock.expectLastCall().andThrow(new IOException());
		EasyMock.replay(fileContentBuilderMock, pageContentBuilderMock, pageMock, modelMock, osMock);
		boolean result = contentProvider.writePageContent(externalKey, modelMock, osMock);
		assertTrue (result == false);
		assertTrue ((new String(capture.getValue()).equals(content)));
	} catch (Exception e)
	{
		assertTrue(false);
	}	
}

@Test
public void test_writePageContent_wbexception()
{
	try
	{
		String externalKey = "abc";
		String content = "content";
		OutputStream osMock  = PowerMock.createMock(OutputStream.class);
		WBWebPage pageMock = EasyMock.createMock(WBWebPage.class);
		WBModel modelMock = EasyMock.createMock(WBModel.class);
		EasyMock.expect(pageContentBuilderMock.findWebPage(externalKey)).andReturn(pageMock);
		EasyMock.expect(pageContentBuilderMock.buildPageContent(pageMock, modelMock)).andThrow(new WBException(""));
		EasyMock.replay(fileContentBuilderMock, pageContentBuilderMock, pageMock, modelMock, osMock);
		boolean result = contentProvider.writePageContent(externalKey, modelMock, osMock);
		assertTrue (result == false);
	} catch (Exception e)
	{
		assertTrue(false);
	}	
}

}
