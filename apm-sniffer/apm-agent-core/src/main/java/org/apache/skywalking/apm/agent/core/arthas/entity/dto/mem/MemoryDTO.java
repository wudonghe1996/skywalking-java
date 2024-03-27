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

package org.apache.skywalking.apm.agent.core.arthas.entity.dto.mem;

import lombok.Data;

import java.util.List;

@Data
public class MemoryDTO {

    private Integer jobId;

    private String type;

    private Integer statusCode;

    private MemoryInfo memoryInfo;

    @Data
    public static class MemoryInfo {

        private List<Memory> heap;

        private List<Memory> nonheap;

//        private List<Memory> buffer_pool;

        @Data
        public static class Memory {

            private Double max;

            private String name;

            private Double total;

            private String type;

            private Double used;
        }
    }

}
