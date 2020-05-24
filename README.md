# json-data-generator [![Maven Central](https://img.shields.io/maven-central/v/com.github.vincentrussell/json-data-generator.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.vincentrussell%22%20AND%20a:%22json-data-generator%22) [![Build Status](https://travis-ci.org/vincentrussell/json-data-generator.svg?branch=master)](https://travis-ci.org/vincentrussell/json-data-generator)

json-data-generator helps you build json data that you need for test data purposes.   It has a lot of nice features that you can use to build as much test data as you need.

## Maven

Add a dependency to `com.github.vincentrussell:json-data-generator`. 

```
<dependency>
   <groupId>com.github.vincentrussell</groupId>
   <artifactId>json-data-generator</artifactId>
   <version>1.9</version>
</dependency>
```

## Requirements
- JDK 1.8 or higher
- Apache Maven 3.1.0 or higher

## Running it from Java

```
JsonDataGeneratorImpl parser = new JsonDataGeneratorImpl();
parser.generateTestDataJson(String text, OutputStream outputStream);
```

or

```
JsonDataGeneratorImpl parser = new JsonDataGeneratorImpl();
parser.generateTestDataJson(URL classPathResource, OutputStream outputStream);
```

or

```
JsonDataGeneratorImpl parser = new JsonDataGeneratorImpl();
parser.generateTestDataJson(File file, OutputStream outputStream);
```

or

```
JsonDataGeneratorImpl parser = new JsonDataGeneratorImpl();
parser.generateTestDataJson(InputStream inputStream, OutputStream outputStream);
```

## Running it as a standalone jar

```
java -jar json-data-generator-1.9-standalone.jar -s source.json -d destination.json
```
### Options

```
usage: [-s <arg>] [-d
       <arg>] [-f <arg>] [-i] [-t <arg>]
 -s,--sourceFile <arg>        the source file.
 -d,--destinationFile <arg>   the destination file.  Defaults to
                              System.out
 -f,--functionClasses <arg>   additional function classes that are on the
                              classpath and should be loaded
 -i,--interactiveMode         interactive mode
 -t,--timeZone <arg>          default time zone to use when dealing with dates
```


### Example

```
{
    "id": "{{uuid()}}",
    "name": "A green door",
    "age": {{integer(1,50)}},
    "price": 12.50,
    "tags": ["home", "green"]
}
```

### Repeats
You can repeat sections of json to make repeating json objects.

For instance you can do repeats like this:
```
[
    '{{repeat(2)}}',
    {
        "id": "{{uuid()}}",
        "name": "Test",
        "attributes": [
            '{{repeat(2)}}',
            {
                "shirt": "{{random("red","yellow","green")}}",
                "pants": "{{random("blue","black","orange")}}"
            }
        ]

    }
]
```

You can also get a random repeat within a range like this: 

```
'{{repeat(2,9)}}',
```

## Available functions

You can do a lot of cool functions in your puesdo json that that help you randomize your test data.

random integer in range:
```
{{integer(min,max)}}
```

random float in range (with optional format):
```
{{float(min,max)}}
```

```
{{float(min,max,"%.2f")}}
```

random double in range (with optional format):
```
{{double(min,max)}}
```

```
{{double(min,max,"%.2f")}}
```

random long in range:
```
{{long(min,max)}}
```

uuid:
```
{{uuid()}}
```

uuid without dashes:
```
{{uuid("false")}}
```

hex (16 bytes):
```
{{hex()}}
```

hex (with byte size):
```
{{hex(size)}}
```

objectId (12 byte hex string):
```
{{objectId()}}
```

random boolean:
```
{{bool()}}
```

random boolean with given probability:
```
{{bool(0.9)}}
```

an incrementing index integer
```
{{index()}
```

a named incrementing index integer
```
{{index("index-name")}
```

an incrementing index integer with a specific starting point
```
{{index(78)}
```

an incrementing index integer with a name and a specific starting point
```
{{index("index-name",78)}
```

reset the default index
```
{{resetIndex("inner")}}
```

reset an index with name (more detailed example)

```
[
  '{{repeat(3)}}',
  {
    index: '{{index("outer")}}',
    friends: [
      '{{repeat(3)}}',
      {
        id: '{{index("inner")}}',
        name: 'nameValue'{{resetIndex("inner")}}
      }
    ],
}
]
```

lorem ipsum words:
```
{{lorem(count,"words")}}
```

lorem ipsum paragraphs:
```
{{lorem(count,"paragraphs")}}
```

random phone number:
```
{{phone()}}
```

random gender (male or female):
```
{{gender()}}
```

current date date:
```
{{date()}}
```

current date with format:
```
{{date("java-simple-date-format")}}
```

random date between two dates with format (your input must be in this format dd-MM-yyyy HH:mm:ss):
```
{{date("begin-date","end-date","java-simple-date-format"}}
```

random date between two dates with default format (your input must be in this format EEE, d MMM yyyy HH:mm:ss z):
```
{{date("begin-date","end-date"}}
```

convert date format from one format to another:
```
{{dateFormat("06-16-1956 12:00:00", "from-simple-date-format", "to-simple-date-format")}}
```

