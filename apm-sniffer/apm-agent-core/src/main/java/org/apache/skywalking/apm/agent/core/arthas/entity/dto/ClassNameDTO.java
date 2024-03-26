package org.apache.skywalking.apm.agent.core.arthas.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import java.util.List;

@Data
@Accessors(chain = true)
public class ClassNameDTO {

    private List<String> classNames;

    private Boolean detailed;

    private Integer jobId;

    private Integer segment;

    private String type;

    private Boolean withField;

    private Integer rowCount;

    private Integer statusCode;
}
