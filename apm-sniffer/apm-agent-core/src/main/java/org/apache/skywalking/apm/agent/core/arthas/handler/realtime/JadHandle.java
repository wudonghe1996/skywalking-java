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

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.ArthasBaseDTO;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.ArthasExecDTO;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.JadDTO;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.thread.ThreadStackDTO;
import org.apache.skywalking.apm.agent.core.arthas.enums.ArthasCommandEnums;
import org.apache.skywalking.apm.agent.core.arthas.factory.ArthasHttpFactory;
import org.apache.skywalking.apm.agent.core.arthas.factory.impl.clazz.JadClass;
import org.apache.skywalking.apm.agent.core.arthas.factory.impl.cpu.ThreadCode;

import java.util.List;

@Slf4j
public class JadHandle {

    public JadDTO sampling(String command) {
        JadDTO jadDTO = new JadDTO();
        try {
            ArthasHttpFactory<ArthasExecDTO, ArthasBaseDTO<JSONObject>> httpFactory = new JadClass();
            ArthasBaseDTO<JSONObject> execute = httpFactory.execute(new ArthasExecDTO().setCommand(command));
            List<JSONObject> results = execute.getBody().getResults();
            for (JSONObject result : results) {
                if (ArthasCommandEnums.JAD.getCommand().equals(result.get("type"))) {
                    jadDTO = JSONObject.parseObject(result.toString(), JadDTO.class);
                    break;
                }
            }
        } catch (Exception e) {
            log.error("get arthas jad data fail, {}", e.getMessage());
            e.printStackTrace();
        }
        return jadDTO;
    }


}
