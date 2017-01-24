package com.gft.application.file.model;

import org.junit.Test;

import java.util.Objects;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class PathViewTest {

    @Test
    public void returnsTrueIfPathAndIdAreEqual() throws Exception {
        PathView id = new PathView("id", "/tmp");

        assertThat(id.equals(new PathView("id", "/tmp")), is(true));
    }

    @Test
    public void returnsHashCodeComputedBasedOnIdAndPath() throws Exception {
        PathView id = new PathView("id", "/tmp");

        assertThat(id.hashCode(), is(Objects.hash("id", "/tmp")));
    }
}
