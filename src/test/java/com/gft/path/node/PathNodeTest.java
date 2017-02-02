package com.gft.path.node;

import org.junit.Test;

import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class PathNodeTest {

    @Test
    public void returnsPathPassedInConstructor() throws Exception {
        Path path = mock(Path.class);
        PathNode pathNode = new PathNode(path);

        assertThat(pathNode.getPayload(), is(path));
    }
}
