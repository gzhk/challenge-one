package com.gft.application.file.model;

import com.gft.path.treenode.PathTreeNode;
import org.junit.Test;
import org.springframework.util.DigestUtils;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PathViewFactoryTest {

    @Test
    public void createPathViewFromPathThreeNode() throws Exception {
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("/tmp");
        String md5Hash = DigestUtils.md5DigestAsHex(new ByteArrayInputStream("/tmp".getBytes()));

        PathViewFactory pathViewFactory = new PathViewFactory();

        assertThat(pathViewFactory.createFromPathTreeNode(new PathTreeNode(path)), is(new PathView(md5Hash, "/tmp")));
    }
}
