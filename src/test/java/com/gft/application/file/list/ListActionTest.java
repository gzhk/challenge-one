package com.gft.application.file.list;

import org.junit.Test;
import org.springframework.ui.Model;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class ListActionTest {

    @Test
    public void returnsPathToTheView() throws Exception {
        assertThat(new ListAction("dir").invoke(mock(Model.class)), is("file/list"));
    }
}
