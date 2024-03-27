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

package org.apache.skywalking.apm.agent.core.arthas.factory.impl.cpu;

import com.alibaba.fastjson2.JSONObject;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.ArthasExecDTO;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.FlameDiagramDTO;
import org.apache.skywalking.apm.agent.core.arthas.enums.ArthasActionEnums;
import org.apache.skywalking.apm.agent.core.arthas.factory.ArthasHttpFactory;
import org.apache.skywalking.apm.agent.core.arthas.factory.constant.ArthasConstant;
import org.apache.skywalking.apm.agent.core.arthas.utils.HttpUtil;

import java.io.IOException;

public class FlameDiagram extends ArthasHttpFactory<FlameDiagramDTO, Boolean> {
    @Override
    public Boolean execute(FlameDiagramDTO flameDiagramDTO) {
        // profiler start --duration 300 --file /tmp/test.html -i 1000000
        ArthasExecDTO request = new ArthasExecDTO().setAction(ArthasActionEnums.EXEC);
        String command = ArthasConstant.COMMAND_PROFILER +
                "--duration " + flameDiagramDTO.getDuration() + " " +
                "--file " + flameDiagramDTO.getFilePath() + " " +
                "-i 1000000";
        request.setCommand(command);

        try {
            HttpUtil.doPostJson(ARTHAS_HTTP_URL, JSONObject.toJSONString(request));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("get flame diagram data fail: " + e.getMessage());
        }
    }
}

