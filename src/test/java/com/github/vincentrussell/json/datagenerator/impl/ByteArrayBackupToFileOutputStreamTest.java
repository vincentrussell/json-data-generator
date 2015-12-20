package com.github.vincentrussell.json.datagenerator.impl;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class ByteArrayBackupToFileOutputStreamTest {

    private static final Random RANDOM = new Random();

    @Test
    public void notOverflow() throws IOException {
        int size = 3;
        byte[] bytes = new byte[size];
        new Random().nextBytes(bytes);
        try (ByteArrayBackupToFileOutputStream byteArrayBackupToFileOutputStream = new ByteArrayBackupToFileOutputStream(size)) {
            for (int i = 0; i < size; i++) {
                byteArrayBackupToFileOutputStream.write(bytes[i]);
            }
            try (InputStream inputStream = byteArrayBackupToFileOutputStream.getNewInputStream(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                assertTrue(ByteArrayInputStream.class.isInstance(inputStream));
                IOUtils.copy(inputStream,outputStream);
                assertArrayEquals(bytes,outputStream.toByteArray());
            }
        }
    }

    @Test
    public void overflow() throws IOException {
        int size = 1000;
        byte[] bytes = new byte[size];
        new Random().nextBytes(bytes);
        try (ByteArrayBackupToFileOutputStream byteArrayBackupToFileOutputStream = new ByteArrayBackupToFileOutputStream(size-1)) {
            for (int i = 0; i < size; i++) {
                byteArrayBackupToFileOutputStream.write(bytes[i]);
            }
            try (InputStream inputStream = byteArrayBackupToFileOutputStream.getNewInputStream(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                assertTrue(FileInputStream.class.isInstance(inputStream));
                IOUtils.copy(inputStream,outputStream);
                assertArrayEquals(bytes,outputStream.toByteArray());
            }
        }
    }

}
