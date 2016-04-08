package com.github.vincentrussell.json.datagenerator.functions.impl;

import com.github.vincentrussell.json.datagenerator.functions.FunctionRegistry;
import com.github.vincentrussell.json.datagenerator.functions.ObjectRegistry;
import com.github.vincentrussell.json.datagenerator.impl.IndexHolder;
import com.google.common.base.Splitter;
import org.bitstrings.test.junit.runner.ClassLoaderPerTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

@RunWith(ClassLoaderPerTestRunner.class)
public class DefaultFunctionsTest {

    FunctionRegistry functionRegistry;
    ObjectRegistry objectRegistry;

    @Before
    public void registerClasses() {
        objectRegistry = ObjectRegistry.getInstance();
        functionRegistry = FunctionRegistry.getInstance();
    }

    @Test
    public void randomInteger() throws InvocationTargetException, IllegalAccessException {
        String[] args = new String[]{"10", "20"};
        Integer integer = Integer.valueOf(functionRegistry.executeFunction("integer", args));
        assertTrue(integer >= 10 && integer <= 20);
    }

    @Test
    public void randomDouble() throws InvocationTargetException, IllegalAccessException {
        String[] args = new String[]{"23.124", "34.12"};
        Double doub = Double.valueOf(functionRegistry.executeFunction("double", args));
        assertTrue(doub >= 23.124 && doub <= 34.12);
    }

    @Test
    public void randomFloat() throws InvocationTargetException, IllegalAccessException {
        String[] args = new String[]{"0.90310", "1.3421"};
        Float flo = Float.valueOf(functionRegistry.executeFunction("float", args));
        assertTrue(flo >= 0.90310 && flo <= 1.3421);
    }

    @Test
    public void randomFloatNegativeNumbers() throws InvocationTargetException, IllegalAccessException {
        String[] args = new String[]{"-90.000001", "90"};
        Float flo = Float.valueOf(functionRegistry.executeFunction("float", args));
        assertTrue(flo >= -90.000001 && flo <= 90);
    }

    @Test
    public void randomFloating() throws InvocationTargetException, IllegalAccessException {
        String[] args = new String[]{"0.90310", "1.3421"};
        Float flo = Float.valueOf(functionRegistry.executeFunction("floating", args));
        assertTrue(flo >= 0.90310 && flo <= 1.3421);
    }

    @Test
    public void randomFloatWithFormat() throws InvocationTargetException, IllegalAccessException {
        String[] args = new String[]{"0.90310", "1.3421", "%.2f"};
        String result = functionRegistry.executeFunction("float", args);
        Float flo = Float.valueOf(result);
        assertTrue(flo >= 0.90310 && flo <= 1.3421);
        assertDecimalPlaces(result, 2);
    }

    private void assertDecimalPlaces(String text, int decimalPlaces) {
        int integerPlaces = text.indexOf('.');
        assertEquals(decimalPlaces, text.length() - integerPlaces - 1);
    }

    @Test
    public void randomLong() throws InvocationTargetException, IllegalAccessException {
        String[] args = new String[]{"90210", "1342534634646"};
        Long lon = Long.valueOf(functionRegistry.executeFunction("long", args));
        assertTrue(lon >= 90210 && lon <= 1342534634646L);
    }

    @Test
    public void uuid() throws InvocationTargetException, IllegalAccessException {
        String result = functionRegistry.executeFunction("uuid", null);
        Pattern pattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
        assertTrue(pattern.matcher(result).matches());
    }

    @Test
    public void guid() throws InvocationTargetException, IllegalAccessException {
        String result = functionRegistry.executeFunction("guid", null);
        Pattern pattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
        assertTrue(pattern.matcher(result).matches());
    }

    @Test
    public void objectId() throws InvocationTargetException, IllegalAccessException {
        String result = functionRegistry.executeFunction("objectId", null);
        Pattern pattern = Pattern.compile("[0-9a-f]{24}");
        assertTrue(pattern.matcher(result).matches());
    }

    @Test
    public void bool() throws InvocationTargetException, IllegalAccessException {
        String result = functionRegistry.executeFunction("bool", null);
        Pattern pattern = Pattern.compile("true|false");
        assertTrue(pattern.matcher(result).matches());
    }

