package com.github.vincentrussell.json.datagenerator.functions;

import org.bitstrings.test.junit.runner.ClassLoaderPerTestRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith( ClassLoaderPerTestRunner.class )
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
        thrownException.expectMessage("class must be annotated with " + Function.class);
        functionRegistry.registerClass(Object.class);
    }

    @Test
    public void registerFunctionClassAnnotatedWithoutName() {
        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage(Function.class + " annotation must have name attribute populated");
        functionRegistry.registerClass(TestFunctionClazzWithoutName.class);
    }

    @Test
    public void registerFunctionClassAnnotatedWithoutFunctionInvocation() {
        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage("could not find any public methods annotated with " + FunctionInvocation.class);
        functionRegistry.registerClass(TestFunctionClazzWithoutFunctionInvocation.class);
    }

    @Test
    public void registerFunctionClassAnnotatedWithFunctionInvocationAndExecuteMethod() {
        functionRegistry.registerClass(TestFunctionClazzWithFunctionInvocation.class);
        assertEquals("arg1=arg1,arg2=arg2",functionRegistry.execute("function","arg1","arg2"));
        assertEquals("varargs.length=4",functionRegistry.execute("function","arg1","arg2","arg3","arg4"));
    }

    @Test
    public void registerFunctionClassAnnotatedWithFunctionInvocationAndExecuteMethodMethodNotFound() {
        thrownException.expect(IllegalArgumentException.class);
        thrownException.expectMessage("could not find method to invoke.");
        functionRegistry.registerClass(TestFunctionClazzWithFunctionInvocation.class);
        functionRegistry.execute("notFound","arg1","arg2");
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

    @Function
    public static class TestFunctionClazzWithoutName {

    }

    @Function(name = "function")
    public static class TestFunctionClazzWithFunctionInvocation {

        @FunctionInvocation
        public String invocation(String arg1, String arg2) {
            return "arg1=" + arg1 + ",arg2=" +  arg2 ;
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

        public TestFunctionClazzWithFunctionInvocationWithoutNoArgConstructor(Object object) {}

        @FunctionInvocation
        public String invocation(String arg1, String arg2) {
            return "value";
        }


    }

    @Function(name = "function4")
    public static class TestFunctionClazzWithFunctionInvocationNonStrings {

        @FunctionInvocation
        public String invocation(Object arg1, Object arg2) {
            return "arg1=" + arg1 + ",arg2=" +  arg2 ;
        }

    }

    @Function(name = "function4")
    public static class TestFunctionClazzWithFunctionInvocationNonStrings2 {

        @FunctionInvocation
        public String invocation(Object[] args) {
            return "varargs.length=" + args.length;
        }

    }


}
