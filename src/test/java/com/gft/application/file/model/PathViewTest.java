package com.gft.application.file.model;

import org.junit.Test;

import java.util.Objects;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PathViewTest {

    @Test
    public void returnsTrueIfPathAndIdAreEqual() throws Exception {
        PathView pathView = new PathView("id", "/tmp");

        assertThat(pathView.equals(new PathView("id", "/tmp")), is(true));
    }

    @Test
    public void returnsHashCodeComputedBasedOnIdAndPath() throws Exception {
        PathView pathView = new PathView("id", "/tmp");

        assertThat(pathView.hashCode(), is(Objects.hash("id")));
    }
}
