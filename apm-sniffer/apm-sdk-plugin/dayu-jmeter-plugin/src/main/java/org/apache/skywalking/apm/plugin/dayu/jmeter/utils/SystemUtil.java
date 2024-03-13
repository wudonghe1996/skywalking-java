package org.apache.skywalking.apm.plugin.dayu.jmeter.utils;

import org.apache.skywalking.apm.agent.core.context.ids.GlobalIdGenerator;
import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;
import org.apache.skywalking.apm.plugin.dayu.jmeter.common.TaskIdCache;

import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Enumeration;

public class SystemUtil {

    private static final ILog LOGGER = LogManager.getLogger(SystemUtil.class);

    public static String getSw8(){
        Integer taskId = TaskIdCache.TASK_ID;
        LOGGER.info("本次获取的TaskId：" + taskId);
        if(taskId == null){
            LOGGER.error("获取taskId失败");
            throw new RuntimeException();
        }

        SnowFlakeIdGenerator.createInstance(0, 0,taskId);
        String traceId = "dayu-"+ taskId + "-" + SnowFlakeIdGenerator.generateId();
        LOGGER.info("本次TraceId: " + traceId);
        return StringUtil.join(
                '-',
                "1",
                Base64.getEncoder().encodeToString(traceId.getBytes(StandardCharsets.UTF_8)),
                Base64.getEncoder().encodeToString(traceId.getBytes(StandardCharsets.UTF_8)),
                "0",
                Base64.getEncoder().encodeToString("Dayu-Jmeter".getBytes(StandardCharsets.UTF_8)),
                Base64.getEncoder().encodeToString("Dayu-Jmeter".getBytes(StandardCharsets.UTF_8)),
                Base64.getEncoder().encodeToString("Dayu-Jmeter".getBytes(StandardCharsets.UTF_8)),
                Base64.getEncoder().encodeToString(getLocal().getBytes(StandardCharsets.UTF_8))
        );
    }

    public static String getLocal() {
        try {
            for (Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); ) {
                NetworkInterface item = e.nextElement();
                for (InterfaceAddress address : item.getInterfaceAddresses()) {
                    if (item.isLoopback() || !item.isUp()) {
                        continue;
                    }
                    if (address.getAddress() instanceof Inet4Address) {
                        Inet4Address inet4Address = (Inet4Address) address.getAddress();
                        return inet4Address.getHostAddress();
                    }
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (SocketException | UnknownHostException e) {
            return "127.0.0.1";
        }
    }

}