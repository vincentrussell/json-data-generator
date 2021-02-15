package com.github.vincentrussell.json.datagenerator.impl;

import com.google.common.base.Charsets;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * {@link OutputStream} that will write to an byte array before overflowing to a file
 */
public class ByteArrayBackupToFileOutputStream extends OutputStream {

    private static final int DEFAULT_INITIAL_BUFFER_SIZE = 1028;
    private static final int DEFAULT_SIZE_BEFORE_OVER_FLOW = 1024000;
    private byte[] buf;

    private int count;
    private int copyCount = 0;
    private long byteLength = 0;
    private final int sizeBeforeOverFlow;
    private File file = null;
    private FileOutputStream fileOutputStream = null;
    private long lastMark = 0;

    /**
     * constructor with default settings
     */
    public ByteArrayBackupToFileOutputStream() {
        this(DEFAULT_INITIAL_BUFFER_SIZE, DEFAULT_SIZE_BEFORE_OVER_FLOW);
    }

    /**
     * constructor
     * @param initialBufferSize initial buffer size in bytes
     * @param sizeBeforeOverFlow size in bytes before overflow to file
     */
    public ByteArrayBackupToFileOutputStream(final int initialBufferSize,
        final int sizeBeforeOverFlow) {
        if (sizeBeforeOverFlow < 0) {
            throw new IllegalArgumentException(
                "Negative initial sizeBeforeOverFlow: " + sizeBeforeOverFlow);
        }
        buf = new byte[initialBufferSize];
        this.sizeBeforeOverFlow = sizeBeforeOverFlow;
    }

    private void ensureCapacity(final int minCapacity) throws IOException {
        if (buf != null && minCapacity - buf.length > 0 && minCapacity <= sizeBeforeOverFlow) {
            grow(minCapacity);
            return;
        }

        if (minCapacity > sizeBeforeOverFlow && file == null) {
            file = File.createTempFile("temp", "temp");
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(Arrays.copyOf(buf, count));
            fileOutputStream.flush();
            buf = null;
            return;
        }
    }

    private void grow(final int minCapacity) {
        // overflow-conscious code
        int oldCapacity = buf.length;
        int newCapacity = oldCapacity << 1;
        if (newCapacity > sizeBeforeOverFlow) {
            newCapacity = sizeBeforeOverFlow;
        }
        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }
        if (newCapacity < 0) {
            if (minCapacity < 0) { // overflow
                throw new OutOfMemoryError();
            }
            newCapacity = Integer.MAX_VALUE;
        }
        buf = Arrays.copyOf(buf, newCapacity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void write(final int b) throws IOException {
        ensureCapacity(count + 1);
        if (buf == null) {
            fileOutputStream.write(b);
            return;
        }
        buf[count] = (byte) b;
        count++;
        byteLength++;
    }

    /**
     * remove one byte from the written outputstream
     * @throws IOException if there is no more buffer to unwrite from
     */
    public void unwrite() throws IOException {
        if (count == 0) {
            throw new IOException("Pushback buffer overflow");
        }
        if (buf == null) {
            @SuppressWarnings("resource") RandomAccessFile randomAccessFile =
                new RandomAccessFile(file, "rw");
            randomAccessFile.setLength(file.length() - 1);
            return;
        }


        buf[--count] = (byte) 0;
        byteLength--;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void write(final byte[] b, final int off, final int len)
        throws IOException {
        if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) - b.length > 0)) {
            throw new IndexOutOfBoundsException();
        }
        ensureCapacity(count + len);
        if (buf == null) {
            fileOutputStream.write(b, 0, len);
            return;
        }
        System.arraycopy(b, off, buf, count, len);
        count += len;
        byteLength += len;
    }

    /**
     * get the number or bytes written to the {@link OutputStream}
     * @return the size
     */
    public synchronized long size() {
        return byteLength;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized String toString() {
        if (buf == null) {
            try {
                try (InputStream inputStream = new FileInputStream(file)) {
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(inputStream, writer, "UTF-8");
                    return writer.toString();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new String(buf, 0, count, Charsets.UTF_8);
        }
    }

    /**
     * convert the current buffer for text... can be used for testing.
     * @return the hex representation of the bytes in UTF-8
     */
    public String toHex() {
        return Hex.encodeHexString(toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        if (fileOutputStream != null) {
            fileOutputStream.close();
        }
        if (file != null) {
            try {
                FileUtils.forceDelete(file);
            } catch (FileNotFoundException e) {
                //noop
            }
        }
    }

    /**
     * create an {@link InputStream} based on the data written to the {@link OutputStream}
     * @return the new {@link InputStream}
     * @throws IOException if the {@link OutputStream} can not be flushed or closed
     */
    public InputStream getNewInputStream() throws IOException {
        if (buf != null) {
            return new ByteArrayInputStream(Arrays.copyOf(buf, count));
        } else {
            fileOutputStream.flush();
            return new FileInputStream(file);
        }
    }

    /**
     * Copy the contents of this {@link OutputStream} to a new {@link OutputStream}.
     * @param outputStream
     * @throws IOException
     */
    public void copyToOutputStream(final OutputStream outputStream) throws IOException {
        try (InputStream repeatBufferNewInputStream = getNewInputStream()) {
            IOUtils.copy(repeatBufferNewInputStream, outputStream);
        } finally {
            copyCount++;
        }
    }

    /**
     * Marks the current position in this input stream.
     * @throws IOException if the length of {@link OutputStream} cannot be retrieved
     */
    public void mark() throws IOException {
        lastMark = getLength();
    }

    /**
     * Repositions this stream to the position at the time the mark method was
     * last called on this input stream.
     * @throws IOException if the length of {@link OutputStream} cannot be set
     */
    public void reset() throws IOException {
        copyCount = 0;
        if (lastMark <= 0) {
            throw new IOException("mark has not been set yet.");
        }
        setLength(lastMark);
    }

    private void shrink(final int newCapacity) {
        buf = Arrays.copyOf(buf, newCapacity);
        count = newCapacity;
        byteLength = newCapacity;
    }

    /**
     * set the length of the {@link OutputStream}
     * @param length the length in bytes
     * @throws IOException if the length is greater than the {@link OutputStream} size
     */
    public void setLength(final long length) throws IOException {
        if (buf != null) {
            if (length > buf.length) {
                throw new IllegalStateException(
                    "length: " + length + " is greater than buffer length");
            }
            shrink((int) length);
        } else {
            if (length > file.length()) {
                throw new IllegalStateException(
                    "length: " + length + " is greater than file length");
            }
            @SuppressWarnings("resource") RandomAccessFile randomAccessFile =
                new RandomAccessFile(file, "rw");
            randomAccessFile.setLength(length);
            fileOutputStream = new FileOutputStream(file, true);
        }

    }

    /**
     * get the byte length of the {@link OutputStream}
     * @return the length in bytes
     */
    public long getLength() {
        if (buf != null) {
            return byteLength;
        } else {
            return file.length();
        }
    }
}
