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

package org.apache.skywalking.apm.agent.core.arthas.factory.impl.clazz;

import com.alibaba.fastjson2.JSONObject;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.ArthasBaseDTO;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.ArthasExecDTO;
import org.apache.skywalking.apm.agent.core.arthas.enums.ArthasActionEnums;
import org.apache.skywalking.apm.agent.core.arthas.factory.ArthasHttpFactory;
import org.apache.skywalking.apm.agent.core.arthas.factory.constant.ArthasConstant;
import org.apache.skywalking.apm.agent.core.arthas.utils.HttpUtil;
import java.io.IOException;

public class JadClass extends ArthasHttpFactory<ArthasExecDTO, ArthasBaseDTO<JSONObject>> {
    @Override
    public ArthasBaseDTO<JSONObject> execute(ArthasExecDTO arthasExecDTO) throws IOException {
        ArthasExecDTO request = new ArthasExecDTO().setAction(ArthasActionEnums.EXEC);
        String command = arthasExecDTO.getCommand();
        request.setCommand(ArthasConstant.COMMAND_JAD + command);

        String response = HttpUtil.doPostJson(ARTHAS_HTTP_URL, JSONObject.toJSONString(request));
        return JSONObject.parseObject(response, ArthasBaseDTO.class);
    }
}
