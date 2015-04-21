package com.webpagebytes.cms.controllers;

import org.junit.runner.RunWith;

import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;
import java.util.Map;
import java.util.HashMap;

import com.webpagebytes.cms.cmsdata.WPBPage;
import com.webpagebytes.cms.controllers.PageValidator;


@RunWith(PowerMockRunner.class)

public class TestWBPageValidator {

private PageValidator pageValidator;
private WPBPage wbPage;
private Map<String,String> noErrors;

@Before
public void setup()
{
	pageValidator = new PageValidator();
	wbPage = new WPBPage();
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
	wbPage.setExternalKey("1");
	wbPage.setName("test");
	Map<String,String> errors = pageValidator.validateUpdate(wbPage);
	assertTrue (errors.equals(noErrors));
}

}
