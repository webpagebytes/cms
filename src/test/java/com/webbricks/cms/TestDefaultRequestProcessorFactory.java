package com.webbricks.cms;
import com.webbricks.cms.DefaultRequestProcessorFactory;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;

@RunWith(PowerMockRunner.class)
public class TestDefaultRequestProcessorFactory {

	@Test
	public void testCreateResourceRequestProcessor()
	{
		DefaultRequestProcessorFactory processorFactory = new DefaultRequestProcessorFactory();
		ResourceRequestProcessor processor = processorFactory.createResourceRequestProcessor();
		assertTrue (processor != null);
	}

	@Test
	public void testCreateAjaxRequestProcessor()
	{
		DefaultRequestProcessorFactory processorFactory = new DefaultRequestProcessorFactory();
		AjaxRequestProcessor processor = processorFactory.createAjaxRequestProcessor();
		assertTrue (processor != null);
	}

}
