package com.github.vincentrussell.json.datagenerator.impl;


import com.github.vincentrussell.json.datagenerator.JsonDataGenerator;
import com.github.vincentrussell.json.datagenerator.JsonDataGeneratorException;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;

import java.io.*;
import java.net.URL;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notNull;

public class JsonDataGeneratorImpl implements JsonDataGenerator {

    public static final String UTF_8 = "UTF-8";
    public static final String REPEAT = "'{{repeat(";
    private static final String REPEAT_TEXT = REPEAT;
    private static final byte[] CLOSE_BRACKET_BYTE_ARRAY = "]".getBytes();
    private static final byte[] COMMA_NEWLINE_BYTE_ARRAY = ",\n".getBytes();
    private static Pattern REPEAT_PARAMETERS_PATTERN = Pattern.compile("^(\\d+),*\\s*(\\d+)*$");

    private static final Random RANDOM = new Random();

    @Override
    public void generateTestDataJson(String text, OutputStream outputStream) throws JsonDataGeneratorException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(text.getBytes())) {
            generateTestDataJson(byteArrayInputStream, outputStream);
        } catch (JsonDataGeneratorException | IOException e) {
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
        notNull(file,"file can not be null");
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            generateTestDataJson(fileInputStream, outputStream);
        } catch (IOException e) {
            throw new JsonDataGeneratorException(e);
        }
    }

    @Override
    public void generateTestDataJson(File file, File outputFile) throws JsonDataGeneratorException {
        notNull(file,"file can not be null");
        notNull(outputFile,"outputFile can not be null");
        isTrue(!outputFile.exists(),"outputFile can not exist");
        try (FileInputStream fileInputStream = new FileInputStream(file);
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            generateTestDataJson(fileInputStream, fileOutputStream);
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
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, UTF_8));
        final CircularFifoQueue<Character> lastCharQueue = new CircularFifoQueue<>(REPEAT_TEXT.length());
        boolean isRepeating = false;
        int bracketCount = 0;
        int repeatTimes = 0;
        try (ByteArrayBackupToFileOutputStream tempBuffer = new ByteArrayBackupToFileOutputStream();
             ByteArrayBackupToFileOutputStream repeatBuffer = new ByteArrayBackupToFileOutputStream()) {
            int i;
            do {
                i = bufferedReader.read();
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
                        bufferedReader.mark(1000);
                        try {
                            i = bufferedReader.read();
                            tempBuffer.write((char) i);
                            String numRepeats = "";
                            while (i != ')') {
                                numRepeats = numRepeats + Character.toString((char) i);
                                i = bufferedReader.read();
                                if (i != ')') {
                                    tempBuffer.write((char) i);
                                }
                            }
                            //)}}'
                            tempBuffer.write((char) i);
                            tempBuffer.write((char) (i = bufferedReader.read()));
                            if (i != '}') {
                                throw new IllegalStateException();
                            }
                            tempBuffer.write((char) (i = bufferedReader.read()));
                            if (i != '}') {
                                throw new IllegalStateException();
                            }
                            tempBuffer.write((char) (i = bufferedReader.read()));
                            if (i != '\'') {
                                throw new IllegalStateException();
                            }
                            tempBuffer.write((char) (i = bufferedReader.read()));
                            if (i != ',') {
                                throw new IllegalStateException();
                            }
                            repeatTimes = parseRepeats(numRepeats);
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
                            bufferedReader.reset();
                            tempBuffer.reset();
                        }
                    }
                }
            } while (i != -1);
            bufferedReader.close();
            try (InputStream inputStream1 = tempBuffer.getNewInputStream()) {
                IOUtils.copy(inputStream1, outputStream);
            }
        } finally {
            bufferedReader.close();
        }
    }

    private int parseRepeats(String repeatArguments) {
        final Matcher matcher = REPEAT_PARAMETERS_PATTERN.matcher(repeatArguments);
        if (matcher.find()) {
            Integer integer = Integer.parseInt(matcher.group(1));
            Integer integer2 = Integer.parseInt(matcher.group(1));
            if (matcher.group(2)!=null) {
                integer2 = Integer.parseInt(matcher.group(2));
            } else if (integer >= integer2 && !integer.equals(integer2)) {
                throw new IllegalArgumentException("the second number must be greater than the first number" + repeatArguments);
            } else {
                return integer;
            }
            if (integer.equals(integer2)) {
                return integer;
            } else {
                return new Random().nextInt(integer2 - integer) + integer;
            }
        }
        throw new IllegalArgumentException("invalid arguments for repeat function: " + repeatArguments);
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
