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

package org.apache.skywalking.apm.agent.core.arthas.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;

public class HttpUtil {
    private static PoolingHttpClientConnectionManager CONNECTION_MANAGER = null;
    private static CloseableHttpClient CLIENT;
    private static final Integer MAX_TOTAL = 50;
    private static final Integer DEFAULT_MAX_TOTAL = 10;

    private static void init() {
        synchronized (HttpUtil.class) {
            if (CLIENT == null) {
                CONNECTION_MANAGER = new PoolingHttpClientConnectionManager();
                ConnectionConfig connConfig = ConnectionConfig.custom().setCharset(StandardCharsets.UTF_8).build();
                SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(5000).build();
                CONNECTION_MANAGER.setDefaultConnectionConfig(connConfig);
                CONNECTION_MANAGER.setDefaultSocketConfig(socketConfig);
                CONNECTION_MANAGER.setMaxTotal(MAX_TOTAL);
                CONNECTION_MANAGER.setDefaultMaxPerRoute(DEFAULT_MAX_TOTAL);
                RequestConfig config = RequestConfig.custom().setConnectTimeout(30000)
                        .setConnectionRequestTimeout(500)
                        .setSocketTimeout(30000)
                        .build();
                HttpClientBuilder builder = HttpClients.custom();
                builder.setConnectionManager(CONNECTION_MANAGER).setConnectionManagerShared(true);
                builder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());
                CLIENT = builder.setDefaultRequestConfig(config).build();
            }
        }
    }

    private static CloseableHttpClient getClientFromHttpPool() {
        if (CLIENT == null) {
            init();
        }
        return CLIENT;
    }

    public static String doPostJson(String url, String param) throws IOException {
        HttpPost post = new HttpPost(url);
        post.setEntity(new StringEntity(param, ContentType.APPLICATION_JSON));
        CloseableHttpClient httpClient = getClientFromHttpPool();

        HttpResponse response = httpClient.execute(post);
        int httpStatusCode = response.getStatusLine().getStatusCode();
        if (httpStatusCode != HttpStatus.SC_OK) {
            throw new RuntimeException("http send fail url=" + url + ",param=" + param);
        }
        HttpEntity entity = response.getEntity();
        return EntityUtils.toString(entity, StandardCharsets.UTF_8);
    }

    public static boolean isTcpPortAvailable(String ip, int port) {
        try {
            ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(port, 1,
                    InetAddress.getByName(ip));
            serverSocket.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}
