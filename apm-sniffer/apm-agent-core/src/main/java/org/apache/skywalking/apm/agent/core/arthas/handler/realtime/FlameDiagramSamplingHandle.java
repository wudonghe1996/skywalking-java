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

import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.FlameDiagramDTO;
import org.apache.skywalking.apm.agent.core.arthas.factory.ArthasHttpFactory;
import org.apache.skywalking.apm.agent.core.arthas.factory.impl.cpu.FlameDiagram;

@Slf4j
public class FlameDiagramSamplingHandle {

    private static final Integer DEFAULT_DURATION = 3;

    public void sampling(String command) {
        try {
            ArthasHttpFactory<FlameDiagramDTO, Boolean> httpFactory = new FlameDiagram();
            String filePath = System.getProperty("user.dir") + command;
            httpFactory.execute(new FlameDiagramDTO().setDuration(DEFAULT_DURATION).setFilePath(filePath));
        } catch (Exception e) {
            log.error("sampling flame diagram data fail, {}", e.getMessage());
            e.printStackTrace();
        }
    }


}
