package com.gft.path.watcher;

import java.nio.file.Path;

public final class CouldNotRegisterPath extends Exception {

    public CouldNotRegisterPath(final Path path, final Throwable cause) {
        super("Could not register path " + path + ".", cause);
    }
}
