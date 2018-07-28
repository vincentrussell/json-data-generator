package com.github.vincentrussell.json.datagenerator.functions;

import com.github.vincentrussell.json.datagenerator.functions.impl.*;
import com.github.vincentrussell.json.datagenerator.functions.impl.Date;
import com.github.vincentrussell.json.datagenerator.functions.impl.Random;
import com.github.vincentrussell.json.datagenerator.functions.impl.UUID;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.Validate.notNull;

public class FunctionRegistry {

    private static FunctionRegistry INSTANCE;

    private final Map<FunctionInvocationHolder, MethodAndObjectHolder> functionInvocationHolderMethodConcurrentHashMap = new ConcurrentHashMap<>();
    private final Map<Method, Object> methodInstanceMap = new ConcurrentHashMap<>();
    private final Set<String> nonOverridableFunctionNames = new HashSet<>();

    private FunctionRegistry() {
        registerClass(RandomInteger.class);
        registerClass(RandomDouble.class);
        registerClass(RandomFloat.class);
        registerClass(RandomLong.class);
        registerClass(Random.class);
        registerClass(UUID.class);
        registerClass(Bool.class);
        registerClass(Index.class);
        registerClass(LoremIpsum.class);
        registerClass(Concat.class);
        registerClass(ToUpper.class);
        registerClass(ToLower.class);
        registerClass(Substring.class);
        registerClass(Phone.class);
        registerClass(Gender.class);
        registerClass(Date.class);
        registerClass(Timestamp.class);
        registerClass(Alpha.class);
        registerClass(AlphaNumeric.class);
        registerClass(City.class);
        registerClass(Company.class);
        registerClass(Country.class);
        registerClass(Email.class);
        registerClass(FirstName.class);
        registerClass(LastName.class);
        registerClass(Username.class);
        registerClass(State.class);
        registerClass(Street.class);
        registerClass(Ssn.class);
        registerClass(Ipv4.class);
        registerClass(Ipv6.class);
        registerClass(ObjectId.class);
        registerClass(Hex.class);
    }

