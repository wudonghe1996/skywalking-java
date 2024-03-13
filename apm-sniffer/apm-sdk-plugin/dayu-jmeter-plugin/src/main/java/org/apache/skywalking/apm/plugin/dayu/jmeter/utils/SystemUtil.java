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

package org.apache.skywalking.apm.plugin.dayu.jmeter.utils;

import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;
import org.apache.skywalking.apm.plugin.dayu.jmeter.common.TaskIdCache;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Enumeration;

public class SystemUtil {

    private static final ILog LOGGER = LogManager.getLogger(SystemUtil.class);

    public static String getSw8() {
        Integer taskId = TaskIdCache.TASK_ID;
        if (taskId == null) {
            throw new RuntimeException();
        }

        SnowFlakeIdGenerator.createInstance(0, 0, taskId);
        String traceId = "dayu-" + taskId + "-" + SnowFlakeIdGenerator.generateId();
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