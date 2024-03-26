package org.apache.skywalking.apm.agent.core.arthas.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class StartArthasDTO {

    private String serviceName;

    private String instanceName;

}
