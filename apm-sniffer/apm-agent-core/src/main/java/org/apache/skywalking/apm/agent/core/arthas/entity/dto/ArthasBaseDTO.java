package org.apache.skywalking.apm.agent.core.arthas.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import java.util.List;

@Data
@Accessors(chain = true)
public class ArthasBaseDTO<T> {

    private String sessionId;

    private String state;

    private ArthasBody<T> body;

    @Data
    @Accessors(chain = true)
    public static class ArthasBody<T>{
        String command;
        Integer jobId;
        String jobStatus;
        List<T> results;
        Boolean timeExpired;
    }
}
