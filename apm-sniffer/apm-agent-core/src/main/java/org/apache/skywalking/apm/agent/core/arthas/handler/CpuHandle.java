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

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.ArthasBaseDTO;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.ArthasExecDTO;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.thread.ThreadDTO;
import org.apache.skywalking.apm.agent.core.arthas.enums.ArthasCommandEnums;
import org.apache.skywalking.apm.agent.core.arthas.factory.ArthasHttpFactory;
import org.apache.skywalking.apm.agent.core.arthas.factory.impl.cpu.Thread;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
public class CpuHandle {

    public void sampling(Integer profileTaskId) {
        try {
            ArthasHttpFactory<ArthasExecDTO, ArthasBaseDTO<JSONObject>> httpFactory = new Thread();
            ArthasBaseDTO<JSONObject> execute = httpFactory.execute(new ArthasExecDTO());

            List<JSONObject> results = execute.getBody().getResults();

            results.forEach(result -> {
                ThreadDTO threadDTO = JSONObject.parseObject(result.toString(), ThreadDTO.class);
                if (threadDTO.getType().equals(ArthasCommandEnums.THREAD.getCommand())) {
                    List<ThreadDTO.ThreadStats> threadDataList = threadDTO.getThreadStats();
                    double cpu = 0;
                    for (ThreadDTO.ThreadStats threadStats : threadDataList) {
                        cpu += threadStats.getCpu();
                    }
                    BigDecimal scaleCpu = new BigDecimal(cpu);
                    cpu = scaleCpu.setScale(2, RoundingMode.HALF_UP).doubleValue();
                    ProfileSaveManager.saveCpuData(profileTaskId, cpu, threadDataList, LocalDateTime.now(ZoneId.of("GMT+8")));
                }
            });
        } catch (Exception e) {
            log.error("get arthas cpu data fail, {}", e.getMessage());
            e.printStackTrace();
        }
    }

}
