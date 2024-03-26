package org.apache.skywalking.apm.agent.core.arthas.entity.dto.mem;

import lombok.Data;
import java.util.List;

@Data
public class MemoryDTO {

    private Integer jobId;

    private String type;

    private Integer statusCode;

    private MemoryInfo memoryInfo;

    @Data
    public static class MemoryInfo{

        private List<Memory> heap;

        private List<Memory> nonheap;

        private List<Memory> buffer_pool;

        @Data
        public static class Memory{

            private Double max;

            private String name;

            private Double total;

            private String type;

            private Double used;
        }
    }

}
