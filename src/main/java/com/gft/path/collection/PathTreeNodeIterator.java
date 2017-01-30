package com.gft.path.collection;

import com.gft.path.treenode.PathTreeNode;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;

final class PathTreeNodeIterator implements Iterator<PathTreeNode> {

    private PathTreeNode rootPath;
    private PathTreeNode next;
    private boolean hasNext;
    private PathTreeNode lastPath;

    public PathTreeNodeIterator() {
        this.hasNext = false;
    }

    PathTreeNodeIterator(@NotNull PathTreeNode rootPath, @NotNull PathTreeNode lastPath) {
        this.rootPath = rootPath;
        this.hasNext = true;
        this.next = rootPath;
        this.lastPath = lastPath;
    }

    @Override
    public boolean hasNext() {
        if (rootPath == null || lastPath == null) {
            return false;
        }

        boolean hasNext = this.hasNext;
        this.hasNext = !next.equals(lastPath);

        return hasNext;
    }

    @Override
    public PathTreeNode next() {
        if (rootPath == null || lastPath == null) {
            return null;
        }

        PathTreeNode nextPath = next;
        next = new PathTreeNode(pathForNextIteration(rootPath.getPath(), nextPath.getPath()));

        return nextPath;
    }

    private Path pathForNextIteration(@NotNull Path rootPath, @NotNull Path lastPath) {
        FindNextPathFileVisitor visitor = new FindNextPathFileVisitor(rootPath, lastPath);

        try {
            Files.walkFileTree(rootPath, visitor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return visitor.nextPath();
    }

    private static final class FindNextPathFileVisitor extends SimpleFileVisitor<Path> {

        private final Path rootPath;
        private Path path;
        private boolean setupPathAsNext;

        FindNextPathFileVisitor(@NotNull Path rootPath, @NotNull Path lastPath) {
            this.rootPath = rootPath;
            this.path = lastPath;
            this.setupPathAsNext = false;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            return setupAsNextPath(dir);
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            return setupAsNextPath(file);
        }

        private FileVisitResult setupAsNextPath(Path pathCandidate) {
            if (setupPathAsNext) {
                path = pathCandidate;

                return FileVisitResult.TERMINATE;
            }

            if (pathCandidate.equals(path) || path.equals(rootPath)) {
                setupPathAsNext = true;
            }

            return FileVisitResult.CONTINUE;
        }

        public Path nextPath() {
            return path;
        }
    }
}
