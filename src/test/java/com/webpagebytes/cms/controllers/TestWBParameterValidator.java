package com.webpagebytes.cms.controllers;

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

import com.webpagebytes.cms.cmsdata.WBParameter;
import com.webpagebytes.cms.cmsdata.WBWebPage;
import com.webpagebytes.cms.controllers.WBErrors;
import com.webpagebytes.cms.controllers.ParameterValidator;


@RunWith(PowerMockRunner.class)

public class TestWBParameterValidator {

private ParameterValidator parameterValidator;
private WBParameter wbParameter;
private Map<String,String> noErrors;

@Before
public void setup()
{
	parameterValidator = new ParameterValidator();
	wbParameter = EasyMock.createMock(WBParameter.class);
	noErrors = new HashMap<String,String>();
}
@Test
public void test_validateCreate_ok()
{
	EasyMock.expect(wbParameter.getName()).andReturn("xyz");
	EasyMock.replay(wbParameter);
	Map<String,String> errors = parameterValidator.validateCreate(wbParameter);
	EasyMock.verify(wbParameter);
	assertTrue (errors.equals(noErrors));
}

@Test
public void test_validateUpdate_ok()
{
	EasyMock.expect(wbParameter.getName()).andReturn("xyz");
	EasyMock.replay(wbParameter);
	Map<String,String> errors = parameterValidator.validateUpdate(wbParameter);
	EasyMock.verify(wbParameter);
	assertTrue (errors.equals(noErrors));
}

@Test
public void test_validateCreate_null()
{
	EasyMock.expect(wbParameter.getName()).andReturn(null);
	EasyMock.replay(wbParameter);
	Map<String,String> errors = parameterValidator.validateCreate(wbParameter);
	EasyMock.verify(wbParameter);
	assertTrue (errors.get("name").compareTo(WBErrors.WBPARAMETER_EMPTY_NAME) == 0);
}

@Test
public void test_validateUpdate_null()
{
	EasyMock.expect(wbParameter.getName()).andReturn(null);
	EasyMock.replay(wbParameter);
	Map<String,String> errors = parameterValidator.validateUpdate(wbParameter);
	EasyMock.verify(wbParameter);
	assertTrue (errors.get("name").compareTo(WBErrors.WBPARAMETER_EMPTY_NAME) == 0);
}

@Test
public void test_validateCreate_empty()
{
	EasyMock.expect(wbParameter.getName()).andReturn("");
	EasyMock.replay(wbParameter);
	Map<String,String> errors = parameterValidator.validateCreate(wbParameter);
	EasyMock.verify(wbParameter);
	assertTrue (errors.get("name").compareTo(WBErrors.WBPARAMETER_EMPTY_NAME) == 0);
}

@Test
public void test_validateUpdate_empty()
{
	EasyMock.expect(wbParameter.getName()).andReturn("");
	EasyMock.replay(wbParameter);
	Map<String,String> errors = parameterValidator.validateUpdate(wbParameter);
	EasyMock.verify(wbParameter);
	assertTrue (errors.get("name").compareTo(WBErrors.WBPARAMETER_EMPTY_NAME) == 0);
}

}
