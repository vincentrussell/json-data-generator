package com.github.vincentrussell.json.datagenerator.functions.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import org.bitstrings.test.junit.runner.ClassLoaderPerTestRunner;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.vincentrussell.json.datagenerator.functions.FunctionRegistry;
import com.google.common.base.Splitter;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RunWith(ClassLoaderPerTestRunner.class)
public class DefaultFunctionsTest {

    private static TimeZone DEFAULT_TIMEZONE = TimeZone.getDefault();

    private FunctionRegistry functionRegistry;

    @BeforeClass
    public static void changeTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
    }

    @AfterClass
    public static void resetTimeZone() {
        TimeZone.setDefault(DEFAULT_TIMEZONE);
    }

    @Before
    public void registerClasses() {
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
    public void randomDoubleWithFormat() throws InvocationTargetException, IllegalAccessException {
        String[] args = new String[]{"23.124", "34.12", "%.2f"};
        String result = functionRegistry.executeFunction("double", args);
        Double doub = Double.valueOf(result);
        assertTrue(doub >= 23.124 && doub <= 34.12);
        assertDecimalPlaces(result, 2);
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
    public void uuidWithoutDashes() throws InvocationTargetException, IllegalAccessException {
        String result = functionRegistry.executeFunction("uuid", "false");
        Pattern pattern = Pattern.compile("[0-9a-f]{32}");
        assertTrue(pattern.matcher(result).matches());
    }

    @Test
    public void hexDefault16bytes() throws InvocationTargetException, IllegalAccessException {
        String result = functionRegistry.executeFunction("hex", null);
        Pattern pattern = Pattern.compile("[0-9a-f]{32}");
        assertTrue(pattern.matcher(result).matches());
    }

    @Test
    public void hex2bytes() throws InvocationTargetException, IllegalAccessException {
        String result = functionRegistry.executeFunction("hex", "2");
        Pattern pattern = Pattern.compile("[0-9a-f]{4}");
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
    public void boolWithChance() throws InvocationTargetException, IllegalAccessException {
        String result = functionRegistry.executeFunction("bool", "0.9");
        Pattern pattern = Pattern.compile("true|false");
        assertTrue(pattern.matcher(result).matches());
    }

    @Test
    public void index() throws InvocationTargetException, IllegalAccessException {
        assertEquals("0", functionRegistry.executeFunction("index", null));
        assertEquals("1", functionRegistry.executeFunction("index", null));
        assertEquals("2", functionRegistry.executeFunction("index", null));
    }

    @Test
    public void indexNamed() throws InvocationTargetException, IllegalAccessException {
        assertEquals("0", functionRegistry.executeFunction("index", "name1"));
        assertEquals("0", functionRegistry.executeFunction("index", "name2"));
        assertEquals("1", functionRegistry.executeFunction("index", "name1"));
        assertEquals("1", functionRegistry.executeFunction("index", "name2"));
        assertEquals("2", functionRegistry.executeFunction("index", "name1"));
        assertEquals("2", functionRegistry.executeFunction("index", "name2"));
    }

    @Test
    public void indexWithStartingPoint() throws InvocationTargetException, IllegalAccessException {
        assertEquals("45", functionRegistry.executeFunction("index", "45"));
        assertEquals("46", functionRegistry.executeFunction("index", "45"));
        assertEquals("47", functionRegistry.executeFunction("index", "45"));
    }

    @Test
    public void indexWithStartingPointIgnoreAdditionalChangesInNumber() throws InvocationTargetException, IllegalAccessException {
        assertEquals("45", functionRegistry.executeFunction("index", "45"));
        assertEquals("46", functionRegistry.executeFunction("index", "47"));
        assertEquals("47", functionRegistry.executeFunction("index", "49"));
    }

    @Test
    public void indexWithNameAndStartingPoint() throws InvocationTargetException, IllegalAccessException {
        assertEquals("45", functionRegistry.executeFunction("index", "name1", "45"));
        assertEquals("46", functionRegistry.executeFunction("index", "name1", "45"));
        assertEquals("47", functionRegistry.executeFunction("index", "name1", "45"));
    }

    @Test
    public void indexWithNameAndStartingPointIgnoreAdditionalChangesInNumber() throws InvocationTargetException, IllegalAccessException {
        assertEquals("45", functionRegistry.executeFunction("index", "name1", "45"));
        assertEquals("46", functionRegistry.executeFunction("index", "name1", "47"));
        assertEquals("47", functionRegistry.executeFunction("index", "name1", "49"));
    }

    @Test(expected = InvocationTargetException.class)
    public void resetIndexDefaultNotFound() throws InvocationTargetException, IllegalAccessException {
        functionRegistry.executeFunction("resetIndex");
    }

    @Test(expected = InvocationTargetException.class)
    public void resetIndexSpecifiedNameNotFound() throws InvocationTargetException, IllegalAccessException {
        functionRegistry.executeFunction("resetIndex", "name1");
    }

    @Test
    public void resetIndex() throws InvocationTargetException, IllegalAccessException {
        assertEquals("45", functionRegistry.executeFunction("index", "name1", "45"));
        assertEquals("46", functionRegistry.executeFunction("index", "name1", "45"));
        assertEquals("47", functionRegistry.executeFunction("index", "name1", "45"));
        assertEquals("", functionRegistry.executeFunction("resetIndex", "name1"));
        assertEquals("45", functionRegistry.executeFunction("index", "name1", "45"));
        assertEquals("46", functionRegistry.executeFunction("index", "name1", "45"));
        assertEquals("47", functionRegistry.executeFunction("index", "name1", "45"));
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
    public void concatWithBrackets() throws InvocationTargetException, IllegalAccessException {
        String[] args = new String[]{"{", "test", "}"};
        assertEquals("{test}", functionRegistry.executeFunction("concat", args));
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
    public void dateFormat() throws ParseException, InvocationTargetException, IllegalAccessException {
        String result = functionRegistry.executeFunction("dateFormat", "06-16-1956 12:00:00", "dd-MM-yyyy HH:mm:ss", "EEEEE dd MMMMM yyyy HH:mm:ss.SSSZ");
        assertEquals("Saturday 06 April 1957 12:00:00.000+0000", result);
    }


    @Test
    public void addDays() throws ParseException, InvocationTargetException, IllegalAccessException {
        addIntervalTest("dd-MM-yyyy HH:mm:ss", "12-12-2012 12:12:12", "addDays", 3, "15-12-2012 12:12:12");
    }

    @Test
    public void addDaysDefaultFormat() throws ParseException, InvocationTargetException, IllegalAccessException {
        addIntervalTest(null, "12-12-2012 12:12:12", "addDays", 3, "15-12-2012 12:12:12");
    }

    @Test
    public void addDaysNegative() throws ParseException, InvocationTargetException, IllegalAccessException {
        addIntervalTest("dd-MM-yyyy HH:mm:ss", "12-12-2012 12:12:12", "addDays", -3, "09-12-2012 12:12:12");
    }

    @Test
    public void addHours() throws ParseException, InvocationTargetException, IllegalAccessException {
        addIntervalTest("dd-MM-yyyy HH:mm:ss", "12-12-2012 12:12:12", "addHours", 3, "12-12-2012 15:12:12");
    }

    @Test
    public void addHoursDefaultFormat() throws ParseException, InvocationTargetException, IllegalAccessException {
        addIntervalTest(null, "12-12-2012 12:12:12", "addHours", 3, "12-12-2012 15:12:12");
    }

    @Test
    public void addHoursNegative() throws ParseException, InvocationTargetException, IllegalAccessException {
        addIntervalTest("dd-MM-yyyy HH:mm:ss", "12-12-2012 12:12:12", "addHours", -3, "12-12-2012 09:12:12");
    }

    @Test
    public void addMinutes() throws ParseException, InvocationTargetException, IllegalAccessException {
        addIntervalTest("dd-MM-yyyy HH:mm:ss", "12-12-2012 12:12:12", "addMinutes", 3, "12-12-2012 12:15:12");
    }

    @Test
    public void addMinutesDefaultFormat() throws ParseException, InvocationTargetException, IllegalAccessException {
        addIntervalTest(null, "12-12-2012 12:12:12", "addMinutes", 3, "12-12-2012 12:15:12");
    }

    @Test
    public void addMinutesNegative() throws ParseException, InvocationTargetException, IllegalAccessException {
        addIntervalTest("dd-MM-yyyy HH:mm:ss", "12-12-2012 12:12:12", "addMinutes", -3, "12-12-2012 12:09:12");
    }

    @Test
    public void addMonths() throws ParseException, InvocationTargetException, IllegalAccessException {
        addIntervalTest("dd-MM-yyyy HH:mm:ss", "12-12-2012 12:12:12", "addMonths", 3, "12-03-2013 12:12:12");
    }

    @Test
    public void addMonthsDefaultFormat() throws ParseException, InvocationTargetException, IllegalAccessException {
        addIntervalTest(null, "12-12-2012 12:12:12", "addMonths", 3, "12-03-2013 12:12:12");
    }

    @Test
    public void addMonthsNegative() throws ParseException, InvocationTargetException, IllegalAccessException {
        addIntervalTest("dd-MM-yyyy HH:mm:ss", "12-12-2012 12:12:12", "addMonths", -3, "12-09-2012 12:12:12");
    }

    @Test
    public void addSeconds() throws ParseException, InvocationTargetException, IllegalAccessException {
        addIntervalTest("dd-MM-yyyy HH:mm:ss", "12-12-2012 12:12:12", "addSeconds", 3, "12-12-2012 12:12:15");
    }

    @Test
    public void addSecondsDefaultFormat() throws ParseException, InvocationTargetException, IllegalAccessException {
        addIntervalTest(null, "12-12-2012 12:12:12", "addSeconds", 3, "12-12-2012 12:12:15");
    }

    @Test
    public void addSecondsNegative() throws ParseException, InvocationTargetException, IllegalAccessException {
        addIntervalTest("dd-MM-yyyy HH:mm:ss", "12-12-2012 12:12:12", "addSeconds", -3, "12-12-2012 12:12:09");
    }

    @Test
    public void addWeeks() throws ParseException, InvocationTargetException, IllegalAccessException {
        addIntervalTest("dd-MM-yyyy HH:mm:ss", "12-12-2012 12:12:12", "addWeeks", 3, "02-01-2013 12:12:12");
    }

    @Test
    public void addWeeksDefaultFormat() throws ParseException, InvocationTargetException, IllegalAccessException {
        addIntervalTest(null, "12-12-2012 12:12:12", "addWeeks", 3, "02-01-2013 12:12:12");
    }

    @Test
    public void addWeeksNegative() throws ParseException, InvocationTargetException, IllegalAccessException {
        addIntervalTest("dd-MM-yyyy HH:mm:ss", "12-12-2012 12:12:12", "addWeeks", -3, "21-11-2012 12:12:12");
    }

    @Test
    public void addYears() throws ParseException, InvocationTargetException, IllegalAccessException {
        addIntervalTest("dd-MM-yyyy HH:mm:ss", "12-12-2012 12:12:12", "addYears", 3, "12-12-2015 12:12:12");
    }

    @Test
    public void addYearsDefaultFormat() throws ParseException, InvocationTargetException, IllegalAccessException {
        addIntervalTest(null, "12-12-2012 12:12:12", "addYears", 3, "12-12-2015 12:12:12");
    }

    @Test
    public void addYearsNegative() throws ParseException, InvocationTargetException, IllegalAccessException {
        addIntervalTest("dd-MM-yyyy HH:mm:ss", "12-12-2012 12:12:12", "addYears", -3, "12-12-2009 12:12:12");
    }

    private void addIntervalTest(String dateFormatText, String date, String function, Integer interval, String expected) throws ParseException, InvocationTargetException, IllegalAccessException {
        List<String> args = new ArrayList<>();

        if (dateFormatText != null) {
            args.add(dateFormatText);
        }

        args.addAll(Lists.newArrayList(date, interval.toString()));
        String result = functionRegistry.executeFunction(function, args.toArray(new String[args.size()]));
        assertEquals(expected, result);
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

    private void assertFunctionRandomFromField(String functionName, Class<?> clazz, String... fieldNames) throws NoSuchFieldException, IllegalAccessException, InvocationTargetException {
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
    public void emailWithDomain() throws InvocationTargetException, IllegalAccessException {
        String result = functionRegistry.executeFunction("email", "github.com");
        Pattern pattern = Pattern.compile("\\w+\\.\\w+\\@\\w+\\.com");
        assertTrue(pattern.matcher(result).matches());
        assertTrue(result.endsWith("@github.com"));
    }

    @Test
    public void username() throws InvocationTargetException, IllegalAccessException {
        String result = functionRegistry.executeFunction("username", null);
        assertTrue(result.length() > 1);
        assertLowercase(result);
    }

    private void assertLowercase(String result) {
        assertTrue(result.equals(result.toLowerCase()));
    }

    @Test
    public void random() throws InvocationTargetException, IllegalAccessException {
        String[] args = new String[]{"A", "B", "C"};
        String result = functionRegistry.executeFunction("random", args);
        assertTrue(Arrays.asList(args).indexOf(result) > -1);
    }

    @Test
    public void alphaWithMinAndMax() throws InvocationTargetException, IllegalAccessException {
        String result = functionRegistry.executeFunction("alpha", new String[]{"10", "20"});
        int size = result.length();
        assertTrue(size >= 10 && size <= 20);
        Pattern pattern = Pattern.compile("[a-zA-Z]{10,20}");
        assertTrue(pattern.matcher(result).matches());
    }

    @Test
    public void alphaWithLength() throws InvocationTargetException, IllegalAccessException {
        String result = functionRegistry.executeFunction("alpha", new String[]{"50"});
        int size = result.length();
        assertEquals(50,size);
        Pattern pattern = Pattern.compile("[a-zA-Z]{50}");
        assertTrue(pattern.matcher(result).matches());
    }

    @Test
    public void alpha() throws InvocationTargetException, IllegalAccessException {
        String result = functionRegistry.executeFunction("alpha");
        int size = result.length();
        assertTrue(size >= 10 && size <= 20);
        Pattern pattern = Pattern.compile("[a-zA-Z]{10,20}");
        assertTrue(pattern.matcher(result).matches());
    }

    @Test
    public void alphaNumericWithMinAndMax() throws InvocationTargetException, IllegalAccessException {
        String result = functionRegistry.executeFunction("alphaNumeric", new String[]{"10", "20"});
        int size = result.length();
        assertTrue(size >= 10 && size <= 20);
        Pattern pattern = Pattern.compile("[0-9a-zA-Z]{10,20}");
        assertTrue(pattern.matcher(result).matches());
    }

    @Test
    public void alphaNumericWithLength() throws InvocationTargetException, IllegalAccessException {
        String result = functionRegistry.executeFunction("alphaNumeric", new String[]{"50"});
        int size = result.length();
        assertEquals(50,size);
        Pattern pattern = Pattern.compile("[0-9a-zA-Z]{50}");
        assertTrue(pattern.matcher(result).matches());
    }

    @Test
    public void alphaNumeric() throws InvocationTargetException, IllegalAccessException {
        String result = functionRegistry.executeFunction("alphaNumeric");
        int size = result.length();
        assertTrue(size >= 10 && size <= 20);
        Pattern pattern = Pattern.compile("[0-9a-zA-Z]{10,20}");
        assertTrue(pattern.matcher(result).matches());
    }

	@Test
    public void countriesListWillAllCountry() throws InvocationTargetException, IllegalAccessException {
    	String result = functionRegistry.executeFunction("countriesList");
    	
		assertTrue(result.contains("\"AF\": \"Afghanistan\", \"AL\": \"Albania\""));
		assertTrue(result.startsWith("{"));
		assertTrue(result.endsWith("}"));
    }

	@Test
	public void countriesListWithSpecificCode() throws InvocationTargetException, IllegalAccessException {
		String result = functionRegistry.executeFunction("countriesList", "IN", "US", "UK");

		JsonObject jsonObject = new JsonParser().parse(result).getAsJsonObject();
		assertTrue(jsonObject.get("IN").getAsString().equals("India"));
		assertTrue(jsonObject.get("US").getAsString().equals("United States"));
		assertTrue(jsonObject.get("UK").getAsString().equals("United Kingdom"));
		assertNull(jsonObject.get("AF"));
	}

	@Test
	public void countriesListWithOneSpecificCode() throws InvocationTargetException, IllegalAccessException {
		String result = functionRegistry.executeFunction("countriesList", "IN");

		assertTrue(result.equals("India"));
	}

  @Test
  public void putAndGet() throws InvocationTargetException, IllegalAccessException {
      assertEquals("value", functionRegistry.executeFunction("put", "key", "value"));
      assertEquals("value", functionRegistry.executeFunction("get", "key"));
      assertEquals("value2", functionRegistry.executeFunction("put", "key", "value2"));
      assertEquals("value2", functionRegistry.executeFunction("get", "key"));
  }

    @Test
    public void regexify() throws InvocationTargetException, IllegalAccessException {
        String regex = "[a-z1-9]{10}";
        String[] args = new String[]{regex};
        String result = functionRegistry.executeFunction("regexify", args);
        Matcher alphaNumericMatcher = Pattern.compile(regex).matcher(result);
        assertTrue(String.format("%s doesn't match regex: %s", result, regex),alphaNumericMatcher.find());
    }

    @Test
    public void regexifyWithLocale() throws InvocationTargetException, IllegalAccessException {
        String regex = "[a-z1-9]{10}";
        String[] args = new String[]{"en-GB", regex};
        String result = functionRegistry.executeFunction("regexify", args);
        Matcher alphaNumericMatcher = Pattern.compile(regex).matcher(result);
        assertTrue(alphaNumericMatcher.find());
    }

    private List<String> getArrayAsListFromStaticField(Class<?> clazz, String fieldName) throws IllegalAccessException, NoSuchFieldException {
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        String[] values = (String[]) field.get(null);
        return Arrays.asList(values);
    }

}
