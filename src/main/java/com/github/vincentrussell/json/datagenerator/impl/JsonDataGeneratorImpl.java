package com.github.vincentrussell.json.datagenerator.impl;


import com.github.vincentrussell.json.datagenerator.JsonDataGenerator;
import com.github.vincentrussell.json.datagenerator.JsonDataGeneratorException;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;

public class JsonDataGeneratorImpl implements JsonDataGenerator {

    public static final String UTF_8 = "UTF-8";
    public static final String REPEAT = "'{{repeat(";
    private static final String REPEAT_TEXT = REPEAT;
    private static final byte[] CLOSE_BRACKET_BYTE_ARRAY = "]".getBytes();
    private static final byte[] COMMA_NEWLINE_BYTE_ARRAY = ",\n".getBytes();

    @Override
    public void generateTestDataJson(String text, OutputStream outputStream) throws JsonDataGeneratorException {
        try {
            generateTestDataJson(new ByteArrayInputStream(text.getBytes()), outputStream);
        } catch (JsonDataGeneratorException e) {
            throw new JsonDataGeneratorException(e);
        }
    }

    @Override
    public void generateTestDataJson(URL classPathResource, OutputStream outputStream) throws JsonDataGeneratorException {
        try {
            generateTestDataJson(classPathResource.openStream(), outputStream);
        } catch (IOException e) {
            throw new JsonDataGeneratorException(e);
        }
    }

    @Override
    public void generateTestDataJson(File file, OutputStream outputStream) throws JsonDataGeneratorException {
        try {
            generateTestDataJson(new FileInputStream(file), outputStream);
        } catch (IOException e) {
            throw new JsonDataGeneratorException(e);
        }
    }

    @Override
    public void generateTestDataJson(InputStream inputStream, OutputStream outputStream) throws JsonDataGeneratorException {
        try (ByteArrayBackupToFileOutputStream byteArrayBackupToFileOutputStream = new ByteArrayBackupToFileOutputStream()) {
            handleRepeats(inputStream, byteArrayBackupToFileOutputStream);
            try (InputStream copyInputStream = byteArrayBackupToFileOutputStream.getNewInputStream()) {
                handleNestedFunctions(copyInputStream, outputStream);
            }
        } catch (IOException e) {
            throw new JsonDataGeneratorException(e);
        }
    }

