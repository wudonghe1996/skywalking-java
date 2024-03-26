package org.apache.skywalking.apm.agent.core.arthas.entity.dto.thread;

import lombok.Data;
import lombok.experimental.Accessors;
import java.util.List;
import java.util.Map;

@Data
public class ThreadDTO {

    private Boolean all;

    private Integer jobId;

    private Map<String, Integer> threadStateCount;

    private String type;

    private Integer statusCode;

    private List<ThreadStats> threadStats;

    @Data
    @Accessors(chain = true)
    public static class ThreadStats{
        private Double cpu;

        private Boolean daemon;

        private Integer deltaTime;

        private String group;

        private Integer id;

        private Boolean interrupted;

        private String name;

        private Integer priority;

        private String state;

        private Integer time;
    }
}
