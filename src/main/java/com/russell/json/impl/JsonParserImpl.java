package com.russell.json.impl;


import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.russell.json.JsonParser;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonParserImpl implements JsonParser {

    Pattern functionPattern = Pattern.compile("\\{\\{([\\w]+)\\((.*)\\)\\}\\}");
    Pattern repeatFunctionPattern = Pattern.compile("\'\\{\\{(repeat)\\((\\d+)\\)\\}\\}\'\\s*,");

    public JsonParserImpl() {
        //noop
    }

    public boolean isFunction(CharSequence input) {
        Matcher matcher = functionPattern.matcher(input);
        return matcher.find();
    }

    public boolean isRepeatFunction(CharSequence input) {
        Matcher matcher = repeatFunctionPattern.matcher(input);
        return matcher.find();
    }

    public static int indexOf(Pattern pattern, CharSequence input) {
        Matcher matcher = pattern.matcher(input);
        return matcher.find() ? matcher.start() : -1;
    }

    public Object[] getFunctionNameAndArguments(CharSequence input) {
        return getFunctionNameAndArguments(input,functionPattern);
    }

    public Object[] getRepeatFunctionNameAndArguments(CharSequence input) {
        return getFunctionNameAndArguments(input, repeatFunctionPattern);
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

    public void generateTestDataJson(String text, OutputStream outputStream) {
        generateTestDataJson(new ByteArrayInputStream(text.getBytes()),outputStream);
    }

    public void generateTestDataJson(InputStream inputStream, OutputStream outputStream) {
        StringBuilder builder = new StringBuilder();
        try {
            builder.append(IOUtils.toString(inputStream));
            doGenerateTestDataJson(builder, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void doGenerateTestDataJson(StringBuilder in, OutputStream out) {
        StringBuilder tempBuffer = new StringBuilder();
        StringBuilder repeatBuffer = new StringBuilder();
        boolean isRepeating = false;
        int bracketCount = 0;
        int repeatTimes = 0;
        try {
            for (int i=0; i < in.length(); i++) {
                if (isRepeating) {
                    repeatBuffer.append(in.substring(i,i+1));
                    if (in.substring(i,i+1).equals("{")) {
                        bracketCount++;
                    } else if (in.substring(i,i+1).equals("}")) {
                        bracketCount--;
                        if (bracketCount == 0) {
                            tempBuffer.append(repeatBuffer);
                            for (int j = 1; j < repeatTimes; j++) {
                                tempBuffer.append(",\n").append(repeatBuffer);
                            }
                            File tempFile = File.createTempFile("tempFile","json");
                            tempFile.deleteOnExit();
                            FileOutputStream outputStream = new FileOutputStream(tempFile);
                            doGenerateTestDataJson(tempBuffer,outputStream);
                            outputStream.close();
                            StringBuilder builder = new StringBuilder();
                            FileInputStream fileInputSteam = new FileInputStream(tempFile);
                            builder.append(IOUtils.toString(fileInputSteam));
                            out.write(String.valueOf(builder).getBytes());
                            fileInputSteam.close();
                            repeatBuffer.setLength(0);
                            tempBuffer.setLength(0);
                            isRepeating = false;
                            bracketCount = 0;
                        }
                    }
                } else {
                    tempBuffer.append(in.substring(i,i+1));
                }
                if (isRepeatFunction(tempBuffer)) {
                    tempBuffer.append(in.substring(i,i+1));
                    repeatTimes = (Integer) getRepeatFunctionNameAndArguments(tempBuffer)[1];
                    int indexOfRepeat = indexOf(repeatFunctionPattern, tempBuffer);
                    tempBuffer.setLength(indexOfRepeat);
                    out.write(String.valueOf(tempBuffer).getBytes());
                    tempBuffer.setLength(0);
                    repeatBuffer.setLength(0);
                    isRepeating = true;
                    bracketCount = 0;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.write(String.valueOf(tempBuffer).getBytes());
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
