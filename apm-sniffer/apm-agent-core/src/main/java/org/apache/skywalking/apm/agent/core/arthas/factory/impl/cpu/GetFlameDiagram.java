package org.apache.skywalking.apm.agent.core.arthas.factory.impl.cpu;

import org.apache.skywalking.apm.agent.core.arthas.entity.dto.FlameDiagramDTO;
import org.apache.skywalking.apm.agent.core.arthas.factory.ArthasHttpFactory;
import org.apache.skywalking.apm.agent.core.arthas.profile.FileUtils;
import java.io.IOException;

public class GetFlameDiagram extends ArthasHttpFactory<FlameDiagramDTO, String> {
    @Override
    public String execute(FlameDiagramDTO flameDiagramDTO) throws IOException {
        return FileUtils.readFileNewLineWithBr(flameDiagramDTO.getFilePath(), "\n");
    }
}