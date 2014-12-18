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

package com.webpagebytes.cms.engine;

import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webpagebytes.cms.WPBContentProvider;
import com.webpagebytes.cms.WPBModel;
import com.webpagebytes.cms.cmsdata.WPBFile;
import com.webpagebytes.cms.cmsdata.WPBPage;
import com.webpagebytes.cms.exception.WPBException;

public class WPBDefaultContentProvider implements WPBContentProvider {

	private FileContentBuilder fileContentBuilder;
	private PageContentBuilder pageContentBuilder;
	private static final Logger log = Logger.getLogger(WPBDefaultContentProvider.class.getName());
	
	public WPBDefaultContentProvider(FileContentBuilder fileContentBuilder, PageContentBuilder pageContentBuilder)
	{
		this.fileContentBuilder = fileContentBuilder;
		this.pageContentBuilder = pageContentBuilder;
	}
	
	public boolean writeFileContent(String externalKey, OutputStream os) 
	{
		try
		{
			WPBFile file = fileContentBuilder.find(externalKey);
			if (null == file)
			{
				return false;
			}
			fileContentBuilder.writeFileContent(file, os);
		}
		catch (WPBException e)
		{
			log.log(Level.SEVERE, "writeFileContent for " + externalKey, e);
			return false;
		}
		return true;
	}

	public boolean writePageContent(String externalKey, WPBModel model,
			OutputStream os) 
	{
		try
		{
			WPBPage wbWebPage = pageContentBuilder.findWebPage(externalKey);
			if (null == wbWebPage)
			{
				return false;
			}
			String content = pageContentBuilder.buildPageContent(wbWebPage, (InternalModel)model);
			os.write(content.getBytes("UTF-8"));
		} catch (Exception e)
		{
			log.log(Level.SEVERE, "writeFileContent for " + externalKey, e);
			return false;
		}
		return true;
	}

}
