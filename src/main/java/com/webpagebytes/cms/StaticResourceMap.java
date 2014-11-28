/*
 *   Copyright 2014 Webpagebytes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.webpagebytes.cms;

import java.util.Map;
import java.util.HashMap;

import com.webpagebytes.cms.exception.*;

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
		
	public void initialize(String adminResourceFolder, String resourcesWhiteList) throws WPBException
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
	
	public byte[] getResource(String path) throws WPBResourceNotFoundException
	{
		if (resourcesMap.containsKey(path))
		{
			return resourcesMap.get(path);
		}
		throw new WPBResourceNotFoundException("Could not find resource " + path);
	}
	
	public String getResourceHash(String path) throws WPBResourceNotFoundException
	{
		if (resourcesMapHash.containsKey(path))
		{
			return resourcesMapHash.get(path);
		}
		throw new WPBResourceNotFoundException("Could not find resource " + path);
	
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
