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

import io.grpc.Channel;
import org.apache.skywalking.apm.agent.core.boot.BootService;
import org.apache.skywalking.apm.agent.core.boot.DefaultImplementor;
import org.apache.skywalking.apm.agent.core.boot.ServiceManager;
import org.apache.skywalking.apm.agent.core.conf.Config;
import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;
import org.apache.skywalking.apm.agent.core.remote.GRPCChannelListener;
import org.apache.skywalking.apm.agent.core.remote.GRPCChannelManager;
import org.apache.skywalking.apm.agent.core.remote.GRPCChannelStatus;
import org.apache.skywalking.apm.network.arthas.v3.ArthasCommandServiceGrpc;
import org.apache.skywalking.apm.network.arthas.v3.ArthasDataRequest;
import org.apache.skywalking.apm.network.arthas.v3.ArthasSamplingData;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import static org.apache.skywalking.apm.agent.core.conf.Config.Collector.GRPC_UPSTREAM_TIMEOUT;

@DefaultImplementor
public class ArthasSender implements BootService, GRPCChannelListener {
    private static final ILog LOGGER = LogManager.getLogger(ArthasSender.class);
    private volatile GRPCChannelStatus status = GRPCChannelStatus.DISCONNECT;
    private volatile ArthasCommandServiceGrpc.ArthasCommandServiceBlockingStub stub = null;
    private LinkedBlockingQueue<ArthasSamplingData> queue;

    @Override
    public void prepare() {
        queue = new LinkedBlockingQueue<>(Config.Jvm.BUFFER_SIZE);
        ServiceManager.INSTANCE.findService(GRPCChannelManager.class).addChannelListener(this);
    }

    @Override
    public void boot() {

    }

    public void offer(ArthasSamplingData arthasSamplingData) {
        if (!queue.offer(arthasSamplingData)) {
            queue.poll();
            queue.offer(arthasSamplingData);
        }
    }

    public void run() {
//        if (status == GRPCChannelStatus.CONNECTED) {
            try {
                ArthasDataRequest.Builder builder = ArthasDataRequest.newBuilder();
                LinkedList<ArthasSamplingData> buffer = new LinkedList<>();
                queue.drainTo(buffer);
                if (buffer.size() > 0) {
                    builder.addAllArthasSamplingData(buffer);
                    stub.withDeadlineAfter(GRPC_UPSTREAM_TIMEOUT, TimeUnit.SECONDS)
                            .sendArthasData(builder.build());
                }
            } catch (Throwable t) {
                LOGGER.error(t, "send process machine metrics to Collector fail.");
                ServiceManager.INSTANCE.findService(GRPCChannelManager.class).reportError(t);
            }
//        }
    }

    @Override
    public void statusChanged(GRPCChannelStatus status) {
        if (GRPCChannelStatus.CONNECTED.equals(status)) {
            Channel channel = ServiceManager.INSTANCE.findService(GRPCChannelManager.class).getChannel();
            stub = ArthasCommandServiceGrpc.newBlockingStub(channel);
        }
        this.status = status;
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void shutdown() {

    }
}
