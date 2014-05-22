package com.russell.json.impl;


import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.russell.json.JsonParser;

import java.io.*;
import java.nio.ByteBuffer;
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
        generateTestDataJson(new StringReader(text),outputStream);
    }

    public void generateTestDataJson(Reader reader, OutputStream outputStream) {
        BufferedReader br = new BufferedReader(reader);
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
                                tempBuffer.append(repeatBuffer);
                                for (int j = 1; j < repeatTimes; j++) {
                                    tempBuffer.append(",\n").append(repeatBuffer);
                                }
                                outputStream.write(String.valueOf(tempBuffer).getBytes());
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
                        br.mark(1000);
                        tempBuffer.append((char) i);
                        repeatTimes = (Integer)getRepeatFunctionNameAndArguments(tempBuffer)[1];
                        int indexOfRepeat = indexOf(repeatFunctionPattern,tempBuffer);
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
        }
         catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.write(String.valueOf(tempBuffer).getBytes());
            } catch (IOException e) {
                //noop
            }
        }


        JsonReader jsonReader = new JsonReader(reader);
//
//        try {
//            handleObject(jsonReader);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


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
