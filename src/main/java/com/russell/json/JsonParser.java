package com.russell.json;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonParser {

    JsonFactory jfactory = new JsonFactory();
    Pattern functionPattern = Pattern.compile("\\{\\{([\\w]+)\\((.*)\\)\\}\\}");

    public JsonParser() {
        jfactory.enable(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_COMMENTS);
    }

    public boolean isFunction(String field) {
        Matcher matcher = functionPattern.matcher(field);
        return matcher.find();
    }

    public Object[] getFunctionNameAndArguments(String field) {
        Matcher matcher = functionPattern.matcher(field);
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


    public String generateTestDataJson(String inputData) {

        com.fasterxml.jackson.core.JsonParser jParser = null;
        try {
            jParser = jfactory.createParser(inputData);

            JsonToken jsonToken = null;
            while ((jsonToken = jParser.getCurrentToken()) != JsonToken.END_OBJECT) {
                String fieldname = jParser.getCurrentName();
                if (fieldname!=null) {
                    String value = jParser.getValueAsString();
                    System.out.println(fieldname);
                    System.out.println(value);
                }

                jParser.nextToken();
            }

        } catch (IOException e) {
            //noop
        } finally {
            if (jParser != null) {
                try {
                    jParser.close();
                } catch (IOException e) {
                    //noop
                }
            }
        }

        return null;
    }

}
