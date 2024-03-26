package org.apache.skywalking.apm.agent.core.arthas.handler;


import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.agent.core.arthas.ArthasSender;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.ArthasBaseDTO;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.ArthasExecDTO;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.thread.ThreadDTO;
import org.apache.skywalking.apm.agent.core.arthas.enums.ArthasCommandEnums;
import org.apache.skywalking.apm.agent.core.arthas.factory.ArthasHttpFactory;
import org.apache.skywalking.apm.agent.core.arthas.factory.impl.cpu.Thread;
import org.apache.skywalking.apm.agent.core.boot.ServiceManager;
import org.apache.skywalking.apm.agent.core.dayu.DayuSender;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

@Slf4j
public class CpuHandle {

    public void sampling(Integer profileTaskId) {
        try{
            ArthasHttpFactory<ArthasExecDTO, ArthasBaseDTO<JSONObject>> httpFactory = new Thread();
            ArthasBaseDTO<JSONObject> execute = httpFactory.execute(new ArthasExecDTO());

            List<JSONObject> results = execute.getBody().getResults();

            results.forEach(result -> {
                ThreadDTO threadDTO = JSONObject.parseObject(result.toString(), ThreadDTO.class);
                if(threadDTO.getType().equals(ArthasCommandEnums.THREAD.getCommand())){
                    List<ThreadDTO.ThreadStats> threadDataList = threadDTO.getThreadStats();
                    double cpu = 0;
                    for (ThreadDTO.ThreadStats threadStats : threadDataList) {
                        cpu += threadStats.getCpu();
                    }
                    // 保留两位小数
                    BigDecimal scaleCpu = new BigDecimal(cpu);
                    cpu = scaleCpu.setScale(2, RoundingMode.HALF_UP).doubleValue();
                    ProfileSaveManager.saveCpuData(profileTaskId, cpu, threadDataList, LocalDateTime.now(ZoneOffset.UTC));
                }
            });
        } catch(Exception e){
            log.error("获取性能分析CPU数据失败，{}" , e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ArthasHttpFactory.init("172.21.0.70", 16108);
        CpuHandle cpuHandle = new CpuHandle();
        cpuHandle.sampling(1);
    }
}
