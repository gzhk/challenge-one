package com.gft.application.file.model;

import com.gft.path.treenode.PathTreeNode;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public final class PathViewFactory {

    public PathView createFromPathTreeNode(PathTreeNode pathTreeNode) {
        String path = pathTreeNode.getPath().toString();

        return new PathView(DigestUtils.md5DigestAsHex(path.getBytes()), path);
    }
}
