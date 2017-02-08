package com.gft.node.watcher;

import rx.functions.Action1;

public interface PayloadWatcher<T> extends Iterable<T>, Action1<T> {
}
