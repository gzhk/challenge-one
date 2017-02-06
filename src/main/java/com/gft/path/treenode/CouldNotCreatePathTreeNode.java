package com.gft.path.treenode;

import java.nio.file.Path;

public final class CouldNotCreatePathTreeNode extends Exception {

    public CouldNotCreatePathTreeNode(final Path path, final Throwable cause) {
        super("Could not create path three node for path: " + path, cause);
    }
}
