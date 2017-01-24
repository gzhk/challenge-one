package com.gft.application.file.model;

import java.util.Objects;

public final class PathView {

    public final String id;
//    public final String parentId;
    public final String name;

    public PathView(String name, String path) {
        this.id = name;
        this.name = path;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PathView
            && Objects.equals(id, ((PathView) obj).id)
            && Objects.equals(name, ((PathView) obj).name);
    }
}
