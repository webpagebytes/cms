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

package com.webpagebytes.cms.cmsdata;

import java.io.Serializable;
import com.webpagebytes.cms.datautility.WPBAdminFieldKey;
import com.webpagebytes.cms.datautility.WPBAdminFieldStore;

public class WPBResource implements Serializable {
	public static final int URI_TYPE = 1;
	public static final int PAGE_TYPE = 2;
	public static final int PAGE_MODULE_TYPE = 3;
	public static final int MESSAGE_TYPE = 4;
	public static final int ARTICLE_TYPE = 5;
	public static final int FILE_TYPE = 6;
	public static final int GLOBAL_PARAMETER_TYPE = 7;
	

	@WPBAdminFieldKey
	private String rkey;
				
	@WPBAdminFieldStore
	private String name;
	
	@WPBAdminFieldStore
	private Integer type;

	public WPBResource()
	{
		
	}
	public WPBResource(String key, String name, int type)
	{
		this.rkey = key;
		this.name = name;
		this.type = type;
	}
	public String getRkey() {
		return rkey;
	}

	public void setRkey(String key) {
		this.rkey = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	
}
