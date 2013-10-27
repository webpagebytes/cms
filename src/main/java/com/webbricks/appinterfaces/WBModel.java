package com.webbricks.appinterfaces;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WBModel {
	protected WBCmsModel cmsModel = new WBCmsModel();
	protected WBCustomModel customModel = new WBCustomModel();
	
	public WBCmsModel getCmsModel()
	{
		return cmsModel;
	}
	public WBCustomModel getCmsCustomModel()
	{
		return customModel;
	}
	
	public void transferModel(Map<String, Object> rootObject)
	{
		Set<String> keys = cmsModel.keySet();
		for(String key: keys)
		{
			rootObject.put(key, cmsModel.get(key));
		}
		keys = customModel.keySet();
		for(String key: keys)
		{
			rootObject.put(key, customModel.get(key));
		}
		
	}
}
