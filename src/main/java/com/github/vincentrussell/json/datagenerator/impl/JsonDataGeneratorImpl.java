package com.github.vincentrussell.json.datagenerator.impl;


import com.github.vincentrussell.json.datagenerator.JsonDataGenerator;
import com.github.vincentrussell.json.datagenerator.JsonDataGeneratorException;
import com.google.common.base.Charsets;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notNull;

/**
 * default implementation for {@link JsonDataGenerator}
 */
public class JsonDataGeneratorImpl implements JsonDataGenerator {

    public static final String REPEAT = "'{{repeat(";
    private static final String REPEAT_TEXT = REPEAT;
    private static final byte[] CLOSE_BRACKET_BYTE_ARRAY = "]".getBytes(Charsets.UTF_8);
    private static final byte[] COMMA_NEWLINE_BYTE_ARRAY = ",\n".getBytes(Charsets.UTF_8);
    private static final byte[] NEWLINE_BYTE_ARRAY = "\n".getBytes(Charsets.UTF_8);
    public static final int DEFAULT_READ_AHEAD_LIMIT = 1000;
    private static Pattern REPEAT_PARAMETERS_PATTERN = Pattern.compile("^(\\d+),*\\s*(\\d+)*$");

    /**
     * {@inheritDoc}
     */
    @Override
    public void generateTestDataJson(final String text, final OutputStream outputStream)
        throws JsonDataGeneratorException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
            text.getBytes(Charsets.UTF_8))) {
            generateTestDataJson(byteArrayInputStream, outputStream);
        } catch (JsonDataGeneratorException | IOException e) {
            throw new JsonDataGeneratorException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void generateTestDataJson(final URL classPathResource, final OutputStream outputStream)
        throws JsonDataGeneratorException {
        try {
            generateTestDataJson(classPathResource.openStream(), outputStream);
        } catch (IOException e) {
            throw new JsonDataGeneratorException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void generateTestDataJson(final File file, final OutputStream outputStream)
        throws JsonDataGeneratorException {
        notNull(file, "file can not be null");
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            generateTestDataJson(fileInputStream, outputStream);
        } catch (IOException e) {
            throw new JsonDataGeneratorException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void generateTestDataJson(final File file, final File outputFile)
        throws JsonDataGeneratorException {
        notNull(file, "file can not be null");
        notNull(outputFile, "outputFile can not be null");
        isTrue(!outputFile.exists(), "outputFile can not exist");
        try (FileInputStream fileInputStream = new FileInputStream(file);
            FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
            generateTestDataJson(fileInputStream, fileOutputStream);
        } catch (IOException e) {
            throw new JsonDataGeneratorException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void generateTestDataJson(final InputStream inputStream, final OutputStream outputStream)
        throws JsonDataGeneratorException {
        try (ByteArrayBackupToFileOutputStream
            byteArrayBackupToFileOutputStream = new ByteArrayBackupToFileOutputStream()) {
            handleRepeats(inputStream, byteArrayBackupToFileOutputStream, true);
            try (InputStream copyInputStream = byteArrayBackupToFileOutputStream
                .getNewInputStream()) {
                handleNestedFunctions(copyInputStream, outputStream);
            }
        } catch (IOException e) {
            throw new JsonDataGeneratorException(e);
        }
    }

    @SuppressWarnings({"checkstyle:linelength", "checkstyle:innerassignment",
        "checkstyle:methodlength", "checkstyle:magicnumber"})
    private void handleRepeats(final InputStream inputStream, final OutputStream outputStream,
                               final boolean shouldWriteToRepeatStream)
        throws IOException {
        final BufferedReader bufferedReader =
            new BufferedReader(new InputStreamReader(inputStream, Charsets.UTF_8));
        final CircularFifoQueue<Character> lastCharQueue =
            new CircularFifoQueue<>(REPEAT_TEXT.length());
        final List<Character> xmlRepeatTagList = new LinkedList<>();
        boolean isRepeating = false;
        int bracketCount = 0;
        int repeatTimes = 0;
        int firstNonWhitespaceCharacter = -1;
        int xmlTag = 0;
        String xmlRepeatTag = "";
        try (ByteArrayBackupToFileOutputStream tempBuffer = new ByteArrayBackupToFileOutputStream();
            ByteArrayBackupToFileOutputStream repeatBuffer = new ByteArrayBackupToFileOutputStream()) {
            int i;
            while ((i = bufferedReader.read()) != -1) {
                String charAsUTF8String = Character.valueOf((char) i).toString();
                if (isRepeating) {
                    repeatBuffer.write(charAsUTF8String.getBytes(Charsets.UTF_8));

                    if (!Character.isWhitespace(i) && firstNonWhitespaceCharacter == -1) {
                        firstNonWhitespaceCharacter = i;
                    }

                    if ('{' == i) {
                        bracketCount++;
                    } else if ('}' == i || (']' == i) && bracketCount == 0) {
                        bracketCount--;
                        if (bracketCount == 0 && xmlTag == 0) {
                            bufferedReader.mark(1);
                            i = bufferedReader.read();
                            charAsUTF8String = Character.valueOf((char) i).toString();
                            if (i == firstNonWhitespaceCharacter) {
                                repeatBuffer.write(charAsUTF8String.getBytes(Charsets.UTF_8));
                            } else {
                                bufferedReader.reset();
                            }

                            try (ByteArrayBackupToFileOutputStream newCopyOutputStream = new ByteArrayBackupToFileOutputStream()) {
                                repeatBuffer.copyToOutputStream(newCopyOutputStream);
                                copyRepeatStream(repeatTimes, repeatBuffer, newCopyOutputStream,
                                    COMMA_NEWLINE_BYTE_ARRAY);
                                try (ByteArrayBackupToFileOutputStream recursiveOutputStream = new ByteArrayBackupToFileOutputStream()) {
                                    try (InputStream newCopyOutputStreamNewInputStream = newCopyOutputStream
                                        .getNewInputStream()) {
                                        handleRepeats(newCopyOutputStreamNewInputStream, recursiveOutputStream, !isRepeating || (isRepeating && repeatTimes > 0));
                                    }
                                    recursiveOutputStream.copyToOutputStream(outputStream);
                                    repeatBuffer.setLength(0);
                                    tempBuffer.setLength(0);
                                    isRepeating = false;
                                    bracketCount = 0;
                                }
                            }
                        } else if (bracketCount == -1) {
                            repeatBuffer.setLength(repeatBuffer.getLength() - 1);
                            try (ByteArrayBackupToFileOutputStream newCopyFileStream = new ByteArrayBackupToFileOutputStream()) {
                                repeatBuffer.copyToOutputStream(newCopyFileStream);
                                copyRepeatStream(repeatTimes, repeatBuffer, newCopyFileStream,
                                    COMMA_NEWLINE_BYTE_ARRAY);
                                newCopyFileStream.write(CLOSE_BRACKET_BYTE_ARRAY);
                                try (ByteArrayBackupToFileOutputStream recursiveOutputStream = new ByteArrayBackupToFileOutputStream()) {
                                    try (InputStream inputStream1 = newCopyFileStream
                                        .getNewInputStream()) {
                                        handleRepeats(inputStream1, recursiveOutputStream, shouldWriteToRepeatStream);
                                    }
                                    recursiveOutputStream.copyToOutputStream(outputStream);
                                }
                                repeatBuffer.setLength(0);
                                tempBuffer.setLength(0);
                                isRepeating = false;
                                bracketCount = 0;
                            }
                        }
                    }
                    if ('<' == i && xmlTag == 0) {
                        i = bufferedReader.read();
                        while ('>' != i && i != -1) {
                            charAsUTF8String = Character.valueOf((char) i).toString();
                            xmlRepeatTagList.add((char) i);
                            repeatBuffer.write(charAsUTF8String.getBytes(Charsets.UTF_8));
                            i = bufferedReader.read();
                            charAsUTF8String = Character.valueOf((char) i).toString();
                        }
                        xmlTag++;
                        repeatBuffer.write(charAsUTF8String.getBytes(Charsets.UTF_8));
                        xmlRepeatTag = "</" + readAsString(xmlRepeatTagList) + ">";
                    }
                    if (repeatBuffer.getLength() > 0 && !xmlRepeatTag.isEmpty() && repeatBuffer
                        .toString().endsWith(xmlRepeatTag)) {
                        try (ByteArrayBackupToFileOutputStream newCopyOutputStream = new ByteArrayBackupToFileOutputStream()) {
                            repeatBuffer.copyToOutputStream(newCopyOutputStream);
                            copyRepeatStream(repeatTimes, repeatBuffer, newCopyOutputStream,
                                NEWLINE_BYTE_ARRAY);
                            try (ByteArrayBackupToFileOutputStream recursiveOutputStream = new ByteArrayBackupToFileOutputStream()) {
                                try (InputStream newCopyOutputStreamNewInputStream = newCopyOutputStream
                                    .getNewInputStream()) {
                                    handleRepeats(newCopyOutputStreamNewInputStream,
                                        recursiveOutputStream, shouldWriteToRepeatStream);
                                }
                                recursiveOutputStream.copyToOutputStream(outputStream);
                                repeatBuffer.setLength(0);
                                tempBuffer.setLength(0);
                                isRepeating = false;
                                bracketCount = 0;
                                xmlTag = 0;
                            }
                        }
                    }
                } else {
                    tempBuffer.write(charAsUTF8String.getBytes(Charsets.UTF_8));
                    lastCharQueue.add((char) i);
                }
                if ((lastCharQueue.peek() != null && lastCharQueue.peek().equals('\''))
                    && REPEAT.equals(readAsString(lastCharQueue))) {
                    tempBuffer.mark();
                    bufferedReader.mark(DEFAULT_READ_AHEAD_LIMIT);
                    try {
                        i = bufferedReader.read();
                        charAsUTF8String = Character.valueOf((char) i).toString();
                        tempBuffer.write(charAsUTF8String.getBytes());
                        String numRepeats = "";
                        while (i != ')') {
                            numRepeats = numRepeats + Character.toString((char) i);
                            i = bufferedReader.read();
                            charAsUTF8String = Character.valueOf((char) i).toString();
                            if (i != ')') {
                                tempBuffer.write(charAsUTF8String.getBytes());
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
                        tempBuffer.setLength(
                            tempBuffer.getLength() - numRepeats.length()
                                - lastCharQueue.size() - 5);
                        tempBuffer.copyToOutputStream(outputStream);
                        tempBuffer.setLength(0);
                        repeatBuffer.setLength(0);
                        isRepeating = true;
                        bracketCount = 0;
                        lastCharQueue.clear();
                        xmlRepeatTagList.clear();
                    } catch (IllegalStateException e) {
                        setQueueCharacters(lastCharQueue, REPEAT);
                        bufferedReader.reset();
                        tempBuffer.reset();
                    }
                }
        }
            bufferedReader.close();
            if (shouldWriteToRepeatStream) {
                tempBuffer.copyToOutputStream(outputStream);
            }
        } finally {
            bufferedReader.close();
        }
    }

    private void copyRepeatStream(final int repeatTimes, final ByteArrayBackupToFileOutputStream repeatBuffer,
        final ByteArrayBackupToFileOutputStream newCopyFileStream, final byte[] separatorBytes)
        throws IOException {
        for (int j = 1; j < repeatTimes; j++) {
            newCopyFileStream.write(separatorBytes);
            repeatBuffer.copyToOutputStream(newCopyFileStream);
        }
    }

    private int parseRepeats(final String repeatArguments) {
        final Matcher matcher = REPEAT_PARAMETERS_PATTERN.matcher(repeatArguments);
        if (matcher.find()) {
            Integer integer = Integer.parseInt(matcher.group(1));
            Integer integer2 = Integer.parseInt(matcher.group(1));
            if (matcher.group(2) != null) {
                integer2 = Integer.parseInt(matcher.group(2));
            } else if (integer >= integer2 && !integer.equals(integer2)) {
                throw new IllegalArgumentException(
                    "the second number must be greater than the first number" + repeatArguments);
            } else {
                return integer;
            }
            if (integer.equals(integer2)) {
                return integer;
            } else {
                return new Random().nextInt((integer2 - integer) + 1) + integer;
            }
        }
        throw new IllegalArgumentException(
            "invalid arguments for repeat function: " + repeatArguments);
    }

    private String readAsString(final CircularFifoQueue<Character> characters) {
        char[] charArray = new char[characters.size()];
        for (int i = 0; i < charArray.length; i++) {
            charArray[i] = characters.get(i);
        }
        return new String(charArray);
    }

    private String readAsString(final List<Character> characters) {
        char[] charArray = new char[characters.size()];
        for (int i = 0; i < charArray.length; i++) {
            charArray[i] = characters.get(i);
        }
        return new String(charArray);
    }

    private void setQueueCharacters(final CircularFifoQueue<Character> characters,
        final String string) {
        characters.clear();
        char[] charArray = string.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            characters.add(charArray[i]);
        }
    }

    private void handleNestedFunctions(final InputStream inputStream,
        final OutputStream outputStream)
        throws IOException {
        Reader reader = new FunctionReplacingReader(
            new InputStreamReader(inputStream, Charsets.UTF_8), new FunctionTokenResolver());

        int data = 0;
        try {
            while ((data = reader.read()) != -1) {
                String charAsUTF8String = Character.valueOf((char) data).toString();
                outputStream.write(charAsUTF8String.getBytes(Charsets.UTF_8));
            }

        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(reader);
        }

    }

}
