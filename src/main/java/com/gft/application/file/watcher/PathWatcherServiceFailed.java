package com.gft.application.file.watcher;

public final class PathWatcherServiceFailed extends RuntimeException {
    public PathWatcherServiceFailed(final String message, final Throwable cause) {
        super(message, cause);
    }
}
