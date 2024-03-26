package org.apache.skywalking.apm.agent.core.arthas.entity.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class FlameDiagramDTO extends ArthasExecDTO{

    private String filePath;

    private Integer duration;
}
