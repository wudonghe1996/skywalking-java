package org.apache.skywalking.apm.agent.core.arthas.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.agent.core.arthas.exception.ArthasException;
import org.apache.skywalking.apm.agent.core.arthas.factory.ArthasHttpFactory;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ProfileBaseHandle {
    private static ScheduledFuture<?> PROFILE_FUTURE;
    private static final ScheduledExecutorService PROFILE_TASK_SCHEDULE_EXECUTOR = Executors.newScheduledThreadPool(5);

    private static final CpuHandle cpuHandle = new CpuHandle();

    public static void submit(Integer profileTaskId, String arthasIp, Integer arthasPort){
        if(Objects.isNull(PROFILE_FUTURE)) {
            ArthasHttpFactory.init(arthasIp, arthasPort);
            PROFILE_FUTURE = PROFILE_TASK_SCHEDULE_EXECUTOR.scheduleAtFixedRate(() -> startSampling(profileTaskId),
                    0, 1, TimeUnit.SECONDS);
        } else {
            throw new ArthasException("arthas is start, can't reopening");
        }
    }

    public static void cancel(){
        if(Objects.nonNull(PROFILE_FUTURE)){
            PROFILE_FUTURE.cancel(true);
            PROFILE_FUTURE = null;
        }
    }

    private static void startSampling(Integer profileTaskId){
        cpuHandle.sampling(profileTaskId);
//        memHandle.sampling(profileTaskId, serviceName, instanceName);
    }

}