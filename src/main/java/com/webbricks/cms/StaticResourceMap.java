package com.webbricks.cms;

import java.util.Map;

import java.util.HashMap;
import com.webbricks.exception.*;
import java.util.Set;
import java.util.zip.CRC32;

public class StaticResourceMap {

	private Map<String, byte[]> resourcesMap;
	private Map<String, String> resourcesMapHash;
	private ResourceReader resReader; 

	public StaticResourceMap() {
		resourcesMap = new HashMap<String, byte[]>();
		resourcesMapHash = new HashMap<String, String>();
		resReader = new ResourceReader();
	}
		
	public void initialize(String adminResourceFolder, String resourcesWhiteList) throws WBException
	{
		Set<String> resources = resReader.parseWhiteListFile(resourcesWhiteList);
		CRC32 crc32 = new CRC32();
		for(String resource: resources)
		{
			String resPath = "META-INF/".concat(adminResourceFolder).concat(resource);
			byte[] resContent = resReader.getResourceContent(resPath);
			resourcesMap.put(resource, resContent);
			crc32.reset();
			crc32.update(resContent);
			resourcesMapHash.put(resource, Long.toString(crc32.getValue()));
		}
	}
	
	public byte[] getResource(String path) throws WBResourceNotFoundException
	{
		if (resourcesMap.containsKey(path))
		{
			return resourcesMap.get(path);
		}
		throw new WBResourceNotFoundException("Could not find resource " + path);
	}
	
	public String getResourceHash(String path) throws WBResourceNotFoundException
	{
		if (resourcesMapHash.containsKey(path))
		{
			return resourcesMapHash.get(path);
		}
		throw new WBResourceNotFoundException("Could not find resource " + path);
	
	}

	public Map<String, byte[]> getResourcesMap() {
		return resourcesMap;
	}

	public void setResourcesMap(Map<String, byte[]> resourcesMap) {
		this.resourcesMap = resourcesMap;
	}

	public Map<String, String> getResourcesMapHash() {
		return resourcesMapHash;
	}

	public void setResourcesMapHash(Map<String, String> resourcesMapHash) {
		this.resourcesMapHash = resourcesMapHash;
	}

	public ResourceReader getResReader() {
		return resReader;
	}

	public void setResReader(ResourceReader resReader) {
		this.resReader = resReader;
	}

}