current timestamp (milliseconds, between the current time and midnight, January 1, 1970 UTC):
```
{{timestamp()}}
```

random timestamp (milliseconds since midnight, January 1, 1970 UTC) between two dates with default format (your input must be in this format EEE, d MMM yyyy HH:mm:ss z):
```
{{timestamp("begin-date","end-date"}}
```

random country:
```
{{country()}}
```

add (or subtract) days to date with format:
```
{{addDays("dd-MM-yyyy HH:mm:ss", "03-11-2018 09:27:56", 12)}}
```

add (or subtract) days to date with default input format:
```
{{addDays("03-11-2018 09:27:56", 12)}}
```

add (or subtract) hours to date with format:
```
{{addHours("dd-MM-yyyy HH:mm:ss", "03-11-2018 09:27:56", 12)}}
```

add (or subtract) hours to date with default input format:
```
{{addHours("03-11-2018 09:27:56", 12)}}
```

add (or subtract) minutes to date with format:
```
{{addMinutes("dd-MM-yyyy HH:mm:ss", "03-11-2018 09:27:56", 12)}}
```

add (or subtract) minutes to date with default input format:
```
{{addMinutes("03-11-2018 09:27:56", 12)}}
```

add (or subtract) months to date with format:
```
{{addMonths("dd-MM-yyyy HH:mm:ss", "03-11-2018 09:27:56", 12)}}
```

add (or subtract) months to date with default input format:
```
{{addMonths("03-11-2018 09:27:56", 12)}}
```

add (or subtract) seconds to date with format:
```
{{addSeconds("dd-MM-yyyy HH:mm:ss", "03-11-2018 09:27:56", 12)}}
```

add (or subtract) seconds to date with default input format:
```
{{addSeconds("03-11-2018 09:27:56", 12)}}
```

add (or subtract) weeks to date with format:
```
{{addWeeks("dd-MM-yyyy HH:mm:ss", "03-11-2018 09:27:56", 12)}}
```

add (or subtract) weeks to date with default input format:
```
{{addWeeks("03-11-2018 09:27:56", 12)}}
```

add (or subtract) years to date with format:
```
{{addYears("dd-MM-yyyy HH:mm:ss", "03-11-2018 09:27:56", 12)}}
```

add (or subtract) years to date with default input format:
```
{{addYears("03-11-2018 09:27:56", 12)}}
```

a json mapping with all country codes to mappings:
```
{{countryList()}}
```


a json mapping with just specified country codes to mappings:

{"IN":"India","US":"United States","UK":"United Kingdom"}

```
{{countryList("IN", "US", "UK")}}
```

random city:
```
{{city()}}
```

random state:
```
{{state()}}
```

random company:
```
{{company()}}
```

random lastname:
```
{{lastName()}}
```

random first name:
```
{{firstName()}}
```

random username based on first initial from random first name and lastname lowercased:
```
{{username()}}
```

random email:
```
{{email()}}
```

random email with domain:
```
{{email("mydomain.com")}}
```

random social security number:
```
{{ssn()}}
```

random ipv4:
```
{{ipv4()}}
```

random ipv6:
```
{{ipv6()}}
```

random ipv6 (uppercase):
```
{{ipv6("upper")}}
```

random ipv6 (lowercase):
```
{{ipv6("lower")}}
```

concat (var arg):
```
{{concat("A","B","C","D")}}
```

substring:
```
{{substring("word",3)}}
```

```
{{substring("long word", 1, 6)}}
```

random item from list:
```
{{random("red","yellow","green")}}
```

random string with alphabetic characters (defaults to between 10 and 20 characters):
```
{{alpha()}}
```

```
{{alpha(min,max)}}
```

```
{{alpha(length)}}
```

random string with alpha-numeric characters (defaults to between 10 and 20 characters):
```
{{alphaNumeric()}}
```

```
{{alphaNumeric(min,max)}}
```

```
{{alphaNumeric(length)}}
```

lower case a string:
```
{{toLowerCase("red")}}
```

upper case a string:
```
{{toUpperCase("red")}}
```

put a value in the cache:
```
{{put("key", "VALUE"}}
```

retrieve a value from the cache:
```
{{get("key"}}
```


## Escape braces

If you want to escape braces from within a function use a single escape character as seen in the example below:

```
{{concat("\{", "test", "\}")}}
```

## XML support

```
<?xml version="1.0" encoding="UTF-8"?>
<root>
  '{{repeat(2)}}',
  <element>
    <id>{{guid()}}</id>
	<name>{{firstName()}}</name>
    <index>{{lastName()}}</index>
  </element>

<tags>
      '{{repeat(7)}}',
      {{lorem(1, "words")}}
</tags>
<friends>
      '{{repeat(3)}}',
	  <friend>
        <id>{{index()}}</id>
        <name>{{firstName()}} {{surname()}}</name>
	</friend>
</friends>
</root>
```

## Nesting functions

jason-data-generator supports nesting functions as well.

For example, if you wanted to create results that looked like dollar amounts you could do something like:

```
{{concat("$",float(0.90310, 5.3421, "%.2f"))}}
```

or something like this if you wanted a capitalized F or M:

