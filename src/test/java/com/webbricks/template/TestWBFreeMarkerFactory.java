package com.webbricks.template;


import org.easymock.EasyMock;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.junit.Assert.*;
import org.junit.Test;
import com.webbricks.cache.WBCacheFactory;
import com.webbricks.cache.WBCacheInstances;
import com.webbricks.cache.WBWebPagesCache;
import com.webbricks.cache.WBWebPageModulesCache;

import freemarker.template.Configuration;

@RunWith(PowerMockRunner.class)
public class TestWBFreeMarkerFactory {

@Test
public void createConfiguration()
{
	WBFreeMarkerFactory factory = new WBFreeMarkerFactory();
	Configuration configuration = factory.createConfiguration();
	assertTrue (configuration != null);
}

@Test
public void createWBFreeMarkerModuleDirective()
{
	WBFreeMarkerFactory factory = new WBFreeMarkerFactory();
	WBFreeMarkerModuleDirective moduleDirective = factory.createWBFreeMarkerModuleDirective();
	assertTrue (moduleDirective != null);	
}

@Test
public void createWBFreeMarkerTemplateLoader()
{
	WBFreeMarkerFactory factory = new WBFreeMarkerFactory();
	WBCacheInstances cacheInstancesMock = PowerMock.createMock(WBCacheInstances.class);
	
	PowerMock.replay(cacheInstancesMock);
	WBFreeMarkerTemplateLoader loader = factory.createWBFreeMarkerTemplateLoader(cacheInstancesMock);	
}

}

