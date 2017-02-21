package com.gft.path;

import com.sun.nio.file.SensitivityWatchEventModifier;
import org.jetbrains.annotations.NotNull;
import rx.Observable.OnSubscribe;
import rx.Subscriber;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public final class WatchPathOnSubscribe implements OnSubscribe<Path> {

    private final Path rootDir;
    private final WatchService watchService;
    private final Map<WatchKey, Path> keys = new HashMap<>();


    public WatchPathOnSubscribe(@NotNull final Path rootDir, @NotNull final WatchService watchService) {
        this.rootDir = rootDir;
        this.watchService = watchService;
    }

    @Override
    public void call(final Subscriber<? super Path> subscriber) {
        watchChangesRecursive(rootDir);

        while (true) {
            final WatchKey watchKey;

            try {
                watchKey = watchService.take();
            } catch (InterruptedException e) {
                return;
            }

            watchKey.pollEvents()
                .stream()
                .filter(watchEvent -> watchEvent.kind() != OVERFLOW)
                .map(watchEvent -> ((WatchEvent<Path>) watchEvent).context())
                .map(path -> keys.get(watchKey).resolve(path))
                .forEach(path -> {
                    watchChangesRecursive(path);
                    try {
                        Files.walk(path).forEach(subscriber::onNext);
                    } catch (IOException e) {
                        subscriber.onError(e);
                    }
                });

            boolean valid = watchKey.reset();

            if (!valid) {
                break;
            }
        }
    }

    private void watchChangesRecursive(@NotNull final Path rootPath) {
        try {
            Iterator<Path> pathIterator = Files.walk(rootPath).iterator();

            while (pathIterator.hasNext()) {
                Path path = pathIterator.next();
                WatchKey watchKey = path.register(watchService, new WatchEvent.Kind[]{ENTRY_CREATE}, SensitivityWatchEventModifier.HIGH);
                keys.put(watchKey, path);
            }
        } catch (IOException e) {
            throw new WatchPathOnSubscribeException("Could not register path for watching changes." + rootPath, e);
        }
    }
}