    protected void handleRepeats(InputStream inputStream, OutputStream outputStream) throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, UTF_8));
        final CircularFifoQueue<Character> lastCharQueue = new CircularFifoQueue<>(REPEAT_TEXT.length());
        boolean isRepeating = false;
        int bracketCount = 0;
        int repeatTimes = 0;
        try (ByteArrayBackupToFileOutputStream tempBuffer = new ByteArrayBackupToFileOutputStream();
             ByteArrayBackupToFileOutputStream repeatBuffer = new ByteArrayBackupToFileOutputStream()) {
            int i;
            do {
                i = br.read();
                if (i != -1) {
                    if (isRepeating) {
                        repeatBuffer.write((char) i);
                        if ('{' == i) {
                            bracketCount++;
                        } else if ('}' == i || (']' == i) && bracketCount == 0) {
                            bracketCount--;
                            if (bracketCount == 0) {
                                try (ByteArrayBackupToFileOutputStream newCopyOutputStream = new ByteArrayBackupToFileOutputStream()) {
                                    try (InputStream repeatBufferNewInputStream = repeatBuffer.getNewInputStream()) {
                                        IOUtils.copy(repeatBufferNewInputStream, newCopyOutputStream);
                                    }
                                    for (int j = 1; j < repeatTimes; j++) {
                                        newCopyOutputStream.write(COMMA_NEWLINE_BYTE_ARRAY);
                                        try (InputStream repeatBufferNewInputStream = repeatBuffer.getNewInputStream()) {
                                            IOUtils.copy(repeatBufferNewInputStream, newCopyOutputStream);
                                        }
                                    }
                                    try (ByteArrayBackupToFileOutputStream recursiveOutputStream = new ByteArrayBackupToFileOutputStream()) {
                                        try (InputStream newCopyOutputStreamNewInputStream = newCopyOutputStream.getNewInputStream()) {
                                            handleRepeats(newCopyOutputStreamNewInputStream, recursiveOutputStream);
                                        }
                                        try (InputStream recursiveOutputStreamNewInputStream = recursiveOutputStream.getNewInputStream()) {
                                            IOUtils.copy(recursiveOutputStreamNewInputStream, outputStream);
                                        }
                                        repeatBuffer.setLength(0);
                                        tempBuffer.setLength(0);
                                        isRepeating = false;
                                        bracketCount = 0;
                                    }
                                }
                            } else if (bracketCount == -1) {
                                repeatBuffer.setLength(repeatBuffer.getLength() - 1);
                                try (ByteArrayBackupToFileOutputStream newCopyFileStream = new ByteArrayBackupToFileOutputStream()) {
                                    try (InputStream repeatBufferNewInputStream = repeatBuffer.getNewInputStream()) {
                                        IOUtils.copy(repeatBufferNewInputStream, newCopyFileStream);
                                    }
                                    for (int j = 1; j < repeatTimes; j++) {
                                        newCopyFileStream.write(COMMA_NEWLINE_BYTE_ARRAY);
                                        try (InputStream repeatBufferInputStream = repeatBuffer.getNewInputStream()) {
                                            IOUtils.copy(repeatBufferInputStream, newCopyFileStream);
                                        }
                                    }
                                    newCopyFileStream.write(CLOSE_BRACKET_BYTE_ARRAY);
                                    try (ByteArrayBackupToFileOutputStream recursiveOutputStream = new ByteArrayBackupToFileOutputStream()) {
                                        try (InputStream inputStream1 = newCopyFileStream.getNewInputStream()) {
                                            handleRepeats(inputStream1, recursiveOutputStream);
                                        }
                                        try (InputStream inputStream1 = recursiveOutputStream.getNewInputStream()) {
                                            IOUtils.copy(inputStream1, outputStream);
                                        }
                                    }
                                    repeatBuffer.setLength(0);
                                    tempBuffer.setLength(0);
                                    isRepeating = false;
                                    bracketCount = 0;
                                }
                            }
                        }
                    } else {
                        tempBuffer.write((char) i);
                        lastCharQueue.add((char) i);
                    }
                    if ((lastCharQueue.peek() != null && lastCharQueue.peek().equals('\'')) && REPEAT.equals(readAsString(lastCharQueue))) {
                        tempBuffer.mark();
                        br.mark(1000);
                        try {
                            i = br.read();
                            tempBuffer.write((char) i);
                            String numRepeats = "";
                            while (i != ')') {
                                numRepeats = numRepeats + Character.toString((char) i);
                                i = br.read();
                                if (i != ')') {
                                    tempBuffer.write((char) i);
                                }
                            }
                            //)}}'
                            tempBuffer.write((char) i);
                            tempBuffer.write((char) (i = br.read()));
                            if (i != '}') {
                                throw new IllegalStateException();
                            }
                            tempBuffer.write((char) (i = br.read()));
                            if (i != '}') {
                                throw new IllegalStateException();
                            }
                            tempBuffer.write((char) (i = br.read()));
                            if (i != '\'') {
                                throw new IllegalStateException();
                            }
                            tempBuffer.write((char) (i = br.read()));
                            if (i != ',') {
                                throw new IllegalStateException();
                            }
                            repeatTimes = Integer.parseInt(numRepeats);
                            tempBuffer.setLength(tempBuffer.getLength() - numRepeats.length() - lastCharQueue.size() - 5);
                            try (InputStream tempBufferNewInputStream = tempBuffer.getNewInputStream()) {
                                IOUtils.copy(tempBufferNewInputStream, outputStream);
                            }
                            tempBuffer.setLength(0);
                            repeatBuffer.setLength(0);
                            isRepeating = true;
                            bracketCount = 0;
                            lastCharQueue.clear();
                        } catch (IllegalStateException e) {
                            setQueueCharacters(lastCharQueue, REPEAT);
                            br.reset();
                            tempBuffer.reset();
                        }
                    }
                }
            } while (i != -1);
            br.close();
            try (InputStream inputStream1 = tempBuffer.getNewInputStream()) {
                IOUtils.copy(inputStream1, outputStream);
            }
        } finally {
            br.close();
        }
    }

    private String readAsString(CircularFifoQueue<Character> characters) {
        char[] charArray = new char[characters.size()];
        for (int i = 0; i < charArray.length; i++) {
            charArray[i] = characters.get(i);
        }
        return new String(charArray);
    }

    private void setQueueCharacters(CircularFifoQueue<Character> characters, String string) {
        characters.clear();
        char[] charArray = string.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            characters.add(charArray[i]);
        }
    }

    protected void handleNestedFunctions(InputStream inputStream, OutputStream outputStream) throws IOException {
        Reader reader = new FunctionReplacingReader(new InputStreamReader(inputStream, UTF_8), new FunctionTokenResolver());

        int data = 0;
        try {
            while ((data = reader.read()) != -1) {
                outputStream.write(data);
            }

        } finally {
            inputStream.close();
        }

    }

}
