package com.gft.path.watcher;

public interface PathWatcherFactory {
    PathWatcher create() throws CouldNotCreatePathWatcher;
}
