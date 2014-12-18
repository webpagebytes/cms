package com.webpagebytes.cms.engine;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import com.webpagebytes.cms.engine.URLMatcher;
import com.webpagebytes.cms.engine.URLMatcherResult;

@RunWith(PowerMockRunner.class)
public class TestUrlMatcher {

private URLMatcher urlMarcher;

@Before
public void setUp()
{
	urlMarcher = new URLMatcher();
	Set<String> patterns = new HashSet<String>();
	patterns.add("/");
	patterns.add("/test");
	patterns.add("/xyz/abc");
	patterns.add("/111/222/333/");
	patterns.add("/test/");
	patterns.add("/test-{id}");
	patterns.add("/news-{id}");
	patterns.add("/culture-{*}-{id}");
	patterns.add("/articles/{id}");
	patterns.add("/test_{keywords}_{key}");
	patterns.add("/test_{keywords}/all-{key}");
	patterns.add("/test_{keywords}-{key}/rest-{action}_{param}");
	urlMarcher.initialize(patterns, 9L);
}

@Test 
public void test_matchSubUrls_ok_one_param_prefix()
{
	Map<String, String> params = urlMarcher.matchSubUrls("test-{id}", "test-abc");
	Map<String, String> expectParams = new HashMap<String, String>();
	expectParams.put("id", "abc");	
	assertTrue(params.equals(expectParams));
}

@Test 
public void test_matchSubUrls_fail_one_param_prefix()
{
	Map<String, String> params = urlMarcher.matchSubUrls("testX-{id}", "testY-abc");
	assertTrue(params == null);
}

@Test 
public void test_matchSubUrls_ok_one_param_suffix()
{
	Map<String, String> params = urlMarcher.matchSubUrls("{id}-test", "abc-test");
	Map<String, String> expectParams = new HashMap<String, String>();
	expectParams.put("id", "abc");	
	assertTrue(params.equals(expectParams));
}

@Test 
public void test_matchSubUrls_fail_one_param_suffix1()
{
	Map<String, String> params = urlMarcher.matchSubUrls("{id}-testX", "abc-testY");
	assertTrue(params == null);
}

@Test 
public void test_matchSubUrls_fail_one_param_suffix2()
{
	Map<String, String> params = urlMarcher.matchSubUrls("{id}-testX", "abctestX");
	assertTrue(params == null);
}

@Test 
public void test_matchSubUrls_ok_one_param()
{
	Map<String, String> params = urlMarcher.matchSubUrls("{id}", "abc");
	Map<String, String> expectParams = new HashMap<String, String>();
	expectParams.put("id", "abc");	
	assertTrue(params.equals(expectParams));
}

@Test 
public void test_matchSubUrls_ok_one_param_emptyvalue()
{
	Map<String, String> params = urlMarcher.matchSubUrls("test-{id}", "test-");
	Map<String, String> expectParams = new HashMap<String, String>();
	expectParams.put("id", "");	
	assertTrue(params.equals(expectParams));
}

@Test 
public void test_matchSubUrls_ok_two_param_asterix()
{
	Map<String, String> params = urlMarcher.matchSubUrls("test-{*}-{id}", "test-key1-key2-12");
	Map<String, String> expectParams = new HashMap<String, String>();
	expectParams.put("*", "key1-key2");
	expectParams.put("id", "12");
	assertTrue(params.equals(expectParams));
}

@Test 
public void test_matchSubUrls_ok_two_params()
{
	Map<String, String> params = urlMarcher.matchSubUrls("{id}-{key}", "abc-test");
	Map<String, String> expectParams = new HashMap<String, String>();
	expectParams.put("id", "abc");	
	expectParams.put("key", "test");	
	assertTrue(params.equals(expectParams));
}

@Test 
public void test_matchSubUrls_fail_two_params()
{
	Map<String, String> params = urlMarcher.matchSubUrls("{id}_{key}", "abc-test");
	assertTrue(params == null);
}

@Test 
public void test_matchSubUrls_ok_two_params_prefix()
{
	Map<String, String> params = urlMarcher.matchSubUrls("sports-{id}-{key}", "sports-abc-test");
	Map<String, String> expectParams = new HashMap<String, String>();
	expectParams.put("id", "abc");	
	expectParams.put("key", "test");	
	assertTrue(params.equals(expectParams));
}

@Test 
public void test_matchSubUrls_ok_two_params_prefix_suffix()
{
	Map<String, String> params = urlMarcher.matchSubUrls("hot-{id}-{key}-news", "hot-abc-test-news");
	Map<String, String> expectParams = new HashMap<String, String>();
	expectParams.put("id", "abc");	
	expectParams.put("key", "test");	
	assertTrue(params.equals(expectParams));
}

@Test 
public void test_matchSubUrls_ok_two_params_nodelims()
{
	Map<String, String> params = urlMarcher.matchSubUrls("{id}test{key}", "xyztestabc");
	Map<String, String> expectParams = new HashMap<String, String>();
	expectParams.put("id", "xyz");	
	expectParams.put("key", "abc");	
	assertTrue(params.equals(expectParams));
}

@Test 
public void test_matchSubUrls_ok_noparams()
{
	Map<String, String> params = urlMarcher.matchSubUrls("test", "test");
	Map<String, String> expectParams = new HashMap<String, String>();
	assertTrue(params.equals(expectParams));
}

@Test 
public void test_matchSubUrls_fail_noparams()
{
	Map<String, String> params = urlMarcher.matchSubUrls("testX", "testY");
	assertTrue(params == null);
}

@Test 
public void test_matchSubUrls_ok_noparams_emptystring()
{
	Map<String, String> params = urlMarcher.matchSubUrls("", "");
	Map<String, String> expectParams = new HashMap<String, String>();
	assertTrue(params.equals(expectParams));
}

@Test 
public void test_matchSubUrls_fail_wrongformatted1()
{
	Map<String, String> params = urlMarcher.matchSubUrls("test-{id-{key}", "test-123-abc");
	assertTrue(params == null);
}

@Test 
public void test_matchSubUrls_fail_wrongformatted2()
{
	Map<String, String> params = urlMarcher.matchSubUrls("test-{id", "test-123-abc");
	assertTrue(params == null);
}

@Test
public void test_matchUrlToPattern_ok_noparam_twolevels()
{
	URLMatcherResult result = urlMarcher.matchUrlToPattern("/xyz/abc");
	assertTrue (result != null);
	assertTrue (result.getUrlPattern().equals("/xyz/abc"));
	assertTrue (result.getUrlRequest().equals("/xyz/abc"));
	assertTrue (result.getPatternParams() == null);
}

@Test
public void test_matchUrlToPattern_ok_noparam_fourlevels()
{
	URLMatcherResult result = urlMarcher.matchUrlToPattern("/111/222/333/");
	assertTrue (result != null);
	assertTrue (result.getUrlPattern().equals("/111/222/333/"));
	assertTrue (result.getPatternParams() == null);
}

@Test
public void test_matchUrlToPattern_ok_oneparam_onelevel1()
{
	URLMatcherResult result = urlMarcher.matchUrlToPattern("/test-123");
	assertTrue (result != null);
	assertTrue (result.getUrlPattern().equals("/test-{id}"));
	assertTrue (result.getUrlRequest().equals("/test-123"));
	
	Map<String, String> params = new HashMap<String, String>();
	params.put("id", "123");
	assertTrue (result.getPatternParams().equals(params));
}

@Test
public void test_matchUrlToPattern_ok_oneparam_onelevel2()
{
	URLMatcherResult result = urlMarcher.matchUrlToPattern("/news-a");
	assertTrue (result != null);
	assertTrue (result.getUrlPattern().equals("/news-{id}"));
	
	Map<String, String> params = new HashMap<String, String>();
	params.put("id", "a");
	assertTrue (result.getPatternParams().equals(params));
}

@Test
public void test_matchUrlToPattern_ok_oneparam_onelevel()
{
	URLMatcherResult result = urlMarcher.matchUrlToPattern("/test-123");
	assertTrue (result != null);
	assertTrue (result.getUrlPattern().equals("/test-{id}"));
	
	Map<String, String> params = new HashMap<String, String>();
	params.put("id", "123");
	assertTrue (result.getPatternParams().equals(params));
}

@Test
public void test_matchUrlToPattern_ok_twoparams_onelevel()
{
	URLMatcherResult result = urlMarcher.matchUrlToPattern("/test_news-sports-lifestyle_xyz");
	assertTrue (result != null);
	assertTrue (result.getUrlPattern().equals("/test_{keywords}_{key}"));
	
	Map<String, String> params = new HashMap<String, String>();
	params.put("keywords", "news-sports-lifestyle");
	params.put("key", "xyz");
	assertTrue (result.getPatternParams().equals(params));
}

@Test
public void test_matchUrlToPattern_ok_twoparams_twolevels()
{
	URLMatcherResult result = urlMarcher.matchUrlToPattern("/test_news-sports/all-1ab");
	assertTrue (result != null);
	assertTrue (result.getUrlPattern().equals("/test_{keywords}/all-{key}"));
	
	Map<String, String> params = new HashMap<String, String>();
	params.put("keywords", "news-sports");
	params.put("key", "1ab");
	assertTrue (result.getPatternParams().equals(params));
}

@Test
public void test_matchUrlToPattern_ok_fourparams_twolevels()
{
	URLMatcherResult result = urlMarcher.matchUrlToPattern("/test_news-sports/rest-get_abc");
	
	assertTrue (result != null);
	assertTrue (result.getUrlPattern().equals("/test_{keywords}-{key}/rest-{action}_{param}"));
	
	Map<String, String> params = new HashMap<String, String>();
	params.put("keywords", "news");
	params.put("key", "sports");
	params.put("action", "get");
	params.put("param", "abc");
	assertTrue (result.getPatternParams().equals(params));
}

@Test
public void test_matchUrlToPattern_ok_fourparams_twolevels_urlparams()
{
	URLMatcherResult result = urlMarcher.matchUrlToPattern("/test_news-sports/rest-get_abc?param1=1&param2=2");
	
	assertTrue (result != null);
	assertTrue (result.getUrlPattern().equals("/test_{keywords}-{key}/rest-{action}_{param}"));
	
	Map<String, String> params = new HashMap<String, String>();
	params.put("keywords", "news");
	params.put("key", "sports");
	params.put("action", "get");
	params.put("param", "abc");
	assertTrue (result.getPatternParams().equals(params));
}

@Test
public void test_matchUrlToPattern_ok_oneparam_onelevel_asterix()
{
	URLMatcherResult result = urlMarcher.matchUrlToPattern("/culture-key1-key2-key3_key4-key5-12");
	
	assertTrue (result != null);
	assertTrue (result.getUrlPattern().equals("/culture-{*}-{id}"));
	
	Map<String, String> params = new HashMap<String, String>();
	params.put("*", "key1-key2-key3_key4-key5");
	params.put("id", "12");
	assertTrue (result.getPatternParams().equals(params));
}

@Test
public void test_matchUrlToPattern_ok_zerolevel()
{
	URLMatcherResult result = urlMarcher.matchUrlToPattern("abc");	
	assertTrue (result == null);
}

@Test
public void test_matchUrlToPattern_fail_onelevel()
{
	URLMatcherResult result = urlMarcher.matchUrlToPattern("/mysite");	
	assertTrue (result == null);
}

@Test
public void test_matchUrlToPattern_mixed_level1_and_two()
{
	URLMatcher urlMarcher = new URLMatcher();
	Set<String> patterns = new HashSet<String>();
	patterns.add("/");
	patterns.add("/news/{*}-{key}");
	patterns.add("/{language}/{keywords}-{key}");
	patterns.add("/about-us");
	urlMarcher.initialize(patterns, 10L);
	URLMatcherResult result = urlMarcher.matchUrlToPattern("/news/aaa-222");	
	Map<String, String> params = new HashMap<String, String>();
	params.put("key", "222");
	params.put("*", "aaa");
	
	assertTrue (result.getUrlPattern().equals("/news/{*}-{key}"));
	assertTrue (result.getPatternParams().equals(params));	
	
}
}
