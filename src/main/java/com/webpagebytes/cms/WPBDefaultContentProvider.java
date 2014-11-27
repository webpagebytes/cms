package com.webpagebytes.cms;

import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webpagebytes.cms.appinterfaces.WPBContentProvider;
import com.webpagebytes.cms.appinterfaces.WPBModel;
import com.webpagebytes.cms.cmsdata.WBFile;
import com.webpagebytes.cms.cmsdata.WBWebPage;
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
			WBFile file = fileContentBuilder.find(externalKey);
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
			WBWebPage wbWebPage = pageContentBuilder.findWebPage(externalKey);
			if (null == wbWebPage)
			{
				return false;
			}
			String content = pageContentBuilder.buildPageContent(wbWebPage, model);
			os.write(content.getBytes("UTF-8"));
		} catch (Exception e)
		{
			log.log(Level.SEVERE, "writeFileContent for " + externalKey, e);
			return false;
		}
		return true;
	}

}
