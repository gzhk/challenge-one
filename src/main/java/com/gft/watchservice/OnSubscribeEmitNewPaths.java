package com.gft.watchservice;

import com.gft.watchservice.iterator.WatchServiceIterator;
import com.gft.watchservice.iterator.WatchServiceIteratorFactory;
import com.sun.nio.file.SensitivityWatchEventModifier;
import org.jetbrains.annotations.NotNull;
import rx.Observable.OnSubscribe;
import rx.Subscriber;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchService;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

public final class OnSubscribeEmitNewPaths implements OnSubscribe<Path> {

    private final Path rootPath;
    private final WatchServiceFactory watchServiceFactory;
    private final WatchServiceIteratorFactory watchServiceIteratorFactory;

    public OnSubscribeEmitNewPaths(
        @NotNull final Path rootPath,
        @NotNull final WatchServiceFactory watchServiceFactory,
        @NotNull final WatchServiceIteratorFactory watchServiceIteratorFactory
    ) {
        this.rootPath = rootPath;
        this.watchServiceFactory = watchServiceFactory;
        this.watchServiceIteratorFactory = watchServiceIteratorFactory;
    }

    @Override
    public void call(final Subscriber<? super Path> subscriber) {
        WatchService watchService = watchServiceFactory.create();
        registerPathsRecursively(rootPath, watchService);

        try (WatchServiceIterator iterator = watchServiceIteratorFactory.create(watchService)) {
            while (iterator.hasNext()) {
                if (subscriber.isUnsubscribed()) {
                    break;
                }

                subscriber.onNext(iterator.next());
            }
        } catch (Exception e) {
            if (!subscriber.isUnsubscribed()) {
                subscriber.onError(e);
                return;
            }
        }

        if (!subscriber.isUnsubscribed()) {
            subscriber.onCompleted();
        }
    }

    private void registerPathsRecursively(@NotNull final Path rootPath, @NotNull final WatchService watchService) {
        try {
            Files.walk(rootPath).forEach(path -> {
                if (Files.isDirectory(path)) {
                    try {
                        path.register(watchService, new WatchEvent.Kind[]{ENTRY_CREATE}, SensitivityWatchEventModifier.HIGH);
                    } catch (IOException e) {
                        throw new OnSubscribeEmitNewPathsException("Could not register path: '" + rootPath.resolve(path) + "' for watching changes.", e);
                    }
                }
            });
        } catch (IOException e) {
            throw new OnSubscribeEmitNewPathsException("Could not walk path: " + rootPath, e);
        }
    }
}
