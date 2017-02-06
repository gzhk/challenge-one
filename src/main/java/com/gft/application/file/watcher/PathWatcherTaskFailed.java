package com.gft.application.file.watcher;

public final class PathWatcherTaskFailed extends RuntimeException {
    public PathWatcherTaskFailed(final String message, final Throwable cause) {
        super(message, cause);
    }
}
