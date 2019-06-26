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

package com.eaap.elasticjob.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * 事件追踪数据源配置.
 *
 * @author zhangxinguo
 */
@NoArgsConstructor
@Getter
@Setter
public final class EventTraceDataSourceConfiguration implements Serializable {
    
    private static final long serialVersionUID = -5996257770767863699L;
    
    private String name;
    
    private String driver;
    
    private String url;
    
    private String username;
    
    private String password;
    
    private boolean activated;
    
    public EventTraceDataSourceConfiguration(final String driver, final String url, final String username) {
        this.driver = driver;
        this.url = url;
        this.username = username;
    }
}
