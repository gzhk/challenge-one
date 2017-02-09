package com.gft.node.watcher.async;

import java.util.concurrent.BlockingQueue;

public interface Registry<T> {

    BlockingQueue<T> queue();

    void watch(T payload);
}
