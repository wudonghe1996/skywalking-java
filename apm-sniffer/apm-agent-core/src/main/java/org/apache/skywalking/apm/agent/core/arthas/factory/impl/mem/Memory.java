package org.apache.skywalking.apm.agent.core.arthas.factory.impl.mem;

import com.alibaba.fastjson2.JSONObject;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.ArthasBaseDTO;
import org.apache.skywalking.apm.agent.core.arthas.entity.dto.ArthasExecDTO;
import org.apache.skywalking.apm.agent.core.arthas.enums.ArthasActionEnums;
import org.apache.skywalking.apm.agent.core.arthas.factory.ArthasHttpFactory;
import org.apache.skywalking.apm.agent.core.arthas.factory.constant.ArthasConstant;
import org.apache.skywalking.apm.agent.core.arthas.utils.HttpUtil;
import java.io.IOException;

public class Memory extends ArthasHttpFactory<ArthasExecDTO, ArthasBaseDTO<JSONObject>> {
    @Override
    public ArthasBaseDTO<JSONObject> execute(ArthasExecDTO arthasExecDTO) throws IOException {
        ArthasExecDTO request = new ArthasExecDTO()
                .setCommand(ArthasConstant.COMMAND_MEMORY)
                .setAction(ArthasActionEnums.EXEC);

        String response = HttpUtil.doPostJson(ARTHAS_HTTP_URL, JSONObject.toJSONString(request));
        return JSONObject.parseObject(response, ArthasBaseDTO.class);
    }
}
