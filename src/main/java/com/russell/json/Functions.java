package com.russell.json;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class Functions {

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
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String randomInt(Integer min, Integer max) {
        int randomNum = min + (int)(Math.random()*max);
        return new Integer(randomNum).toString();
    }

    public String uuid(){
        return UUID.randomUUID().toString();
    }

}
