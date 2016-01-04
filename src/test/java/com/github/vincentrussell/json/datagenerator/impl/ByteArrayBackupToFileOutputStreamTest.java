package com.github.vincentrussell.json.datagenerator.impl;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ByteArrayBackupToFileOutputStreamTest {

    private static final Random RANDOM = new Random();

    @Test
    public void notOverflow() throws IOException {
        int size = 3;
        byte[] bytes = new byte[size];
        new Random().nextBytes(bytes);
        try (ByteArrayBackupToFileOutputStream byteArrayBackupToFileOutputStream = new ByteArrayBackupToFileOutputStream(size,size)) {
            for (int i = 0; i < size; i++) {
                byteArrayBackupToFileOutputStream.write(bytes[i]);
            }
            try (InputStream inputStream = byteArrayBackupToFileOutputStream.getNewInputStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
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
        try (ByteArrayBackupToFileOutputStream byteArrayBackupToFileOutputStream = new ByteArrayBackupToFileOutputStream(size-25,size-1)) {
            for (int i = 0; i < size; i++) {
                byteArrayBackupToFileOutputStream.write(bytes[i]);
            }
            try (InputStream inputStream = byteArrayBackupToFileOutputStream.getNewInputStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                assertTrue(FileInputStream.class.isInstance(inputStream));
                IOUtils.copy(inputStream,outputStream);
                assertArrayEquals(bytes,outputStream.toByteArray());
            }
        }
    }

    @Test
    public void writeByteArray() throws IOException {
        String string = "This is a test of a byte array";
        byte[] bytes = string.getBytes();
        new Random().nextBytes(bytes);
        try (ByteArrayBackupToFileOutputStream byteArrayBackupToFileOutputStream = new ByteArrayBackupToFileOutputStream(1,bytes.length)) {
            byteArrayBackupToFileOutputStream.write(bytes);
            try (InputStream inputStream = byteArrayBackupToFileOutputStream.getNewInputStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                assertTrue(ByteArrayInputStream.class.isInstance(inputStream));
                IOUtils.copy(inputStream,outputStream);
                assertArrayEquals(bytes,outputStream.toByteArray());
            }
        }
    }

    @Test
    public void growFromInitialSize() throws IOException {
        int size = 1000;
        int initializeSize = size/2;
        int maxSize = size-3;
        byte[] bytes = new byte[size];
        new Random().nextBytes(bytes);
        try (ByteArrayBackupToFileOutputStream byteArrayBackupToFileOutputStream = new ByteArrayBackupToFileOutputStream(initializeSize,maxSize)) {
            for (int i = 1; i <= size; i++) {
                    byteArrayBackupToFileOutputStream.write(bytes[i-1]);

                if (i <= maxSize) {
                    try (InputStream inputStream = byteArrayBackupToFileOutputStream.getNewInputStream()) {
                        assertTrue(ByteArrayInputStream.class.isInstance(inputStream));
                    }
                } else {
                    try (InputStream inputStream = byteArrayBackupToFileOutputStream.getNewInputStream()) {
                        assertTrue(FileInputStream.class.isInstance(inputStream));
                    }
                }
            }

            try (InputStream inputStream = byteArrayBackupToFileOutputStream.getNewInputStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                assertTrue(FileInputStream.class.isInstance(inputStream));
                IOUtils.copy(inputStream,outputStream);
                assertArrayEquals(bytes,outputStream.toByteArray());
            }
        }
    }

    @Test
    public void unreadWhenByteArrayInputStream() throws IOException {
        int size = 1000;
        byte[] bytes = new byte[size];
        new Random().nextBytes(bytes);
        try (ByteArrayBackupToFileOutputStream byteArrayBackupToFileOutputStream = new ByteArrayBackupToFileOutputStream(size-25,size)) {
            for (int i = 0; i < size; i++) {
                byteArrayBackupToFileOutputStream.write(bytes[i]);
            }

            byteArrayBackupToFileOutputStream.unwrite();
            byteArrayBackupToFileOutputStream.unwrite();

            byte[] bytes2 = new byte[size-2];
            System.arraycopy(bytes, 0, bytes2, 0, size-2);

            try (InputStream inputStream = byteArrayBackupToFileOutputStream.getNewInputStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                IOUtils.copy(inputStream,outputStream);
                assertArrayEquals(bytes2,outputStream.toByteArray());
            }
        }
    }

    @Test
    public void unreadWhenFileInputStream() throws IOException {
        int size = 100000;
        int sizeToRemove = 5000;
        byte[] bytes = new byte[size];
        new Random().nextBytes(bytes);
        try (ByteArrayBackupToFileOutputStream byteArrayBackupToFileOutputStream = new ByteArrayBackupToFileOutputStream(size-25,size-5)) {
            for (int i = 0; i < size; i++) {
                byteArrayBackupToFileOutputStream.write(bytes[i]);
            }

            for (int i = 0; i < sizeToRemove;i++) {
                byteArrayBackupToFileOutputStream.unwrite();
            }

            byte[] bytes2 = new byte[size-sizeToRemove];
            System.arraycopy(bytes, 0, bytes2, 0, size-sizeToRemove);

            try (InputStream inputStream = byteArrayBackupToFileOutputStream.getNewInputStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                IOUtils.copy(inputStream,outputStream);
                assertArrayEquals(bytes2,outputStream.toByteArray());
            }
        }
    }

    @Test
    public void toStringByteArray() throws IOException {
        String testString = "what in the world is this?";
        byte[] bytes = testString.getBytes();

        try (ByteArrayBackupToFileOutputStream byteArrayBackupToFileOutputStream = new ByteArrayBackupToFileOutputStream(5,bytes.length)) {
            for (int i = 0; i < bytes.length; i++) {
                byteArrayBackupToFileOutputStream.write(bytes[i]);
            }
            assertEquals(testString,byteArrayBackupToFileOutputStream.toString());
        }
    }

    @Test
    public void toStringFileInputStream() throws IOException {
        String testString1 = "what in the world is this?";
        byte[] bytes1 = testString1.getBytes();
        String testString2 = "what in the world is that?";
        byte[] bytes2 = testString2.getBytes();

        try (ByteArrayBackupToFileOutputStream byteArrayBackupToFileOutputStream = new ByteArrayBackupToFileOutputStream(5,bytes1.length)) {
            for (int i = 0; i < bytes1.length; i++) {
                byteArrayBackupToFileOutputStream.write(bytes1[i]);
            }
            for (int i = 0; i < bytes2.length; i++) {
                byteArrayBackupToFileOutputStream.write(bytes2[i]);
            }
            assertEquals(testString1+testString2,byteArrayBackupToFileOutputStream.toString());
        }
    }

    @Test
    public void getLength() throws IOException {
        String testString1 = "what in the world is this?";
        byte[] bytes1 = testString1.getBytes();
        String testString2 = "what in the world is that?";
        byte[] bytes2 = testString2.getBytes();

        try (ByteArrayBackupToFileOutputStream byteArrayBackupToFileOutputStream = new ByteArrayBackupToFileOutputStream(5,bytes1.length)) {
            for (int i = 0; i < bytes1.length; i++) {
                byteArrayBackupToFileOutputStream.write(bytes1[i]);
            }

            assertEquals(testString1.length(),byteArrayBackupToFileOutputStream.getLength());

            for (int i = 0; i < bytes2.length; i++) {
                byteArrayBackupToFileOutputStream.write(bytes2[i]);
            }
            assertEquals(testString1.length() + testString1.length(),byteArrayBackupToFileOutputStream.getLength());
        }
    }

    @Test
    public void setLengthByteArray() throws IOException {
        String testString1 = "what in the world is this?";
        byte[] bytes1 = testString1.getBytes();

        try (ByteArrayBackupToFileOutputStream byteArrayBackupToFileOutputStream = new ByteArrayBackupToFileOutputStream(5,bytes1.length)) {
            for (int i = 0; i < bytes1.length; i++) {
                byteArrayBackupToFileOutputStream.write(bytes1[i]);
            }

            int newLength = 5;
            byteArrayBackupToFileOutputStream.setLength(newLength);

            assertEquals(testString1.substring(0,newLength),byteArrayBackupToFileOutputStream.toString());
            assertEquals(newLength,byteArrayBackupToFileOutputStream.getLength());

            byteArrayBackupToFileOutputStream.write(bytes1[5]);
            assertEquals(testString1.substring(0,newLength+1),byteArrayBackupToFileOutputStream.toString());
            assertEquals(newLength+1,byteArrayBackupToFileOutputStream.getLength());

        }
    }

    @Test(expected = IllegalStateException.class)
    public void setLengthByteArrayGreaterThanBufferSize() throws IOException {
        try (ByteArrayBackupToFileOutputStream byteArrayBackupToFileOutputStream = new ByteArrayBackupToFileOutputStream(5,10)) {
            byteArrayBackupToFileOutputStream.setLength(1000);

        }
    }

    @Test(expected = IllegalStateException.class)
    public void setLengthByteArrayGreaterThanFileSize() throws IOException {
        String testString1 = "what in the world is this?";
        byte[] bytes1 = testString1.getBytes();

        try (ByteArrayBackupToFileOutputStream byteArrayBackupToFileOutputStream = new ByteArrayBackupToFileOutputStream(5,testString1.length()-2)) {
            for (int i = 0; i < bytes1.length; i++) {
                byteArrayBackupToFileOutputStream.write(bytes1[i]);
            }

            byteArrayBackupToFileOutputStream.setLength(1000);

        }
    }

    @Test
    public void setLengthFile() throws IOException {
        String testString1 = "what in the world is this?";
        byte[] bytes1 = testString1.getBytes();
        String testString2 = "what in the world is that?";
        byte[] bytes2 = testString2.getBytes();

        try (ByteArrayBackupToFileOutputStream byteArrayBackupToFileOutputStream = new ByteArrayBackupToFileOutputStream(5,bytes1.length)) {
            for (int i = 0; i < bytes1.length; i++) {
                byteArrayBackupToFileOutputStream.write(bytes1[i]);
            }
            for (int i = 0; i < bytes2.length; i++) {
                byteArrayBackupToFileOutputStream.write(bytes2[i]);
            }
            int newLength = 5;
            byteArrayBackupToFileOutputStream.setLength(newLength);

            assertEquals(testString1.substring(0,newLength),byteArrayBackupToFileOutputStream.toString());
            assertEquals(newLength,byteArrayBackupToFileOutputStream.getLength());

            byteArrayBackupToFileOutputStream.write(bytes1[5]);
            assertEquals(testString1.substring(0,newLength+1),byteArrayBackupToFileOutputStream.toString());
            assertEquals(newLength+1,byteArrayBackupToFileOutputStream.getLength());
        }
    }


    @Test
    public void markAndReset() throws IOException {
        String testString1 = "what in the world is this?";
        byte[] bytes1 = testString1.getBytes();
        String testString2 = "what in the world is that?";
        byte[] bytes2 = testString2.getBytes();

        try (ByteArrayBackupToFileOutputStream byteArrayBackupToFileOutputStream = new ByteArrayBackupToFileOutputStream(5,bytes1.length)) {
            for (int i = 0; i < bytes1.length; i++) {
                byteArrayBackupToFileOutputStream.write(bytes1[i]);
            }

            byteArrayBackupToFileOutputStream.mark();

            String markedString = byteArrayBackupToFileOutputStream.toString();

            for (int i = 0; i < bytes2.length; i++) {
                byteArrayBackupToFileOutputStream.write(bytes2[i]);
            }
            assertEquals(testString1+testString2,byteArrayBackupToFileOutputStream.toString());

            byteArrayBackupToFileOutputStream.reset();

            assertEquals(markedString,byteArrayBackupToFileOutputStream.toString());
        }
    }

    @Test(expected = IOException.class)
    public void resetWithoutMark() throws IOException {
        try (ByteArrayBackupToFileOutputStream byteArrayBackupToFileOutputStream = new ByteArrayBackupToFileOutputStream(5,5)) {
            byteArrayBackupToFileOutputStream.reset();
        }
    }

}
