package com.gft.application.file.model;

import java.util.Objects;

public final class PathView {

    public final String id;
    public final String parentId;
    public final String name;

    public PathView(final String id, final String parentId, final String path) {
        this.id = id;
        this.parentId = parentId;
        this.name = path;
    }

    public PathView(final String name, final String path) {
        this.id = name;
        this.parentId = null;
        this.name = path;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PathView
            && Objects.equals(id, ((PathView) obj).id)
            && Objects.equals(name, ((PathView) obj).name)
            && ((parentId == null && ((PathView) obj).parentId == null) || Objects.equals(parentId, ((PathView) obj).parentId));
    }
}
