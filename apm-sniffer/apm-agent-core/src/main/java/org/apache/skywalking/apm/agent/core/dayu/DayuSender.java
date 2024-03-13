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

package org.apache.skywalking.apm.agent.core.dayu;

import io.grpc.Channel;
import org.apache.skywalking.apm.agent.core.boot.BootService;
import org.apache.skywalking.apm.agent.core.boot.DefaultImplementor;
import org.apache.skywalking.apm.agent.core.boot.ServiceManager;
import org.apache.skywalking.apm.agent.core.commands.CommandService;
import org.apache.skywalking.apm.agent.core.conf.Config;
import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;
import org.apache.skywalking.apm.agent.core.remote.GRPCChannelListener;
import org.apache.skywalking.apm.agent.core.remote.GRPCChannelManager;
import org.apache.skywalking.apm.agent.core.remote.GRPCChannelStatus;
import org.apache.skywalking.apm.network.common.v3.Commands;
import org.apache.skywalking.apm.network.dayu.v3.DayuServiceGrpc;
import org.apache.skywalking.apm.network.dayu.v3.Machine;
import org.apache.skywalking.apm.network.dayu.v3.MachineMetric;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.apache.skywalking.apm.agent.core.conf.Config.Collector.GRPC_UPSTREAM_TIMEOUT;

@DefaultImplementor
public class DayuSender implements BootService, Runnable, GRPCChannelListener {
    private static final ILog LOGGER = LogManager.getLogger(DayuSender.class);

    private volatile GRPCChannelStatus status = GRPCChannelStatus.DISCONNECT;
    private volatile DayuServiceGrpc.DayuServiceBlockingStub stub = null;

    private LinkedBlockingQueue<MachineMetric> queue;

    @Override
    public void prepare() {
        queue = new LinkedBlockingQueue<>(Config.Jvm.BUFFER_SIZE);
        ServiceManager.INSTANCE.findService(GRPCChannelManager.class).addChannelListener(this);
    }

    @Override
    public void boot() {

    }

    public void offer(MachineMetric machineMetric) {
        if (!queue.offer(machineMetric)) {
            queue.poll();
            queue.offer(machineMetric);
        }
    }

    @Override
    public void run() {
        if (status == GRPCChannelStatus.CONNECTED) {
            try {
                Machine.Builder builder = Machine.newBuilder();
                LinkedList<MachineMetric> buffer = new LinkedList<>();
                queue.drainTo(buffer);
                if (buffer.size() > 0) {
                    builder.addAllMetrics(buffer);
                    builder.setServiceName(Config.Agent.SERVICE_NAME);
                    builder.setInstanceName(Config.Agent.INSTANCE_NAME);
                    Commands commands = stub.withDeadlineAfter(GRPC_UPSTREAM_TIMEOUT, TimeUnit.SECONDS)
                            .sendMachineMetrics(builder.build());
                    ServiceManager.INSTANCE.findService(CommandService.class).receiveCommand(commands);
                }
            } catch (Throwable t) {
                LOGGER.error(t, "send process machine metrics to Collector fail.");
                ServiceManager.INSTANCE.findService(GRPCChannelManager.class).reportError(t);
            }
        }
    }

    @Override
    public void statusChanged(GRPCChannelStatus status) {
        if (GRPCChannelStatus.CONNECTED.equals(status)) {
            Channel channel = ServiceManager.INSTANCE.findService(GRPCChannelManager.class).getChannel();
            stub = DayuServiceGrpc.newBlockingStub(channel);
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
