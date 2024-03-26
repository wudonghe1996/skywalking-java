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
 */

package org.apache.skywalking.apm.plugin.activemq.artemis.jakarta.client.define;

import org.apache.activemq.artemis.jms.client.ActiveMQDestination;

/**
 * {@link EnhanceInfo} saves the brokerUrl/name/address/TYPE properties.
 */
public class EnhanceInfo {
    private String brokerUrl;
    private String name;
    private String address;
    private ActiveMQDestination.TYPE type;

    public EnhanceInfo() {
    }

    /**
     * get the brokerUrl of ActiveMQ
     */
    public String getBrokerUrl() {
        return brokerUrl;
    }

    /**
     * set the brokerUrl of ActiveMQ
     */
    public void setBrokerUrl(final String brokerUrl) {
        this.brokerUrl = brokerUrl;
    }

    /**
     * get the type of destination(Queue/Topic)
     */
    public ActiveMQDestination.TYPE getType() {
        return type;
    }

    /**
     * set the type of destination(Queue/Topic)
     */
    public void setType(final ActiveMQDestination.TYPE type) {
        this.type = type;
    }

    /**
     * get the name of destination
     */
    public String getName() {
        return name;
    }

    /**
     * set the name of destination
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * get the address of destination
     */
    public String getAddress() {
        return address;
    }

    /**
     * set the address of destination
     */
    public void setAddress(final String address) {
        this.address = address;
    }
}
