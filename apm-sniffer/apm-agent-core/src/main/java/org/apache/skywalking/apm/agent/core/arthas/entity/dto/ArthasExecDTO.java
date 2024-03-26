package org.apache.skywalking.apm.agent.core.arthas.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.skywalking.apm.agent.core.arthas.enums.ArthasActionEnums;

@Data
@Accessors(chain = true)
public class ArthasExecDTO {

    private ArthasActionEnums action;

    private String command;

    private String serviceName;

    private String instanceName;

}