    public void registerClass(Class clazz) {
        Function annotation = (Function) clazz.getAnnotation(Function.class);
        checkClassValidity(clazz, annotation);
        try {
            for (String annotationName : annotation.name()) {
                Object instance = clazz.newInstance();
                for (final Method method : clazz.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(FunctionInvocation.class)) {
                        checkMethodValidity(method);
                        MethodAndObjectHolder methodAndObjectHolder = new MethodAndObjectHolder(method, instance);
                        functionInvocationHolderMethodConcurrentHashMap.put(new FunctionInvocationHolder(annotationName, method.getParameterTypes()), methodAndObjectHolder);
                        methodInstanceMap.put(method, instance);
                    }
                }
                if (!annotation.overridable()) {
                    nonOverridableFunctionNames.add(annotationName);
                }
            }
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void checkMethodValidity(Method method) {
        int stringClassesCount = Iterables.size(Iterables.filter(Arrays.asList(method.getParameterTypes()), new Predicate<Class<?>>() {
            public boolean apply(Class<?> aClass) {
                return aClass == String.class;
            }
        }));

        int stringArrayClassesCount = Iterables.size(Iterables.filter(Arrays.asList(method.getParameterTypes()), new Predicate<Class<?>>() {
            public boolean apply(Class<?> aClass) {
                return aClass == String[].class;
            }
        }));

        if (!String.class.isAssignableFrom(method.getReturnType())) {
            throw new IllegalArgumentException("method " + method.getName() + " on class " + method.getDeclaringClass().getName() + " must return type String");
        }

        if ((stringClassesCount != method.getParameterTypes().length && method.getParameterTypes().length > 1)
                || (stringArrayClassesCount != 1 && stringClassesCount == 0 && method.getParameterTypes().length == 1)) {
            throw new IllegalArgumentException("for method " + method.getName() + " on class " + method.getDeclaringClass().getName() + ": all method parameters need to be a String or a single String var-arg parameter");
        }
    }

    private void checkClassValidity(Class clazz, Function annotation) {
        if (annotation == null) {
            throw new IllegalArgumentException(clazz.getName() + " must be annotated with " + Function.class.getName());
        }

        for (String annotationName : annotation.name()){
            if (isEmpty(annotationName)) {
                throw new IllegalArgumentException(Function.class.getName() + "annotation on class" + clazz.getName() + " annotation must have name attribute populated");
            }

            if (nonOverridableFunctionNames.contains(annotationName)) {
                throw new IllegalArgumentException(clazz.getName() + " can not override existing function with the same annotation: " + annotationName + " because it does not allow overriding.");
            }
        }

        int zeroArgConstructorCount = Iterables.size(Iterables.filter(Arrays.asList(clazz.getConstructors()), new Predicate<Constructor>() {
            public boolean apply(Constructor constructor) {
                return constructor.getParameterTypes().length == 0;
            }
        }));

        if (zeroArgConstructorCount != 1) {
            throw new IllegalArgumentException(clazz.getName() + " must have a no-arg constructor");
        }

        int validMethodCount = Iterables.size(Iterables.filter(Arrays.asList(clazz.getDeclaredMethods()), new Predicate<Method>() {
            public boolean apply(Method method) {
                return method.isAnnotationPresent(FunctionInvocation.class);
            }
        }));

        if (validMethodCount == 0) {
            throw new IllegalArgumentException(clazz.getName() + ": could not find any public methods annotated with " + FunctionInvocation.class.getName());
        }
    }

    public String executeFunction(String functionName, String... arguments) throws InvocationTargetException, IllegalAccessException {
        Method method = getMethod(functionName, arguments);
        return executeMethod(method, arguments);
    }

    public String executeMethod(Method method, String... arguments) throws InvocationTargetException, IllegalAccessException {
        Object instance = methodInstanceMap.get(method);
        if (method.getParameterTypes().length == 1 && method.getParameterTypes()[0].equals(String[].class)) {
            return method.invoke(instance, new Object[]{arguments}).toString();
        } else {
            return method.invoke(instance, arguments).toString();
        }

    }

    public Method getMethod(String functionName, String... arguments) throws IllegalArgumentException {
        final List<Class> classList = new ArrayList<>();
        if (arguments != null) {
            for (String argument : arguments) {
                if (argument != null) {
                    classList.add(argument.getClass());
                }
            }
        }

        MethodAndObjectHolder holder = null;
        try {
            holder = getHolder(functionName, classList);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }

        if (holder == null) {
            throw new IllegalArgumentException("could not find method to invoke.");
        }

        return holder.getMethod();
    }

    private MethodAndObjectHolder getHolder(String functionName, List<Class> classList) throws IllegalAccessException {
        MethodAndObjectHolder holder = functionInvocationHolderMethodConcurrentHashMap.get(new FunctionInvocationHolder(functionName, classList.toArray(new Class[classList.size()])));

        if (holder == null) {
            holder = functionInvocationHolderMethodConcurrentHashMap.get(new FunctionInvocationHolder(functionName, new Class[]{String[].class}));
        }

        return holder;
    }

    public static FunctionRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FunctionRegistry();
        }
        return INSTANCE;
    }

    private static class MethodAndObjectHolder {
        private final Method method;
        private final Object instance;

        private MethodAndObjectHolder(Method method, Object instance) {
            this.method = method;
            this.instance = instance;
        }

        public Method getMethod() {
            return method;
        }

        public Object getInstance() {
            return instance;
        }
    }

    private static class FunctionInvocationHolder {
        private final String functionName;
        private final Class[] parameterTypes;

        private FunctionInvocationHolder(String functionName, Class[] parameterTypes) {
            notNull(functionName, "a function name must be provided");
            notNull(parameterTypes, "parameter types must be provided");
            this.functionName = functionName;
            this.parameterTypes = parameterTypes;
        }

        public String getFunctionName() {
            return functionName;
        }

        public Class[] getParameterTypes() {
            return parameterTypes;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            FunctionInvocationHolder functionInvocationHolder = (FunctionInvocationHolder) obj;
            return new EqualsBuilder()
                    .append(functionName, functionInvocationHolder.functionName)
                    .append(parameterTypes, functionInvocationHolder.parameterTypes)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(5, 33).
                    append(functionName).
                    append(parameterTypes).
                    toHashCode();
        }
    }


}
