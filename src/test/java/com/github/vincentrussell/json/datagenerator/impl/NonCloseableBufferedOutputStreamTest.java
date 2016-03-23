package com.github.vincentrussell.json.datagenerator.impl;

import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;

import static org.mockito.Mockito.*;

public class NonCloseableBufferedOutputStreamTest {

    @Test
    public void closeNotCalledOnWrappedOutputStream() throws IOException {
        OutputStream mockOutputStream = mock(OutputStream.class);
        NonCloseableBufferedOutputStream nonCloseableBufferedOutputStream = new NonCloseableBufferedOutputStream(mockOutputStream);
        nonCloseableBufferedOutputStream.close();
        verify(mockOutputStream).flush();
        verify(mockOutputStream, never()).close();
    }

}