    @Test
    public void index() throws InvocationTargetException, IllegalAccessException {
        IndexHolder indexHolder = new IndexHolder();
        objectRegistry.register(IndexHolder.class, indexHolder);
        assertEquals("0", functionRegistry.executeFunction("index", null));
        assertEquals("1", functionRegistry.executeFunction("index", null));
        assertEquals("2", functionRegistry.executeFunction("index", null));
    }

    @Test
    public void loremWords() throws InvocationTargetException, IllegalAccessException {
        String[] args = new String[]{"3", "words"};
        String result = functionRegistry.executeFunction("lorem", args);
        assertTrue(result.startsWith("lorem ipsum"));
    }

    @Test
    public void loremParagraphs() throws InvocationTargetException, IllegalAccessException {
        String[] args = new String[]{"3", "paragraphs"};
        String result = functionRegistry.executeFunction("lorem", args);
        assertEquals(5, Splitter.on("\n").splitToList(result).size());
        assertEquals(4, Splitter.on("\t").splitToList(result).size());
    }

    @Test
    public void concat() throws InvocationTargetException, IllegalAccessException {
        String[] args = new String[]{"one", "two", "three"};
        assertEquals("onetwothree", functionRegistry.executeFunction("concat", args));
    }

    @Test
    public void toUpper() throws InvocationTargetException, IllegalAccessException {
        String[] args = new String[]{"one"};
        assertEquals("ONE", functionRegistry.executeFunction("toUpperCase", args));
    }

    @Test
    public void toLower() throws InvocationTargetException, IllegalAccessException {
        String[] args = new String[]{"ONE"};
        assertEquals("one", functionRegistry.executeFunction("toLowerCase", args));
    }

    @Test
    public void substring() throws InvocationTargetException, IllegalAccessException {
        assertEquals("d", functionRegistry.executeFunction("substring", "word", "3"));
        assertEquals("word", functionRegistry.executeFunction("substring", "word", "0"));
        assertEquals("ong w", functionRegistry.executeFunction("substring", "long word", "1", "6"));
    }

    @Test
    public void phone() throws InvocationTargetException, IllegalAccessException {
        String result = functionRegistry.executeFunction("phone", null);
        Pattern pattern = Pattern.compile("[0-9]{3}-[0-9]{3}-[0-9]{4}");
        assertTrue(pattern.matcher(result).matches());
    }

    @Test
    public void ssn() throws InvocationTargetException, IllegalAccessException {
        String result = functionRegistry.executeFunction("ssn", null);
        Pattern pattern = Pattern.compile("[0-9]{3}-[0-9]{2}-[0-9]{4}");
        assertTrue(pattern.matcher(result).matches());
    }

    @Test
    public void ipv4() throws InvocationTargetException, IllegalAccessException {
        String result = functionRegistry.executeFunction("ipv4", null);
        Pattern pattern = Pattern.compile("[0-9]+\\.[0-9]+\\.[0-9]+\\.[0-9]+");
        assertTrue(pattern.matcher(result).matches());
    }

    @Test
    public void ipv6Lower() throws InvocationTargetException, IllegalAccessException {
        String result = functionRegistry.executeFunction("ipv6", "lower");
        Pattern pattern = Pattern.compile("^(?:[0-9a-f]{1,4}:){7}[0-9a-f]{1,4}$");
        assertTrue(pattern.matcher(result).matches());
    }

    @Test
    public void ipv6DefaultLower() throws InvocationTargetException, IllegalAccessException {
        String result = functionRegistry.executeFunction("ipv6", null);
        Pattern pattern = Pattern.compile("^(?:[0-9a-f]{1,4}:){7}[0-9a-f]{1,4}$");
        assertTrue(pattern.matcher(result).matches());
    }

    @Test
    public void ipv6Upper() throws InvocationTargetException, IllegalAccessException {
        String result = functionRegistry.executeFunction("ipv6", "upper");
        Pattern pattern = Pattern.compile("^(?:[0-9A-F]{1,4}:){7}[0-9A-F]{1,4}$");
        assertTrue(pattern.matcher(result).matches());
    }

    @Test
    public void gender() throws InvocationTargetException, IllegalAccessException {
        String result = functionRegistry.executeFunction("gender", null);
        Pattern pattern = Pattern.compile("male|female");
        assertTrue(pattern.matcher(result).matches());
    }


