package com.gft.path;

import com.sun.nio.file.SensitivityWatchEventModifier;
import org.jetbrains.annotations.NotNull;
import rx.Observable.OnSubscribe;
import rx.Subscriber;

import java.io.IOException;
import java.nio.file.*;
import java.util.Iterator;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

public final class WatchPathOnSubscribe implements OnSubscribe<Path> {

    private final Path rootDir;
    private final WatchService watchService;

    public WatchPathOnSubscribe(@NotNull final Path rootDir, @NotNull final WatchService watchService) {
        this.rootDir = rootDir;
        this.watchService = watchService;
    }

    @Override
    public void call(final Subscriber<? super Path> subscriber) {
        Asd asd = new Asd(watchService, subscriber);

        try {
            asd.register(rootDir);
        } catch (IOException e) {
            return;
        }

        while (true) {
            final WatchKey watchKey;

            try {
                watchKey = watchService.take();
            } catch (InterruptedException e) {
                return;
            }

            for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
                final WatchEvent<Path> pathWatchEvent = (WatchEvent<Path>) watchEvent;
                final Path newPath = rootDir.resolve(pathWatchEvent.context());

                try {
                    asd.register(newPath);
                    Iterator<Path> pathIterator = Files.walk(newPath).iterator();

                    while (pathIterator.hasNext()) {
                        asd.register(pathIterator.next());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            boolean valid = watchKey.reset();

            if (!valid) {
                break;
            }
        }
    }

    private static final class Asd {

        private final WatchService watchService;
        private final Subscriber<? super Path> subscriber;

        public Asd(final WatchService watchService, final Subscriber<? super Path> subscriber) {
            this.watchService = watchService;
            this.subscriber = subscriber;
        }

        public void register(final Path path) throws IOException {
            path.register(watchService, new WatchEvent.Kind[]{ENTRY_CREATE}, SensitivityWatchEventModifier.HIGH);
            subscriber.onNext(path);
        }
    }
}
