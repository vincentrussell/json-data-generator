package com.github.vincentrussell.json.datagenerator.impl;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Arrays;

public class ByteArrayBackupToFileOutputStream extends OutputStream {

    protected byte buf[];

    protected int count;
    protected final int sizeBeforeOverFlow;
    protected File file = null;
    protected FileOutputStream fileOutputStream = null;
    private long lastMark = 0;

    public ByteArrayBackupToFileOutputStream() {
        this(1028, 1024000);
    }

    public ByteArrayBackupToFileOutputStream(int initialBufferSize, int sizeBeforeOverFlow) {
        if (sizeBeforeOverFlow < 0) {
            throw new IllegalArgumentException("Negative initial sizeBeforeOverFlow: "
                    + sizeBeforeOverFlow);
        }
        buf = new byte[initialBufferSize];
        this.sizeBeforeOverFlow = sizeBeforeOverFlow;
    }

    private void ensureCapacity(int minCapacity) throws IOException {
        if (buf != null && minCapacity - buf.length > 0 && minCapacity <= sizeBeforeOverFlow) {
            grow(minCapacity);
            return;
        }

        if (minCapacity > sizeBeforeOverFlow && file == null) {
            file = File.createTempFile("temp", "temp");
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(buf);
            buf = null;
            return;
        }
    }

    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = buf.length;
        int newCapacity = oldCapacity << 1;
        if (newCapacity > sizeBeforeOverFlow) {
            newCapacity = sizeBeforeOverFlow;
        }
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity < 0) {
            if (minCapacity < 0) // overflow
                throw new OutOfMemoryError();
            newCapacity = Integer.MAX_VALUE;
        }
        buf = Arrays.copyOf(buf, newCapacity);
    }

    @Override
    public synchronized void write(int b) throws IOException {
        ensureCapacity(count + 1);
        if (buf == null) {
            fileOutputStream.write(b);
            return;
        }
        buf[count] = (byte) b;
        count += 1;
    }

    public void unwrite() throws IOException {
        if (count == 0) {
            throw new IOException("Pushback buffer overflow");
        }
        if (buf == null) {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.setLength(file.length() - 1);
            return;
        }


        buf[--count] = (byte) 0;
    }

    public synchronized void write(byte b[], int off, int len) throws IOException {
        if ((off < 0) || (off > b.length) || (len < 0) ||
                ((off + len) - b.length > 0)) {
            throw new IndexOutOfBoundsException();
        }
        ensureCapacity(count + len);
        if (buf == null) {
            fileOutputStream.write(b);
            return;
        }
        System.arraycopy(b, off, buf, count, len);
        count += len;
    }

    public synchronized int size() {
        return count;
    }

    @Override
    public synchronized String toString() {
        if (buf == null) {
            try {
                try (InputStream inputStream = new FileInputStream(file)) {
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(inputStream, writer);
                    return writer.toString();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new String(buf, 0, count);
        }
    }

    public synchronized String toString(String charsetName)
            throws UnsupportedEncodingException {
        return new String(buf, 0, count, charsetName);
    }

    public void close() throws IOException {
        if (fileOutputStream != null) {
            fileOutputStream.close();
        }
        if (file != null) {
            FileUtils.forceDelete(file);
        }
    }

    public InputStream getNewInputStream() throws IOException {
        if (buf != null) {
            return new ByteArrayInputStream(Arrays.copyOf(buf, count));
        } else {
            fileOutputStream.flush();
            return new FileInputStream(file);
        }
    }

    public void mark() throws IOException {
        lastMark = getLength();
    }

    public void reset() throws IOException {
        if (lastMark <= 0) {
            throw new IOException("mark has not been set yet.");
        }
        setLength(lastMark);
    }

    private void shrink(int newCapacity) {
        buf = Arrays.copyOf(buf, newCapacity);
        count = newCapacity;
    }

    public void setLength(long length) throws IOException {
        if (buf != null) {
            if (length > buf.length) {
                throw new IllegalStateException("length: " + length + " is greater than buffer length");
            }
            shrink((int) length);
        } else {
            if (length > file.length()) {
                throw new IllegalStateException("length: " + length + " is greater than file length");
            }
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.setLength(length);
            fileOutputStream = new FileOutputStream(file, true);
        }

    }

    public long getLength() {
        if (buf != null) {
            return count;
        } else {
            return file.length();
        }
    }
}