    @Test
    public void date() throws InvocationTargetException, IllegalAccessException, ParseException {
        String result = functionRegistry.executeFunction("date", null);
        DateFormat dateFormat = new SimpleDateFormat(Date.DEFAULT_DATE_STRING);
        java.util.Date date = dateFormat.parse(result);
        assertNotNull(date);
    }

    @Test
    public void dateWithFormat() throws ParseException, InvocationTargetException, IllegalAccessException {
        String dateFormatText = "MMM yyyy HH:mm:ss z";
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatText);
        String result = functionRegistry.executeFunction("date", dateFormatText);
        java.util.Date date = dateFormat.parse(result);
        assertNotNull(date);
    }

    @Test
    public void dateWithinRange() throws ParseException, InvocationTargetException, IllegalAccessException {
        String dateFormatText = "EEE, d MMM yyyy HH:mm:ss z";
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatText);
        String result = functionRegistry.executeFunction("date", "06-16-1956 12:00:00", "06-16-1975 12:00:00");
        java.util.Date date = dateFormat.parse(result);
        assertNotNull(date);
    }

    @Test
    public void dateWithinRangeWithFormat() throws ParseException, InvocationTargetException, IllegalAccessException {
        String dateFormatText = "MMM yyyy HH:mm:ss z";
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatText);
        String result = functionRegistry.executeFunction("date", "06-16-1956 12:00:00", "06-16-1975 12:00:00", dateFormatText);
        java.util.Date date = dateFormat.parse(result);
        assertNotNull(date);
    }

    @Test
    public void timestamp() throws InvocationTargetException, IllegalAccessException, ParseException {
        String result = functionRegistry.executeFunction("timestamp", null);
        java.util.Date date = new java.util.Date();
        date.setTime(Long.parseLong(result));
        assertNotNull(date);
        assertTrue(Long.parseLong(result) > 0);
    }

    @Test
    public void timestampWithinRangeWithFormat() throws ParseException, InvocationTargetException, IllegalAccessException {
        String result = functionRegistry.executeFunction("timestamp", "06-16-2012 12:00:00", "06-16-2015 12:00:00");
        java.util.Date date = new java.util.Date();
        date.setTime(Long.parseLong(result));
        assertNotNull(date);
        assertTrue(Long.parseLong(result) > 0);
    }

    @Test
    public void city() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        assertFunctionRandomFromField("city", City.class, "CITIES");
    }

    private void assertFunctionRandomFromField(String functionName, Class clazz, String... fieldNames) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        String result = functionRegistry.executeFunction(functionName, null);
        List<String> values = new ArrayList<String>();
        for (String fieldName : fieldNames) {
            values.addAll(getArrayAsListFromStaticField(clazz, fieldName));
        }
        assertTrue(values.contains(result));
    }

    @Test
    public void state() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        assertFunctionRandomFromField("state", State.class, "STATES");
    }

    @Test
    public void country() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        assertFunctionRandomFromField("country", Country.class, "COUNTRIES");
    }

    @Test
    public void company() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        assertFunctionRandomFromField("company", Company.class, "COMPANIES");
    }

    @Test
    public void firstName() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        assertFunctionRandomFromField("firstName", FirstName.class, "MALE_FIRST_NAMES", "FEMALE_FIRST_NAMES");
    }

    @Test
    public void lastName() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        assertFunctionRandomFromField("lastName", LastName.class, "LAST_NAMES");
    }

    @Test
    public void surname() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        assertFunctionRandomFromField("surname", LastName.class, "LAST_NAMES");
    }

    @Test
    public void street() throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        assertFunctionRandomFromField("street", Street.class, "STREETS");
    }

    @Test
    public void email() throws InvocationTargetException, IllegalAccessException {
        String result = functionRegistry.executeFunction("email", null);
        Pattern pattern = Pattern.compile("\\w+\\.\\w+\\@\\w+\\.com");
        assertTrue(pattern.matcher(result).matches());
    }

    @Test
    public void random() throws InvocationTargetException, IllegalAccessException {
        String[] args = new String[]{"A", "B", "C"};
        String result = functionRegistry.executeFunction("random", args);
        assertTrue(Arrays.asList(args).indexOf(result) > -1);
    }

    private List<String> getArrayAsListFromStaticField(Class clazz, String fieldName) throws IllegalAccessException, NoSuchFieldException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        String[] values = (String[]) field.get(null);
        return Arrays.asList(values);
    }

}
