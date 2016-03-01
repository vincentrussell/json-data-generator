# json-data-generator

json-data-generator helps you build json data that you need for test data purposes.   It has a lot of nice features that you can use to build as much test data as you need.

##How to use it

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

or you can run the standalone jar on a source json file:

```
java -jar json-data-generator-1.0-standalone.jar -s source.json -d destination.json
```

##Repeats
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

-When you create Functions you must annotate the class with the @Function annotation and you must specify a name for the function.
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
java -cp json-data-generator-1.0-standalone.jar:yourfunctions.jar com.github.vincentrussell.json.datagenerator.CLIMain -s source.json -d destination.json -f my.package.NewFunction
```
