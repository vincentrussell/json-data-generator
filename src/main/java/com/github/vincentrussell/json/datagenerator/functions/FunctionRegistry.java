package com.github.vincentrussell.json.datagenerator.functions;

import com.github.vincentrussell.json.datagenerator.impl.IndexHolder;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.Validate.notNull;

/**
 * Class responsible for registering functions so that they can be used from within the data
 * generator
 */
public final class FunctionRegistry {

  private final Map<FunctionInvocationHolder,
      MethodAndObjectHolder> functionInvocationHolderMethodConcurrentHashMap =
      new ConcurrentHashMap<>();
  private final Map<Method, Object> methodInstanceMap = new ConcurrentHashMap<>();
  private final Set<String> nonOverridableFunctionNames = new HashSet<>();
  private final Map<String, String> getAndPutCache = new ConcurrentHashMap<>();
  private final Map<String, IndexHolder> stringIndexHolderMap = new ConcurrentHashMap<>();

  /**
   * this is a singleton so private constructor
   */
  public FunctionRegistry() {
    Reflections reflections = new Reflections(getClass().getPackage().getName() + ".impl");
    Set<Class<? extends Object>> functionClasses =
            reflections.getTypesAnnotatedWith(Function.class);
    for (Class<?> clazz: functionClasses) {
      registerClass(clazz);
    }
  }

  /**
   * the cache that is used to gets and puts
   * in the {@link com.github.vincentrussell.json.datagenerator.functions.impl.Get}
   * and {@link com.github.vincentrussell.json.datagenerator.functions.impl.Put}
   * @return the cache as map
   */
  public Map<String, String> getGetAndPutCache() {
    return getAndPutCache;
  }

  /**
   * get the map that is responsible for keeping track of indices via the {@link IndexHolder}
   * @return the index holder as a map
   */
  public Map<String, IndexHolder> getStringIndexHolderMap() {
    return stringIndexHolderMap;
  }

