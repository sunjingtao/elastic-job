/*
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.eaap.elasticjob.service;

import com.eaap.elasticjob.model.ServerBriefInfo;

import java.util.Collection;

/**
 * 作业服务器状态展示的API.
 *
 * @author caohao
 */
public interface IServerStatisticsAPI {
    
    /**
     * 获取作业服务器总数.
     *
     * @return 作业服务器总数
     */
    int getServersTotalCount();
    
    /**
     * 获取所有作业服务器简明信息.
     *
     * @return 作业服务器简明信息集合
     */
    Collection<ServerBriefInfo> getAllServersBriefInfo();
}
