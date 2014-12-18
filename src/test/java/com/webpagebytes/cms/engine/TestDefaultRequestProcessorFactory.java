package com.webpagebytes.cms.engine;
import com.webpagebytes.cms.engine.AjaxRequestProcessor;
import com.webpagebytes.cms.engine.BaseRequestProcessorFactory;
import com.webpagebytes.cms.engine.ResourceRequestProcessor;

import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class TestDefaultRequestProcessorFactory {

	@Test
	public void testCreateResourceRequestProcessor()
	{
		BaseRequestProcessorFactory processorFactory = new BaseRequestProcessorFactory();
		ResourceRequestProcessor processor = processorFactory.createResourceRequestProcessor();
		assertTrue (processor != null);
	}

	@Test
	public void testCreateAjaxRequestProcessor()
	{
		BaseRequestProcessorFactory processorFactory = new BaseRequestProcessorFactory();
		AjaxRequestProcessor processor = processorFactory.createAjaxRequestProcessor();
		assertTrue (processor != null);
	}

}
