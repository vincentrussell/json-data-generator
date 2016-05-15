package com.github.vincentrussell.json.datagenerator.functions;

import org.bitstrings.test.junit.runner.ClassLoaderPerTestRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(ClassLoaderPerTestRunner.class)
public class FunctionRegistryTest {

    @Rule
    public ExpectedException thrownException = ExpectedException.none();

    FunctionRegistry functionRegistry;

    @Before
    public void loadFunctionRegistry() {
        functionRegistry = FunctionRegistry.getInstance();
    }

    @Test
    public void registerFunctionClassNotAnnotated() {
        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(Object.class.getName() + " must be annotated with " + Function.class.getName());
        functionRegistry.registerClass(Object.class);
    }

    @Test
    public void registerFunctionClassAnnotatedWithoutName() {
        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(TestFunctionClazzWithoutName.class.getName() + " annotation must have name attribute populated");
        functionRegistry.registerClass(TestFunctionClazzWithoutName.class);
    }

    @Test
    public void registerFunctionClassAnnotatedWithoutFunctionInvocation() {
        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage("could not find any public methods annotated with " + FunctionInvocation.class.getName());
        functionRegistry.registerClass(TestFunctionClazzWithoutFunctionInvocation.class);
    }

    @Test
    public void registerFunctionClassAnnotatedWithFunctionInvocationAndExecuteMethod() throws InvocationTargetException, IllegalAccessException {
        functionRegistry.registerClass(TestFunctionClazzWithFunctionInvocation.class);
        String[] args = new String[]{"arg1", "arg2"};
        String[] args2 = new String[]{"arg1", "arg2", "arg3", "arg4"};
        assertEquals("arg1=arg1,arg2=arg2", functionRegistry.executeFunction("function", args));
        assertEquals("varargs.length=4", functionRegistry.executeFunction("function", args2));
    }

    @Test
    public void multipleRegisterFunctionWithMultipleNames() throws InvocationTargetException, IllegalAccessException {
        functionRegistry.registerClass(MultipleNamesFunction.class);
        assertEquals("some value", functionRegistry.executeFunction("function8", null));
        assertEquals("some value", functionRegistry.executeFunction("function8-copy", null));
    }

    @Test
    public void registerFunctionAndGetMethod() throws InvocationTargetException, IllegalAccessException {
        functionRegistry.registerClass(TestFunctionClazzWithFunctionInvocation.class);
        String[] args = new String[]{"arg1", "arg2"};
        String[] args2 = new String[]{"arg1", "arg2", "arg3", "arg4"};
        assertNotNull(functionRegistry.getMethod("function", args));
        assertNotNull(functionRegistry.getMethod("function", args2));
    }

    @Test
    public void registerFunctionClassAnnotatedWithFunctionInvocationAndExecuteFunctionByName() throws InvocationTargetException, IllegalAccessException {
        functionRegistry.registerClass(TestFunctionClazzWithFunctionInvocation.class);
        String[] args = new String[]{"arg1", "arg2"};
        String[] args2 = new String[]{"arg1", "arg2", "arg3", "arg4"};
        assertEquals("arg1=arg1,arg2=arg2", functionRegistry.executeFunction("function", args));
        assertEquals("varargs.length=4", functionRegistry.executeFunction("function", args2));
    }

    @Test
    public void registerFunctionClassAnnotatedWithFunctionInvocationAndExecuteMethodMethodNotFound() {
        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage("could not find method to invoke.");
        functionRegistry.registerClass(TestFunctionClazzWithFunctionInvocation.class);
        functionRegistry.getMethod("notFound", "arg1", "arg2");
    }

    @Test
    public void registerFunctionClassAnnotatedWithFunctionInvocationWithoutNoArgConstructor() {
        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(TestFunctionClazzWithFunctionInvocationWithoutNoArgConstructor.class.getName() + " must have a no-arg constructor");
        functionRegistry.registerClass(TestFunctionClazzWithFunctionInvocationWithoutNoArgConstructor.class);
    }


    @Test
    public void registerFunctionClassAnnotatedWithoutFunctionInvocationArgumentsNotStrings() {
        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage("method parameters need to be a String or a single String var-arg parameter");
        functionRegistry.registerClass(TestFunctionClazzWithFunctionInvocationNonStrings.class);
    }

