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

package com.webpagebytes.cms.template;

public class FreeMarkerTemplateObject {
	public enum TemplateType {
		TEMPLATE_PAGE,
		TEMPLATE_MODULE
	};
	private long lastModified = 0;
	private String name;
	private TemplateType type;
	public FreeMarkerTemplateObject(String name, TemplateType type, long lastModified)
	{
		setType(type);
		setLastModified(lastModified);
		setName(name);
	}
	
	public long getLastModified() {
		return lastModified;
	}
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public TemplateType getType() {
		return type;
	}

	public void setType(TemplateType type) {
		this.type = type;
	}

	@Override public boolean equals(Object other) 
	 {
        boolean result = false;
        if (other instanceof FreeMarkerTemplateObject) {
            FreeMarkerTemplateObject that = (FreeMarkerTemplateObject) other;
            result = (this.lastModified == that.lastModified 
            		 && this.name.equals(that.name)
            		 && this.type.equals(that.type));
        }
        return result;
	 }
	 @Override public int hashCode() 
	 {
		 	int nameHash = name != null ? name.hashCode() : 0;
		 	int typeint = type == TemplateType.TEMPLATE_PAGE ? 1 : 2;
		 	return 41*(41 * (41 + (int)lastModified) + typeint) + nameHash;
	 }
	 
	 
}
