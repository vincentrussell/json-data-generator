package com.github.vincentrussell.json.datagenerator.impl;


import com.github.vincentrussell.json.datagenerator.JsonDataGenerator;
import com.github.vincentrussell.json.datagenerator.JsonDataGeneratorException;
import com.google.common.collect.Iterables;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonDataGeneratorImpl implements JsonDataGenerator {

    public static final String UTF_8 = "UTF-8";
    private static final String REPEAT_TEXT = "'{{repeat(";
    private static final byte[] CLOSE_BRACKET_BYTE_ARRAY = "]".getBytes();
    private static final byte[] COMMA_NEWLINE_BYTE_ARRAY = ",\n".getBytes();

    public JsonDataGeneratorImpl() {
    }

    public static int indexOf(Pattern pattern, CharSequence input) {
        Matcher matcher = pattern.matcher(input);
        return matcher.find() ? matcher.start() : -1;
    }


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
                    }
                    if (FunctionTokenResolver.isRepeatFunction(tempBuffer.toString(UTF_8))) {
                        tempBuffer.write((char) i);
                        repeatTimes = Integer.parseInt(Iterables.get(FunctionTokenResolver.getRepeatFunctionNameAndArguments(tempBuffer.toString(UTF_8)), 1).toString());
                        int indexOfRepeat = indexOf(FunctionTokenResolver.REPEAT_FUNCTION_PATTERN, tempBuffer.toString(UTF_8));
                        tempBuffer.setLength(indexOfRepeat);
                        try (InputStream tempBufferNewInputStream = tempBuffer.getNewInputStream()) {
                            IOUtils.copy(tempBufferNewInputStream, outputStream);
                        }
                        tempBuffer.setLength(0);
                        repeatBuffer.setLength(0);
                        isRepeating = true;
                        bracketCount = 0;
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
