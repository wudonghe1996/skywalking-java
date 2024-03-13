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

package org.apache.skywalking.apm.agent.core.arthas.profile;

import io.grpc.Channel;
import org.apache.skywalking.apm.agent.core.boot.BootService;
import org.apache.skywalking.apm.agent.core.boot.DefaultNamedThreadFactory;
import org.apache.skywalking.apm.agent.core.boot.ServiceManager;
import org.apache.skywalking.apm.agent.core.conf.Config;
import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;
import org.apache.skywalking.apm.agent.core.remote.GRPCChannelListener;
import org.apache.skywalking.apm.agent.core.remote.GRPCChannelManager;
import org.apache.skywalking.apm.agent.core.remote.GRPCChannelStatus;
import org.apache.skywalking.apm.network.arthas.v3.FlameDiagramGrpc;
import org.apache.skywalking.apm.network.arthas.v3.Request;
import org.apache.skywalking.apm.network.arthas.v3.Response;
import org.apache.skywalking.apm.network.arthas.v3.SendRequest;
import org.apache.skywalking.apm.util.RunnableWithExceptionProtection;
import org.apache.skywalking.apm.util.StringUtil;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.apache.skywalking.apm.agent.core.conf.Config.Collector.GRPC_UPSTREAM_TIMEOUT;

public class FlameDiagramHandler implements BootService, GRPCChannelListener {

    private static final ILog LOGGER = LogManager.getLogger(FlameDiagramHandler.class);
    private volatile ScheduledFuture<?> getPathFuture;
    private volatile GRPCChannelStatus status = GRPCChannelStatus.DISCONNECT;
    private volatile FlameDiagramGrpc.FlameDiagramBlockingStub flameDiagramBlockingStub;

    @Override
    public void prepare() throws Throwable {
        ServiceManager.INSTANCE.findService(GRPCChannelManager.class).addChannelListener(this);
    }

    @Override
    public void boot() throws Throwable {
        getPathFuture = Executors.newSingleThreadScheduledExecutor(
                new DefaultNamedThreadFactory("FlameDiagramHandler")
        ).scheduleWithFixedDelay(
                new RunnableWithExceptionProtection(
                        this::getFilePath,
                        t -> LOGGER.error("get flame diagram path error.", t)
                ), 0, 1, TimeUnit.SECONDS
        );
    }

    private void getFilePath() {
        if (!GRPCChannelStatus.CONNECTED.equals(status) || flameDiagramBlockingStub == null) {
            return;
        }

        Request.Builder builder = Request.newBuilder();
        builder.setServiceName(Config.Agent.SERVICE_NAME);
        builder.setInstanceName(Config.Agent.INSTANCE_NAME);

        final Response response = flameDiagramBlockingStub.withDeadlineAfter(
                GRPC_UPSTREAM_TIMEOUT, TimeUnit.SECONDS).getFlameDiagramPath(builder.build());

        String filePath = response.getFilePath();
        if (StringUtil.isEmpty(filePath)) {
            return;
        }
        try {
            String flameDiagramData = FileUtils.readFileNewLineWithBr(filePath, "\n");
            flameDiagramBlockingStub.sendFlameDiagramData(SendRequest.newBuilder()
                            .setServiceName(Config.Agent.SERVICE_NAME)
                            .setInstanceName(Config.Agent.INSTANCE_NAME)
                            .setFlameDiagramData(flameDiagramData).build());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onComplete() throws Throwable {

    }

    @Override
    public void shutdown() throws Throwable {
        if (getPathFuture != null) {
            getPathFuture.cancel(true);
        }
    }

    @Override
    public void statusChanged(GRPCChannelStatus status) {
        if (GRPCChannelStatus.CONNECTED.equals(status)) {
            Channel channel = ServiceManager.INSTANCE.findService(GRPCChannelManager.class).getChannel();
            flameDiagramBlockingStub = FlameDiagramGrpc.newBlockingStub(channel);
        } else {
            flameDiagramBlockingStub = null;
        }
        this.status = status;
    }
}
