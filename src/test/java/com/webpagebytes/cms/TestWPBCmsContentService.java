package com.webpagebytes.cms;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.webpagebytes.cms.appinterfaces.WBContentProvider;
import com.webpagebytes.cms.cache.WBCacheInstances;

@RunWith(PowerMockRunner.class)
@PrepareForTest ({WPBCmsContentService.class})
public class TestWPBCmsContentService {

private	WPBCmsContentService contentService;

@Before
public void setUp()
{
	PowerMock.suppress(PowerMock.method(WPBCmsContentService.class, "createCacheInstances"));
	PowerMock.suppress(PowerMock.method(WPBCmsContentService.class, "createModelBuilder"));
	PowerMock.suppress(PowerMock.method(WPBCmsContentService.class, "createCacheFactory"));
	contentService = PowerMockito.spy(new WPBCmsContentService());	
}

@Test
public void test_getContentProvider()
{
	try
	{
		PowerMockito.doAnswer(new Answer<Void>() {
	        public Void answer(InvocationOnMock invocation) {
	        	WPBCmsContentService contentService = (WPBCmsContentService)invocation.getMock();
	        	WBContentProvider mockProvider = EasyMock.createMock(WBContentProvider.class);
	    		Whitebox.setInternalState(contentService, "contentProvider", mockProvider);
	        	return null;
	        } }).when(contentService, "initializeContentProvider");
		WBContentProvider result = contentService.getContentProvider();
		assertTrue (result != null);
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_getContentProvider_already_set()
{
	try
	{
      	WBContentProvider mockProvider = EasyMock.createMock(WBContentProvider.class);
	    Whitebox.setInternalState(contentService, "contentProvider", mockProvider);
	    WBContentProvider result = contentService.getContentProvider();
		assertTrue (result == mockProvider);
		
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_initializeContentProvider()
{
	try
	{
		PageContentBuilder pageContentBuilderMock = EasyMock.createMock(PageContentBuilder.class);
		PowerMockito.doReturn(pageContentBuilderMock).when(contentService, "createPageContentBuilder", Matchers.any(WBCacheInstances.class), Matchers.any(ModelBuilder.class) );
	
		FileContentBuilder fileContentBuilderMock = EasyMock.createMock(FileContentBuilder.class);
		PowerMockito.doReturn(fileContentBuilderMock).when(contentService, "createFileContentBuilder", Matchers.any(WBCacheInstances.class));
	
		Whitebox.invokeMethod(contentService, "initializeContentProvider");
		
		assertTrue (null != Whitebox.getInternalState(contentService, "contentProvider"));
		
	} catch (Exception e)
	{
		assertTrue (false);
	}
}
}
