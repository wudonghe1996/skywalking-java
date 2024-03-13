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

package org.apache.skywalking.apm.agent.core.jvm.machine;

import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.network.dayu.v3.MachineMetric;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.OperatingSystem;

import java.util.List;

@Slf4j
public class MachineAccessor {

    public MachineMetric getMachineMetrics() {
        SystemInfo si = new SystemInfo();
        MachineMetric.Builder machine = MachineMetric.newBuilder();
        HardwareAbstractionLayer hal = si.getHardware();
        getSystemData(machine, si.getOperatingSystem());
        getCpuData(machine, hal);
        getMemData(machine, hal);
        getNetData(machine, hal);
        return machine.build();
    }

    private void getCpuData(MachineMetric.Builder machine, HardwareAbstractionLayer hal) {
        CentralProcessor processor = hal.getProcessor();
        machine.setSystemCpuUsed(getCpuTotal(processor));
        int cpuCore = processor.getLogicalProcessorCount();
        machine.setCpuCore(cpuCore);
    }

    private void getMemData(MachineMetric.Builder machine, HardwareAbstractionLayer hal) {
        GlobalMemory memory = hal.getMemory();
        machine.setMachineMemoryUsed(getMemTotal(memory));
        long memoryTotal = memory.getTotal() / 1024 / 1024;
        machine.setMemoryTotal(memoryTotal);
    }

    private void getNetData(MachineMetric.Builder machine, HardwareAbstractionLayer hal) {
        List<NetworkIF> networks = hal.getNetworkIFs();

        long recv = 0L;
        long sent = 0L;
        for (NetworkIF network : networks) {
            recv += network.getBytesRecv();
            sent += network.getBytesSent();
        }

        recv /= 1024;
        sent /= 1024;
        machine.setNetRecv(recv);
        machine.setNetSent(sent);
    }

    private void getSystemData(MachineMetric.Builder machine, OperatingSystem operatingSystem) {
        machine.setProcessCount(operatingSystem.getProcessCount());
        machine.setThreadCount(operatingSystem.getThreadCount());
    }

    private double getCpuTotal(CentralProcessor processor) {
        return processor.getSystemCpuLoad(1000) * 100;
    }

    private double getMemTotal(GlobalMemory memory) {
        return ((double) (memory.getTotal() - memory.getAvailable())) / memory.getTotal() * 100;
    }

}