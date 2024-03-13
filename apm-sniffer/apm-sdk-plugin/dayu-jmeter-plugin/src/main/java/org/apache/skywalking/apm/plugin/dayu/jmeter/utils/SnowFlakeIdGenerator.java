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

package org.apache.skywalking.apm.plugin.dayu.jmeter.utils;

import java.util.Objects;

public class SnowFlakeIdGenerator {
    private static final long SEQUENCE_BITS = 12L;
    private static final long SEQUENCE_SHIFT = 0L;
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);
    private static final long WORKER_ID_BITS = 5L;
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long WORKER_ID_MASK = ~(-1L << WORKER_ID_BITS);
    private static final long DATA_CENTER_ID_BITS = 5L;
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long DATA_CENTER_ID_MASK = ~(-1L << DATA_CENTER_ID_BITS);
    // time in milliseconds
    private static final long TIMESTAMP_BITS = 41L;
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;
    private static final long TIMESTAMP_MASK = ~(-1L << TIMESTAMP_BITS);

    private static final long TWEPOCH = 1656739812000L;

    private static SnowFlakeIdGenerator INSTANCE;

    private long sequence = 0L;
    private long workerId;
    private long dataCenterId;
    private Integer customId;

    private long lastTimestamp = -1L;

    public SnowFlakeIdGenerator() {
        this(0, 0, 0);
    }

    public SnowFlakeIdGenerator(long workerId, long dataCenterId, Integer customId) {
        if (workerId > WORKER_ID_MASK || workerId < 0) {
            throw new IllegalArgumentException(String.format("workerId can't be greater than %d or less than 0", WORKER_ID_MASK));
        }

        if (dataCenterId > DATA_CENTER_ID_MASK || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("dataCenterId can't be greater than %d or less than 0", DATA_CENTER_ID_MASK));
        }

        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
        this.customId = customId;
    }

    public static void createInstance(long workerId, long dataCenterId, Integer customId) {
        synchronized (SnowFlakeIdGenerator.class) {
            if (Objects.isNull(INSTANCE)) {
                synchronized (SnowFlakeIdGenerator.class) {
                    INSTANCE = new SnowFlakeIdGenerator(workerId, dataCenterId, customId);
                }
            }
        }
    }

    public static String generateId() {
        return INSTANCE.nextId();
    }

    public synchronized String nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        long low = ((timestamp - TWEPOCH) << TIMESTAMP_LEFT_SHIFT)
                | (dataCenterId << DATA_CENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;

        StringBuilder id = new StringBuilder();
        id.append(customId).append(low);

        return id.toString();
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long tilNextMillis(long timestamp, long lastTimestamp) {
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }

}
