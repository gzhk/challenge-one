package com.gft.path;

import java.nio.file.Path;

public final class CannotCreateObservable extends Exception {
    public CannotCreateObservable(final Path path, final Throwable cause) {
        super("Cannot create observable for path: " + path, cause);
    }
}
