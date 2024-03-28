/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.skywalking.apm.agent.core.arthas;

import org.apache.skywalking.apm.agent.core.arthas.entity.dto.thread.ThreadStackDTO;
import org.apache.skywalking.apm.agent.core.arthas.factory.ArthasHttpFactory;
import org.apache.skywalking.apm.agent.core.arthas.handler.realtime.CpuCodeStackHandle;
import org.apache.skywalking.apm.agent.core.boot.ServiceManager;
import org.apache.skywalking.apm.network.arthas.v3.RealTimeCommand;

public class ArthasTest {

    public static void main(String[] args) throws InterruptedException {
        ServiceManager.INSTANCE.boot();
//        ProfileBaseHandle.submit(2032139, "172.21.0.70", 8042);
//        Thread.sleep(3000L);
//        ArthasSender arthasSender = ServiceManager.INSTANCE.findService(ArthasSender.class);
//        arthasSender.run();

        ArthasHttpFactory.init("172.21.0.70", 7543);
        CpuCodeStackHandle cpuCodeStackHandle = new CpuCodeStackHandle();
        ThreadStackDTO.ThreadInfo sampling = cpuCodeStackHandle.sampling("8593");
        ArthasService arthasService = new ArthasService();
//        arthasService.sendRealTimeDataForOap(sampling.toString(), RealTimeCommand.CPU_CODE_STACK);
//        JadHandle jadHandle = new JadHandle();
//        JadDTO sampling = jadHandle.sampling("com.dayu.mock.service.MockApplication");
//        System.out.println(sampling);
    }
}
