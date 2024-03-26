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

package org.apache.skywalking.apm.agent.core.arthas;

import com.taobao.arthas.common.PidUtils;
import com.taobao.arthas.common.SocketUtils;
import io.grpc.Channel;
import org.apache.skywalking.apm.agent.core.arthas.handler.ProfileBaseHandle;
import org.apache.skywalking.apm.agent.core.arthas.utils.ArthasUtil;
import org.apache.skywalking.apm.agent.core.arthas.utils.HttpUtil;
import org.apache.skywalking.apm.agent.core.arthas.utils.IpUtil;
import org.apache.skywalking.apm.agent.core.boot.BootService;
import org.apache.skywalking.apm.agent.core.boot.DefaultImplementor;
import org.apache.skywalking.apm.agent.core.boot.DefaultNamedThreadFactory;
import org.apache.skywalking.apm.agent.core.boot.ServiceManager;
import org.apache.skywalking.apm.agent.core.conf.Config;
import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;
import org.apache.skywalking.apm.agent.core.remote.GRPCChannelListener;
import org.apache.skywalking.apm.agent.core.remote.GRPCChannelManager;
import org.apache.skywalking.apm.agent.core.remote.GRPCChannelStatus;
import org.apache.skywalking.apm.network.arthas.v3.ArthasCommandServiceGrpc;
import org.apache.skywalking.apm.network.arthas.v3.ArthasRequest;
import org.apache.skywalking.apm.network.arthas.v3.ArthasResponse;
import org.apache.skywalking.apm.network.dayu.v3.ArthasIpMessage;
import org.apache.skywalking.apm.network.dayu.v3.DayuServiceGrpc;
import org.apache.skywalking.apm.util.RunnableWithExceptionProtection;
import org.apache.skywalking.apm.util.StringUtil;

import javax.net.ServerSocketFactory;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.apache.skywalking.apm.agent.core.conf.Config.Collector.GRPC_UPSTREAM_TIMEOUT;

@DefaultImplementor
public class ArthasService implements BootService, GRPCChannelListener {

    private volatile ScheduledFuture<?> getCommandFuture;
    private volatile GRPCChannelStatus status = GRPCChannelStatus.DISCONNECT;
    private volatile ArthasCommandServiceGrpc.ArthasCommandServiceBlockingStub commandServiceBlockingStub;
    private volatile DayuServiceGrpc.DayuServiceBlockingStub dayuServiceBlockingStub;

    private volatile Integer arthasTelnetPort;

    private volatile String arthasIp;

    private volatile Integer arthasHttpPort;

    private static final ILog LOGGER = LogManager.getLogger(ArthasService.class);

    @Override
    public void statusChanged(final GRPCChannelStatus status) {
        if (GRPCChannelStatus.CONNECTED.equals(status)) {
            Channel channel = ServiceManager.INSTANCE.findService(GRPCChannelManager.class).getChannel();
            commandServiceBlockingStub = ArthasCommandServiceGrpc.newBlockingStub(channel);
            dayuServiceBlockingStub = DayuServiceGrpc.newBlockingStub(channel);
        } else {
            commandServiceBlockingStub = null;
            dayuServiceBlockingStub = null;
        }
        this.status = status;
    }

    @Override
    public void prepare() throws Throwable {
        ServiceManager.INSTANCE.findService(GRPCChannelManager.class).addChannelListener(this);
    }

    @Override
    public void boot() throws Throwable {
        getCommandFuture = Executors.newSingleThreadScheduledExecutor(
                new DefaultNamedThreadFactory("ArthasService")
        ).scheduleWithFixedDelay(
                new RunnableWithExceptionProtection(
                        this::getCommand,
                        t -> LOGGER.error("get arthas command error.", t)
                ), 0, 2, TimeUnit.SECONDS
        );
    }

    @Override
    public void onComplete() throws Throwable {

    }

    @Override
    public void shutdown() throws Throwable {
        if (getCommandFuture != null) {
            getCommandFuture.cancel(true);
        }
    }

    private void getCommand() {
        LOGGER.debug("ArthasService running, status:{}.", status);

        if (!GRPCChannelStatus.CONNECTED.equals(status) || commandServiceBlockingStub == null) {
            return;
        }

        ArthasRequest.Builder builder = ArthasRequest.newBuilder();
        builder.setServiceName(Config.Agent.SERVICE_NAME);
        builder.setInstanceName(Config.Agent.INSTANCE_NAME);

        final ArthasResponse arthasResponse = commandServiceBlockingStub.withDeadlineAfter(
                GRPC_UPSTREAM_TIMEOUT, TimeUnit.SECONDS).getCommand(builder.build());

        switch (arthasResponse.getCommand()) {
            case START:
                if (alreadyAttached()) {
                    LOGGER.warn("arthas already attached, no need start again");
                    return;
                }

                try {
                    arthasTelnetPort = SocketUtils.findAvailableTcpPort();
                    if (StringUtil.isNotEmpty(Config.Arthas.HTTP_IP)) {
                        arthasIp = Config.Arthas.HTTP_IP;
                    } else {
                        arthasIp = Objects.requireNonNull(IpUtil.getLocalHostExactAddress()).getHostAddress();
                    }

                    if (StringUtil.isNotBlank(Config.Arthas.HTTP_PORT)) {
                        arthasHttpPort = Integer.valueOf(Config.Arthas.HTTP_PORT);
                    } else {
                        arthasHttpPort = SocketUtils.findAvailableTcpPort();
                    }

//                    Boolean startFlag = ArthasUtil.startArthas(PidUtils.currentLongPid(), arthasTelnetPort, arthasIp, arthasHttpPort);
//                    if (startFlag) {
//                        LOGGER.info("start arthas success, arthasIp: {}, telnetPort: {}, httpPort: {}", arthasIp, arthasTelnetPort, arthasHttpPort);
                        ProfileBaseHandle.submit(arthasResponse.getProfileTaskId(), arthasIp, arthasHttpPort);
//                    }
                } catch (Exception e) {
                    LOGGER.info("error when start arthas", e);
                    e.printStackTrace();
                }
                break;
            case STOP:
                if (!alreadyAttached()) {
                    LOGGER.warn("no arthas attached, no need to stop");
                    return;
                }

                try {
                    Boolean stopFlag = ArthasUtil.stopArthas(arthasIp, arthasTelnetPort);
                    if(stopFlag) {
                        arthasTelnetPort = null;
                        arthasIp = null;
                        arthasHttpPort = null;
                        LOGGER.info("stop arthas success, set params null");
                    }
                } catch (Exception e) {
                    LOGGER.info("error when stop arthas", e);
                }
                break;
            default:
                break;
        }
    }

    private boolean alreadyAttached() {
        LOGGER.info("arthas telnet ip {} , arthas telnet port : {}", arthasIp, arthasTelnetPort);
        return arthasTelnetPort != null && arthasIp != null && !HttpUtil.isTcpPortAvailable(arthasIp, arthasTelnetPort);
    }

    @Deprecated
    private void sendLocalIpForOap(String ip, Integer httpPort) {
        ArthasIpMessage.Builder message = ArthasIpMessage.newBuilder();
        message.setServiceName(Config.Agent.SERVICE_NAME);
        message.setInstanceName(Config.Agent.INSTANCE_NAME);
        message.setIp(ip + ":" + httpPort);
        dayuServiceBlockingStub.sendArthasIp(message.build());
    }

}
