package com.gft.path.treenode;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PathTreeNodeFactory {

    public PathTreeNode createFromPath(Path rootPath) throws CouldNotCreatePathTreeNode {
        final PathTreeNode rootPathTreeNode = new PathTreeNode(rootPath);
        final Map<Path, List<Path>> children = new HashMap<>();

        try {
            Files.walkFileTree(rootPath, new FillChildrenMap(children));
        } catch (IOException e) {
            throw new CouldNotCreatePathTreeNode(rootPath, e);
        }

        add(rootPathTreeNode, children);

        return rootPathTreeNode;
    }

    private void add(PathTreeNode parentPathTreeNode, Map<Path, List<Path>> children) {
        if (!children.containsKey(parentPathTreeNode.getPath())) {
            return;
        }

        for (Path path : children.get(parentPathTreeNode.getPath())) {
            PathTreeNode child = new PathTreeNode(path, parentPathTreeNode);
            child.setParent(parentPathTreeNode);
            parentPathTreeNode.addChild(child);
            add(child, children);
        }
    }

    private static class FillChildrenMap extends SimpleFileVisitor<Path> {

        private final Map<Path, List<Path>> children;

        FillChildrenMap(final Map<Path, List<Path>> children) {
            this.children = children;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            addChild(dir.getParent(), dir);

            return super.preVisitDirectory(dir, attrs);
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            addChild(file.getParent(), file);

            return super.visitFile(file, attrs);
        }

        private void addChild(Path parent, Path child) {
            children.computeIfAbsent(parent, path -> new ArrayList<>());
            children.get(parent).add(child);
        }
    }
}
