package com.gft.application.file.model;

import com.gft.path.treenode.PathTreeNode;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public final class PathViewFactory {

    public PathView createFromPathTreeNode(PathTreeNode pathTreeNode) {
        String pathString = pathTreeNode.toString();

        if (pathTreeNode.getParent() != null) {
            return new PathView(
                DigestUtils.md5DigestAsHex(pathString.getBytes()),
                DigestUtils.md5DigestAsHex(pathTreeNode.getParent().toString().getBytes()),
                pathString
            );
        }

        return new PathView(DigestUtils.md5DigestAsHex(pathString.getBytes()), pathString);
    }
}
