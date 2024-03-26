package org.apache.skywalking.apm.agent.core.arthas.entity.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArthasData {
    private LocalDateTime dataSamplingTime;

    private Integer profileTaskId;

}