  /**
   * register a class that has functions
   *
   * @param clazz the class that has the {@link Function} and {@link FunctionInvocation}
   */
  public void registerClass(final Class<?> clazz) {
    Function annotation = clazz.getAnnotation(Function.class);
    checkClassValidity(clazz, annotation);
    try {
      for (String annotationName : annotation.name()) {

        int zeroArgConstructorCount = getZeroArgConstructorCount(clazz);
        int functionRegistryConstructorArgumentCount = getFunctionRegistryConstructorArgumentCount(
                clazz);

        Object instance = null;
        if (functionRegistryConstructorArgumentCount == 1) {
          instance = clazz.getConstructors()[0].newInstance(this);
        } else if (zeroArgConstructorCount == 1) {
          instance = clazz.newInstance();
        } else {
          throw new IllegalArgumentException("proper constructor for class "
                  + clazz + " cannot be found");
        }
        for (final Method method : clazz.getDeclaredMethods()) {
          if (method.isAnnotationPresent(FunctionInvocation.class)) {
            checkMethodValidity(method);
            MethodAndObjectHolder methodAndObjectHolder =
                new MethodAndObjectHolder(method, instance);
            functionInvocationHolderMethodConcurrentHashMap.put(
                new FunctionInvocationHolder(annotationName, method.getParameterTypes()),
                methodAndObjectHolder);
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
    } catch (InvocationTargetException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private int getFunctionRegistryConstructorArgumentCount(final Class<?> clazz) {
    return Iterables.size(
            Iterables.filter(Arrays.asList(clazz.getConstructors()),
                    new Predicate<Constructor<?>>() {
              @Override
              public boolean apply(final Constructor<?> constructor) {
                return constructor.getParameterTypes().length == 1
                        && constructor.getParameterTypes()[0].equals(FunctionRegistry.class);
              }
            }));
  }

  private void checkMethodValidity(final Method method) {
    int stringClassesCount = Iterables.size(
        Iterables.filter(Arrays.asList(method.getParameterTypes()), new Predicate<Class<?>>() {
          @Override
          public boolean apply(final Class<?> aClass) {
            return aClass == String.class;
          }
        }));

    int stringArrayClassesCount = Iterables.size(
        Iterables.filter(Arrays.asList(method.getParameterTypes()), new Predicate<Class<?>>() {
          @Override
          public boolean apply(final Class<?> aClass) {
            return aClass == String[].class;
          }
        }));

    if (!String.class.isAssignableFrom(method.getReturnType())) {
      throw new IllegalArgumentException("method " + method.getName() + " on class "
          + method.getDeclaringClass().getName() + " must return type String");
    }

    if ((stringClassesCount != method.getParameterTypes().length
        && method.getParameterTypes().length > 1)
        || (stringArrayClassesCount != 1 && stringClassesCount == 0
            && method.getParameterTypes().length == 1)) {
      throw new IllegalArgumentException(
          "for method " + method.getName() + " on class " + method.getDeclaringClass().getName()
              + ": all method parameters need to be a String or a single"
              + " String var-arg parameter");
    }
  }

  private void checkClassValidity(final Class<?> clazz, final Function annotation) {
    if (annotation == null) {
      throw new IllegalArgumentException(
          clazz.getName() + " must be annotated with " + Function.class.getName());
    }

    for (String annotationName : annotation.name()) {
      if (isEmpty(annotationName)) {
        throw new IllegalArgumentException(Function.class.getName() + "annotation on class"
            + clazz.getName() + " annotation must have name attribute populated");
      }

      if (nonOverridableFunctionNames.contains(annotationName)) {
        throw new IllegalArgumentException(
            clazz.getName() + " can not override existing function with the same annotation: "
                + annotationName + " because it does not allow overriding.");
      }
    }

    int zeroArgConstructorCount = getZeroArgConstructorCount(clazz);

    int functionRegistryConstructorArgumentCount = getFunctionRegistryConstructorArgumentCount(
            clazz);

    if (zeroArgConstructorCount != 1 && functionRegistryConstructorArgumentCount != 1) {
      throw new IllegalArgumentException(clazz.getName() + " must have a no-arg constructor");
    }

    int validMethodCount = Iterables
        .size(Iterables.filter(Arrays.asList(clazz.getDeclaredMethods()), new Predicate<Method>() {
          @Override
          public boolean apply(final Method method) {
            return method.isAnnotationPresent(FunctionInvocation.class);
          }
        }));

    if (validMethodCount == 0) {
      throw new IllegalArgumentException(
          clazz.getName() + ": could not find any public methods annotated with "
              + FunctionInvocation.class.getName());
    }
  }

  private int getZeroArgConstructorCount(final Class<?> clazz) {
    return Iterables.size(
            Iterables.filter(Arrays.asList(clazz.getConstructors()),
                    new Predicate<Constructor<?>>() {
              @Override
              public boolean apply(final Constructor<?> constructor) {
                return constructor.getParameterTypes().length == 0;
              }
            }));
  }

  /**
   * execution a function based on the name of that function and it's argumetns
   *
   * @param functionName the function name
   * @param arguments the arguments
   * @return the result of the function
   * @throws InvocationTargetException if there is an issue running the function
   * @throws IllegalAccessException if there is a issue getting a hold of the method responsible for
   *         serving the function
   */
  public String executeFunction(final String functionName, final String... arguments)
      throws InvocationTargetException, IllegalAccessException {
    Method method = getMethod(functionName, arguments);
    return executeMethod(method, arguments);
  }

  private String executeMethod(final Method method, final String... arguments)
      throws InvocationTargetException, IllegalAccessException {
    Object instance = methodInstanceMap.get(method);
    if (method.getParameterTypes().length == 1
        && method.getParameterTypes()[0].equals(String[].class)) {
      return method.invoke(instance, new Object[] {arguments}).toString();
    } else {
      return method.invoke(instance, arguments).toString();
    }

  }

  /**
   * find a method based on name and it's arguments
   *
   * @param functionName name of functions
   * @param arguments arguments to call on function
   * @return the method found
   * @throws IllegalArgumentException if there is an issue grabbing the method
   */
  public Method getMethod(final String functionName, final String... arguments)
      throws IllegalArgumentException {
    final List<Class<?>> classList = new ArrayList<>();
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

  private MethodAndObjectHolder getHolder(final String functionName, final List<Class<?>> classList)
      throws IllegalAccessException {
    MethodAndObjectHolder holder = functionInvocationHolderMethodConcurrentHashMap.get(
        new FunctionInvocationHolder(functionName, classList.toArray(new Class[classList.size()])));

    if (holder == null) {
      holder = functionInvocationHolderMethodConcurrentHashMap
          .get(new FunctionInvocationHolder(functionName, new Class[] {String[].class}));
    }

    return holder;
  }


  /**
   * helper class thar associates methods (for functions) with the instance of the function class
   * that should be run
   */
  private static final class MethodAndObjectHolder {
    private final Method method;
    private final Object instance;

    private MethodAndObjectHolder(final Method method, final Object instance) {
      this.method = method;
      this.instance = instance;
    }

    public Method getMethod() {
      return method;
    }

    @SuppressWarnings("unused")
    public Object getInstance() {
      return instance;
    }
  }

  /**
   * helper class that holds function names and parameter types
   */
  private static final class FunctionInvocationHolder {
    private final String functionName;
    private final Class<?>[] parameterTypes;

    private FunctionInvocationHolder(final String functionName, final Class<?>[] parameterTypes) {
      notNull(functionName, "a function name must be provided");
      notNull(parameterTypes, "parameter types must be provided");
      this.functionName = functionName;
      this.parameterTypes = parameterTypes;
    }

    @SuppressWarnings("unused")
    public String getFunctionName() {
      return functionName;
    }

    @SuppressWarnings("unused")
    public Class<?>[] getParameterTypes() {
      return parameterTypes;
    }

    @Override
    public boolean equals(final Object obj) {
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
      return new EqualsBuilder().append(functionName, functionInvocationHolder.functionName)
          .append(parameterTypes, functionInvocationHolder.parameterTypes).isEquals();
    }

    @Override
    @SuppressWarnings("checkstyle:magicnumber")
    public int hashCode() {
      return new HashCodeBuilder(5, 33).append(functionName).append(parameterTypes).toHashCode();
    }
  }

}
