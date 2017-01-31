package com.gft.application.file.list;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ListActionTest {

    @Test
    public void returnsPathToTheView() throws Exception {
        assertThat(new ListAction().invoke(), is("file/list"));
    }
}
