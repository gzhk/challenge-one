package com.gft.path;

import rx.Observable.OnSubscribe;
import rx.Subscriber;

import java.nio.file.Path;

public final class PathOnSubscribe implements OnSubscribe<Path> {

    @Override
    public void call(final Subscriber<? super Path> subscriber) {
    }
}
