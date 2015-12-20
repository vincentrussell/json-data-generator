package com.github.vincentrussell.json.datagenerator.impl;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.Arrays;

public class ByteArrayBackupToFileOutputStream extends OutputStream {

    protected byte buf[];

    protected int count;
    protected File file = null;
    protected FileOutputStream fileOutputStream = null;

    public ByteArrayBackupToFileOutputStream() {
        this(1028000000);
    }

    public ByteArrayBackupToFileOutputStream(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative initial size: "
                    + size);
        }
        buf = new byte[size];
    }

    private void ensureCapacity(int minCapacity) throws IOException {
        // overflow-conscious code
        if (minCapacity - buf.length > 0 && file == null) {
            file = File.createTempFile("temp","temp");
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(buf);
            buf = null;
        }
    }

    public synchronized void write(int b) throws IOException {
        ensureCapacity(count + 1);
        if (buf==null) {
            fileOutputStream.write(b);
            return;
        }
        buf[count] = (byte) b;
        count += 1;
    }

    public synchronized void write(byte b[], int off, int len) throws IOException {
        if ((off < 0) || (off > b.length) || (len < 0) ||
                ((off + len) - b.length > 0)) {
            throw new IndexOutOfBoundsException();
        }
        ensureCapacity(count + len);
        if (buf==null) {
            fileOutputStream.write(b);
            return;
        }
        System.arraycopy(b, off, buf, count, len);
        count += len;
    }

    public synchronized byte toByteArray()[] {
        return Arrays.copyOf(buf, count);
    }

    public synchronized int size() {
        return count;
    }

    public synchronized String toString() {
        return new String(buf, 0, count);
    }

    public synchronized String toString(String charsetName)
            throws UnsupportedEncodingException {
        return new String(buf, 0, count, charsetName);
    }

    @Deprecated
    public synchronized String toString(int hibyte) {
        return new String(buf, hibyte, 0, count);
    }

    public void close() throws IOException {
        if (fileOutputStream!=null) {
            fileOutputStream.close();
        }
        if (file!=null) {
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
}