package org.apache.skywalking.apm.agent.core.arthas.entity.dto.system;

import lombok.Data;
import java.util.Map;

@Data
public class SysPropDTO {
    private Integer jobId;

    private String type;

    private Integer statusCode;

    private Map<String, String> props;

}
