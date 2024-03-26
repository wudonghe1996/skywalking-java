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
    private static PoolingHttpClientConnectionManager connectionManager = null;
    private static CloseableHttpClient client;
    private static final Integer MAX_TOTAL = 50;
    private static final Integer DEFAULT_MAX_TOTAL = 10;

    private static void init() {
        synchronized (HttpUtil.class) {
            if (client == null) {
                connectionManager = new PoolingHttpClientConnectionManager();
                ConnectionConfig connConfig = ConnectionConfig.custom().setCharset(StandardCharsets.UTF_8).build();
                SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(5000).build();
                connectionManager.setDefaultConnectionConfig(connConfig);
                connectionManager.setDefaultSocketConfig(socketConfig);
                connectionManager.setMaxTotal(MAX_TOTAL);
                connectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_TOTAL);
                RequestConfig config = RequestConfig.custom().setConnectTimeout(30000)
                        .setConnectionRequestTimeout(500)
                        .setSocketTimeout(30000)
                        .build();
                HttpClientBuilder builder = HttpClients.custom();
                builder.setConnectionManager(connectionManager).setConnectionManagerShared(true);
                builder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());
                client = builder.setDefaultRequestConfig(config).build();
            }
        }
    }

    private static CloseableHttpClient getClientFromHttpPool() {
        if (client == null) {
            init();
        }
        return client;
    }

    public static String doPostJson(String url, String param) throws IOException {
        // 参数设置
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
