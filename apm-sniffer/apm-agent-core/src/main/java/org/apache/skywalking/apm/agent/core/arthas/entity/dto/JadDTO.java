package org.apache.skywalking.apm.agent.core.arthas.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import java.util.HashMap;

@Data
@Accessors(chain = true)
public class JadDTO {

    private HashMap<String, Object> classInfo;

    private Integer jobId;

    private String location;

    private HashMap<String, Object> mappings;

    private String source;

    private String type;

    private Integer rowCount;

    private Integer statusCode;

}
