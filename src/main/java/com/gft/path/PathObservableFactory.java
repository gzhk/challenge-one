package com.gft.path;

import rx.Observable;

import java.nio.file.Path;

public final class PathObservableFactory {

    public Observable<Path> create(Path path) {
        return Observable.create(new PathOnSubscribe());
    }
}
