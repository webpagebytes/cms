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

import java.util.HashMap;
import java.util.Map;
/**
 * Class that represents the Webpagebytes CMS specific model data. Controllers or the site pages/site modules can get CMS specific data about the current request using this class.
 * The class will be populated by the CMS engine with the current request information (Locale, Global parameters, Site urls customization parameters, Site pages customization parameters).  
 * @see WPBCmsModel
 */
public class WPBCmsModel extends HashMap<String, Map<String, String>> {

}
