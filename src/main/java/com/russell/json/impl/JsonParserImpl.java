package com.russell.json.impl;


import com.russell.json.Functions;
import com.russell.json.JsonParser;
import com.russell.json.JsonParserException;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonParserImpl implements JsonParser {

    Functions functions = new FunctionsImpl();

    public JsonParserImpl() {}

    public static int indexOf(Pattern pattern, CharSequence input) {
        Matcher matcher = pattern.matcher(input);
        return matcher.find() ? matcher.start() : -1;
    }


    @Override
    public void generateTestDataJson(String text, OutputStream outputStream) throws JsonParserException {
        try {
            processRepeatsAndFunctions(new ByteArrayInputStream(text.getBytes()), outputStream);
        } catch (IOException e) {
            throw new JsonParserException(e);
        }
    }

    @Override
    public void generateTestDataJson(URL classPathResource, OutputStream outputStream) throws JsonParserException {
        try {
            processRepeatsAndFunctions(classPathResource.openStream(), outputStream);
        } catch (IOException e) {
            throw new JsonParserException(e);
        }
    }

    @Override
    public void generateTestDataJson(File file, OutputStream outputStream) throws JsonParserException {
        try {
            processRepeatsAndFunctions(new FileInputStream(file), outputStream);
        } catch (IOException e) {
            throw new JsonParserException(e);
        }
    }

    protected void processRepeatsAndFunctions(InputStream inputStream, OutputStream outputStream) throws IOException {
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
        } finally {
            if (copyInputStream!=null) {
                copyInputStream.close();
            }
            if (repeatsOutputStream!=null) {
                repeatsOutputStream.close();
            }
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
                    } else if (Character.valueOf((char) i).equals("}".toCharArray()[0])) {
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
                        }
                    }
                } else {
                    tempBuffer.append((char) i);
                }
                if (functions.isRepeatFunction(tempBuffer)) {
                    tempBuffer.append((char) i);
                    repeatTimes = (Integer) functions.getRepeatFunctionNameAndArguments(tempBuffer)[1];
                    int indexOfRepeat = indexOf(FunctionsImpl.REPEAT_FUNCTION_PATTERN, tempBuffer);
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
            data = reader.read();

        while(data != -1){
            //System.out.print((char) data);
            outputStream.write(data);
            data = reader.read();
        }

        } finally {
                inputStream.close();
        }

    }

}