    @Test
    public void registerFunctionClassAnnotatedWithoutFunctionInvocationArgumentsNotStrings2() {
        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage("method parameters need to be a String or a single String var-arg parameter");
        functionRegistry.registerClass(TestFunctionClazzWithFunctionInvocationNonStrings2.class);
    }

    @Test
    public void registerFunctionClassAnnotatedWithoutFunctionInvocationArgumentsOneNotString() {
        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage("method parameters need to be a String or a single String var-arg parameter");
        functionRegistry.registerClass(TestFunctionClazzWithFunctionInvocationOneNotString.class);
    }

    @Test
    public void registerFunctionClassAnnotationThatDoestReturnString() {
        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage("method invocation on class " + TestFunctionClazzMethodDoesntReturnString.class.getName() + " must return type String");
        functionRegistry.registerClass(TestFunctionClazzMethodDoesntReturnString.class);
    }

    @Test
    public void registerFunctionClassAnnotatedWithFunctionMethodWithNoArgs() throws InvocationTargetException, IllegalAccessException {
        functionRegistry.registerClass(TestFunctionClazzWithNoArgsMethod.class);
        assertEquals("no args", functionRegistry.executeFunction("function7", null));
        assertEquals("no args", functionRegistry.executeFunction("function7"));
    }

    @Test
    public void nonOverridableFunction() throws InvocationTargetException, IllegalAccessException {
        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(TestNonOverrideableFunction2.class.getName() + " can not override existing function with the same annotation: function1 because it does not allow overriding.");
        functionRegistry.registerClass(TestNonOverrideableFunction1.class);
        functionRegistry.registerClass(TestNonOverrideableFunction2.class);
    }


    @Function
    public static class TestFunctionClazzWithoutName {

    }

    @Function(name = "function")
    public static class TestFunctionClazzWithFunctionInvocation {

        @FunctionInvocation
        public String invocation(String arg1, String arg2) {
            return "arg1=" + arg1 + ",arg2=" + arg2;
        }

        @FunctionInvocation
        public String invocation(String[] args) {
            return "varargs.length=" + args.length;
        }

    }

    @Function(name = "function2")
    public static class TestFunctionClazzWithoutFunctionInvocation {

    }

    @Function(name = "function3")
    public static class TestFunctionClazzWithFunctionInvocationWithoutNoArgConstructor {

        public TestFunctionClazzWithFunctionInvocationWithoutNoArgConstructor(Object object) {
        }

        @FunctionInvocation
        public String invocation(String arg1, String arg2) {
            return "value";
        }


    }

    @Function(name = "function4")
    public static class TestFunctionClazzWithFunctionInvocationNonStrings {

        @FunctionInvocation
        public String invocation(Object arg1, Object arg2) {
            return "arg1=" + arg1 + ",arg2=" + arg2;
        }

    }

    @Function(name = "function5")
    public static class TestFunctionClazzWithFunctionInvocationNonStrings2 {

        @FunctionInvocation
        public String invocation(Object[] args) {
            return "varargs.length=" + args.length;
        }

    }

    @Function(name = "function7")
    public static class TestFunctionClazzWithNoArgsMethod {

        @FunctionInvocation
        public String invocation() {
            return "no args";
        }


    }

    @Function(name = "function8")
    public static class TestFunctionClazzMethodDoesntReturnString {

        @FunctionInvocation
        public Object invocation() {
            return "no args";
        }


    }

    @Function(name = "function9")
    public static class TestFunctionClazzWithFunctionInvocationOneNotString {

        @FunctionInvocation
        public String invocation(String arg1, Object arg2) {
            return "no args";
        }


    }

    @Function(name = "function1", overridable = false)
    public static class TestNonOverrideableFunction1 {

        @FunctionInvocation
        public String invocation(String arg1) {
            return "no args";
        }

    }

    @Function(name = "function1")
    public static class TestNonOverrideableFunction2 {

        @FunctionInvocation
        public String invocation(String arg1) {
            return "no args";
        }


    }

    @Function(name = {"function8", "function8-copy"})
    public static class MultipleNamesFunction {

        @FunctionInvocation
        public String invocation() {
            return "some value";
        }


    }


}
