package com.webbricks.controllers;

import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.junit.Assert.*;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Map;
import java.util.HashMap;

import com.webbricks.cmsdata.WBWebPage;
import com.webbricks.cmsdata.WBWebPageModule;
import com.webbricks.controllers.WBPageModuleValidator;


@RunWith(PowerMockRunner.class)
public class TestWBPageModuleValidator {

private WBPageModuleValidator pageModuleValidator;
private WBWebPageModule wbPageModuleMock;
private Map<String,String> noErrors;

@Before
public void before()
{
	pageModuleValidator = new WBPageModuleValidator();
	wbPageModuleMock = PowerMock.createMock(WBWebPageModule.class);
	noErrors = new HashMap();
}

@Test
public void test_validateCreate()
{
	PowerMock.replay(wbPageModuleMock);
	Map errors = pageModuleValidator.validateCreate(wbPageModuleMock);
	assertTrue(errors.equals(noErrors));
}

@Test
public void test_validateUpdate()
{
	PowerMock.replay(wbPageModuleMock);
	Map errors = pageModuleValidator.validateUpdate(wbPageModuleMock);
	assertTrue(errors.equals(noErrors));
}

}
