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
import org.apache.skywalking.apm.agent.core.arthas.ArthasSender;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.thread.ThreadDTO;
import org.apache.skywalking.apm.agent.core.boot.BootService;
import org.apache.skywalking.apm.agent.core.boot.ServiceManager;
import org.apache.skywalking.apm.network.arthas.v3.ArthasSamplingData;
import org.apache.skywalking.apm.network.arthas.v3.SamplingEnum;
import org.apache.skywalking.apm.network.arthas.v3.StackData;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ProfileSaveManager implements BootService {
    private static ArthasSender ARTHAS_SENDER;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void saveCpuData(Integer profileTaskId, Double cpuData, List<ThreadDTO.ThreadStats> stackList, LocalDateTime dataSamplingTime) {
        ArthasSamplingData.Builder builder = ArthasSamplingData.newBuilder();
        builder.setDataSamplingTime(dataSamplingTime.format(FORMATTER));
        builder.setProfileTaskId(profileTaskId);
        builder.setSamplingEnum(SamplingEnum.CPU);
        builder.setCpuData(cpuData);
        List<StackData> coverStackList = stackList.stream().map(x -> {
            StackData.Builder stackData = StackData.newBuilder();
            stackData.setCpu(x.getCpu())
                    .setDaemon(x.getDaemon())
                    .setDeltaTime(x.getDeltaTime())
                    .setId(x.getId())
                    .setInterrupted(x.getInterrupted())
                    .setName(x.getName())
                    .setPriority(x.getPriority())
                    .setTime(x.getTime());
            return stackData.build();
        }).collect(Collectors.toList());
        builder.addAllStackList(coverStackList);

        ARTHAS_SENDER.offer(builder.build());
        ARTHAS_SENDER.run();
    }

    @Override
    public void prepare() throws Throwable {
        ARTHAS_SENDER = ServiceManager.INSTANCE.findService(ArthasSender.class);
    }

    @Override
    public void boot() throws Throwable {

    }

    @Override
    public void onComplete() throws Throwable {

    }

    @Override
    public void shutdown() throws Throwable {

    }

//    public void saveMemData(Integer profileTaskId, MemoryVO memoryVO, String dataSamplingTime) {
//        redisUtil.hSet(RedisConstant.PROFILE_MEM_CHART_KEY + profileTaskId, dataSamplingTime, memoryVO);
//    }
}
