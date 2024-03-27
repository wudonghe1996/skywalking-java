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
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.system.JvmDTO;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.system.SysEnvDTO;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.system.SysPropDTO;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.system.VmOptionDTO;
import org.apache.skywalking.apm.agent.core.arthas.enums.ArthasCommandEnums;
import org.apache.skywalking.apm.agent.core.arthas.factory.ArthasHttpFactory;
import org.apache.skywalking.apm.agent.core.arthas.factory.impl.system.JvmInfo;
import org.apache.skywalking.apm.agent.core.arthas.factory.impl.system.SysEnv;
import org.apache.skywalking.apm.agent.core.arthas.factory.impl.system.SysProp;
import org.apache.skywalking.apm.agent.core.arthas.factory.impl.system.VmOption;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
public class SystemHandle {

    public void sampling(Integer profileTaskId) {
        try {
            ProfileSaveManager.saveSystemData(profileTaskId, getJvmInfo(), getSysEnv(),
                    getSysProp(), getVmOption(), LocalDateTime.now(ZoneId.of("GMT+8")));
        } catch (Exception e) {
            log.error("get arthas system data fail, {}", e.getMessage());
            e.printStackTrace();
        }
    }

    private JSONObject getJvmInfo() throws IOException {
        ArthasHttpFactory<ArthasExecDTO, ArthasBaseDTO<JSONObject>> httpFactory = new JvmInfo();
        ArthasBaseDTO<JSONObject> execute = httpFactory.execute(new ArthasExecDTO());
        List<JSONObject> results = execute.getBody().getResults();

        JSONObject result = new JSONObject();
        for (JSONObject jsonObject : results) {
            JvmDTO jvmDTO = JSONObject.parseObject(jsonObject.toString(), JvmDTO.class);
            if (jvmDTO.getType().equals(ArthasCommandEnums.JVM.getCommand())) {
                jvmDTO.getJvmInfo().forEach((k, v) -> {
                    if (!jvmDTO.isIgnoreKey(k)) {
                        v.forEach(jvmInfo -> {
                            Object value = jvmInfo.getValue();
                            if (value instanceof List) {
                                StringBuilder valueBuilder = new StringBuilder();
                                List<String> values = (List<String>) value;
                                for (String tempValue : values) {
                                    valueBuilder.append(tempValue).append(" ");
                                }
                                result.put(jvmInfo.getName(), valueBuilder.toString());
                            } else {
                                result.put(jvmInfo.getName(), value.toString());
                            }
                        });
                    }
                });
            }
        }
        return result;
    }

    private JSONObject getSysEnv() throws IOException {
        ArthasHttpFactory<ArthasExecDTO, ArthasBaseDTO<JSONObject>> httpFactory = new SysEnv();
        ArthasBaseDTO<JSONObject> execute = httpFactory.execute(new ArthasExecDTO());
        List<JSONObject> results = execute.getBody().getResults();

        JSONObject result = new JSONObject();
        for (JSONObject jsonObject : results) {
            SysEnvDTO sysEnvDTO = JSONObject.parseObject(jsonObject.toString(), SysEnvDTO.class);
            if (sysEnvDTO.getType().equals(ArthasCommandEnums.SYS_ENV.getCommand())) {
                result.putAll(sysEnvDTO.getEnv());
            }
        }
        return result;
    }

    private JSONObject getSysProp() throws IOException {
        ArthasHttpFactory<ArthasExecDTO, ArthasBaseDTO<JSONObject>> httpFactory = new SysProp();
        ArthasBaseDTO<JSONObject> execute = httpFactory.execute(new ArthasExecDTO());
        List<JSONObject> results = execute.getBody().getResults();

        JSONObject result = new JSONObject();
        for (JSONObject jsonObject : results) {
            SysPropDTO sysPropDTO = JSONObject.parseObject(jsonObject.toString(), SysPropDTO.class);
            if (sysPropDTO.getType().equals(ArthasCommandEnums.SYS_PROP.getCommand())) {
                result.putAll(sysPropDTO.getProps());
            }
        }
        return result;
    }

    private JSONObject getVmOption() throws IOException {
        ArthasHttpFactory<ArthasExecDTO, ArthasBaseDTO<JSONObject>> httpFactory = new VmOption();
        ArthasBaseDTO<JSONObject> execute = httpFactory.execute(new ArthasExecDTO());
        List<JSONObject> results = execute.getBody().getResults();

        JSONObject result = new JSONObject();
        for (JSONObject jsonObject : results) {
            VmOptionDTO vmOptionDTO = JSONObject.parseObject(jsonObject.toString(), VmOptionDTO.class);
            if (vmOptionDTO.getType().equals(ArthasCommandEnums.VM_OPTION.getCommand())) {
                vmOptionDTO.getVmOptions().forEach(vmOption -> {
                    result.put(vmOption.getName(), vmOption.getValue());
                });
            }
        }
        return result;
    }

}
