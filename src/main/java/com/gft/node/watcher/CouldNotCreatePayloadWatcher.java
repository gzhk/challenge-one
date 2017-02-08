package com.gft.node.watcher;

public final class CouldNotCreatePayloadWatcher extends RuntimeException {
    public CouldNotCreatePayloadWatcher(final String message, final Throwable cause) {
        super(message, cause);
    }
}
