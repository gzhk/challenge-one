package com.gft.path.iterator;

import java.nio.file.Path;
import java.util.Iterator;

public interface WatchServiceIterator extends Iterator<Path>, AutoCloseable {

}
