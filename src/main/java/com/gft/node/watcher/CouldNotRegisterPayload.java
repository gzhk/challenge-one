package com.gft.node.watcher;

public final class CouldNotRegisterPayload extends RuntimeException {
    public CouldNotRegisterPayload(final String message, final Throwable cause) {
        super(message, cause);
    }
}
