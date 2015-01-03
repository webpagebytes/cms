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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.exception.WPBException;
/**
 * <p>
 * Interface that defines a Site url controller.
 * </p>
 * A <i>Site url</i> that has the <i>Resource type</i> value as <i> Url controller </i> will need to specify a 
 * path to a controller that implements WPBRequestHandler. <br>
 * Everytime that url will be requested the CMS engine will pass the execution to the WPBRequestHandler controller 
 * so that the applicaton can handle the request.
 * A controller that implements WPBRequestHandler will be created a single time and reused across all site urls that 
 * specify the same controller path.
 */
public interface WPBRequestHandler 
{
    /**
     * Method that initializes the controller
     * @param contentProvider Interface that can be used to fetch content from CMS engine.
     */
	public void initialize(
	        WPBContentProvider contentProvider);
	
	/**
	 * Method that is called on each HTTP request for a site url that has the <i>Resource type</i> value as <i> Url controller </i> 
	 * @param request The servlet HttpServletRequest instance of the current request
	 * @param response The servlet HttpServletResponse instance of the current request
	 * @param model The corrent request Model. It can be used to obtain CMS specific information or the application specific model can be populated. 
	 * @param forward Interface to forward the current request to use a particular site page.
	 * @throws WPBException
	 */
	public void handleRequest(
	        HttpServletRequest request, 
			HttpServletResponse response, 
			WPBModel model,
			WPBForward forward) throws WPBException;
}
