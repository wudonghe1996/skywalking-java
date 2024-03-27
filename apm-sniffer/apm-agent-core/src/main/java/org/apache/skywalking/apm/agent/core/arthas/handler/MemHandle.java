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

@Slf4j
public class MemHandle {

//    @Autowired
//    private PishuijianHelper pishuijianHelper;
//
//    @Autowired
//    private ProfileSaveManager profileSaveManager;
//
//    public void sampling(Integer profileTaskId, String serviceName, String instanceName) {
//        try {
//            ArthasBaseDTO<MemoryDTO> arthasBaseDTO = pishuijianHelper.getIPishuijian().getMemory(serviceName, instanceName);
//            ObjectMapper objectMapper = new ObjectMapper();
//            ArthasBaseDTO<MemoryDTO> coverDTO = objectMapper.convertValue(arthasBaseDTO, new TypeReference<ArthasBaseDTO<MemoryDTO>>() {
//            });
//            List<MemoryDTO> results = coverDTO.getBody().getResults();
//
//            results.forEach(result -> {
//                if (result.getType().equals(ArthasCommandEnums.MEMORY.getCommand())) {
//                    MemoryDTO.MemoryInfo memoryInfo = result.getMemoryInfo();
//                    MemoryVO memoryVO = new MemoryVO();
//                    List<MemoryDTO.MemoryInfo.Memory> heap = memoryInfo.getHeap();
//                    computeHeapData(heap, memoryVO);
//
//                    List<MemoryDTO.MemoryInfo.Memory> nonheap = memoryInfo.getNonheap();
//                    computeNonHeapData(nonheap, memoryVO);
//
//                    profileSaveManager.saveMemData(profileTaskId, memoryVO,
//                            TimeUtils.formatToString(TimeUtils.getHourLaterDate(8, TimeUtils.getDate()), Constants.YYYY_MM_DD_HH_MM_SS));
//                }
//            });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void computeHeapData(List<MemoryDTO.MemoryInfo.Memory> heapDataList, MemoryVO memoryVO) {
//        heapDataList.forEach(heap -> {
//            String name = heap.getName();
//            if (name.equals(MemoryName.HEAP.getName())) {
//                memoryVO.setHeapMax(coverMemoryToMb(heap.getMax()))
//                        .setHeapUsed(coverMemoryToMb(heap.getUsed()));
//            } else if (name.contains(MemoryName.EDEN_SPACE.getName())) {
//                memoryVO.setEdenSpaceMax(coverMemoryToMb(heap.getMax()))
//                        .setEdenSpaceUsed(coverMemoryToMb(heap.getUsed()));
//            } else if (name.contains(MemoryName.SURVIVOR_SPACE.getName())) {
//                memoryVO.setSurvivorSpaceMax(coverMemoryToMb(heap.getMax()))
//                        .setSurvivorSpaceUsed(coverMemoryToMb(heap.getUsed()));
//            } else if (name.contains(MemoryName.OLD_GEN.getName())) {
//                memoryVO.setOldGenMax(coverMemoryToMb(heap.getMax()))
//                        .setOldGenUsed(coverMemoryToMb(heap.getUsed()));
//            }
//        });
//    }
//
//    private void computeNonHeapData(List<MemoryDTO.MemoryInfo.Memory> nonHeapDataList, MemoryVO memoryVO) {
//        nonHeapDataList.forEach(heap -> {
//            String name = heap.getName();
//            if (name.equals(MemoryName.NON_HEAP.getName())) {
//                memoryVO.setNonHeapMax(coverMemoryToMb(heap.getMax()))
//                        .setNonHeapUsed(coverMemoryToMb(heap.getUsed()));
//            } else if (name.equals(MemoryName.CODE_CACHE.getName())) {
//                memoryVO.setCodeCacheMax(coverMemoryToMb(heap.getMax()))
//                        .setCodeCacheUsed(coverMemoryToMb(heap.getUsed()));
//            } else if (name.equals(MemoryName.METASPACE.getName())) {
//                memoryVO.setMetaSpaceMax(coverMemoryToMb(heap.getMax()))
//                        .setMetaSpaceUsed(coverMemoryToMb(heap.getUsed()));
//            } else if (name.equals(MemoryName.COMPRESSED_CLASS_SPACE.getName())) {
//                // compressed_class_space
//                memoryVO.setCompressedClassSpaceMax(coverMemoryToMb(heap.getMax()))
//                        .setCompressedClassSpaceUsed(coverMemoryToMb(heap.getUsed()));
//            }
//        });
//    }
//
//    private Double coverMemoryToMb(Double memoryBytes) {
//        BigDecimal scaleCpu = new BigDecimal(memoryBytes / 1024 / 1024);
//        return scaleCpu.setScale(2, RoundingMode.HALF_UP).doubleValue();
//    }

}
