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

package org.apache.skywalking.apm.agent.core.arthas.handler.realtime;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.ArthasBaseDTO;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.ArthasExecDTO;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.thread.ThreadStackDTO;
import org.apache.skywalking.apm.agent.core.arthas.enums.ArthasCommandEnums;
import org.apache.skywalking.apm.agent.core.arthas.factory.ArthasHttpFactory;
import org.apache.skywalking.apm.agent.core.arthas.factory.impl.cpu.ThreadCode;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
public class CpuCodeStackHandle {

    public ThreadStackDTO.ThreadInfo sampling(String command) {
        ThreadStackDTO.ThreadInfo threadInfo = new ThreadStackDTO.ThreadInfo();
        try {
            ArthasHttpFactory<ArthasExecDTO, ArthasBaseDTO<JSONObject>> httpFactory = new ThreadCode();
            ArthasBaseDTO<JSONObject> execute = httpFactory.execute(new ArthasExecDTO().setCommand(command));
            List<JSONObject> results = execute.getBody().getResults();
            for (JSONObject result : results) {
                if (ArthasCommandEnums.THREAD.getCommand().equals(result.get("type"))) {
                    threadInfo = JSONObject.parseObject(result.get("threadInfo").toString(), ThreadStackDTO.ThreadInfo.class);
                    break;
                }
            }
        } catch (Exception e) {
            log.error("get arthas thread code data fail, {}", e.getMessage());
            e.printStackTrace();
        }
        return threadInfo;
    }


}
