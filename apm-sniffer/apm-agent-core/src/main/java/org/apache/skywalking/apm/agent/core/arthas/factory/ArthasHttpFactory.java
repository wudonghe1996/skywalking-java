/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.apm.agent.core.arthas.factory;

import org.apache.skywalking.apm.agent.core.arthas.factory.constant.ArthasConstant;

import java.io.IOException;

public abstract class ArthasHttpFactory<T, R> {

    protected static String ARTHAS_HTTP_URL = "";

    private static final String DEFAULT_HTTP_IP = "127.0.0.1";

    public static void init(String ip, Integer port) {
        StringBuilder sb = new StringBuilder();
        sb.append(ArthasConstant.HTTP);
//        boolean localIpFlag = HttpUtil.isTcpPortAvailable(DEFAULT_HTTP_IP, port);
        boolean localIpFlag = false;
        sb.append(localIpFlag ? DEFAULT_HTTP_IP : ip);
        sb.append(":").append(port).append(ArthasConstant.ARTHAS_API_URL);
        ARTHAS_HTTP_URL = sb.toString();
    }

    public abstract R execute(T t) throws IOException;

}
