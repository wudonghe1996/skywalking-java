package org.apache.skywalking.apm.agent.core.arthas.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.agent.core.arthas.ArthasSender;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.thread.ThreadDTO;
import org.apache.skywalking.apm.agent.core.boot.BootService;
import org.apache.skywalking.apm.agent.core.boot.ServiceManager;
import org.apache.skywalking.apm.network.arthas.v3.ArthasSamplingData;
import org.apache.skywalking.apm.network.arthas.v3.SamplingEnum;
import org.apache.skywalking.apm.network.arthas.v3.StackData;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ProfileSaveManager implements BootService {
    private static ArthasSender arthasSender;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void saveCpuData(Integer profileTaskId, Double cpuData, List<ThreadDTO.ThreadStats> stackList, LocalDateTime dataSamplingTime) {
        ArthasSamplingData.Builder builder = ArthasSamplingData.newBuilder();
        builder.setDataSamplingTime(dataSamplingTime.format(FORMATTER));
        builder.setProfileTaskId(profileTaskId);
        builder.setSamplingEnum(SamplingEnum.CPU);
        builder.setCpuData(cpuData);
        List<StackData> coverStackList = stackList.stream().map(x -> {
            StackData.Builder stackData = StackData.newBuilder();
            stackData.setCpu(x.getCpu())
                    .setDaemon(x.getDaemon())
                    .setDeltaTime(x.getDeltaTime())
                    .setId(x.getId())
                    .setInterrupted(x.getInterrupted())
                    .setName(x.getName())
                    .setPriority(x.getPriority())
                    .setTime(x.getTime());
            return stackData.build();
        }).collect(Collectors.toList());
        builder.addAllStackList(coverStackList);

        arthasSender.offer(builder.build());
        arthasSender.run();
    }

    @Override
    public void prepare() throws Throwable {
        arthasSender = ServiceManager.INSTANCE.findService(ArthasSender.class);
    }

    @Override
    public void boot() throws Throwable {

    }

    @Override
    public void onComplete() throws Throwable {

    }

    @Override
    public void shutdown() throws Throwable {

    }

//    public void saveMemData(Integer profileTaskId, MemoryVO memoryVO, String dataSamplingTime) {
//        // 保存趋势图数据
//        redisUtil.hSet(RedisConstant.PROFILE_MEM_CHART_KEY + profileTaskId, dataSamplingTime, memoryVO);
//    }
}
