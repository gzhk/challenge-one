package com.gft.path;

import com.sun.nio.file.SensitivityWatchEventModifier;
import org.jetbrains.annotations.NotNull;
import rx.Observable.OnSubscribe;
import rx.Subscriber;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

public final class WatchPathOnSubscribe implements OnSubscribe<Path> {

    private final Path rootDir;
    private final WatchService watchService;

    public WatchPathOnSubscribe(@NotNull final Path rootDir, @NotNull final WatchService watchService) {
        this.rootDir = rootDir;
        this.watchService = watchService;
    }

    @Override
    public void call(final Subscriber<? super Path> subscriber) {
        try {
            final Map<WatchKey, Path> keys = new HashMap<>();
            keys.putAll(watchChangesRecursive(rootDir, watchService));

            while (true) {
                if (subscriber.isUnsubscribed()) {
                    return;
                }

                final WatchKey watchKey = watchService.take();

                watchKey.pollEvents()
                    .stream()
                    .filter(watchEvent -> watchEvent.kind() != OVERFLOW)
                    .map(watchEvent -> ((WatchEvent<Path>) watchEvent).context())
                    .map(path -> keys.get(watchKey).resolve(path))
                    .forEach(path -> {
                        keys.putAll(watchChangesRecursive(path, watchService));

                        visitPathRecursive(path, p -> {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(p);
                            }
                        });
                    });

                if (!watchKey.reset()) {
                    break;
                }
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

    private Map<WatchKey, Path> watchChangesRecursive(@NotNull final Path rootPath, @NotNull final WatchService watchService) {
        final Map<WatchKey, Path> keys = new HashMap<>();

        visitPathRecursive(rootPath, path -> {
            if (Files.isDirectory(path)) {
                try {
                    WatchKey watchKey = path.register(watchService, new WatchEvent.Kind[]{ENTRY_CREATE}, SensitivityWatchEventModifier.HIGH);
                    keys.put(watchKey, path);
                } catch (IOException e) {
                    throw new WatchPathOnSubscribeException("Could not register path for watching changes." + rootPath, e);
                }
            }
        });

        return keys;
    }

    private void visitPathRecursive(@NotNull final Path rootPath, @NotNull final Consumer<Path> pathConsumer) {
        try {
            Files.walk(rootPath).forEach(pathConsumer);
        } catch (IOException e) {
            throw new WatchPathOnSubscribeException("Could not visit path recursive.", e);
        }
    }
}
