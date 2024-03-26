package org.apache.skywalking.apm.agent.core.arthas.enums;

import lombok.Getter;

public enum ArthasCommandEnums {

    SC("sc"),
    JAD("jad"),
    THREAD("thread"),
    MEMORY("memory"),
    JVM("jvm"),
    SYS_ENV("sysenv"),
    SYS_PROP("sysprop"),
    VM_OPTION("vmoption"),
    ;

    @Getter
    private final String command;

    ArthasCommandEnums(String command) {
        this.command = command;
    }
}
