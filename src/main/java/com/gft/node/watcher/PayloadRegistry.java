package com.gft.node.watcher;

import rx.Observable;

public interface PayloadRegistry<T> {

    /**
     * @param payload Watch changes on this payload.
     * @throws CouldNotRegisterPayload When occurred problems with registering payload.
     */
    void registerPayload(T payload);

    /**
     * @return Observable with changes from registered payloads.
     */
    Observable<T> changes();
}
