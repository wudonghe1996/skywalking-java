package org.apache.skywalking.apm.agent.core.arthas.entity.dto.system;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class JvmDTO {

    private Integer jobId;

    private String type;

    private Integer statusCode;

    private Map<String, List<JvmInfo>> jvmInfo;

    @Data
    public static class JvmInfo{
        private String name;

        private Object value;

        private String desc;
    }

    /** 内存信息不展示 */
    private final String[] ignoreKeys = new String[]{"GARBAGE-COLLECTORS", "MEMORY-MANAGERS", "MEMORY"};

    public Boolean isIgnoreKey(String key){
        for (String ignoreKey : ignoreKeys) {
            if(key.equals(ignoreKey)){
                return true;
            }
        }
        return false;
    }
}
