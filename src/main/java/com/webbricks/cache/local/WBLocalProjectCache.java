package com.webbricks.cache.local;

import java.util.HashSet;
import java.util.Set;

import com.webbricks.cache.WBProjectCache;
import com.webbricks.cms.Pair;
import com.webbricks.cmsdata.WBProject;
import com.webbricks.exception.WBIOException;

public class WBLocalProjectCache implements WBProjectCache {
	WBProject project = new WBProject();
	
	public String getDefaultLanguage() throws WBIOException
	{
		return "en";
	}
	public Pair<String, String> getDefaultLocale() throws WBIOException
	{
		return new Pair<String, String>("en", "");
	}
	public Set<String> getSupportedLanguages() throws WBIOException
	{
		Set<String> res = new HashSet<String>();
		res.add("en");
		return res;
	}
	public WBProject getProject() throws WBIOException
	{
		project.setDefaultLanguage("en");
		project.setSupportedLanguages("en");
		return project;
	}
	@Override
	public void Refresh() throws WBIOException {
		// TODO Auto-generated method stub
		
	}
}
