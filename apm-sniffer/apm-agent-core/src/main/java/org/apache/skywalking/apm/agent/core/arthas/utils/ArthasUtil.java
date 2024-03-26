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

package org.apache.skywalking.apm.agent.core.arthas.utils;

import org.apache.skywalking.apm.agent.core.boot.AgentPackageNotFoundException;
import org.apache.skywalking.apm.agent.core.boot.AgentPackagePath;
import org.apache.skywalking.apm.agent.core.conf.Config;
import org.apache.skywalking.apm.agent.core.logging.api.ILog;
import org.apache.skywalking.apm.agent.core.logging.api.LogManager;
import org.apache.skywalking.apm.util.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ArthasUtil {

    private static final ILog LOGGER = LogManager.getLogger(ArthasUtil.class);

    public static Boolean startArthas(long pid, int telnetPort, String ip, Integer httpPort)
            throws SecurityException, IllegalArgumentException, AgentPackageNotFoundException {
        // find arthas home
        File arthasHomeDir = getArthasHome();
        LOGGER.info("arthas home: " + arthasHomeDir);

        // start arthas-core.jar
        List<String> attachArgs = new ArrayList<String>();
        attachArgs.add("-jar");
        attachArgs.add(new File(arthasHomeDir, "arthas-core.jar").getAbsolutePath());
        attachArgs.add("-pid");
        attachArgs.add("" + pid);

        attachArgs.add("-core");
        attachArgs.add(new File(arthasHomeDir, "arthas-core.jar").getAbsolutePath());
        attachArgs.add("-agent");
        attachArgs.add(new File(arthasHomeDir, "arthas-agent.jar").getAbsolutePath());

        attachArgs.add("-app-name");
        attachArgs.add(Config.Agent.SERVICE_NAME);
        attachArgs.add("-agent-id");
        attachArgs.add(Config.Agent.INSTANCE_NAME);

        attachArgs.add("-target-ip");
        attachArgs.add(ip);
        attachArgs.add("-telnet-port");
        attachArgs.add("" + telnetPort);
        attachArgs.add("-http-port");
        attachArgs.add("" + httpPort);

        if (StringUtil.isNotBlank(Config.Arthas.DISABLED_COMMANDS)) {
            attachArgs.add("-disabled-commands");
            attachArgs.add(Config.Arthas.DISABLED_COMMANDS);
        }

        LOGGER.info("Try to attach process " + pid);
        LOGGER.debug("Start arthas-core.jar args: " + attachArgs);
        boolean flag = ProcessUtil.runJarWithArgs(attachArgs);
        LOGGER.info("Attach process {} success.", pid);

        return flag;
    }

    public static Boolean stopArthas(String ip, int telnetPort) throws Exception {
        // find arthas home
        File arthasHomeDir = getArthasHome();
        LOGGER.info("arthas home: " + arthasHomeDir);

        // start arthas-client.jar
        List<String> telnetArgs = new ArrayList<String>();

        telnetArgs.add("-jar");
        telnetArgs.add(new File(arthasHomeDir, "arthas-client.jar").getAbsolutePath());

        telnetArgs.add("-c");
        telnetArgs.add("stop");

        // telnet port ,ip
        telnetArgs.add(ip);
        telnetArgs.add("" + telnetPort);

        LOGGER.debug("Start arthas-client.jar args: " + telnetArgs);
        Boolean flag = ProcessUtil.runJarWithArgs(telnetArgs);

        LOGGER.info("Stop arthas process success.");

        return flag;
    }

    private static File getArthasHome() throws AgentPackageNotFoundException {
        File arthasHomeDir;
        if (StringUtil.isEmpty(Config.Arthas.ARTHAS_HOME)) {
            verifyArthasHome(AgentPackagePath.getPath().getAbsolutePath() + "/arthas");
            arthasHomeDir = new File(AgentPackagePath.getPath().getAbsolutePath() + "/arthas");
        } else {
            verifyArthasHome(Config.Arthas.ARTHAS_HOME);
            arthasHomeDir = new File(Config.Arthas.ARTHAS_HOME);
        }
        return arthasHomeDir;
    }

    private static void verifyArthasHome(String arthasHome) {
        File home = new File(arthasHome);
        if (home.isDirectory()) {
            String[] fileList = {
                    "arthas-core.jar",
                    "arthas-agent.jar",
                    "arthas-spy.jar"
            };

            for (String fileName : fileList) {
                if (!new File(home, fileName).exists()) {
                    throw new IllegalArgumentException(fileName + " do not exist, arthas home: " + home.getAbsolutePath());
                }
            }
            return;
        }

        throw new IllegalArgumentException("illegal arthas home: " + home.getAbsolutePath());
    }
}
