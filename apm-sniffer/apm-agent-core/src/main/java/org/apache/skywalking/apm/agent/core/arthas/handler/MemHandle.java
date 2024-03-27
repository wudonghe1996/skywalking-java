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
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.mem.MemoryDTO;
import org.apache.skywalking.apm.agent.core.arthas.enums.ArthasCommandEnums;
import org.apache.skywalking.apm.agent.core.arthas.enums.MemoryName;
import org.apache.skywalking.apm.agent.core.arthas.factory.ArthasHttpFactory;
import org.apache.skywalking.apm.agent.core.arthas.factory.impl.mem.Memory;
import org.apache.skywalking.apm.network.arthas.v3.MemoryData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
public class MemHandle {

    public void sampling(Integer profileTaskId) {
        try {
            ArthasHttpFactory<ArthasExecDTO, ArthasBaseDTO<JSONObject>> httpFactory = new Memory();
            ArthasBaseDTO<JSONObject> execute = httpFactory.execute(new ArthasExecDTO());
            List<JSONObject> results = execute.getBody().getResults();
            results.forEach(result -> {
                MemoryDTO memoryDTO = JSONObject.parseObject(result.toString(), MemoryDTO.class);
                if (memoryDTO.getType().equals(ArthasCommandEnums.MEMORY.getCommand())) {
                    MemoryDTO.MemoryInfo memoryInfo = memoryDTO.getMemoryInfo();
                    MemoryData.Builder memoryData = MemoryData.newBuilder();
                    List<MemoryDTO.MemoryInfo.Memory> heap = memoryInfo.getHeap();
                    computeHeapData(heap, memoryData);
                    List<MemoryDTO.MemoryInfo.Memory> nonheap = memoryInfo.getNonheap();
                    computeNonHeapData(nonheap, memoryData);

                    ProfileSaveManager.saveMemData(profileTaskId, memoryData.build(), LocalDateTime.now(ZoneId.of("GMT+8")));
                }
            });
        } catch (Exception e) {
            log.error("get arthas cpu data fail, {}", e.getMessage());
            e.printStackTrace();
        }
    }

    private void computeHeapData(List<MemoryDTO.MemoryInfo.Memory> heapDataList, MemoryData.Builder memoryVO) {
        heapDataList.forEach(heap -> {
            String name = heap.getName();
            if (name.equals(MemoryName.HEAP.getName())) {
                memoryVO.setHeapMax(coverMemoryToMb(heap.getMax()))
                        .setHeapUsed(coverMemoryToMb(heap.getUsed()));
            } else if (name.contains(MemoryName.EDEN_SPACE.getName())) {
                memoryVO.setEdenSpaceMax(coverMemoryToMb(heap.getMax()))
                        .setEdenSpaceUsed(coverMemoryToMb(heap.getUsed()));
            } else if (name.contains(MemoryName.SURVIVOR_SPACE.getName())) {
                memoryVO.setSurvivorSpaceMax(coverMemoryToMb(heap.getMax()))
                        .setSurvivorSpaceUsed(coverMemoryToMb(heap.getUsed()));
            } else if (name.contains(MemoryName.OLD_GEN.getName())) {
                memoryVO.setOldGenMax(coverMemoryToMb(heap.getMax()))
                        .setOldGenUsed(coverMemoryToMb(heap.getUsed()));
            }
        });
    }

    private void computeNonHeapData(List<MemoryDTO.MemoryInfo.Memory> nonHeapDataList, MemoryData.Builder memoryVO) {
        nonHeapDataList.forEach(heap -> {
            String name = heap.getName();
            if (name.equals(MemoryName.NON_HEAP.getName())) {
                memoryVO.setNonHeapMax(coverMemoryToMb(heap.getMax()))
                        .setNonHeapUsed(coverMemoryToMb(heap.getUsed()));
            } else if (name.equals(MemoryName.CODE_CACHE.getName())) {
                memoryVO.setCodeCacheMax(coverMemoryToMb(heap.getMax()))
                        .setCodeCacheUsed(coverMemoryToMb(heap.getUsed()));
            } else if (name.equals(MemoryName.METASPACE.getName())) {
                memoryVO.setMetaSpaceMax(coverMemoryToMb(heap.getMax()))
                        .setMetaSpaceUsed(coverMemoryToMb(heap.getUsed()));
            } else if (name.equals(MemoryName.COMPRESSED_CLASS_SPACE.getName())) {
                memoryVO.setCompressedClassSpaceMax(coverMemoryToMb(heap.getMax()))
                        .setCompressedClassSpaceUsed(coverMemoryToMb(heap.getUsed()));
            }
        });
    }

    private Double coverMemoryToMb(Double memoryBytes) {
        BigDecimal scaleCpu = new BigDecimal(memoryBytes / 1024 / 1024);
        return scaleCpu.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

}