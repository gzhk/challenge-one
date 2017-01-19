package com.gft.path;

import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreeNode;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public final class PathTreeNode implements TreeNode {

    private Path nodePath;
    private TreeNode parent;
    private final List<TreeNode> children;

    public PathTreeNode(@NotNull Path nodePath) {
        this.nodePath = nodePath;
        this.children = new ArrayList<>();
    }

    public PathTreeNode(@NotNull Path nodePath, @NotNull TreeNode parent) {
        this.nodePath = nodePath;
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    public PathTreeNode(@NotNull Path nodePath, @NotNull List<TreeNode> children) {
        this.nodePath = nodePath;
        this.children = children;
    }

    public PathTreeNode(@NotNull Path nodePath, @NotNull TreeNode parent, @NotNull List<TreeNode> children) {
        this.nodePath = nodePath;
        this.parent = parent;
        this.children = children;
    }

    @Override
    public TreeNode getChildAt(int childIndex) {
        return children.get(childIndex);
    }

    @Override
    public int getChildCount() {
        return children.size();
    }

    @Override
    public TreeNode getParent() {
        return parent;
    }

    @Override
    public int getIndex(TreeNode node) {
        return children.indexOf(node);
    }

    @Override
    public boolean getAllowsChildren() {
        return nodePath.toFile().isDirectory();
    }

    @Override
    public boolean isLeaf() {
        return children.isEmpty();
    }

    @Override
    public Enumeration children() {
        return Collections.enumeration(children);
    }

    public Path path() {
        return nodePath;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof PathTreeNode && nodePath.equals(((PathTreeNode) o).nodePath);
    }

    @Override
    public int hashCode() {
        return nodePath.hashCode();
    }
}
