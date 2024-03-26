package org.apache.skywalking.apm.agent.core.arthas;

import org.apache.skywalking.apm.agent.core.arthas.handler.ProfileBaseHandle;
import org.apache.skywalking.apm.agent.core.boot.ServiceManager;

public class ArthasTest {

    public static void main(String[] args) throws InterruptedException {
        ServiceManager.INSTANCE.boot();
        ProfileBaseHandle.submit(1, "172.21.0.70", 28095);
        Thread.sleep(3000L);
        ArthasSender arthasSender = ServiceManager.INSTANCE.findService(ArthasSender.class);
        arthasSender.run();
    }
}
