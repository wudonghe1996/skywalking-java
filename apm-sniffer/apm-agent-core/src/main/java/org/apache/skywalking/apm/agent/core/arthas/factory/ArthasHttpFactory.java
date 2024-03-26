package org.apache.skywalking.apm.agent.core.arthas.factory;

import org.apache.skywalking.apm.agent.core.arthas.factory.constant.ArthasConstant;
import org.apache.skywalking.apm.agent.core.arthas.utils.HttpUtil;

import java.io.IOException;

public abstract class ArthasHttpFactory<T, R> {

    protected static String ARTHAS_HTTP_URL = "";

    private static final String DEFAULT_HTTP_IP = "127.0.0.1";

    public static void init(String ip, Integer port){
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
