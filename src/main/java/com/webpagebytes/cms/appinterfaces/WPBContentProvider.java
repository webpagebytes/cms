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

package com.webpagebytes.cms.appinterfaces;

import java.io.OutputStream;

/**
 * WPBContentProvider class is used to fetch content from the CMS. This can be used from controllers implementation or in batch mode.
 * @see WPBContentService 
 * 
 */
public interface WPBContentProvider {

    /**
     * 
     * @param externalKey The externalKey identificator for a resource 
     * @param os The output stream where the method will write the content 
     * @return Returns true if the content was written, false if no content was found to match externalKey
     */
	public boolean writeFileContent(String externalKey, OutputStream os);
	
	public boolean writePageContent(String externalKey, WPBModel model, OutputStream os);
}
