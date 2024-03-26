package org.apache.skywalking.apm.agent.core.arthas.entity.dto.thread;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ThreadStackDTO {

    private Boolean all;

    private Integer jobId;

    private String type;

    private Integer statusCode;

    private ThreadInfo threadInfo;

    @Data
    public static class ThreadInfo{
        private Integer blockedCount;

        private Integer blockedTime;

        private Boolean inNative;

        private Map<String, Object> lockInfo;

        private String lockName;

        private Integer lockOwnerId;

        private List<Object> lockedMonitors;

        private List<Object> lockedSynchronizers;

        private Boolean suspended;

        private Integer threadId;

        private String threadName;

        private String threadState;

        private Integer waitedCount;

        private Integer waitedTime;

        private List<StackTrace> stackTrace;

        @Data
        public static class StackTrace {
            private String className;

            private String fileName;

            private Integer lineNumber;

            private String methodName;

            private Boolean nativeMethod;
        }
    }
}
