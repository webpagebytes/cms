package com.webpagebytes.controllers;

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

import com.webpagebytes.cmsdata.WBWebPage;
import com.webpagebytes.controllers.WBPageValidator;


@RunWith(PowerMockRunner.class)

public class TestWBPageValidator {

private WBPageValidator pageValidator;
private WBWebPage wbPage;
private Map<String,String> noErrors;

@Before
public void setup()
{
	pageValidator = new WBPageValidator();
	wbPage = new WBWebPage();
	noErrors = new HashMap<String,String>();
}
@Test
public void test_validateCreate_ok()
{
	wbPage.setName("test");
	Map<String,String> errors = pageValidator.validateCreate(wbPage);
	assertTrue (errors.equals(noErrors));
}

@Test
public void test_validateUpdate_ok()
{
	wbPage.setKey(1L);
	wbPage.setName("test");
	Map<String,String> errors = pageValidator.validateUpdate(wbPage);
	assertTrue (errors.equals(noErrors));
}

}
