package org.apache.skywalking.apm.agent.core.arthas.handler;

import lombok.extern.slf4j.Slf4j;

/**
 * 内存数据采集
 *
 * @author regou
 * @date 2023-10-20 13:36:00
 */
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
//                    // 计算堆内存
//                    List<MemoryDTO.MemoryInfo.Memory> heap = memoryInfo.getHeap();
//                    computeHeapData(heap, memoryVO);
//
//                    // 计算堆外内存
//                    List<MemoryDTO.MemoryInfo.Memory> nonheap = memoryInfo.getNonheap();
//                    computeNonHeapData(nonheap, memoryVO);
//
//                    // 保存数据
//                    profileSaveManager.saveMemData(profileTaskId, memoryVO,
//                            TimeUtils.formatToString(TimeUtils.getHourLaterDate(8, TimeUtils.getDate()), Constants.YYYY_MM_DD_HH_MM_SS));
//                }
//            });
//
//        } catch (Exception e) {
//            log.error("获取性能分析内存数据失败，{}", e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 计算堆内存
//     *
//     * @param heapDataList 堆内存数据
//     * @param memoryVO     返回值
//     */
//    private void computeHeapData(List<MemoryDTO.MemoryInfo.Memory> heapDataList, MemoryVO memoryVO) {
//        heapDataList.forEach(heap -> {
//            String name = heap.getName();
//            // 堆内存
//            if (name.equals(MemoryName.HEAP.getName())) {
//                memoryVO.setHeapMax(coverMemoryToMb(heap.getMax()))
//                        .setHeapUsed(coverMemoryToMb(heap.getUsed()));
//            } else if (name.contains(MemoryName.EDEN_SPACE.getName())) {
//                // eden区数据
//                memoryVO.setEdenSpaceMax(coverMemoryToMb(heap.getMax()))
//                        .setEdenSpaceUsed(coverMemoryToMb(heap.getUsed()));
//            } else if (name.contains(MemoryName.SURVIVOR_SPACE.getName())) {
//                // 年轻代
//                memoryVO.setSurvivorSpaceMax(coverMemoryToMb(heap.getMax()))
//                        .setSurvivorSpaceUsed(coverMemoryToMb(heap.getUsed()));
//            } else if (name.contains(MemoryName.OLD_GEN.getName())) {
//                // 老年代
//                memoryVO.setOldGenMax(coverMemoryToMb(heap.getMax()))
//                        .setOldGenUsed(coverMemoryToMb(heap.getUsed()));
//            }
//        });
//    }
//
//    /**
//     * 计算堆外内存
//     * @param nonHeapDataList 堆外内存数据
//     * @param memoryVO 返回值
//     */
//    private void computeNonHeapData(List<MemoryDTO.MemoryInfo.Memory> nonHeapDataList, MemoryVO memoryVO) {
//        nonHeapDataList.forEach(heap -> {
//            String name = heap.getName();
//            // 堆外内存
//            if (name.equals(MemoryName.NON_HEAP.getName())) {
//                memoryVO.setNonHeapMax(coverMemoryToMb(heap.getMax()))
//                        .setNonHeapUsed(coverMemoryToMb(heap.getUsed()));
//            } else if (name.equals(MemoryName.CODE_CACHE.getName())) {
//                // 代码缓存区
//                memoryVO.setCodeCacheMax(coverMemoryToMb(heap.getMax()))
//                        .setCodeCacheUsed(coverMemoryToMb(heap.getUsed()));
//            } else if (name.equals(MemoryName.METASPACE.getName())) {
//                // meta区
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
//    /**
//     * 内存数据转换，从bytes转为MB
//     *
//     * @param memoryBytes bytes格式的数据
//     * @return java.lang.Integer
//     */
//    private Double coverMemoryToMb(Double memoryBytes) {
//        // 保留两位小数
//        BigDecimal scaleCpu = new BigDecimal(memoryBytes / 1024 / 1024);
//        return scaleCpu.setScale(2, RoundingMode.HALF_UP).doubleValue();
//    }

}
