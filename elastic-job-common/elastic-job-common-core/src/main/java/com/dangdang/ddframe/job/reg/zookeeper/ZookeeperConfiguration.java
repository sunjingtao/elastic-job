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

package com.dangdang.ddframe.job.reg.zookeeper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 基于Zookeeper的注册中心配置.
 * 
 * @author zhangliang
 * @author caohao
 */
@Getter
@Setter
@Configuration
public class ZookeeperConfiguration {
    
    /**
     * 连接Zookeeper服务器的列表.
     * 包括IP地址和端口号.
     * 多个地址用逗号分隔.
     * 如: host1:2181,host2:2181
     */
    @Value("${elasticjob.zookeeper.serverLists}")
    private String serverLists;
    
    /**
     * 命名空间.
     */
    @Value("${elasticjob.zookeeper.namespace}")
    private String namespace;
    
    /**
     * 等待重试的间隔时间的初始值.
     * 单位毫秒.
     */
    @Value("${elasticjob.zookeeper.baseSleepTimeMilliseconds}")
    private int baseSleepTimeMilliseconds = 1000;
    
    /**
     * 等待重试的间隔时间的最大值.
     * 单位毫秒.
     */
    @Value("${elasticjob.zookeeper.maxSleepTimeMilliseconds}")
    private int maxSleepTimeMilliseconds = 3000;
    
    /**
     * 最大重试次数.
     */
    @Value("${elasticjob.zookeeper.maxRetries}")
    private int maxRetries = 3;
    
    /**
     * 会话超时时间.
     * 单位毫秒.
     */
    @Value("${elasticjob.zookeeper.sessionTimeoutMilliseconds}")
    private int sessionTimeoutMilliseconds;
    
    /**
     * 连接超时时间.
     * 单位毫秒.
     */
    @Value("${elasticjob.zookeeper.connectionTimeoutMilliseconds}")
    private int connectionTimeoutMilliseconds;
    
    /**
     * 连接Zookeeper的权限令牌.
     * 缺省为不需要权限验证.
     */
    @Value("${elasticjob.zookeeper.digest}")
    private String digest;
}
