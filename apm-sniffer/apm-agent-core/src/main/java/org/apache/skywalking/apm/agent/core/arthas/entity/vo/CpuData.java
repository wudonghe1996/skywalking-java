package org.apache.skywalking.apm.agent.core.arthas.entity.vo;

import lombok.Builder;
import lombok.Data;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.thread.ThreadDTO;

import java.util.List;

@Data
@Builder
public class CpuData extends ArthasData {

    private Double cpuData;

    private List<ThreadDTO.ThreadStats> stackList;
}
