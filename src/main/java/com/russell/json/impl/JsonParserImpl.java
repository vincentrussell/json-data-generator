package com.russell.json.impl;


import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.russell.json.JsonParser;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonParserImpl implements JsonParser {


    public JsonParserImpl() {
        //noop
    }

    public boolean isFunction(CharSequence input) {
        Matcher matcher = FunctionsImpl.FUNCTION_PATTERN.matcher(input);
        return matcher.find();
    }

    public boolean isRepeatFunction(CharSequence input) {
        Matcher matcher = FunctionsImpl.REPEAT_FUNCTION_PATTERN.matcher(input);
        return matcher.find();
    }

    public static int indexOf(Pattern pattern, CharSequence input) {
        Matcher matcher = pattern.matcher(input);
        return matcher.find() ? matcher.start() : -1;
    }

    public Object[] getFunctionNameAndArguments(CharSequence input) {
        return getFunctionNameAndArguments(input,FunctionsImpl.FUNCTION_PATTERN);
    }

    public Object[] getRepeatFunctionNameAndArguments(CharSequence input) {
        return getFunctionNameAndArguments(input, FunctionsImpl.REPEAT_FUNCTION_PATTERN);
    }

    public Object[] getFunctionNameAndArguments(CharSequence input, Pattern pattern) {
        Matcher matcher = pattern.matcher(input);
        List<Object> objectList = new ArrayList<Object>();
        if (matcher.find()) {
            objectList.add(matcher.group(1));
            for (String arg : matcher.group(2).split(",")) {
                try {
                    objectList.add(Integer.valueOf(arg));
                } catch (NumberFormatException e) {
                    objectList.add(arg.replaceAll("^\"|\"$", ""));
                }

            }
            return objectList.toArray();
        }
        return null;
    }

    @Override
    public void generateTestDataJson(String text, OutputStream outputStream) {
        handleRepeats(new ByteArrayInputStream(text.getBytes()), outputStream);
        handleNestedFunctions(new ByteArrayInputStream(text.getBytes()), outputStream);
    }

    @Override
    public void generateTestDataJson(URL classPathResource, OutputStream outputStream) {
        try {
            handleRepeats(classPathResource.openStream(), outputStream);
            handleNestedFunctions(classPathResource.openStream(), outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void generateTestDataJson(File file, OutputStream outputStream) throws FileNotFoundException {
        handleRepeats(new FileInputStream(file), outputStream);
        handleNestedFunctions(new FileInputStream(file), outputStream);
    }


    protected void handleRepeats(InputStream inputStream, OutputStream outputStream) {
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
                if (isRepeatFunction(tempBuffer)) {
                    tempBuffer.append((char) i);
                    repeatTimes = (Integer) getRepeatFunctionNameAndArguments(tempBuffer)[1];
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


        } catch (IOException e) {
            //noop
        } finally {
            try {
                outputStream.write(String.valueOf(tempBuffer).getBytes());
                br.close();
            } catch (IOException e) {
                //noop
            }
        }
    }

    protected void handleNestedFunctions(InputStream inputStream, OutputStream outputStream) {
        Reader reader = new FunctionReplacingReader(new InputStreamReader(inputStream), new FunctionTokenResolver());

        int data = 0;
        try {
            data = reader.read();

        while(data != -1){
            System.out.print((char) data);
            data = reader.read();
        }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                //noop
            }
        }

    }


// JsonReader jsonReader = new JsonReader(reader);
//
//        try {
//            handleObject(jsonReader);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }



    private static void handleObject(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            JsonToken token = reader.peek();
            if (token.equals(JsonToken.BEGIN_ARRAY))
                handleArray(reader);
            else if (token.equals(JsonToken.END_ARRAY)) {
                reader.endObject();
                return;
            } else
                handleNonArrayToken(reader, token);
        }

    }

    private static void handleArray(JsonReader reader) throws IOException {
        reader.beginArray();
        while (true) {
            JsonToken token = reader.peek();
            if (token.equals(JsonToken.END_ARRAY)) {
                reader.endArray();
                break;
            } else if (token.equals(JsonToken.BEGIN_OBJECT)) {
                handleObject(reader);
            } else
                handleNonArrayToken(reader, token);
        }
    }

    private static void handleNonArrayToken(JsonReader reader, JsonToken token) throws IOException {
        if (token.equals(JsonToken.NAME)) {
            System.out.println(reader.nextName());
        } else if (token.equals(JsonToken.STRING)) {
            System.out.println(reader.nextString());
        }   else if(token.equals(JsonToken.NUMBER)) {
            System.out.println(reader.nextDouble());
        }   else if (token.equals(JsonToken.BOOLEAN)) {
            System.out.println(reader.nextBoolean());
        } else if (token.equals(JsonToken.NULL)) {
            reader.nextNull();
            System.out.println("null");
        } else {
            reader.skipValue();
        }
    }

}
