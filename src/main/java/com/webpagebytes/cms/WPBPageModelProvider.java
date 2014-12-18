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

import com.webpagebytes.cms.exception.WPBException;

/**
 * <p>
 * Server side controller can is executed when a content is generated for a site page.
 * </p>
 * <p>
 * The same server side controller can supply application specific data to multiple site pages. In this case 
 * it is natural to reuse the same code. WPBPageModelProvider provides this posibility to populate the model
 * for a site page.
 * </p>
 */
public interface WPBPageModelProvider {

    /**
     * Method called when content is generated for a site page who's Source interpretation is 'Template text' and 
     * the 'Page model provider' attribute is set. 
     * @param model The request model that can be populated with application specific data.
     * @throws WPBException
     */
    public void populatePageModel(WPBModel model) throws WPBException;
}
