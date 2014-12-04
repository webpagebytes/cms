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
import java.util.Set;

import com.webpagebytes.cms.appinterfaces.WPBApplicationModel;
import com.webpagebytes.cms.appinterfaces.WPBCmsModel;
import com.webpagebytes.cms.appinterfaces.WPBModel;

public class InternalModel implements WPBModel {
        

    protected WPBCmsModel cmsModel = new WPBCmsModel();
    protected WPBApplicationModel applicationModel = new WPBApplicationModel();
    
    public WPBCmsModel getCmsModel()
    {
        return cmsModel;
    }
    public WPBApplicationModel getCmsApplicationModel()
    {
        return applicationModel;
    }

    public void transferModel(Map<String, Object> rootObject)
    {
        Set<String> keys = cmsModel.keySet();
        for(String key: keys)
        {
            rootObject.put(key, cmsModel.get(key));
        }
                
    }
}
