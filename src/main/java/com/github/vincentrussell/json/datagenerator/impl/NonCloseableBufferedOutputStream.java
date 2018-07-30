package com.github.vincentrussell.json.datagenerator.impl;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A {@link BufferedOutputStream} that cannot be closed; only flushed
 */
public class NonCloseableBufferedOutputStream extends BufferedOutputStream {

    /**
     * Creates a new buffered output stream to write data to the
     * specified underlying output stream.
     *
     * @param   out   the underlying output stream.
     */
    public NonCloseableBufferedOutputStream(final OutputStream out) {
        super(out);
    }

    /**
     * Creates a new buffered output stream to write data to the
     * specified underlying output stream with the specified buffer
     * size.
     *
     * @param   out    the underlying output stream.
     * @param   size   the buffer size.
     * @exception IllegalArgumentException if size &lt;= 0.
     */
    public NonCloseableBufferedOutputStream(final OutputStream out, final int size) {
        super(out, size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        try {
            flush();
        } catch (IOException ignored) {
            //noop
        }
    }
}
