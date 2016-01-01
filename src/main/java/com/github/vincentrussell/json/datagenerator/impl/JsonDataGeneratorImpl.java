package com.github.vincentrussell.json.datagenerator.impl;


import com.github.vincentrussell.json.datagenerator.JsonDataGenerator;
import com.github.vincentrussell.json.datagenerator.JsonDataGeneratorException;
import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.google.common.collect.Iterables;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonDataGeneratorImpl implements JsonDataGenerator {

    public JsonDataGeneratorImpl() {}

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
        FileInputStream copyInputStream = null;
        FileOutputStream repeatsOutputStream = null;
        try {
            File repeatsFile = File.createTempFile("repeats","json");
            repeatsFile.deleteOnExit();
            repeatsOutputStream = new FileOutputStream(repeatsFile);
            handleRepeats(inputStream, repeatsOutputStream);
            repeatsOutputStream.close();
            copyInputStream = new FileInputStream(repeatsFile);
            handleNestedFunctions(copyInputStream, outputStream);
        } catch (IOException  e) {
            throw new JsonDataGeneratorException(e);
        } finally {
            IOUtils.closeQuietly(copyInputStream);
            IOUtils.closeQuietly(repeatsOutputStream);
        }
    }

    protected void handleRepeats(InputStream inputStream, OutputStream outputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder tempBuffer = new StringBuilder();
        StringBuilder repeatBuffer = new StringBuilder();
        boolean isRepeating = false;
        int bracketCount = 0;
        int repeatTimes = 0;
        try {
            int i;
             do {
             i = br.read();
               if (i != -1) {
                    if (isRepeating) {
                        repeatBuffer.append((char) i);
                        if (Character.valueOf((char) i).equals("{".toCharArray()[0])) {
                            bracketCount++;
                        } else if (Character.valueOf((char) i).equals("}".toCharArray()[0]) || (Character.valueOf((char) i).equals("]".toCharArray()[0])) && bracketCount==0 ) {
                            bracketCount--;
                            if (bracketCount == 0) {
                                File newCopyFile = File.createTempFile("newCopy","json");
                                newCopyFile.deleteOnExit();
                                FileOutputStream newCopyFileStream = new FileOutputStream(newCopyFile);
                                newCopyFileStream.write(String.valueOf(repeatBuffer).getBytes());
                                for (int j = 1; j < repeatTimes; j++) {
                                    newCopyFileStream.write(String.valueOf(",\n").getBytes());
                                    newCopyFileStream.write(String.valueOf(repeatBuffer).getBytes());
                                }
                                File recursiveJsonObjectFile = File.createTempFile("recursive","json");
                                recursiveJsonObjectFile.deleteOnExit();
                                FileOutputStream recursiveOutputStream = new FileOutputStream(recursiveJsonObjectFile);
                                handleRepeats(new FileInputStream(newCopyFile), recursiveOutputStream);
                                recursiveOutputStream.close();
                                StringBuilder builder = new StringBuilder();
                                FileInputStream fileInputSteam = new FileInputStream(recursiveJsonObjectFile);
                                builder.append(IOUtils.toString(fileInputSteam));
                                outputStream.write(String.valueOf(builder).getBytes());
                                fileInputSteam.close();
                                repeatBuffer.setLength(0);
                                tempBuffer.setLength(0);
                                isRepeating = false;
                                bracketCount = 0;
                            } else if (bracketCount==-1) {
                                repeatBuffer.setLength(repeatBuffer.length() -1);
                                File newCopyFile = File.createTempFile("newCopy","json");
                                newCopyFile.deleteOnExit();
                                FileOutputStream newCopyFileStream = new FileOutputStream(newCopyFile);
                                newCopyFileStream.write(String.valueOf(repeatBuffer).getBytes());
                                for (int j = 1; j < repeatTimes; j++) {
                                    newCopyFileStream.write(String.valueOf(",\n").getBytes());
                                    newCopyFileStream.write(String.valueOf(repeatBuffer).getBytes());
                                }
                                newCopyFileStream.write(String.valueOf("]").getBytes());
                                File recursiveJsonObjectFile = File.createTempFile("recursive","json");
                                recursiveJsonObjectFile.deleteOnExit();
                                FileOutputStream recursiveOutputStream = new FileOutputStream(recursiveJsonObjectFile);
                                handleRepeats(new FileInputStream(newCopyFile), recursiveOutputStream);
                                recursiveOutputStream.close();
                                StringBuilder builder = new StringBuilder();
                                FileInputStream fileInputSteam = new FileInputStream(recursiveJsonObjectFile);
                                builder.append(IOUtils.toString(fileInputSteam));
                                outputStream.write(String.valueOf(builder).getBytes());
                                fileInputSteam.close();
                                repeatBuffer.setLength(0);
                                tempBuffer.setLength(0);
                                isRepeating = false;
                                bracketCount = 0;
                            }
                        }
                } else {
                    tempBuffer.append((char) i);
                }
                if (FunctionTokenResolver.isRepeatFunction(tempBuffer)) {
                    tempBuffer.append((char) i);
                    repeatTimes = Integer.parseInt(Iterables.get(FunctionTokenResolver.getRepeatFunctionNameAndArguments(tempBuffer),1).toString());
                    int indexOfRepeat = indexOf(FunctionTokenResolver.REPEAT_FUNCTION_PATTERN, tempBuffer);
                    tempBuffer.setLength(indexOfRepeat);
                    outputStream.write(String.valueOf(tempBuffer).getBytes());
                    tempBuffer.setLength(0);
                    repeatBuffer.setLength(0);
                    isRepeating = true;
                    bracketCount = 0;
                }
            }
            } while (i != -1);
            br.close();
            //noop
        } finally {
            outputStream.write(String.valueOf(tempBuffer).getBytes());
            br.close();
        }
    }

    protected void handleNestedFunctions(InputStream inputStream, OutputStream outputStream) throws IOException {
        Reader reader = new FunctionReplacingReader(new InputStreamReader(inputStream), new FunctionTokenResolver());

        int data = 0;
        try {
            while((data = reader.read()) != -1){
                outputStream.write(data);
            }

            } finally {
                    inputStream.close();
            }

    }

}
