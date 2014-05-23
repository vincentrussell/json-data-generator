package com.russell.json.impl;

import com.russell.json.Functions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionsImpl implements Functions {

    public static final Pattern FUNCTION_PATTERN = Pattern.compile("\\{\\{([\\w]+)\\((.*)\\)\\}\\}");
    public static final Pattern REPEAT_FUNCTION_PATTERN = Pattern.compile("\'\\{\\{(repeat)\\((\\d+)\\)\\}\\}\'\\s*,");

    public String execute(String functionName, Object... arguments) throws IllegalArgumentException {
        List<Class> classList = new ArrayList<Class>();
        if (arguments != null) {
            for (Object argument : arguments) {
                classList.add(argument.getClass());
            }
        }
        try {
            Method method = this.getClass().getMethod(functionName, classList.toArray(new Class[classList.size()]));
            return method.invoke(this,arguments).toString();
        } catch (SecurityException e) {
          throw new IllegalArgumentException(e);
        } catch (NoSuchMethodException e) {
            try {
                Method method = this.getClass().getMethod(functionName, Object[].class);
                return method.invoke(this,new Object[]{arguments}).toString();
            } catch (NoSuchMethodException e1) {
                throw new IllegalArgumentException(e);
            } catch (InvocationTargetException e1) {
                throw new IllegalArgumentException(e);
            } catch (IllegalAccessException e1) {
                throw new IllegalArgumentException(e);
            }
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public boolean isFunction(CharSequence input) {
        Matcher matcher = FunctionsImpl.FUNCTION_PATTERN.matcher(input);
        return matcher.find();
    }

    public boolean isRepeatFunction(CharSequence input) {
        Matcher matcher = FunctionsImpl.REPEAT_FUNCTION_PATTERN.matcher(input);
        return matcher.find();
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
                if (arg==null || arg.length()==0) {
                    continue;
                }
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

    public String integer(Integer min, Integer max) {
        int randomNum = min + (int)(Math.random()*max);
        return new Integer(randomNum).toString();
    }

    public String uuid(){
        return UUID.randomUUID().toString();
    }

    public boolean bool(){
        return Math.random() < 0.5;
    }

    public Object random(Object[] options){
        int randomNum = 0 + (int)(Math.random()*options.length);
        return options[randomNum];
    }

}
