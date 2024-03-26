package org.apache.skywalking.apm.agent.core.arthas.entity.dto.system;

import lombok.Data;
import java.util.List;

@Data
public class VmOptionDTO {
    private Integer jobId;

    private String type;

    private Integer statusCode;

    private List<VmOptionInfo> vmOptions;

    @Data
    public static class VmOptionInfo{
        private String name;
        private String origin;
        private String value;
        private Boolean writeable;
    }
}
