package com.webpagebytes.cms;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.After;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.webpagebytes.cms.LanguageLocaleManager;
import com.webpagebytes.exception.WBException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LanguageLocaleManager.class})
public class TestLocaleManager {

LanguageLocaleManager localeManager;
@Before
public void setup()
{
	localeManager = new LanguageLocaleManager();
	
}
	
@Test
public void test_loadLocalesfromFile_ok()
{
	String path = "META-INF/config/langs.csv";
	try
	{
		localeManager.loadLocalesfromFile(path);
		
		Map supportedLanguages = localeManager.getSupportedLanguages();
		Map<String, Locale> supportedLanguagesAndCountries = localeManager.getSupportedLanguagesAndCountries();
		
		Map<String, Locale> expectedLanguages = new HashMap<String, Locale>();
		expectedLanguages.put("ar", new Locale("ar"));
		expectedLanguages.put("be", new Locale("be"));

		Map<String, Locale> expectedLangsAndCountries = new HashMap<String, Locale>();
		expectedLangsAndCountries.put("ar", new Locale("ar"));
		expectedLangsAndCountries.put("be", new Locale("be"));
		expectedLangsAndCountries.put("ar_AE", new Locale("ar", "AE"));
		expectedLangsAndCountries.put("be_BY", new Locale("be", "BY"));
		expectedLangsAndCountries.put("bg_BG", new Locale("bg", "BG"));
		
		assertTrue (supportedLanguages.equals(expectedLanguages));
		assertTrue (supportedLanguagesAndCountries.equals(expectedLangsAndCountries));
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_getInstance_fail()
{
	try
	{
		String path = "xyz";
		Whitebox.setInternalState(LanguageLocaleManager.class, "LANGUAGES_CONFIG_FILE", path);
		Whitebox.setInternalState(LanguageLocaleManager.class, "localeManager", (LanguageLocaleManager)null);
		LanguageLocaleManager manager = LanguageLocaleManager.getInstance();
		assertTrue (manager == null);
	} 
	catch (Exception e)
	{
		assertTrue (false);
	}
}


}
