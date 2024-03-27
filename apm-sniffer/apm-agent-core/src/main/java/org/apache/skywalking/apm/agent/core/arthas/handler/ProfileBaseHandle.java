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

package org.apache.skywalking.apm.agent.core.arthas.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.agent.core.arthas.exception.ArthasException;
import org.apache.skywalking.apm.agent.core.arthas.factory.ArthasHttpFactory;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ProfileBaseHandle {
    private static ScheduledFuture<?> PROFILE_FUTURE;
    private static final ScheduledExecutorService PROFILE_TASK_SCHEDULE_EXECUTOR = Executors.newScheduledThreadPool(5);

    private static final CpuHandle CPU_HANDLE = new CpuHandle();
    private static final MemHandle MEM_HANDLE = new MemHandle();
    private static final SystemHandle SYSTEM_HANDLE = new SystemHandle();

    public static void submit(Integer profileTaskId, String arthasIp, Integer arthasPort) {
        if (Objects.isNull(PROFILE_FUTURE)) {
            ArthasHttpFactory.init(arthasIp, arthasPort);
            getSystemData(profileTaskId);
            PROFILE_FUTURE = PROFILE_TASK_SCHEDULE_EXECUTOR.scheduleAtFixedRate(() -> startSampling(profileTaskId), 0, 1, TimeUnit.SECONDS);
        } else {
            throw new ArthasException("arthas is start, can't reopening");
        }
    }

    public static void cancel() {
        if (Objects.nonNull(PROFILE_FUTURE)) {
            PROFILE_FUTURE.cancel(true);
            PROFILE_FUTURE = null;
        }
    }

    private static void startSampling(Integer profileTaskId) {
        CPU_HANDLE.sampling(profileTaskId);
        MEM_HANDLE.sampling(profileTaskId);
    }

    private static void getSystemData(Integer profileTaskId) {
        SYSTEM_HANDLE.sampling(profileTaskId);
    }

}