# json-data-generator

json-data-generator helps you build json data that you need for test data purposes.   It has a lot of nice features that you can use to build as much test data as you need.

## Maven

Add a dependency to `com.github.vincentrussell:json-data-generator`. 

```
<dependency>
   <groupId>com.github.vincentrussell</groupId>
   <artifactId>json-data-generator</artifactId>
   <version>1.3</version>
</dependency>
```

## Requirements
- JDK 1.7 or higher
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
java -jar json-data-generator-1.3-standalone.jar -s source.json -d destination.json
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

###Repeats
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

##Available functions

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

random double in range:
```
{{double(min,max)}}
```

random long in range:
```
{{long(min,max)}}
```

uuid:
```
{{uuid()}}
```

objectId (12 byte hex string):
```
{{objectId()}}
```

random boolean:
```
{{bool()}}
```

an incrementing index integer (will be a different index based on levels of nesting of json)
```
{{index()}
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

random email:
```
{{email()}}
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

##Nesting functions

jason-data-generator supports nesting functions as well.

For example, if you wanted to create results that looked like dollar amounts you could do something like:

```
{{concat("$",float(0.90310, 5.3421, "%.2f"))}}
```

or something like this if you wanted a capitalized F or M:

```
{{toUpperCase(substring(gender(),0,1))}}
```

##Creating Custom Functions

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
java -cp json-data-generator-1.3-standalone.jar:yourfunctions.jar com.github.vincentrussell.json.datagenerator.CLIMain -s source.json -d destination.json -f my.package.NewFunction
```
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