```
{{toUpperCase(substring(gender(),0,1))}}
```

## Use of put and get functions

The get and put functions can be used to use previously-used values later on in the document.  The put function returns the value passed into it.  See the following example.

```
{
  "firstName": "{{put("firstName", firstName())}}",
  "lastName": "{{put("lastName", lastName())}}",
  "email": "{{get("firstName")}}.{{get("lastName")}}@mydomain.com"
}

```

produces

```
{
  "firstName": "Eve",
  "lastName": "Acosta",
  "email": "Eve.Acosta@mydomain.com"
}
```

## Use date add functions

The date add functions can be used in conjuction with the get and put functions to create new date values by adding (or subtracting) 

```
{
    "day1": "{{put("date", date("dd-MM-yyyy HH:mm:ss"))}}",
    "day2": "{{addDays(get("date"), 12)}}"
}

```

produces

```
{
    "day1": "03-11-2018 09:27:56",
    "day2": "15-11-2018 09:27:56"
}
```


## Creating Custom Functions

You can also create new functions if you create the classes and register the function with the function registry.

-When you create Functions you must annotate the class with the @Function annotation and you must specify one or more names for the function.
-Use the @FunctionInvocation annotation to indicate the method that will be executed when the function is called.  The arguments of the function must be strings (or a Vararg String argument) and the method must return a string.

```
package my.package;

import com.github.vincentrussell.json.datagenerator.functions.Function;
import com.github.vincentrussell.json.datagenerator.functions.FunctionInvocation;

import java.util.Random;

@Function(name = "new-function")
public class NewFunction {

    private static final Random RANDOM = new Random();

    @FunctionInvocation
    public String getRandomInteger(String min, String max) {
        return getRandomInteger(Integer.parseInt(min), Integer.parseInt(max));
    }

    private String getRandomInteger(Integer min, Integer max) {
        int randomNumber = RANDOM.nextInt(max - min) + min;
        return Integer.toString(randomNumber);
    }
}

```

then you can put the jar that you have created on the classpath with the the standalone jar (-f registers one or more classes with the Function Registry):

```
java -cp json-data-generator-1.9-standalone.jar:yourfunctions.jar com.github.vincentrussell.json.datagenerator.CLIMain -s source.json -d destination.json -f my.package.NewFunction
```

# Change Log

## [1.9](https://github.com/vincentrussell/json-data-generator/tree/json-data-generator-1.9) (2019-01-01)

**Bugs:**

- random double function now supports a format
- index values can be reset to their starting point with resetIndex function

## [1.8](https://github.com/vincentrussell/json-data-generator/tree/json-data-generator-1.8) (2018-12-01)

**Bugs:**

- String arguments for functions can be single or double-quoted

## [1.7](https://github.com/vincentrussell/json-data-generator/tree/json-data-generator-1.7) (2018-11-05)

**Improvements:**

- Added the ability to generate a uuid without dashes.
- Added the abiilty to add or subtract days, hours, mintues, months, seconds, and years to dates
- Added dateFormat function
- Added UTF-8 international character support

## [1.6](https://github.com/vincentrussell/json-data-generator/tree/json-data-generator-1.6) (2018-10-08)

**Improvements:**

- Added put and get functions to allow to reference values later that had been stored previously
- Upgraded guava to version 25.0-jre
- Upgraded java compile version to java 1.8


## [1.5](https://github.com/vincentrussell/json-data-generator/tree/json-data-generator-1.5) (2018-07-31)

**Improvements:**

- Adding boolean with probability support
- Adding the ability to provide a domain to the email function
- Added the ability to escape brackets within functions so that the interperter can understand that the function and wont return early
- Added a hex function
- Added a countryList function

## [1.4.1](https://github.com/vincentrussell/json-data-generator/tree/json-data-generator-1.4.1) (2018-07-15)

**Bugs:**

- Added check for invalid scenarios to avoid going in infinite loop

## [1.4](https://github.com/vincentrussell/json-data-generator/tree/json-data-generator-1.4) (2018-06-25)

**Improvements:**

- Created username function
- Added XML support


# Change Log

## [1.3](https://github.com/vincentrussell/json-data-generator/tree/json-data-generator-1.3) (2017-02-04)

**Improvements:**

- Changed index function to be based on a String key not it's level of nesting to reduce complexity
- Changed index function to allow for an integer to start at.


# Change Log

## [1.2](https://github.com/vincentrussell/json-data-generator/tree/json-data-generator-1.2) (2016-04-10)

**Improvements:**

- Standalone interactive mode
- The ability to specify a timezone in standalone mode
- New timestamp function
- New alpha function
- New alphaNumeric function

## [1.1](https://github.com/vincentrussell/json-data-generator/tree/json-data-generator-1.1) (2016-03-20)

**Improvements:**

- Repeats can be randomized between a range of two integers
- Multiple names can be used in the @Function annotation provide multiple functions in the json mapping to the same name
- Created objectId (12 byte hex string) function

**Bugs:**

- Floating point numbers can be parsed and used in fuctions
- Repeats can be used with functions surrounded in strings, like '{{lorem(1, "words")}}'
