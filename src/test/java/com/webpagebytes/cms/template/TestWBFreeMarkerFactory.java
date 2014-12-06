package com.webpagebytes.cms.template;


import org.easymock.EasyMock;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;

import org.junit.Test;

import com.webpagebytes.cms.appinterfaces.WPBCacheFactory;
import com.webpagebytes.cms.appinterfaces.WPBPageModulesCache;
import com.webpagebytes.cms.appinterfaces.WPBWebPagesCache;
import com.webpagebytes.cms.cache.WPBCacheInstances;
import com.webpagebytes.cms.template.FreeMarkerResourcesFactory;
import com.webpagebytes.cms.template.FreeMarkerModuleDirective;
import com.webpagebytes.cms.template.FreeMarkerTemplateLoader;

import freemarker.template.Configuration;

@RunWith(PowerMockRunner.class)
public class TestWBFreeMarkerFactory {

@Test
public void createConfiguration()
{
	FreeMarkerResourcesFactory factory = new FreeMarkerResourcesFactory();
	Configuration configuration = factory.createConfiguration();
	assertTrue (configuration != null);
}

@Test
public void createWBFreeMarkerModuleDirective()
{
	FreeMarkerResourcesFactory factory = new FreeMarkerResourcesFactory();
	FreeMarkerModuleDirective moduleDirective = factory.createWBFreeMarkerModuleDirective();
	assertTrue (moduleDirective != null);	
}

@Test
public void createWBFreeMarkerTemplateLoader()
{
	FreeMarkerResourcesFactory factory = new FreeMarkerResourcesFactory();
	WPBCacheInstances cacheInstancesMock = PowerMock.createMock(WPBCacheInstances.class);
	
	PowerMock.replay(cacheInstancesMock);
	FreeMarkerTemplateLoader loader = factory.createWBFreeMarkerTemplateLoader(cacheInstancesMock);	
}

}

