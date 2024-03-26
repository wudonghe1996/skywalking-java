package org.apache.skywalking.apm.agent.core.arthas.exception;

public class ArthasException extends RuntimeException{

    public ArthasException(String message) {
        super(message);
    }
}
