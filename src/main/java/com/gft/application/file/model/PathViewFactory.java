package com.gft.application.file.model;

import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.file.Path;

@Service
public final class PathViewFactory {

    public PathView createFromPathTreeNode(Path path) {
        String pathString = path.toString();

        if (path.getParent() != null) {
            return new PathView(
                DigestUtils.md5DigestAsHex(pathString.getBytes()),
                DigestUtils.md5DigestAsHex(path.getParent().toString().getBytes()),
                pathString
            );
        }

        return new PathView(DigestUtils.md5DigestAsHex(pathString.getBytes()), pathString);
    }
}
