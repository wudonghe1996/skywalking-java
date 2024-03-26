package org.apache.skywalking.apm.agent.core.arthas.factory.impl.cpu;


import com.alibaba.fastjson2.JSONObject;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.ArthasExecDTO;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.FlameDiagramDTO;
import org.apache.skywalking.apm.agent.core.arthas.enums.ArthasActionEnums;
import org.apache.skywalking.apm.agent.core.arthas.factory.ArthasHttpFactory;
import org.apache.skywalking.apm.agent.core.arthas.factory.constant.ArthasConstant;
import org.apache.skywalking.apm.agent.core.arthas.utils.HttpUtil;
import java.io.IOException;

public class FlameDiagram extends ArthasHttpFactory<FlameDiagramDTO, Boolean> {
    @Override
    public Boolean execute(FlameDiagramDTO flameDiagramDTO) {
        // profiler start --duration 300 --file /tmp/test.html -i 1000000
        ArthasExecDTO request = new ArthasExecDTO().setAction(ArthasActionEnums.EXEC);
        String command = ArthasConstant.COMMAND_PROFILER +
                "--duration " + flameDiagramDTO.getDuration() + " " +
                "--file " + flameDiagramDTO.getFilePath() + " " +
                "-i 1000000";
        request.setCommand(command);

        try {
            HttpUtil.doPostJson(ARTHAS_HTTP_URL, JSONObject.toJSONString(request));
            return true;
        } catch (IOException e) {
            throw new RuntimeException("get flame diagram data fail：" + e.getMessage());
        }
    }
}

